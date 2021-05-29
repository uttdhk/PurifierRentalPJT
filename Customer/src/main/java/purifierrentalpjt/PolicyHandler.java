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
    @Autowired CustomerRepository customerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCommentRegistered_CoimmentRequest(@Payload CommentRegistered commentRegistered){

        if(!commentRegistered.validate()) return;

        System.out.println("\n\n##### listener CoimmentRequest : " + commentRegistered.toJson() + "\n\n");

        Customer customer = new Customer();

        customer.setId(commentRegistered.getId());
        customer.setCustomerId(commentRegistered.getCustomerId());
        customer.setProductId(commentRegistered.getProductId());
        customer.setProductName(commentRegistered.getProductName());
        customer.setPoint(commentRegistered.getPoint());
        customer.setCommentMessage(commentRegistered.getCommentMessage());

        customerRepository.save(customer);
            

        System.out.println("\n\n##### customer Comment 등록 완료 : ");

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
