package purifierrentalpjt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


	
/**
 * 설치 Command
 * @author Administrator
 *
 */
@RestController
public class InstallationController {

    @Autowired
    InstallationRepository installationRepository;

    /**
     * 설치취소
     * @param installation
     */
    @RequestMapping(method=RequestMethod.POST, path="/installations")
    public void installationCancellation(@RequestBody Installation installation) {
    	
    	System.out.println( "### 동기호출 -설치취소=" +ToStringBuilder.reflectionToString(installation) );

    	Optional<Installation> opt = installationRepository.findByOrderId(installation.getOrderId());
    	if( opt.isPresent()) {
    		Installation installationCancel =opt.get();
    		installationCancel.setStatus("installationCanceled");
    		installationRepository.save(installationCancel);
    	} else {
    		System.out.println("### 설치취소 - 못찾음");
    	}
    }

    /**
     * 설치완료처리
     * @param orderId
     */
    @RequestMapping(method=RequestMethod.PATCH, path="/installations")
    public void installationCompletion(@RequestParam ("orderId") Long orderId) {
    	System.out.println("### 설치확인할 주문번호(orderId)=" + orderId);

    	Optional<Installation> opt = installationRepository.findByOrderId(orderId);
    	if( opt.isPresent()) {
    		Installation installationCompl =opt.get(); 
    		installationCompl.setStatus("installationComplete");
    		SimpleDateFormat defaultSimpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
    		String today = defaultSimpleDateFormat.format(new Date());
    		installationCompl.setInstallCompleteDate(today);
    		System.out.println( "##vo : " + ToStringBuilder.reflectionToString(installationCompl) );
    		installationRepository.save(installationCompl);
    	} else {
    		System.out.println("### 설치완료 - 못찾음");
    	}
        

    }
 }
