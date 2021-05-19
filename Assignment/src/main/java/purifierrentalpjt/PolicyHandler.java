package purifierrentalpjt;

import purifierrentalpjt.config.kafka.KafkaProcessor;
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

        // Sample Logic //
        Assignment assignment = new Assignment();
        assignmentRepository.save(assignment);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelOrdered_CancelRequest(@Payload CancelOrdered cancelOrdered){

        if(!cancelOrdered.validate()) return;

        System.out.println("\n\n##### listener CancelRequest : " + cancelOrdered.toJson() + "\n\n");

        // Sample Logic //
        Assignment assignment = new Assignment();
        assignmentRepository.save(assignment);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverInstallationCompleted_InstallationCompleteNotify(@Payload InstallationCompleted installationCompleted){

        if(!installationCompleted.validate()) return;

        System.out.println("\n\n##### listener InstallationCompleteNotify : " + installationCompleted.toJson() + "\n\n");

        // Sample Logic //
        Assignment assignment = new Assignment();
        assignmentRepository.save(assignment);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
