package purifierrentalpjt;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 주문상태 DAO
 * @author KYT
 */
public interface OrderStatusRepository extends CrudRepository<OrderStatus, Long> {


}