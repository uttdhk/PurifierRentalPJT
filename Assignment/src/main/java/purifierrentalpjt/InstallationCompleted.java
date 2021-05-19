
package purifierrentalpjt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstallationCompleted extends AbstractEvent {

    private Long id;
    private Long engineerId;
    private String engineerName;
    private String installReservationDate;
    private String installCompleteDate;
    private Long orderId;
    private String status;

}

