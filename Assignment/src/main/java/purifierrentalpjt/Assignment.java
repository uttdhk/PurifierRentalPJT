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


    @PostUpdate
    public void onPostUpdate(){
      

        if (this.getStatus().equals("cancelRequest")) {

            System.out.println("##### 주문 취소에 대한 승인 kafka 발행 #####");
            OrderCancelAccepted orderCancelAccepted = new OrderCancelAccepted();
            
            orderCancelAccepted.setId(this.getId()); 
            orderCancelAccepted.setOrderId(this.getOrderId()); 
            orderCancelAccepted.setStatus("orderCancelAccept"); 

            BeanUtils.copyProperties(this, orderCancelAccepted);
            orderCancelAccepted.publishAfterCommit();


            System.out.println("##### 설치 취소 동기 호출 시작 #####");
            purifierrentalpjt.external.Installation installation = new purifierrentalpjt.external.Installation();

            installation.setId(this.getId());
            installation.setOrderId(this.getOrderId());

            AssignmentApplication.applicationContext.getBean(purifierrentalpjt.external.InstallationService.class)
            .cancelInstallation(installation);

        }
    }

    @PostPersist
    public void onPostPersist(){
        
        System.out.println(this.getStatus() + "POST 처리");
        
        if(this.getStatus().equals("orderRequest")) {

            EngineerAssigned engineerAssigned = new EngineerAssigned();

            engineerAssigned.setId(this.getId()); 
            engineerAssigned.setOrderId(this.getOrderId()); 
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

            BeanUtils.copyProperties(this, joinCompleted);
            joinCompleted.publishAfterCommit();

        } 

    }

}
