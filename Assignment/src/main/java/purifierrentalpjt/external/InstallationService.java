
package purifierrentalpjt.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 설치subsystem 동기호출
 * @author Administrator
 *	아래 주소는 GW주소임
 */
@FeignClient(name="Installation", url="http://installation:8080")
//FeignClient(name="Installation", url="http://localhost:8083")
public interface InstallationService {

    @RequestMapping(method= RequestMethod.POST, path="/installations")
    public void cancelInstallation(@RequestBody Installation installation);
}
