
package purifierrentalpjt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinOrdered extends AbstractEvent {

    private Long id;
    private String Status;
    private Long productId;
    private String productName;
    private String installationAddress;
    private Long customerId;
    private String orderDate;

}

