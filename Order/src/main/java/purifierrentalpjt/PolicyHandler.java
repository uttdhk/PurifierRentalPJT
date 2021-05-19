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
    @Autowired OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelAccepted_OrderCancelAccept(@Payload OrderCancelAccepted orderCancelAccepted){

        if(!orderCancelAccepted.validate()) return;

        System.out.println("\n\n##### listener OrderCancelAccept : " + orderCancelAccepted.toJson() + "\n\n");

        // Sample Logic //
        Order order = new Order();
        orderRepository.save(order);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverJoinCompleted_JoinCompletionNotify(@Payload JoinCompleted joinCompleted){

        if(!joinCompleted.validate()) return;

        System.out.println("\n\n##### listener JoinCompletionNotify : " + joinCompleted.toJson() + "\n\n");

        // Sample Logic //
        Order order = new Order();
        orderRepository.save(order);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
