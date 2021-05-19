package purifierrentalpjt;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 주문상태 View 테이블
 * @author KYT
 */
@Entity
@Getter
@Setter
@Table(name="OrderStatus_table")
public class OrderStatus {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

}
