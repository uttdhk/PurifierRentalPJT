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

            BeanUtils.copyProperties(this, joinCompleted);
            joinCompleted.publishAfterCommit();

        } else if (this.getStatus().equals("cancelRequest")) {

            OrderCancelAccepted orderCancelAccepted = new OrderCancelAccepted();
            
            orderCancelAccepted.setId(this.getId()); 
            orderCancelAccepted.setOrderId(this.getId()); 
            orderCancelAccepted.setStatus("orderCancelAccept"); 

            BeanUtils.copyProperties(this, orderCancelAccepted);
            orderCancelAccepted.publishAfterCommit();

            purifierrentalpjt.external.Installation installation = new purifierrentalpjt.external.Installation();

            installation.setId(this.getId());

            AssignmentApplication.applicationContext.getBean(purifierrentalpjt.external.InstallationService.class)
            .cancelInstallation(installation);

        }

    }

}
