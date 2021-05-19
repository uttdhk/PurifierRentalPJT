package purifierrentalpjt.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Installation {

    private Long id;
    private Long engineerId;
    private String engineerName;
    private String installReservationDate;
    private String installCompleteDate;
    private Long orderId;
    private String status;

}
