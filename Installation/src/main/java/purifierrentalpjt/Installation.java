package purifierrentalpjt;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

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

    @PostPersist
    public void onPostPersist(){
        InstallationAccepted installationAccepted = new InstallationAccepted();
        BeanUtils.copyProperties(this, installationAccepted);
        installationAccepted.publishAfterCommit();


    }

    @PostUpdate
    public void onPostUpdate(){

        if(this.getStatus().equals("INSTALLCOMPLETED")) {
            InstallationCompleted installationCompleted = new InstallationCompleted();
            BeanUtils.copyProperties(this, installationCompleted);
            installationCompleted.publishAfterCommit();
        }

        if(this.getStatus().equals("INSTALLATIONCANCELED")) {
            InstallationCanceled installationCanceled = new InstallationCanceled();
            BeanUtils.copyProperties(this, installationCanceled);
            installationCanceled.publishAfterCommit();
        }

    }


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
