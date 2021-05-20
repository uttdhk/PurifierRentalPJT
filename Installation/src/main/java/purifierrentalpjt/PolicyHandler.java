package purifierrentalpjt;

import purifierrentalpjt.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    InstallationRepository installationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverEngineerAssigned_InstallationRequest(@Payload EngineerAssigned engineerAssigned){

        if(engineerAssigned.validate()){
            Installation installationAccept = new Installation();
            installationAccept.setStatus("installationAccepted"); 
            SimpleDateFormat defaultSimpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
            String today = defaultSimpleDateFormat.format(new Date());
            installationAccept.setInstallReservationDate(today);
            installationAccept.setEngineerId(engineerAssigned.getEngineerId());
            installationAccept.setEngineerName(engineerAssigned.getEngineerName());
            installationAccept.setOrderId(engineerAssigned.getOrderId());

            installationRepository.save(installationAccept);
            System.out.println("##### listener InstallationRequest : " + engineerAssigned.toJson());
        }


/*
        if(!engineerAssigned.validate()) return;

        System.out.println("\n\n##### listener InstallationRequest : " + engineerAssigned.toJson() + "\n\n");

        // Sample Logic //
        Installation installation = new Installation();
        installationRepository.save(installation);
*/

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}