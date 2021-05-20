package purifierrentalpjt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import purifierrentalpjt.config.kafka.KafkaProcessor;
import purifierrentalpjt.event.JoinCompleted;
import purifierrentalpjt.event.OrderCanceled;

/**
 * 주문상태 View핸들러
 * @author Administrator
 *
 */
@Service
public class OrderStatusViewHandler {
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    
    /**
     * 가입주문완료시
     * @param joinCompleted
     */
    @StreamListener(KafkaProcessor.INPUT)
    public void when_JoinCompletionNotify (@Payload JoinCompleted joinCompleted) {
    	System.out.println("###OrderStatusViewHandler- 가입주문완료시");
    	
        try {
        	if( joinCompleted.isMe()) {
	        	// view 객체 생성
	        	OrderStatus orderStatus = new OrderStatus();
	            orderStatus.setId		(	joinCompleted.getOrderId());
	            orderStatus.setStatus	(	joinCompleted.getStatus());
	            orderStatusRepository.save(orderStatus);
        	}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 주문취소 완료시
     * @param orderCanceled
     */
    @StreamListener(KafkaProcessor.INPUT)
    public void when_OrderCanceled (@Payload OrderCanceled orderCanceled) {
    	System.out.println("###OrderStatusViewHandler- 주문취소 완료시");
    	
        try {
        	if( orderCanceled.isMe()) {
	        	// view 객체 생성
	        	OrderStatus orderStatus = new OrderStatus();
	            orderStatus.setId		(	orderCanceled.getId());
	            orderStatus.setStatus	(	orderCanceled.getStatus());
	            orderStatusRepository.save(orderStatus);
        	}
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

}