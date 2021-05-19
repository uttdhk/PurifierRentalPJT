
package purifierrentalpjt.event;

import lombok.Getter;
import lombok.Setter;
import purifierrentalpjt.AbstractEvent;

@Getter
@Setter
public class OrderCancelAccepted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String status;

}

