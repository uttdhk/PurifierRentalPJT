package purifierrentalpjt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

 @RestController
 public class InstallationController {

    @Autowired
    InstallationRepository installationRepository;

    @RequestMapping(method=RequestMethod.POST, path="/installations")
    public void installationCancellation(@RequestBody Installation installation) {

        Installation installationCancel = installationRepository.findByOrderId(installation.getOrderId());
        installationCancel.setStatus("INSTALLATIONCANCELED");
        installationRepository.save(installationCancel);

    }

    @RequestMapping(method=RequestMethod.PATCH, path="/installations")
    public void installationCompletion(@RequestParam (value="orderId", required=false, defaultValue="0") Long orderId) {

        Installation installationCompl = installationRepository.findByOrderId(orderId);
        installationCompl.setStatus("INSTALLCOMPLETED");
        SimpleDateFormat defaultSimpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        String today = defaultSimpleDateFormat.format(new Date());
        installationCompl.setInstallCompleteDate(today);
        installationRepository.save(installationCompl);

    }
 }
