
package purifierrentalpjt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrdered extends AbstractEvent {

    private Long id;
    private String status;
    private Long productId;
    private String productName;
    private String installationAddress;
    private Long customerId;
    private String orderDate;

}

