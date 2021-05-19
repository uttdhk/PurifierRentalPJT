
package purifierrentalpjt.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="Installation", url="http://Installation:8080")
public interface InstallationService {

    @RequestMapping(method= RequestMethod.GET, path="/installations")
    public void cancelInstallation(@RequestBody Installation installation);

}