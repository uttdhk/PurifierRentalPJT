package purifierrentalpjt;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
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

    /**
     * 주문생성시, 이벤트발생
     */
    @PostPersist
    public void onPostPersist(){
    	/* 안쓰는 로직같다...
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();
        */

        JoinOrdered joinOrdered = new JoinOrdered();
        BeanUtils.copyProperties(this, joinOrdered);
        joinOrdered.publishAfterCommit();

    }
    
    /**
     * 주문삭제전, 이벤트발생
     */
    @PreRemove
    public void onPreRemove() {
    	CancelOrdered cancelOrdered = new CancelOrdered();
    	BeanUtils.copyProperties(this, cancelOrdered);
    	cancelOrdered.publishAfterCommit();
    }

    /**
     * 주문변경시, 이벤트발생
     */
    @PostUpdate
    public void onPostUpdate(){
      
    }

}
