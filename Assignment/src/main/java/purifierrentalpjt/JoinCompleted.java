
package purifierrentalpjt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinCompleted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String status;

}

