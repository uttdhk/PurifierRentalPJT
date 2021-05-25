package purifierrentalpjt.external;

public class Product {

    private Long id;
    private Long productId;             // 제품번호 - *필수
    private String productName;         // 제품이름 - *필수
    private Integer countStock;         // 재고수량 - *필수

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
