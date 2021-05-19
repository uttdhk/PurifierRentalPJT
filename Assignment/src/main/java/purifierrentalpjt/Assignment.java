package purifierrentalpjt;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
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
        EngineerAssigned engineerAssigned = new EngineerAssigned();
        BeanUtils.copyProperties(this, engineerAssigned);
        engineerAssigned.publishAfterCommit();


        JoinCompleted joinCompleted = new JoinCompleted();
        BeanUtils.copyProperties(this, joinCompleted);
        joinCompleted.publishAfterCommit();


        OrderCancelAccepted orderCancelAccepted = new OrderCancelAccepted();
        BeanUtils.copyProperties(this, orderCancelAccepted);
        orderCancelAccepted.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        purifierrentalpjt.external.Installation installation = new purifierrentalpjt.external.Installation();
        // mappings goes here
        Application.applicationContext.getBean(purifierrentalpjt.external.InstallationService.class)
            .cancelInstallation(installation);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getInstallationAddress() {
        return installationAddress;
    }

    public void setInstallationAddress(String installationAddress) {
        this.installationAddress = installationAddress;
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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
