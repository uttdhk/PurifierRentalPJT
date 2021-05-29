
package purifierrentalpjt;

public class CommentAccepted extends AbstractEvent {

    private Long id;
    private Long customerId;
    private String orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductId() {
        return customerId;
    }

    public void setProductId(Long customerId) {
        this.customerId = customerId;
    }
    public String getProductName() {
        return orderId;
    }

    public void setProductName(String orderId) {
        this.orderId = orderId;
    }
}

