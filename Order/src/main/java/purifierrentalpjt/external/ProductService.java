
package purifierrentalpjt.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

//@FeignClient(name="Product", url="http://product:8080")
@FeignClient(name="product", url="http://localhost:8084")
public interface ProductService {

    @RequestMapping(method= RequestMethod.GET, path="/checkAndModifyStock")
    public boolean checkAndModifyStock(@RequestParam("productId") Long productId,
                                       @RequestParam("countStock") int countStock);

}