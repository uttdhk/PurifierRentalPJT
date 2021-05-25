package purifierrentalpjt;

public class StockModified extends AbstractEvent {

    private Long id;
    private Long productId;
    private String productName;
    private Integer countStock;

    public StockModified(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public Integer getCountStock() {
        return countStock;
    }

    public void setCountStock(Integer countStock) {
        this.countStock = countStock;
    }
}
