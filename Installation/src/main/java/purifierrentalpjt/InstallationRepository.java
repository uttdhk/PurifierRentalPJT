package purifierrentalpjt;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="installations", path="installations")
public interface InstallationRepository extends PagingAndSortingRepository<Installation, Long>{
    Installation findByOrderId(Long orderId);


}


//public interface InstallationRepository extends CrudRepository<Installation, Long> {
//  Installation findByOrderId(Long orderId);
//}