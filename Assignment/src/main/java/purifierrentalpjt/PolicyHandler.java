package purifierrentalpjt;

import purifierrentalpjt.config.kafka.KafkaProcessor;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired AssignmentRepository assignmentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverJoinOrdered_OrderRequest(@Payload JoinOrdered joinOrdered){

        if(!joinOrdered.validate()) return;

        System.out.println("\n\n##### listener OrderRequest : " + joinOrdered.toJson() + "\n\n");

        Assignment assignment = new Assignment();

        assignment.setId(joinOrdered.getId());
        assignment.setInstallationAddress(joinOrdered.getInstallationAddress());
        assignment.setStatus("orderRequest");
        assignment.setEngineerName("Enginner" + joinOrdered.getId());
        assignment.setEngineerId(joinOrdered.getId());
        assignment.setOrderId(joinOrdered.getId());

        assignmentRepository.save(assignment);
            
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelOrdered_CancelRequest(@Payload CancelOrdered cancelOrdered){

        if(!cancelOrdered.validate()) return;

        System.out.println("\n\n##### listener CancelRequest : " + cancelOrdered.toJson() + "\n\n");

        try {
            Optional<Assignment> assignment = assignmentRepository.findByOrderId(cancelOrdered.getId());
            assignment.get().setStatus("cancelRequest");
            assignmentRepository.save(assignment.get());
        } catch(Exception e) {
            e.printStackTrace();
        }
            
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverInstallationCompleted_InstallationCompleteNotify(@Payload InstallationCompleted installationCompleted){

        if(!installationCompleted.validate()) return;

        System.out.println("\n\n##### listener InstallationCompleteNotify : " + installationCompleted.toJson() + "\n\n");
        try {
            assignmentRepository.findById(installationCompleted.getOrderId()).ifPresent(
                assignment -> {
                    assignment.setStatus("installationComplete");
                    assignmentRepository.save(assignment);
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
            
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

}
