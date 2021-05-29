package purifierrentalpjt;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import purifierrentalpjt.event.CancelOrdered;
import purifierrentalpjt.event.JoinOrdered;
import purifierrentalpjt.event.OrderCanceled;

/**
 * 주문
 * @author KYT
 *
 */
@Entity
@Table(name="Order_table")
@Data
public class Order {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String status;
    private Long productId;
    private String productName;
    private String installationAddress;
    private Long customerId;
    private String orderDate;
    private Integer point;
    private String commentMessage;

    /**
     * 주문생성시, 이벤트발생
     */
    @PostPersist
    public void onPostPersist(){
    
        System.out.println("##### 주문 생성 Pub(orderRequest) #####");
        JoinOrdered joinOrdered = new JoinOrdered();
        BeanUtils.copyProperties(this, joinOrdered);
        joinOrdered.publishAfterCommit();


        ////////////////////////////////////////
        /*
        System.out.println("##### 재고 확인 동기 호출 시작 #####");
        purifierrentalpjt.external.Product product = new purifierrentalpjt.external.Product();

        product.setProductId(this.getProductId());
       

        OrderApplication.applicationContext.getBean(purifierrentalpjt.external.ProductService.class)
        .checkAndModifyStock(product.getProductId(), 5);
        */



    }

    
    /**
     * 주문삭제전, 이벤트발생
     */
    @PreRemove
    public void onPreRemove() {
        /*
    	CancelOrdered cancelOrdered = new CancelOrdered();
    	BeanUtils.copyProperties(this, cancelOrdered);
    	cancelOrdered.publishAfterCommit();
        */
    }

    

    
    @PostUpdate
    public void onPostUpdate(){

            
        /**
         * 코멘트 등록시, 이벤트발생
         */
        
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
    
    
        } else {
            
            /**
             * 주문취소 시, 이벤트발생
             */
        
            System.out.println("##### 주문 취소 Pub(" + this.getStatus() + ") ##### ");


            CancelOrdered cancelOrdered = new CancelOrdered();
            BeanUtils.copyProperties(this, cancelOrdered);
            cancelOrdered.publishAfterCommit();


        }
   

    }

}
