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
    @Autowired InstallationRepository installationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverEngineerAssigned_InstallationRequest(@Payload EngineerAssigned engineerAssigned){

        if(!engineerAssigned.validate()) return;

        System.out.println("\n\n##### listener InstallationRequest : " + engineerAssigned.toJson() + "\n\n");

        // Sample Logic //
        Installation installation = new Installation();
        installationRepository.save(installation);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
