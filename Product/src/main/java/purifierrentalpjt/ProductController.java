package purifierrentalpjt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

 @RestController
 public class ProductController {

        @Autowired
        ProductRepository productRepository;

        @RequestMapping(value = "/checkAndModifyStock",
                method = RequestMethod.GET,
                produces = "application/json;charset=UTF-8")

        public boolean checkAndModifyStock(@RequestParam("productId") Long productId,
                                        @RequestParam("countStock") int countStock)
                throws Exception {

                System.out.println("##### 재고 수정 Command 요청 받음(동기호출) #####");

                boolean status = false;
                Optional<Product> productOptional = productRepository.findByProductId(productId);
                Product product = productOptional.get();

                // 현재 재고수량이 요청 수량보다 
                //  많으면, 현재 재고수량을 요청 수량만큼 재고에서 감소
                //  적으면, 재고 수량 변경 없음          
                if (product.getCountStock() >= countStock) {
                        product.setCountStock(product.getCountStock() - countStock);
                        status = true;

                        System.out.println("##### 재고 수정 완료 #####");
                        productRepository.save(product);
                }else{
                        System.out.println("##### 재고 부족으로 재고 수정 불가 #####");
                }

                return status;

        }

 }
