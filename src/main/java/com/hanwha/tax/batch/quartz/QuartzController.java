package com.hanwha.tax.batch.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
public class QuartzController {

    @Autowired
    SchedulerFactoryBean sfb;

    @RequestMapping(value = "/manualExe", method = RequestMethod.GET)
    public String manualExe(@RequestParam(name = "job", required = true) String job
            , @RequestParam(name = "jobGroup", required = false, defaultValue = "DEFAULT") String jobGroup
            , HttpServletRequest req) {

        log.info("## QuartzController.java [manualExe] Starts..");
        String lRet = "";
//        List<Map<String, Object>> jobList = selectJobs();

        try {

//            // Job 실행 제약 체크
//            String checkJob = checkJob(job);
//            if(!checkJob.isEmpty()) {
//                throw new BizException(checkJob);
//            }
//
//            Optional<Map<String, Object>> tempList = jobList.stream()
//                    .filter(tempMap -> tempMap.get("jobName").toString().equals(job))
//                    .findFirst();
//
//            if(!tempList.isPresent()) {
//                lRet = "## don't find [jobName] : " + job + ", " + " [jobGroup] : " + jobGroup + "\n";
//                lRet += "============== possibility job \n";
//                for(int i=0;i<jobList.size();i++) {
//                    lRet += "[jobName] : " + jobList.get(i).get("jobName") + ", "
//                            + "[jobGroup] : " + jobList.get(i).get("jobGroup") + "\n";
//                }
//                lRet += "==============";
//                return lRet;
//            }

            JobKey jobKey = new JobKey(job, jobGroup);
            Scheduler lScheduler = sfb.getScheduler();
            lScheduler.triggerJob(jobKey);
            lRet = "OK: "+ job;

        } catch (Exception e) {
            lRet = "NOK: Exception, " + e.getMessage();
        } finally {
            log.info("## QuartzController.java [manualExe] "+ lRet);
        }

        return lRet;
    }
}
