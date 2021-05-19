package purifierrentalpjt;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="Assignment_table")
public class Assignment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private String installationAddress;
    private Long engineerId;
    private String engineerName;
    private String status;

    @PostPersist
    public void onPostPersist(){
        EngineerAssigned engineerAssigned = new EngineerAssigned();
        BeanUtils.copyProperties(this, engineerAssigned);
        engineerAssigned.publishAfterCommit();


        JoinCompleted joinCompleted = new JoinCompleted();
        BeanUtils.copyProperties(this, joinCompleted);
        joinCompleted.publishAfterCommit();


        OrderCancelAccepted orderCancelAccepted = new OrderCancelAccepted();
        BeanUtils.copyProperties(this, orderCancelAccepted);
        orderCancelAccepted.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        purifierrentalpjt.external.Installation installation = new purifierrentalpjt.external.Installation();
        // mappings goes here
        //Application 에러 발생 -> 잠시 주석처리
        //Application.applicationContext.getBean(purifierrentalpjt.external.InstallationService.class)
        //    .cancelInstallation(installation);


    }

}
