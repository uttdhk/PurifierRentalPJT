package purifierrentalpjt;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface AssignmentRepository extends CrudRepository<Assignment, Long>{
    Optional<Assignment> findByOrderId(Long orderId);
}
