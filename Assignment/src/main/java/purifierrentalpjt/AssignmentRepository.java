package purifierrentalpjt;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="assignments", path="assignments")
public interface AssignmentRepository extends PagingAndSortingRepository<Assignment, Long>{


}
