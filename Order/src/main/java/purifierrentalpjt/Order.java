package purifierrentalpjt;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Date;

@Entity
@Table(name="Order_table")
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

    @PostPersist
    public void onPostPersist(){
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();

        JoinOrdered joinOrdered = new JoinOrdered();
        BeanUtils.copyProperties(this, joinOrdered);
        joinOrdered.publishAfterCommit();

    }

    @PostUpdate
    public void onPostUpdate(){
        CancelOrdered cancelOrdered = new CancelOrdered();
        BeanUtils.copyProperties(this, cancelOrdered);
        cancelOrdered.publishAfterCommit();

    }

}
