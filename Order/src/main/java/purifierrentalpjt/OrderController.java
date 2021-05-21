package purifierrentalpjt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주문 Command
 * @author KYT
 */
@RestController
public class OrderController {
	@Autowired
	private OrderRepository orderRepository; // Order DAO
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 정수기 가입주문을 한다
	 * @param productId
	 * @param productName
	 * @param installationAddress
	 * @param customerId
	 * @param orderDate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/order/joinOrder", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public boolean joinOrder(
		@RequestParam("productId") 				Long 	productId, 
		@RequestParam("productName")  			String 	productName,
		@RequestParam( value="installationAddress", required = false)  	
			String 	installationAddress,
		@RequestParam("customerId")  			
			Long 	customerId,
		@RequestParam( value="orderDate", required = false)  			
			String 	orderDate
					) throws Exception {
		
		
		System.out.println( "INIT_NAME=" +System.getenv().get("INIT_NAME"));
		System.out.println( "INIT_NAME=" +System.getenv().get("INIT_PW"));
		
		System.out.println( "INIT_NAME=" +System.getProperty("INIT_NAME"));
		System.out.println( "INIT_NAME=" +System.getProperty("INIT_PW"));
		
		
		System.getenv().get("");
		
		
		// http -f POST localhost:8080/order/joinOrder productId=1 productName=ggg installationAddress=분당 customerId=111 orderDate=20210412
		
		// init
		System.out.println("##### /order/joinOrder  called #####");
		boolean status = false;
		
		// 없으면 채워준다
		if( StringUtils.isEmpty(orderDate)) {
			orderDate =getToday();
		}
		
		// 주소가 없으면, 기본주소
		if( StringUtils.isEmpty(installationAddress)) {
			installationAddress ="SKU-Tower";
		}
		
		// 상품이 없으면, 기본상품
		if( StringUtils.isEmpty(productName)) {
			productName ="알찬정수기DX";
			productId 	=1L;
		}
		
		// 새로운 주문생성
		Order order =new Order();
		order.setProductId(productId);
		order.setProductName(productName);
		order.setInstallationAddress(installationAddress);
		order.setCustomerId(customerId);
		order.setOrderDate(orderDate);
		order.setStatus("orderRequest");
		orderRepository.save(order);
		
		status = true;
		return status;
	}
	
	/**
	 * 주문취소
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/order/cancelOrder", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public boolean cancelOrder(
		@RequestParam("id") Long id ) throws Exception {
		
		// init
		System.out.println("##### /order/cancelOrder  called #####");
		boolean status = false;

		// 주문검색후, 삭제
		Optional<Order> orderOpt =orderRepository.findById(id);
		if( orderOpt.isPresent()) {
			Order order =orderOpt.get();
			orderRepository.delete(order);
			status = true;
		} 
		
		return status;
	}
	
	/**
	 * 오늘날짜 구하기
	 * @return
	 */
	private String getToday() {
		return dateFormat.format(new Date());
	}

}
