package purifierrentalpjt;

public class InstallationCanceled extends AbstractEvent {

    private Long id;
    private Long engineerId;
    private String engineerName;
    private String installReservationDate;
    private String installCompleteDate;
    private Long orderId;
    private String status;

    public InstallationCanceled(){
        super();
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
