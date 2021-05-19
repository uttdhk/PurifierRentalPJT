package purifierrentalpjt;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="installations", path="installations")
public interface InstallationRepository extends PagingAndSortingRepository<Installation, Long>{


}
