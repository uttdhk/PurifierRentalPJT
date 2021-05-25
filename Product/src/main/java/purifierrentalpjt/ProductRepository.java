package purifierrentalpjt;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

//@RepositoryRestResource(collectionResourceRel="products", path="products")
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>{

    Optional<Product> findByProductId(Long productId);

}
