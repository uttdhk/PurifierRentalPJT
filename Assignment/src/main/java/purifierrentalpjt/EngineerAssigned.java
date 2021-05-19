
package purifierrentalpjt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EngineerAssigned extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String installationAddress;
    private Long engineerId;
    private String engineerName;

}

