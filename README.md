# PurifierRentalPJT
21년 1차수 4조
# PurifierRentalProject (정수기렌탈 서비스)

4조 정수기 렌탈 신청 서비스 프로젝트 입니다.

# Table of contents

- [pirifierRentalProject (정수기 렌탈 신청 서비스)](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오

고객이 정수기 렌탈 서비스 가입신청을 하면 설치기사가 방문하여 설치를 하고, 가입 취소 시 취소 처리를 할 수 있도록 한다.

기능적 요구사항
1. 고객이 정수기 렌탈 서비스 가입신청을 한다.
1. 가입신청 접수가 되면, 자동으로 시스템이 가입요청 지역의 설치 기사에게 설치 요청이 된다.
1. 설치기사는 설치요청을 할당받는다.
1. 설치기사는 설치를 완료 후 설치 완료 처리를 한다.
1. 설치가 완료되면 정수기 렌탈 서비스 신청이 완료 처리가 된다.
1. 고객이 가입 신청을 취소할 수 있다.
1. 가입신청이 취소되면 설치 취소된다.(설치취소 처리는 Req/Res 테스트를 위해 임의로 동기처리)
1. 고객은 설치진행상태를 수시로 확인할 수 있다.

비기능적 요구사항
1. 트랜잭션
    1. 가입취소 신청은 설치취소가 동시 이루어 지도록 한다
1. 장애격리
    1. 정수기 렌탈 가입신청과 취소는 고객서비스 담당자의 접수, 설치 처리와 관계없이 항상 처리 가능하다.

1. 성능
    1. 고객서비스 담당자는 설치 진행상태를 수시로 확인하여 모니터링 한다.(CQRS)



# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?
    - 


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
![image](https://user-images.githubusercontent.com/56263370/87296744-32433480-c542-11ea-9683-6b792f12cf55.png)  

## TO-BE 조직 (Vertically-Aligned)
![image](https://user-images.githubusercontent.com/56263370/87296805-4d15a900-c542-11ea-8fc2-15640ee62906.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  
  - http://msaez.io/#/storming/tumGnckjgrc4UVXq2EBT4EFYhnT2/mine/c03f2bb6625a2ed5bef6fcf78dde4b26/-MC01LpwJ3zz9a4MgvCj

### 이벤트 도출
![image](https://user-images.githubusercontent.com/56263370/87490118-ce268a80-c67f-11ea-9e0f-28725998ecf4.png)


### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/56263370/87490154-edbdb300-c67f-11ea-9923-d08c29203bc7.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
        - 중복/불필요, 처리 프로세스에 해당하는 이벤트 제거

### 폴리시 부착
![image](https://user-images.githubusercontent.com/56263370/87490165-f8784800-c67f-11ea-919b-edee122caf1f.png)


### 액터, 커맨드 부착하여 읽기 좋게
![image](https://user-images.githubusercontent.com/56263370/87490182-04fca080-c680-11ea-87e3-829b12b1df15.png)


### 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/56263370/87490218-19409d80-c680-11ea-83de-464d8c9e1d47.png)

    -가입신청, 서비스관리센터, 설치 부분을 정의함

### 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/56263370/87490225-2198d880-c680-11ea-9aaa-1210b8455719.png)


    - 도메인 서열 분리 : 가입신청 -> 서비스관리센터 -> 설치 순으로 정의
       


### 폴리시의 이동과 컨텍스트 매핑 (파란색점선은 Pub/Sub, 빨간색실선은 Req/Resp)
![image](https://user-images.githubusercontent.com/56263370/87490238-2e1d3100-c680-11ea-8d63-9b9626cf0fd4.png)


### 완성된 1차 모형
![image](https://user-images.githubusercontent.com/56263370/87490104-bfd86e80-c67f-11ea-95d9-8d6d41dd1eea.png)


    - View Model 추가
![image](https://user-images.githubusercontent.com/56263370/87490657-2ca03880-c681-11ea-9a88-0161e94cdf71.png)	

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증
#### 시나리오 Coverage Check (1)
![image](https://user-images.githubusercontent.com/56263370/87491137-4a21d200-c682-11ea-9e3e-66540f9c0af8.png)

#### 시나리오 Coverage Check (2)
![image](https://user-images.githubusercontent.com/56263370/87491151-59088480-c682-11ea-86a6-53df001934d1.png)

#### 비기능 요구사항 coverage
![image](https://user-images.githubusercontent.com/56263370/87491175-66be0a00-c682-11ea-865f-9ee9e6113ed8.png)



## 헥사고날 아키텍처 다이어그램 도출
![image](https://user-images.githubusercontent.com/56263370/87491731-e0a2c300-c683-11ea-9c78-ac7beda99da4.png)


## 신규 서비스 추가 시 기존 서비스에 영향이 없도록 열린 아키택처 설계

- 신규 개발 조직 추가 시, 기존의 마이크로 서비스에 수정이 발생하지 않도록 Inbund 요청을 REST 가 아닌 Event를 Subscribe 하는 방식으로 구현하였다.
- 기존 마이크로 서비스에 대하여 아키텍처, 데이터베이스 구조와 관계 없이 추가할 수 있다.

![image](https://user-images.githubusercontent.com/56263370/87504063-b7dcf680-c6a0-11ea-880f-629bbabecf57.png)

### 운영과 Retirement

Request/Response 방식으로 구현하지 않았기 때문에 서비스가 더이상 불필요해져도 Deployment 에서 제거되면 기존 마이크로 서비스에 어떤 영향도 주지 않는다.

* [비교] 설치 (installation) 마이크로서비스의 경우 API 변화나 Retire 시에 서비스 관리센터(ManagementCenter) 마이크로 서비스의 변경을 초래한다.

예) API 변화시
```
# ManagementCenter.java (Entity)

    @PostUpdate
    public void onPostUpdate() {
            ipTVShopProject.external.Installation installation = new ipTVShopProject.external.Installation();

            installation.setOrderId(this.getOrderId());
            ManagementCenterApplication.applicationContext.getBean(ipTVShopProject.external.InstallationService.class)
                    .installationCancellation(installation);

	-------->
	
            ManagementCenterApplication.applicationContext.getBean(ipTVShopProject.external.InstallationService.class)
                    .installationCancellation2222222(installation);
    }	    
```

예) Retire 시
```
# ManagementCenter.java (Entity)

    @PostUpdate
    public void onPostUpdate(){
    /**
            ipTVShopProject.external.Installation installation = new ipTVShopProject.external.Installation();

            installation.setOrderId(this.getOrderId());
            ManagementCenterApplication.applicationContext.getBean(ipTVShopProject.external.InstallationService.class)
                    .installationCancellation(installation);

    **/
    } 
```

# 구현:
분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084 이다)

```
- Local
	cd Order
	mvn spring-boot:run

	cd ManagementCenter
	mvn spring-boot:run

	cd Installation
	mvn spring-boot:run

	cd orderstatus
	mvn spring-boot:run

- EKS : CI/CD 통해 빌드/배포 ("운영 > CI-CD 설정" 부분 참조)
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: Order, ManagementCenter, Installation
- Installation(설치) 마이크로서비스 예시

```
	package ipTVShopProject;

	import javax.persistence.*;
	import org.springframework.beans.BeanUtils;
	import java.util.List;

	@Entity
	@Table(name="Installation_table")
	public class Installation {

		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		private Long id;
		private Long engineerId;
		private String engineerName;
		private String installReservationDate;
		private String installCompleteDate;
		private Long orderId;
		private String status;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		public Long getEngineerId() {
			return engineerId;
		}

		public void setEngineerId(Long engineerId) {
			this.engineerId = engineerId;
		}
		public String getEngineerName() {
			return engineerName;
		}

		public void setEngineerName(String engineerName) {
			this.engineerName = engineerName;
		}
		public String getInstallReservationDate() {
			return installReservationDate;
		}

		public void setInstallReservationDate(String installReservationDate) {
			this.installReservationDate = installReservationDate;
		}
		public String getInstallCompleteDate() {
			return installCompleteDate;
		}

		public void setInstallCompleteDate(String installCompleteDate) {
			this.installCompleteDate = installCompleteDate;
		}
		public Long getOrderId() {
			return orderId;
		}

		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	}
```




## 폴리글랏 퍼시스턴스
- order, ManagementCenter, installation 서비스는 H2 적용
- orderstatus 서비스는 My-SQL DB를 적용을 위해 다음 사항을 수정하여 적용(AWS RDS 적용)

pom.xml dependency 추가
```
	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<scope>runtime</scope>
	</dependency>
```

application.yml 파일 수정
```
	datasource:
		url: ${url}
		username: ${username}
		password: ${password}
		driver-class-name: com.mysql.cj.jdbc.Driver
```

buildspec.yml 파일 수정
```
    env:
      - name: url
	valueFrom:
	  configMapKeyRef:
	    name: iptv
	    key: urlstatus 
      - name: username
	valueFrom:
	  secretKeyRef:
	    name: iptv
	    key: username          
      - name: password
	valueFrom:
	  secretKeyRef:
	    name: iptv
	    key: password    
```

## 동기식 호출 과 Fallback 처리

- 분석 단계에서의 조건 중 하나로 서비스 관리센터(ManagementCenter)에서 인터넷 가입신청 취소를 요청 받으면, 
설치(installation) 서비스 취소 처리하는 부분을 동기식 호출하는 트랜잭션으로 처리하기로 하였다. 
- 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어 있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다.

설치 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현
```
# (ManagementCenter) InstallationService.java

	package ipTVShopProject.external;


	@FeignClient(name="Installation", url="http://Installation:8080")
	public interface InstallationService {

		@RequestMapping(method= RequestMethod.PATCH, path="/installations")
		public void installationCancellation(@RequestBody Installation installation);

	}
```

인터넷 가입 취소 요청(cancelRequest)을 받은 후, 처리하는 부분
```
# (Installation) InstallationController.java

	package ipTVShopProject;

	@RestController
	public class InstallationController {
	    @Autowired
	    InstallationRepository installationRepository;

	    @RequestMapping(method=RequestMethod.POST, path="/installations")
	    public void installationCancellation(@RequestBody Installation installation) {

		Installation installationCancel = installationRepository.findByOrderId(installation.getOrderId());
		installationCancel.setStatus("INSTALLATIONCANCELED");
		installationRepository.save(installationCancel);

	    }
	}
```

## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

가입 신청(order)이 이루어진 후에 서비스 관리센터(ManagementCenter) 서비스로 이를 알려주는 행위는 비동기식으로 처리하여, 서비스 관리센터(ManagementCenter) 서비스의 처리를 위하여 가입신청(order)이 블로킹 되지 않도록 처리한다.
 
- 이를 위하여 가입 신청에 기록을 남긴 후에 곧바로 가입 신청이 되었다는 도메인 이벤트를 카프카로 송출한다.(Publish)
```
# (order) order.java

    @PostPersist
    public void onPostPersist(){

        if(this.getStatus().equals("JOINORDED")){
            JoinOrdered joinOrdered = new JoinOrdered();
            BeanUtils.copyProperties(this, joinOrdered);
            joinOrdered.publishAfterCommit();
        }
    }
```
- 서비스 관리센터 서비스에서는 가입신청 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다.
```
# (ManagementCenter) PolicyHandler.java

@Service
public class PolicyHandler{
    @Autowired
    ManagementCenterRepository managementCenterRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverJoinOrdered_OrderRequest(@Payload JoinOrdered joinOrdered){

        if(joinOrdered.isMe()){
            ManagementCenter oa = new ManagementCenter();

            oa.setOrderId(joinOrdered.getId());
            oa.setInstallationAddress(joinOrdered.getInstallationAddress());
            oa.setId(joinOrdered.getId());
            oa.setStatus("JOINORDED");
            oa.setEngineerName("Engineer" + joinOrdered.getId());
            oa.setEngineerId(joinOrdered.getId() + 100);

            managementCenterRepository.save(oa);
        }
    }
}
```
가입신청은 서비스 관리센터와 완전히 분리되어 있으며, 이벤트 수신에 따라 처리되기 때문에, 서비스 관리센터 서비스가 유지보수로 인해 잠시 내려간 상태라도 가입신청을 받는데 문제가 없다.


## CQRS

가입신청 상태 조회를 위한 서비스를 CQRS 패턴으로 구현하였다.
- order, ManagementCenter, Installation 개별 aggregate 통합 조회로 인한 성능 저하를 막을 수 있다.
- 모든 정보는 비동기 방식으로 발행된 이벤트를 수신하여 처리된다.
- 별도의 서비스(orderStatus), 저장소(AWS RDS-mySQL)로 구현하였다.
- 설계 : MSAEz 설계의 view 매핑 설정 참조



## API Gateway

API Gateway를 통하여, 마이크로 서비스들의 진입점을 통일한다.

```
# application.yml 파일에 라우팅 경로 설정

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: Order
          uri: http://Order:8080
          predicates:
            - Path=/orders/** 
        - id: ManagementCenter
          uri: http://ManagementCenter:8080
          predicates:
            - Path=/managementCenters/** 
        - id: Installation
          uri: http://Installation:8080
          predicates:
            - Path=/installations/** 
        - id: orderstatus
          uri: http://orderstatus:8080
          predicates:
            - Path=/orderStatuses/** 
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080
```

- EKS에 배포 시, MSA는 Service type을 ClusterIP(default)로 설정하여, 클러스터 내부에서만 호출 가능하도록 한다.
- API Gateway는 Service type을 LoadBalancer로 설정하여 외부 호출에 대한 라우팅을 처리한다.

```
# buildspec.yml 설정

  cat <<EOF | kubectl apply -f -
  apiVersion: v1
  kind: Service
  metadata:
    name: $_PROJECT_NAME
    labels:
      app: $_PROJECT_NAME
    spec:
    ports:
      - port: 8080
        targetPort: 8080
    selector:
      app: $_PROJECT_NAME
    type: LoadBalancer
  EOF
```


# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 AWS CodeBuild를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 buildspec.yml 에 포함되었다.
아래 Github 소스 코드 변경 시, CodeBuild 빌드/배포가 자동 시작되도록 구성하였다.
- https://github.com/ChaSang-geol/ipTVShopProject_gateway
- https://github.com/ChaSang-geol/ipTVShopProject_Order
- https://github.com/ChaSang-geol/ipTVShopProject_ManagementCenter
- https://github.com/ChaSang-geol/ipTVShopProject_Installation
- https://github.com/ChaSang-geol/ipTVShopProject_orderstatus

## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택
  - Spring FeignClient + Hystrix 옵션을 사용하여 구현할 경우, 도메인 로직과 부가 기능 로직이 서비스에 같이 구현된다.
  - istio를 사용해서 서킷 브레이킹 적용이 가능하다.

- 서비스를 istio로 배포(동기 호출하는 Request/Response 2개 서비스)

```
kubectl get deploy managementcenter -o yaml > managementcenter_deploy.yaml 
kubectl apply -f <(istioctl kube-inject -f managementcenter_deploy.yaml) 

kubectl get deploy installation -o yaml > installation_deploy.yaml 
kubectl apply -f <(istioctl kube-inject -f installation_deploy.yaml) 

```

- istio 에서 서킷브레이커 설정(DestinationRule)
```
cat <<EOF | kubectl apply -f -
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: installation
spec:
  host: installation
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 1           # 목적지로 가는 HTTP, TCP connection 최대 값. (Default 1024)
      http:
        http1MaxPendingRequests: 1  # 연결을 기다리는 request 수를 1개로 제한 (Default 
        maxRequestsPerConnection: 1 # keep alive 기능 disable
        maxRetries: 3               # 기다리는 동안 최대 재시도 수(Default 1024)
    outlierDetection:
      consecutiveErrors: 5          # 5xx 에러가 5번 발생하면
      interval: 1s                  # 1초마다 스캔 하여
      baseEjectionTime: 30s         # 30 초 동안 circuit breaking 처리   
      maxEjectionPercent: 100       # 100% 로 차단
EOF

```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작을 확인한다.
- 동시사용자 100명
- 60초 동안 실시


### 오토스케일 아웃

- 가입신청 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 1프로를 넘어서면 replica 를 10개까지 늘려준다.
```
kubectl autoscale deploy order --min=1 --max=10 --cpu-percent=1
```

- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어준다.
```
kubectl get deploy order -w

kubectl get hpa order -w
```

- 사용자 50명으로 워크로드를 3분 동안 걸어준다.
```
siege -c50 -t180S --content-type "application/json" 'http://a518c6481215d478b8b769aa034cdff4-46291629.us-east-2.elb.amazonaws.com:8080/orders POST {"productId": "2001", "productName": "internet", "installationAddress": "Seoul", "customerId": "1", "orderDate": "20200715", "status": "JOINORDED"}'

```

- 오토스케일 발생하지 않음(siege 실행 결과 오류 없이 수행됨 : Availability 100%)
- 서비스에 복잡한 비즈니스 로직이 포함된 것이 아니어서, CPU 부하를 주지 못한 것으로 추정된다.


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 한다.
```
siege -c30 -t150S --content-type "application/json" 'http://a518c6481215d478b8b769aa034cdff4-46291629.us-east-2.elb.amazonaws.com:8080/orders POST {"productId": "2001", "productName": "internet", "installationAddress": "Seoul", "customerId": "1", "orderDate": "20200715", "status": "JOINORDED"}'
```

- readinessProbe, livenessProbe 설정되지 않은 상태로 buildspec.yml을 수정한다.
- Github에 buildspec.yml 수정 발생으로 CodeBuild 자동 빌드/배포 수행된다.
- siege 수행 결과 : Availability가 100% 미만으로 떨어짐(79.06%) -> 컨테이너 배포는 되었지만 ready 되지 않은 상태에서 호출 유입됨
![image](https://user-images.githubusercontent.com/56263370/87494646-80634f80-c68a-11ea-98ce-1779224ecfbf.png)

- readinessProbe, livenessProbe 설정하고 buildspec.yml을 수정한다.
- Github에 buildspec.yml 수정 발생으로 CodeBuild 자동 빌드/배포 수행된다.
- siege 수행 결과 : Availability가 100%로 무정지 재배포 수행 확인할 수 있다.
![image](https://user-images.githubusercontent.com/56263370/87494675-97a23d00-c68a-11ea-9ad2-a8859861ce9d.png)


## ConfigMap 적용

- 설정의 외부 주입을 통한 유연성을 제공하기 위해 ConfigMap을 적용한다.
- orderstatus 에서 사용하는 mySQL(AWS RDS 활용) 접속 정보를 ConfigMap을 통해 주입 받는다.

```
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: iptv
data:
  urlstatus: "jdbc:mysql://iptv.cgzkudckye4b.us-east-2.rds.amazonaws.com:3306/orderstatus?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8"
EOF
```

## Secret 적용

- username, password와 같은 민감한 정보는 ConfigMap이 아닌 Secret을 적용한다.
- etcd에 암호화 되어 저장되어, ConfigMap 보다 안전하다.
- value는 base64 인코딩 된 값으로 지정한다. (echo root | base64)

```
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
metadata:
  name: iptv
type: Opaque
data:
  username: xxxxx <- 보안 상, 임의의 값으로 표시함 
  password: xxxxx <- 보안 상, 임의의 값으로 표시함
EOF
```


## 운영 모니터링

### 쿠버네티스 구조
쿠버네티스는 Master Node(Control Plane)와 Worker Node로 구성된다.

![image](https://user-images.githubusercontent.com/64656963/86503139-09a29880-bde6-11ea-8706-1bba1f24d22d.png)


### 1. Master Node(Control Plane) 모니터링
Amazon EKS 제어 플레인 모니터링/로깅은 Amazon EKS 제어 플레인에서 계정의 CloudWatch Logs로 감사 및 진단 로그를 직접 제공한다.

- 사용할 수 있는 클러스터 제어 플레인 로그 유형은 다음과 같다.
```
  - Kubernetes API 서버 컴포넌트 로그(api)
  - 감사(audit) 
  - 인증자(authenticator) 
  - 컨트롤러 관리자(controllerManager)
  - 스케줄러(scheduler)

출처 : https://docs.aws.amazon.com/ko_kr/eks/latest/userguide/logging-monitoring.html
```

- 제어 플레인 로그 활성화 및 비활성화
```
기본적으로 클러스터 제어 플레인 로그는 CloudWatch Logs로 전송되지 않습니다. 
클러스터에 대해 로그를 전송하려면 각 로그 유형을 개별적으로 활성화해야 합니다. 
CloudWatch Logs 수집, 아카이브 스토리지 및 데이터 스캔 요금이 활성화된 제어 플레인 로그에 적용됩니다.

출처 : https://docs.aws.amazon.com/ko_kr/eks/latest/userguide/control-plane-logs.html
```

### 2. Worker Node 모니터링

- 쿠버네티스 모니터링 솔루션 중에 가장 인기 많은 것은 Heapster와 Prometheus 이다.
- Heapster는 쿠버네티스에서 기본적으로 제공이 되며, 클러스터 내의 모니터링과 이벤트 데이터를 수집한다.
- Prometheus는 CNCF에 의해 제공이 되며, 쿠버네티스의 각 다른 객체와 구성으로부터 리소스 사용을 수집할 수 있다.

- 쿠버네티스에서 로그를 수집하는 가장 흔한 방법은 fluentd를 사용하는 Elasticsearch 이며, fluentd는 node에서 에이전트로 작동하며 커스텀 설정이 가능하다.

- 그 외 오픈소스를 활용하여 Worker Node 모니터링이 가능하다. 아래는 istio, mixer, grafana, kiali를 사용한 예이다.

```
아래 내용 출처: https://bcho.tistory.com/1296?category=731548

```
- 마이크로 서비스에서 문제점중의 하나는 서비스가 많아 지면서 어떤 서비스가 어떤 서비스를 부르는지 의존성을 알기가 어렵고, 각 서비스를 개별적으로 모니터링 하기가 어렵다는 문제가 있다. Istio는 네트워크 트래픽을 모니터링함으로써, 서비스간에 호출 관계가 어떻게 되고, 서비스의 응답 시간, 처리량등의 다양한 지표를 수집하여 모니터링할 수 있다.

![image](https://user-images.githubusercontent.com/64656963/86347967-ff738380-bc99-11ea-9b5e-6fb94dd4107a.png)

- 서비스 A가 서비스 B를 호출할때 호출 트래픽은 각각의 envoy 프록시를 통하게 되고, 호출을 할때, 응답 시간과 서비스의 처리량이 Mixer로 전달된다. 전달된 각종 지표는 Mixer에 연결된 Logging Backend에 저장된다.

- Mixer는 위의 그림과 같이 플러그인이 가능한 아답터 구조로, 운영하는 인프라에 맞춰서 로깅 및 모니터링 시스템을 손쉽게 변환이 가능하다.  쿠버네티스에서 많이 사용되는 Heapster나 Prometheus에서 부터 구글 클라우드의 StackDriver 그리고, 전문 모니터링 서비스인 Datadog 등으로 저장이 가능하다.

![image](https://user-images.githubusercontent.com/64656963/86348023-14501700-bc9a-11ea-9759-a40679a6a61b.png)

- 이렇게 저장된 지표들은 여러 시각화 도구를 이용해서 시각화 될 수 있는데, 아래 그림은 Grafana를 이용해서 서비스의 지표를 시각화 한 그림이다.

![image](https://user-images.githubusercontent.com/64656963/86348092-25992380-bc9a-11ea-9d7b-8a7cdedc11fc.png)

- 그리고 근래에 소개된 오픈소스 중에서 흥미로운 오픈 소스중의 하나가 Kiali (https://www.kiali.io/)라는 오픈소스인데, Istio에 의해서 수집된 각종 지표를 기반으로, 서비스간의 관계를 아래 그림과 같이 시각화하여 나타낼 수 있다.  아래는 그림이라서 움직이는 모습이 보이지 않지만 실제로 트래픽이 흘러가는 경로로 에니메이션을 이용하여 표현하고 있고, 서비스의 각종 지표, 처리량, 정상 여부, 응답 시간등을 손쉽게 표현해 준다.

![image](https://user-images.githubusercontent.com/64656963/86348145-3a75b700-bc9a-11ea-8477-e7e7178c51fe.png)


# 시연
 1. 인터넷 가입신청 -> installation 접수 완료 상태
 2. 설치 기사 설치 완료 처리 -> 가입 신청 완료 상태
 3. 가입 취소
 4. EDA 구현
   - ManagementCenter 장애 상황에서 order(가입 신청) 정상 처리
   - ManagementCenter 정상 전환 시 수신 받지 못한 이벤트 처리
 5. 무정지 재배포
 6. 오토 스케일링
