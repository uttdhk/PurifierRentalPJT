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
/*
    @PostUpdate
    public void onPostUpdate(){


        CommentAccepted commentAccepted = new CommentAccepted();
        BeanUtils.copyProperties(this, commentAccepted);
        commentAccepted.publishAfterCommit();


    }
*/
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
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }
    public String getCommentMessage() {
        return commentMessage;
    }

    public void setCommentMessage(String commentMessage) {
        this.commentMessage = commentMessage;
    }




}
