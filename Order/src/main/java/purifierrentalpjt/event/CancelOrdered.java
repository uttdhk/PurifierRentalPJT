
package purifierrentalpjt.event;

import lombok.Getter;
import lombok.Setter;
import purifierrentalpjt.AbstractEvent;

/**
 * 주문취소 이벤트
 * @author Administrator
 */
@Getter
@Setter
public class CancelOrdered extends AbstractEvent {
    
	private Long id;		// 주문번호 - *필수
    private String status;	// 상태		- *필수
    
    private Long productId;
    private String productName;
    private String installationAddress;
    private Long customerId;
    private String orderDate;
}

