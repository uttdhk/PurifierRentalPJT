# PurifierRentalPJT
21년 1차수 과제
# PurifierRentalProject (정수기렌탈 서비스)

정수기 렌탈 신청 서비스 프로젝트 입니다.

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW

# Table of contents

- [PurifierRentalProject (정수기 렌탈 신청 서비스)](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [Saga 패턴 적용](#Saga-패턴-적용)
    - [동기식 호출과 Fallback 처리](#동기식-호출과-Fallback-처리)
    - [비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트](#비동기식-호출--시간적-디커플링--장애격리--최종-Eventual-일관성-테스트)
  - [운영](#운영)
    - [CI/CD 설정](#cicd-설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출--서킷-브레이킹--장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
    - [ConfigMap 적용](#ConfigMap-적용)
    - [Secret 적용](#Secret-적용)
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
1. (개인과제)고객이 주문 건에 대한 후기를 등록하면, 고객담당팀은 후기 정보를 받을 수 있다.

비기능적 요구사항
1. 트랜잭션
    1. 가입취소 신청은 설치취소가 동시 이루어 지도록 한다.
1. 장애격리
    1. 정수기 렌탈 가입신청과 취소는 고객서비스 담당자의 접수, 설치 처리와 관계없이 항상 처리 가능하다.
    1. (개인과제)후기 등록은 고객담당팀에서 후기 정보 수신 여부와 관련 없이 항상 처리 가능하다.

1. 성능
    1. 고객은 주문/설치 진행상태를 수시로 확인한다.(CQRS)



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


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
![as_is](https://user-images.githubusercontent.com/81946287/118763701-4323ab80-b8b3-11eb-9a23-15da2ea74528.png)

## TO-BE 조직 (Vertically-Aligned)
![200  ToBe](https://user-images.githubusercontent.com/81424367/120090704-7b42ae00-c13f-11eb-9f69-affbe7f7bc10.png)


## Event Storming 결과
* MSAEZ 로 모델링한 이벤트스토밍 결과:  
  - http://www.msaez.io/#/storming/WkOgp05NDFSWcJJQPBwSchRA0Kj1/mine/7d3e4aaa05043621312558cb132e0a41

### 이벤트 도출
![Event](https://user-images.githubusercontent.com/81946287/118763908-9d247100-b8b3-11eb-992f-f930e774b284.png)


### 폴리시,어그리게잇 부착
![Policy](https://user-images.githubusercontent.com/81946287/118763987-bb8a6c80-b8b3-11eb-9fe4-81c8f0380262.png)

    -가입신청, 서비스관리센터, 설치 부분을 정의함

### 액터, 커맨드 부착
![Command](https://user-images.githubusercontent.com/81946287/118764046-d1982d00-b8b3-11eb-9f90-87bf95f1660e.png)


### 바운디드 컨텍스트로 묶기
![BoundedContext](https://user-images.githubusercontent.com/81946287/118764169-099f7000-b8b4-11eb-9b65-acc48be3d56c.png)

    - 도메인 서열 분리 : 가입신청 -> 서비스관리센터 -> 설치 순으로 정의
       

### View모델 추가 및 폴리시의 이동과 컨텍스트 매핑 (파란색점선은 Pub/Sub, 빨간색실선은 Req/Resp)
![AddView](https://user-images.githubusercontent.com/81946287/118764287-38b5e180-b8b4-11eb-997c-92032adf193d.png)


### 완성된 1차 모형
![firstDesign](https://user-images.githubusercontent.com/81946287/118764341-4d927500-b8b4-11eb-8345-71254fe30f70.png)

### 수정된 2차 모형
![2ndDesign](https://user-images.githubusercontent.com/81946287/118765229-bc240280-b8b5-11eb-8bf4-2015470e7987.png)

    - 시나리오 내용을 매끄럽게 반영하기 위해 '서비스관리센터'를 '배정' 으로 변경
    - 가입신청과 동시에 자동 배정되는 요구사항에 따라 Manager 액터가 불필요하여 제거
    - 고객이 실시간 상태 확인을 위한 View 모델 배치

### 수정된 3차 모형(개인 과제 customer추가)
![msa](https://user-images.githubusercontent.com/81424367/120059540-39592f80-c08d-11eb-9da7-ff8eae7c0290.png)

    - 고객담당팀 마이크로서비스 추가
    - 후기 등록 후 Customer 조직으로 비동기 호출 되도록 구성

### 3차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증
#### 시나리오 Coverage Check (1)
![200  주문요청 및 설치완료](https://user-images.githubusercontent.com/81424367/120090709-7da50800-c13f-11eb-924f-a417090795c0.png)

#### 시나리오 Coverage Check (2)
![200  주문 취소 시나리오](https://user-images.githubusercontent.com/81424367/120090708-7d0c7180-c13f-11eb-947b-e30c97f8a76e.png)

#### 시나리오 Coverage Check (3)
![200  후기등록 시나리오](https://user-images.githubusercontent.com/81424367/120090712-7e3d9e80-c13f-11eb-91dc-fa0f5024c348.png)

#### 비기능 요구사항 coverage
![200  비기능적 요구사항](https://user-images.githubusercontent.com/81424367/120090705-7c73db00-c13f-11eb-8004-5f00c5621b43.png)


## 헥사고날 아키텍처 다이어그램 도출
![200  헥사고날 아키텍처](https://user-images.githubusercontent.com/81424367/120472109-781a1d00-c3e0-11eb-89ec-fd5cf380ae3c.png)


## 신규 서비스 추가 시 기존 서비스에 영향이 없도록 열린 아키택처 설계

- 신규 개발 조직 추가 시, 기존의 마이크로 서비스에 수정이 발생하지 않도록 Inbund 요청을 REST 가 아닌 Event를 Subscribe 하는 방식으로 구현하였다.
- 기존 마이크로 서비스에 대하여 아키텍처, 데이터베이스 구조와 관계 없이 추가할 수 있다.

![200  신규 서비스 추가 아키텍처](https://user-images.githubusercontent.com/81424367/120472138-7d776780-c3e0-11eb-9f8e-5cc1d45e806d.png)

# 구현:
분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084 이다)

```
- Local
	cd Order
	mvn spring-boot:run

	cd Assignment
	mvn spring-boot:run

	cd Installation
	mvn spring-boot:run

	cd Customer
	mvn spring-boot:run

- EKS : CI/CD 통해 빌드/배포 ("운영 > CI-CD 설정" 부분 참조)
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: Order, Assignment, Installation, Customer
- Assignment(배정) 마이크로서비스 예시

```
	package purifierrentalpjt;

	import javax.persistence.*;
	import org.springframework.beans.BeanUtils;
	
	import lombok.Getter;
	import lombok.Setter;
	
	import java.util.List;
	import java.util.Date;

	@Entity
	@Getter
	@Setter
	@Table(name="Assignment_table")
	public class Assignment {
		
		@Id
    		@GeneratedValue(strategy=GenerationType.AUTO)
    		private Long id;
    		private Long orderId;
    		private String installationAddress;
    		private Long engineerId;
    		private String engineerName;
    		private String status;

    		@PostPersist
    		public void onPostPersist(){
        
        		System.out.println(this.getStatus() + "POST TEST");
        
        		if(this.getStatus().equals("orderRequest")) {

            		  EngineerAssigned engineerAssigned = new EngineerAssigned();

            		  engineerAssigned.setId(this.getId()); 
            		  engineerAssigned.setOrderId(this.getId()); 
            		  engineerAssigned.setInstallationAddress(this.getInstallationAddress()); 
            		  engineerAssigned.setEngineerId(this.getEngineerId()); 
            		  engineerAssigned.setEngineerName(this.getEngineerName()); 
            
            		  BeanUtils.copyProperties(this, engineerAssigned);
            		  engineerAssigned.publishAfterCommit();

        		} else if (this.getStatus().equals("installationComplete")) {

            		  JoinCompleted joinCompleted = new JoinCompleted();

            		  joinCompleted.setId(this.getId()); 
            		  joinCompleted.setOrderId(this.orderId); 
            		  joinCompleted.setStatus(this.getStatus());
```

적용 후 REST API의 테스트
1) 정수기 렌탈 서비스 신청 & 설치완료 처리

- (a) http -f POST localhost:8081/order/joinOrder productId=101 productName="PURI1" installationAddress="Address1001" customerId=201
![101  주문요청(01  POST)](https://user-images.githubusercontent.com/81424367/120089885-46335d00-c139-11eb-9557-6e470c44f4ed.png)

- (b) http -f PATCH http://localhost:8083/installations orderId=1 
![104  설치완료(02  REST 호출 처리)](https://user-images.githubusercontent.com/81424367/120089930-b641e300-c139-11eb-9bf6-24196f2648dd.png)

2) 카프카 메시지 확인

- (a) 서비스 신청 후 : JoinOrdered -> EngineerAssigned -> InstallationAccepted
- (b) 설치완료 처리 후 : InstallationCompleted
![104  설치완료(05  kafka)](https://user-images.githubusercontent.com/81424367/120089933-b7731000-c139-11eb-8e4b-c5196d678fc6.png)

- (개인과제)Customer(고객담당팀) 마이크로서비스 예시

```
package purifierrentalpjt;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Customer_table")
public class Customer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long productId;
    private String productName;
    private Long customerId;
    private Integer point;
    private String commentMessage;

    @PostPersist
    public void onPostPersist(){
        
        CommentAccepted commentAccepted = new CommentAccepted();
        System.out.println("##### 코멘트 확인 Pub(commentAccepted) #####" + commentAccepted.toJson() + "\n\n"); 

        commentAccepted.setId(this.getId());
        commentAccepted.setOrderId(this.getId());
        commentAccepted.setCustomerId(this.getCustomerId());

        BeanUtils.copyProperties(this, commentAccepted);
        commentAccepted.publishAfterCommit();

    }
```

적용 후 REST API의 테스트
1) 정수기 후기 등록

- (a) http -f POST localhost:8081/order/registerComment id=1 productId=101 productName="PURI1" customerId=201 point=97 commentMessage="Good Water"
![103  후기등록(01  REST POST registerComment)](https://user-images.githubusercontent.com/81424367/120089947-cc4fa380-c139-11eb-8e0d-eebf72960f19.png)
![103  후기등록(02  order_table update registerComment Controller)](https://user-images.githubusercontent.com/81424367/120089948-cce83a00-c139-11eb-8e2a-59801df4379a.png)
![103  후기등록(03  order_commentRegistered pub)](https://user-images.githubusercontent.com/81424367/120089949-cd80d080-c139-11eb-9fd0-9bf5e8ebc364.png)
![103  후기등록(04  customer_commentRequest sub)](https://user-images.githubusercontent.com/81424367/120089950-cd80d080-c139-11eb-9e6c-6457427458ec.png)
![103  후기등록(05  customer_comment등록)](https://user-images.githubusercontent.com/81424367/120089951-ce196700-c139-11eb-9de4-cfacf607bf41.png)
![103  후기등록(06  customer_commentAccepted pub)](https://user-images.githubusercontent.com/81424367/120089952-ce196700-c139-11eb-9bc8-79cb7eaacef1.png)
![103  후기등록(07  order_notifyCommentAccepted  sub)](https://user-images.githubusercontent.com/81424367/120089953-ceb1fd80-c139-11eb-98d0-6e1b3c4e276a.png)
![103  후기등록(08  order_table)](https://user-images.githubusercontent.com/81424367/120089955-ceb1fd80-c139-11eb-83fb-1d48f47dc281.png)

2) 카프카 메시지 확인

- (a) 후기 등록 : CommentRegistered(pub) -> CommentRequest(sub) -> CommentAcceped(pub) -> NotifyCommentAccept(sub)
![103  후기등록(10  kafka)](https://user-images.githubusercontent.com/81424367/120089958-cf4a9400-c139-11eb-8952-ff37cb5c27a2.png)



## 폴리글랏 퍼시스턴스
- Order, Assignment, Installation 서비스 모두 H2 메모리DB를 적용하였다.
- 신규 Customer 서비스는 MongoDB를 적용하였다.
- 다양한 데이터소스 유형 (RDB or NoSQL) 적용 시 데이터 객체에 @Entity 가 아닌 @Document로 마킹 후, 
  기존의 Entity Pattern / Repository Pattern 적용과 데이터베이스 제품의 설정 (application.yml) 만으로 가능하다.

Customer 서비스의 pom.xml에 MongoDB Dependency 설정
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

application.yml에 MongoDB 설정
```
spring:
  #MongoDB
  data:
    mongodb:
      uri: mongodb://localhost:27017/tutorial
```

Customer.java에 Entity 수정
```
//@Entity
//@Table(name="Customer_table")
@Document(collection = "Customer_table")
public class Customer {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long productId;
    private String productName;
    private Long customerId;
    private Integer point;
    private String commentMessage;
```

Repository 수정
```
package purifierrentalpjt;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="customers", path="customers")
//public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>{
public interface CustomerRepository extends MongoRepository<Customer, Long>{
}
```

결과 확인하기

- 주문 요청
```
E:\Cloud\src\PurifierRentalPJT_폴리글랏>http -f POST localhost:8081/order/joinOrder productId=101 productName="PURI1" installationAddress="Address1001" customerId=201
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Wed, 02 Jun 2021 10:49:45 GMT
Transfer-Encoding: chunked

true
```

- 후기 등록
```
E:\Cloud\src\PurifierRentalPJT_폴리글랏>http -f POST localhost:8081/order/registerComment id=1 productId=101 productName="PURI1" customerId=201 point=97 commentMessage="Good Water"
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Wed, 02 Jun 2021 10:50:24 GMT
Transfer-Encoding: chunked

true
```

- MongoDB 데이터 확인

![400  MongoDB(03  테스트)](https://user-images.githubusercontent.com/81424367/120469596-81ee5100-c3dd-11eb-98d5-91052090ef7d.png)

## Saga 패턴 적용

- SAGA 패턴은 각 서비스의 트랜잭션은 단일 서비스 내의 데이터를 갱신하는 일종의 로컬 트랜잭션 방법이고 서비스의 트랜잭션이 완료 후에 다음 서비스가 트리거 되어, 트랜잭션을 실행하는 방법입니다.

- 현재 정수기렌탈 시스템에도 Saga 패턴에 맞추어서 작성되어 있다.

```
OrderController.java (후기 등록 처리)

	@RequestMapping(value = "/order/registerComment", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public boolean registerComment(
		@RequestParam("id") 					Long 	id, 
		@RequestParam("productId") 				Long 	productId, 
		@RequestParam("productName")  			String 	productName,
		@RequestParam("customerId")  			Long 	customerId,
		@RequestParam("point")  				Integer 	point,
		@RequestParam("commentMessage")  		String 	commentMessage
					) throws Exception {

		// init
		System.out.println("##### /order/registerComment  called #####");
		boolean status = false;		
		
		// 주문검색후, 코멘트 입력
		Optional<Order> orderOpt = orderRepository.findById(id);
		if( orderOpt.isPresent()) {
			Order order =orderOpt.get();
			status = true;
			order.setStatus("commentRequest");
			order.setPoint(point);
			order.setCommentMessage(commentMessage);
			orderRepository.save(order);

		} 		
		
		return status;
	}
```

- 주문 검색하여 해당 주문이 있는 경우에만, 후기 정보를 기록하고, CommentRegistered를 통해 Publish하여 Customer 마이크로서비스에서 Subscribe할 수 있도록 한다.

- 해당 주문이 없는 경우에는 후기 정보도 기록하지 않고, pub/sub도 하지 않는다..

201번 고객의 정수기 주문(Order Id=1)
![201  Saga(01  주문요청)](https://user-images.githubusercontent.com/81424367/120413826-f4d2da00-c393-11eb-8c1a-128a86bdb88b.png)

201번 고객의 정수기 후기 등록(Order Id=1)
![201  Saga(02  Comment등록)](https://user-images.githubusercontent.com/81424367/120413831-f6040700-c393-11eb-9f44-427b7cd7d47d.png)

201번 고객의 정수기 후기 정상 반영
![201  Saga(03  Comment등록확인)](https://user-images.githubusercontent.com/81424367/120413832-f69c9d80-c393-11eb-9546-8634af7a02c1.png)

고객의 정수기 주문 없는 상태(Order Id=3)
Saga(04  3번 주문없음) HTTP GET
![201  Saga(04  3번 주문없음)](https://user-images.githubusercontent.com/81424367/120413833-f69c9d80-c393-11eb-87aa-01bbc2c3f470.png)

고객의 정수기 후기 등록 요청 실패(Order Id=3)
![201  Saga(05  Comment등록불가)](https://user-images.githubusercontent.com/81424367/120413835-f7353400-c393-11eb-9658-74e0e7b728fc.png)


## 동기식 호출과 Fallback 처리

- 분석 단계에서의 조건 중 하나로 배정(Assignment) 서비스에서 인터넷 가입신청 취소를 요청 받으면, 
설치(installation) 서비스 취소 처리하는 부분을 동기식 호출하는 트랜잭션으로 처리하기로 하였다. 
- 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어 있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다.

설치 서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현
```
# (Assignment) InstallationService.java

	package purifierrentalpjt.external;
	
	import org.springframework.cloud.openfeign.FeignClient;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestMethod;

	/**
 	 * 설치subsystem 동기호출
 	 * @author Administrator
 	 * 아래 주소는 Gateway주소임
 	*/


	@FeignClient(name="Installation", url="http://installation:8080")
	//@FeignClient(name="Installation", url="http://localhost:8083")
	public interface InstallationService {

		@RequestMapping(method= RequestMethod.POST, path="/installations")
    		public void cancelInstallation(@RequestBody Installation installation);

	}
```

정수기 렌탈 서비스 가입 취소 요청(cancelRequest)을 받은 후, 처리하는 부분
```
# (Installation) InstallationController.java

	package purifierrentalpjt;

	@RestController
	public class InstallationController {

    	  @Autowired
    	  InstallationRepository installationRepository;

    	  /**
     	   * 설치취소
     	   * @param installation
           */
	  @RequestMapping(method=RequestMethod.POST, path="/installations")
    	  public void installationCancellation(@RequestBody Installation installation) {
    	
    		System.out.println( "### 동기호출 -설치취소=" +ToStringBuilder.reflectionToString(installation) );

    		Optional<Installation> opt = installationRepository.findByOrderId(installation.getOrderId());
    		if( opt.isPresent()) {
    			Installation installationCancel =opt.get();
    			installationCancel.setStatus("installationCanceled");
    			installationRepository.save(installationCancel);
    		} else {
    			System.out.println("### 설치취소 - 못찾음");
    		}
    	}
```

주문 취소에 대한 동기 호출 요청(Assignment)
![102  주문취소(05  assignment-orderCancelAccepted pub 그리고 cancelInstallation 동기호출)](https://user-images.githubusercontent.com/81424367/120089969-e2f5fa80-c139-11eb-8088-40fb62562ef8.png)

주문 취소에 대한 동기 호출 처리(Installation)
![102  주문취소(07  installation-동기호출 처리)](https://user-images.githubusercontent.com/81424367/120089971-e38e9100-c139-11eb-82d5-e30958103b9c.png)

## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

가입 신청(order)이 이루어진 후에 배정(Assignment) 서비스로 이를 알려주는 행위는 비동기식으로 처리하여, 배정(Assignment) 서비스의 처리를 위하여 가입신청(order)이 블로킹 되지 않도록 처리한다.
 
- 이를 위하여 가입 신청에 기록을 남긴 후에 곧바로 가입 신청이 되었다는 도메인 이벤트를 카프카로 송출한다.(Publish)
```
# (order) Order.java

    @PostPersist
    public void onPostPersist(){

        System.out.println("##### 주문 생성 Pub(orderRequest) #####");
        JoinOrdered joinOrdered = new JoinOrdered();
        BeanUtils.copyProperties(this, joinOrdered);
        joinOrdered.publishAfterCommit();
    }
```
- 배정 서비스에서는 가입신청 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다.
```
# (Assignment) PolicyHandler.java

@Service
public class PolicyHandler{
    @Autowired AssignmentRepository assignmentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverJoinOrdered_OrderRequest(@Payload JoinOrdered joinOrdered){

        if(!joinOrdered.validate()) return;

        System.out.println("\n\n##### listener OrderRequest : " + joinOrdered.toJson() + "\n\n");

        Assignment assignment = new Assignment();

        assignment.setId(joinOrdered.getId());
        assignment.setInstallationAddress(joinOrdered.getInstallationAddress());
        assignment.setStatus("orderRequest");
        assignment.setEngineerName("Enginner" + joinOrdered.getId());
        assignment.setEngineerId(joinOrdered.getId());
        assignment.setOrderId(joinOrdered.getId());

        assignmentRepository.save(assignment);
        }
    }
}
```
가입신청은 배정 서비스와 완전히 분리되어 있으며, 이벤트 수신에 따라 처리되기 때문에, 배정 서비스가 유지보수로 인해 잠시 내려간 상태라도 가입신청을 받는데 문제가 없다.


(개인과제)
```
# (order) Order.java

    @PostUpdate
    public void onPostUpdate(){
         
        // 후기 등록시, 이벤트발생

        if(this.getStatus().equals("commentRequest")) {

            System.out.println("##### 코멘트 등록 Pub(" + this.getStatus() + ") #####");
            CommentRegistered commentRegistered = new CommentRegistered();
    
            commentRegistered.setId(this.getId()); 
            commentRegistered.setCustomerId(this.getCustomerId()); 
            commentRegistered.setProductId(this.getProductId()); 
            commentRegistered.setProductName(this.getProductName()); 
            commentRegistered.setPoint(this.getPoint()); 
            commentRegistered.setCommentMessage(this.getCommentMessage()); 
    
            BeanUtils.copyProperties(this, commentRegistered);
            commentRegistered.publishAfterCommit();
        }
    }
```
- 고객 담당팀에서는 후기 등록 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다.
```
# (customer) PolicyHandler.java
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCommentRegistered_CoimmentRequest(@Payload CommentRegistered commentRegistered){

        if(!commentRegistered.validate()) return;

        System.out.println("\n\n##### listener CoimmentRequest : " + commentRegistered.toJson() + "\n\n");

        Customer customer = new Customer();

        customer.setId(commentRegistered.getId());
        customer.setCustomerId(commentRegistered.getCustomerId());
        customer.setProductId(commentRegistered.getProductId());
        customer.setProductName(commentRegistered.getProductName());
        customer.setPoint(commentRegistered.getPoint());
        customer.setCommentMessage(commentRegistered.getCommentMessage());

        customerRepository.save(customer);            

        System.out.println("\n\n##### customer Comment 등록 완료 : ");
    }
```

후기 등록은 고객 관리 서비스와 완전히 분리되어 있으며, 이벤트 수신에 따라 처리되기 때문에, 고객 관리 서비스가 유지보수로 인해 잠시 내려간 상태라도 후기 정보 등록을 받는데 문제가 없다.

후기 등록에 대한 비동기 호출(Kafka Order)
![103  후기등록(03  order_commentRegistered pub)](https://user-images.githubusercontent.com/81424367/120089949-cd80d080-c139-11eb-9fd0-9bf5e8ebc364.png)

후기 등록에 대한 비동기 호출 처리(Kafka Customer)
![103  후기등록(04  customer_commentRequest sub)](https://user-images.githubusercontent.com/81424367/120089950-cd80d080-c139-11eb-9e6c-6457427458ec.png)

## CQRS

가입신청 상태 조회를 위한 서비스를 CQRS 패턴으로 구현하였다.
- Order, Assignment, Installation 개별 aggregate 통합 조회로 인한 성능 저하를 막을 수 있다.
- 모든 정보는 비동기 방식으로 발행된 이벤트를 수신하여 처리된다.
- 설계 : MSAEZ 설계의 view 매핑 설정 참조

- 주문요청

![101  주문요청(01  POST)](https://user-images.githubusercontent.com/81424367/120089885-46335d00-c139-11eb-9557-6e470c44f4ed.png)

- 카프카 메시지

![101  주문요청(10  Kafka)](https://user-images.githubusercontent.com/81424367/120089883-459ac680-c139-11eb-9c98-acd5302a3114.png)

- 주문취소

![102  주문취소(01  POST)](https://user-images.githubusercontent.com/81424367/120089964-e1c4cd80-c139-11eb-950f-158cf3aa23b2.png)

- 카프카 메시지

![102  주문취소(09  Kafka)](https://user-images.githubusercontent.com/81424367/120089973-e4272780-c139-11eb-8917-1014dc598b06.png)

- 뷰테이블 수신처리

![101  주문요청(11  order-view)](https://user-images.githubusercontent.com/81424367/120089884-46335d00-c139-11eb-99f6-c32d23a4c871.png)


## API Gateway

API Gateway를 통하여, 마이크로 서비스들의 진입점을 통일한다.

```
# application.yml 파일에 라우팅 경로 설정

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: order
          uri: http://order:8080
          predicates:
            - Path=/order/**,/orders/**,/orderStatuses/**
        - id: assignment
          uri: http://assignment:8080
          predicates:
            - Path=/assignment/**,/assignments/** 
        - id: installation
          uri: http://installation:8080
          predicates:
            - Path=/installation/**,/installations/** 
        - id: customer
          uri: http://customer:8080
          predicates:
            - Path=/customers/** 
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
## Gateway로 접속
1. Gateway 01. 주문요청
![105  Gateway 01  주문요청](https://user-images.githubusercontent.com/81424367/120091238-3ec58100-c144-11eb-9078-1368668563d0.png)

1. Gateway 02. 설치완료
![105  Gateway 02  설치완료](https://user-images.githubusercontent.com/81424367/120091239-3f5e1780-c144-11eb-98c1-af885f49d115.png)

1. Gateway 03. 후기 등록
![105  Gateway 03  후기 등록](https://user-images.githubusercontent.com/81424367/120091240-3ff6ae00-c144-11eb-9b37-173214f753de.png)
1. Gateway 04.  설치 취소
![105  Gateway 04  설치 취소](https://user-images.githubusercontent.com/81424367/120091241-3ff6ae00-c144-11eb-9cd0-2fb2abf61abd.png)


- EKS에 배포 시, MSA는 Service type을 ClusterIP(default)로 설정하여, 클러스터 내부에서만 호출 가능하도록 한다.
- API Gateway는 Service type을 LoadBalancer로 설정하여 외부 호출에 대한 라우팅을 처리한다.



# 운영

## CI/CD 설정
### 빌드/배포
각 프로젝트 jar를 Dockerfile을 통해 Docker Image 만들어 ECR저장소에 올린다.   
EKS 클러스터에 접속한 뒤, 각 서비스의 deployment.yml, service.yml을 kuectl명령어로 서비스를 배포한다.   
  - 코드 형상관리 : https://github.com/uttdhk/PurifierRentalPJT 하위 repository에 각각 구성   
  - 운영 플랫폼 : AWS의 EKS(Elastic Kubernetes Service)   
  - Docker Image 저장소 : AWS의 ECR(Elastic Container Registry)

#### docker build
```
cd /home/project/PurifierRentalPJT/Order;docker build -t 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-order:v2 .;
cd /home/project/PurifierRentalPJT/Installation;docker build -t 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-installation:v2 .;
cd /home/project/PurifierRentalPJT/Assignment;docker build -t 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-assignment:v2 .;
cd /home/project/PurifierRentalPJT/Customer;docker build -t 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-customer:v2 .;
cd /home/project/PurifierRentalPJT/gateway;docker build -t 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-gateway:v1 .;
```

#### docker push
```
cd /home/project/PurifierRentalPJT/Order;docker push 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-order:v2;
cd /home/project/PurifierRentalPJT/Installation;docker push 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-installation:v2;
cd /home/project/PurifierRentalPJT/Assignment;docker push 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-assignment:v2;
cd /home/project/PurifierRentalPJT/Customer;docker push 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-customer:v2;
cd /home/project/PurifierRentalPJT/gateway;docker push 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-gateway:v1;
```

#### kubectrl deploy, service
```
kubectl create deploy gateway --image=879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-gateway:v1
kubectl expose deployment gateway --type=LoadBalancer --port=8080

cd /home/project/PurifierRentalPJT/Order/kubernetes/;
kubectl apply -f deployment.yml;
kubectl apply -f service.yaml;

cd /home/project/PurifierRentalPJT/Installation/kubernetes/;
kubectl apply -f deployment.yml;
kubectl apply -f service.yaml;

cd /home/project/PurifierRentalPJT/Assignment/kubernetes/;
kubectl apply -f deployment.yml;
kubectl apply -f service.yaml;

cd /home/project/PurifierRentalPJT/Customer/kubernetes/;
kubectl apply -f deployment.yml;
kubectl apply -f service.yaml;
```

##### 배포 결과
![503  02  customer deploy service](https://user-images.githubusercontent.com/81424367/120590520-9e3dcc80-c475-11eb-832a-ae0fad1d54d6.png)

## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택
  - Spring FeignClient + Hystrix 옵션을 사용하여 구현할 경우, 도메인 로직과 부가 기능 로직이 서비스에 같이 구현된다.
  - istio를 사용해서 서킷 브레이킹 적용이 가능하다.

### istio 설치
```
cd /home/project
curl -L https://istio.io/downloadIstio | ISTIO_VERSION=1.7.1 TARGET_ARCH=x86_64 sh -        # istio 설치파일 download
cd istio-1.7.1
export PATH=$PWD/bin:$PATH                                                                  # istio PATH 추가
istioctl install --set profile=demo --set hub=gcr.io/istio-release                          # istio 설치
kubectl label namespace default istio-injection=enabled                                     # istio를 kubectl에 injection
kubectl get all -n istio-system                                                             # istio injection 상태 확인

마이크로서비스 재배포

kubectl get all                                                                             # 쿠버네티스 pod 상태 확인

```

#### istio 설치 파일 다운로드
![510  01  istio 설치](https://user-images.githubusercontent.com/81424367/120595003-b6651a00-c47c-11eb-9cac-67095c0d71d7.png)

#### istio 설치 확인(istio namespace service 확인)
![510  02  istio 설치 확인](https://user-images.githubusercontent.com/81424367/120595004-b7964700-c47c-11eb-9f8e-af052478fdc1.png)

#### 마이크로서비스 재배포 후(Pod의 READY가 1/1에서 2/2로 변경됨)
![510  03  서비스 재배포 후 서비스 확인 ready가 2개씩](https://user-images.githubusercontent.com/81424367/120595005-b7964700-c47c-11eb-919a-ec34fd925cea.png)

### kiali 설치

#### kiali.yaml 수정
```
vi istio-1.7.1/istiosamples/addons/kiali.yaml
	4라인의 apiVersion: 
		apiextensions.k8s.io/v1beta1을 apiVersion: apiextensions.k8s.io/v1으로 수정
```

#### kiali 서비스 설정
```
kubectl apply -f samples/addons
	kiali.yaml 오류발생시, 아래 명령어 실행
		kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.7/samples/addons/kiali.yaml
```

![511  01  kiali 설치](https://user-images.githubusercontent.com/81424367/120602961-79058a00-c486-11eb-831a-359645a7d09d.png)

#### kiali External IP 설정 및 IP 확인
```
kubectl edit svc kiali -n istio-system                                   # kiali External IP 설정
	:%s/ClusterIP/LoadBalancer/g
	:wq!
kubectl get all -n istio-system                                          # EXTERNAL-IP 확인
```
![511  02  kiali  서비스 등록 후 external IP 설정완료  istio ip확인](https://user-images.githubusercontent.com/81424367/120602967-7a36b700-c486-11eb-8dbc-767f624fd3a8.png)

#### kiali 접속 확인(application/graph 화면)
![511  03  kiali  접속(applications)](https://user-images.githubusercontent.com/81424367/120602969-7acf4d80-c486-11eb-92f2-7477755d855a.png)
![511  04  kiali  접속(graph)](https://user-images.githubusercontent.com/81424367/120602971-7acf4d80-c486-11eb-9a3b-4a040d68b183.png)


### 서킷브레이커 설정

#### 서킷브레이커 설정(DestinationRule)
```
cat <<EOF | kubectl apply -f -
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: customer
spec:
  host: customer
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

#### Seige 툴을 통한 서킷 브레이커 동작 확인
```
siege -c100 -t60S -v 'http://ae725b80f27be48caaea2ae8ed546c7d-1955668814.ap-southeast-2.elb.amazonaws.com:8080/order/registerComment POST id=1&productId=101&productName=PURI1&customerId=201&point=97&commentMessage=Good Water'
```
* 부하테스터 siege 툴을 통한 서킷 브레이커 동작을 확인한다.
- 동시사용자 100명
- 60초 동안 실시
- 결과 화면
![512  07  seige 결과(후기등록)](https://user-images.githubusercontent.com/81424367/120731964-8fabef80-c51f-11eb-8693-1bab991ab9b8.png)
![512  06  seige 결과(kiali)](https://user-images.githubusercontent.com/81424367/120731960-8de22c00-c51f-11eb-82bf-0f363858b8a0.png)

### 서킷브레이커 해제

#### 서킷브레이커 해제(DestinationRule)
```
kubectl delete dr --all
```

#### Seige 툴을 통한 서킷 브레이커 동작 확인
```
siege -c100 -t60S -v 'http://ae725b80f27be48caaea2ae8ed546c7d-1955668814.ap-southeast-2.elb.amazonaws.com:8080/order/registerComment POST id=1&productId=101&productName=PURI1&customerId=201&point=97&commentMessage=Good Water'
```
- 결과 화면
![512  08  destination rule 삭제 후 seige 명령 결과](https://user-images.githubusercontent.com/81424367/120731965-8fabef80-c51f-11eb-81f6-b34198bb947f.png)
![512  09  destination rule 삭제 후 seige 명령 결과(kiali)](https://user-images.githubusercontent.com/81424367/120731967-90448600-c51f-11eb-90fa-d724c287f9e4.png)

### Liveness

pod의 container가 정상적으로 기동되는지 확인하여, 비정상 상태인 경우 pod를 재기동하도록 한다.   

아래의 값으로 liveness를 설정한다.
- 재기동 제어값 : /tmp/healthy 파일의 존재를 확인
- 기동 대기 시간 : 3초
- 재기동 횟수 : 5번까지 재시도

##### order 서비스에 Liveness 적용
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order
  labels:
    app: order
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order
  template:
    metadata:
      labels:
        app: order
    spec:
      containers:
        - name: order
          image: 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-order:v2
          args:
          - /bin/sh
          - -c
          - touch /tmp/healthy; sleep 10; rm -rf /tmp/healthy; sleep 600;
          ports:
            - containerPort: 8080
          livenessProbe:
            exec:
              command:
              - cat
              - /tmp/healthy
            initialDelaySeconds: 3
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
```

#### liveness 적용 후 결과 화면
```
kubectl get pods -w                                        # pod의 상태 모니터링
```

- Order Pod의 CrashLoopBackOff 상태 확인
- 이때, 재기동 제어값인 /tmp/healthy파일을 강제로 지워 liveness가 pod를 비정상 상태라고 판단하도록 하였다.    
- 5번 재시도 후에도 파드가 뜨지 않았을 경우 CrashLoopBackOff 상태가 됨을 확인하였다.
![513  02  liveness테스트](https://user-images.githubusercontent.com/81424367/120605297-dc90b700-c488-11eb-95f0-da9df688bf23.png)

- 일정 시간 후 화면

![513  03  liveness테스트2](https://user-images.githubusercontent.com/81424367/120605303-dd294d80-c488-11eb-9d57-79598232f69f.png)


### 오토스케일 아웃

- 가입신청 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 
- 설정은 CPU 사용량이 10프로를 넘어서면 replica 를 10개까지 늘려준다.
```
kubectl autoscale deploy order --min=1 --max=10 --cpu-percent=1
```

#### autoscale.yml
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order
  labels:
    app: order
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order
  template:
    metadata:
      labels:
        app: order
    spec:
      containers:
        - name: order
          resources:
            limits: 
              cpu: 500m
            requests:
              cpu: 200m
          image: 879772956301.dkr.ecr.ap-southeast-2.amazonaws.com/user13-order:v2
```

#### 오토스케일이 어떻게 되고 있는지 모니터링을 걸어준다.
```
kubectl get deploy order -w

kubectl get hpa order -w
```

#### 사용자 50명으로 워크로드를 3분 동안 걸어준다.
```
siege -c50 -t180S -v 'http://ae725b80f27be48caaea2ae8ed546c7d-1955668814.ap-southeast-2.elb.amazonaws.com:8080/order/joinOrder POST productId=101&productName=PURI1&installationAddress=AWS_Address&customerId=301'
```

- AutoScaleout이 발생하지 않음(siege 실행 결과 오류 없이 수행됨 : Availability 100%)
- AutoScaleout 대상이 unknown이며, 확인해본 결과 메트릭스 서버를 설치해야한다.

![514  01  AutoScaleout](https://user-images.githubusercontent.com/81424367/120621957-5a5cbe80-c499-11eb-932a-e896c737d3a4.png)

#### 메트릭스 서버 설치

![514  02  메트릭스 설치](https://user-images.githubusercontent.com/81424367/120621965-5b8deb80-c499-11eb-8c5c-3ee3175f72d5.png)

#### AutoScaleout을 확인

![514  03  autoscalout 확인](https://user-images.githubusercontent.com/81424367/120621967-5c268200-c499-11eb-8303-6f320937604b.png)
![514  05  쿠버네티스 상태 확인](https://user-images.githubusercontent.com/81424367/120625903-153a8b80-c49d-11eb-90fd-57a8fc4e0dae.png)

## 무정지 재배포

- 미 수행함.

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 서킷브레이커 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 한다.
```
siege -c50 -t180S  -v 'http://a39e59e8f1e324d23b5546d96364dc45-974312121.ap-southeast-2.elb.amazonaws.com:8080/order/joinOrder POST productId=5&productName=PURI5&installationAddress=Address5&customerId=205'
```

- readinessProbe, livenessProbe 설정되지 않은 상태로 buildspec.yml을 수정한다.
- Github에 buildspec.yml 수정 발생으로 CodeBuild 자동 빌드/배포 수행된다.
- siege 수행 결과 : 

- readinessProbe, livenessProbe 설정하고 buildspec.yml을 수정한다.
- Github에 buildspec.yml 수정 발생으로 CodeBuild 자동 빌드/배포 수행된다.
- siege 수행 결과 : 


## ConfigMap 적용

- 미 수행함.

- 설정의 외부 주입을 통한 유연성을 제공하기 위해 ConfigMap을 적용한다.

- 환경변수를 주입한다.
```
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: order
data:
  my.language: korean
EOF
```

- deployment에 configmap 주입 정보를 추가한다.
```
          env:
          - name: INIT_LANGUAGE
            valueFrom:
              configMapKeyRef:
                name: order
                key: my.language
```

- orderstatus 에서 사용하는 mySQL(AWS RDS 활용) 접속 정보를 ConfigMap을 통해 주입 받는다.


```
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: order
data:
  urlstatus: "jdbc:mysql://order.cgzkudckye4b.ap-southeast-2:3306/orderstatus?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8"
EOF
```

## Secret 적용

- username, password와 같은 민감한 정보는 ConfigMap이 아닌 Secret을 적용한다.
- etcd에 암호화 되어 저장되어, ConfigMap 보다 안전하다.
- value는 base64 인코딩 된 값으로 지정한다. (echo root | base64)

### base64 암호화
```
root@labs--93793215:/home/project/operation# echo 'Administrator' | base64
QWRtaW5pc3RyYXRvcgo=
root@labs--93793215:/home/project/operation# echo 'pass123456' | base64
cGFzczEyMzQ1Ngo=
```

### Secret 설정
```
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
metadata:
  name: order
type: Opaque
data:
  username: QWRtaW5pc3RyYXRvcgo=
  password: cGFzczEyMzQ1Ngo=
EOF
```

### 가입 요청

![515  01  Secret (HTTP POST ORDER)](https://user-images.githubusercontent.com/81424367/120637579-0a3a2800-c4aa-11eb-861d-c1d9b3217d74.png)

### 가입 요청 시 ID/PW를 읽음
```
	@RequestMapping(value = "/order/joinOrder", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public boolean joinOrder(
		@RequestParam("productId") 					Long 	productId, 
		@RequestParam("productName")  					String 	productName,
		@RequestParam( value="installationAddress", required = false)  	String 	installationAddress,
		@RequestParam("customerId")  					Long 	customerId,
		@RequestParam( value="orderDate", required = false)  		String 	orderDate
					) throws Exception {
		
		
		System.out.println( "### 로그인 사용자 아이디 = " +System.getenv().get("INIT_NAME"));
		System.out.println( "### 로그인 사용자 비밀번호 = " +System.getenv().get("INIT_PW"));		
```

### order 로그 확인
```
kubectl logs pod/order-6d5b449987-nqbws -c order				# istio 사용으로 order container 옵션 설정을 해야함
```

![515  02  Secret (kubectl order 로그 확인](https://user-images.githubusercontent.com/81424367/120637583-0b6b5500-c4aa-11eb-95be-01ee44782b36.png)


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
 1. 정수기 렌탈 서비스 가입신청 -> installation 접수 완료 상태
 2. 설치 기사 설치 완료 처리 -> 가입 신청 완료 상태
 3. 후기 등록 -> Customer에 후기 등록 완료 상태
 4. 가입 취소
 5. EDA 구현
   - Assignment 장애 상황에서 order(가입 신청) 정상 처리
   - Assignment 정상 전환 시 수신 받지 못한 이벤트 처리
 6. 서킷 브레이킹
 7. Liveness
 8. AutoScaleout
 9. Secret 
