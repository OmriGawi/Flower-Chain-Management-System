package cronjob;

import org.apache.log4j.PropertyConfigurator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * This class handles all background tasks (cron jobs) by different triggers and jobs
 */
public class CronjobScheduler {
    private static CronjobScheduler cronjobScheduler;
    // https://crontab.cronhub.io/
    // http://www.cronmaker.com/?0
    private final String CRON_JOB_QUARTERLY_EXPRESSION = "0 0 0 1 1,4,7,10 ? *";
    private final String CRON_JOB_MONTHLY_EXPRESSION = "0 0 12 1 1/1 ? *";
    private final String CRON_JOB_MINUTELY_EXPRESSION = "0 0/1 * 1/1 * ? *";
    private final String CRON_JOB_SECONDLY_EXPRESSION = "0/1 * * 1/1 * ? *";

    /**
     * Initiate all cronjobs to run by the server (in background)
     */
    private CronjobScheduler() {
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "WARN");
        PropertyConfigurator.configure(prop);

        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = null;
        try {
            sched = sf.getScheduler();

            startMonitorComplaints(sched);
            startMonthlyReports(sched);
            startQuarterlyReports(sched);

            sched.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static CronjobScheduler startCronjobScheduler() {
        if (cronjobScheduler == null)
            cronjobScheduler = new CronjobScheduler();
        return cronjobScheduler;
    }

    /**
     * Start all quarterly reports cronjob schedule
     * @param sched
     * @throws SchedulerException
     */
    private void startMonthlyReports(Scheduler sched) throws SchedulerException {
        JobDetail job = newJob(MonthlyReportGeneratorJob.class)
                .withIdentity("MonthlyReports", "MONTHLY")
                .build();

        CronTrigger trigger = newTrigger()
                .withIdentity("FirstOfEachMonth", "MONTHLY")
                .withSchedule(cronSchedule(CRON_JOB_MONTHLY_EXPRESSION))
                .build();

        sched.scheduleJob(job, trigger);
    }

    /**
     * Start all monthly reports cronjob schedule
     * @param sched
     * @throws SchedulerException
     */
    private void startQuarterlyReports(Scheduler sched) throws SchedulerException {
        JobDetail job = newJob(QuarterlyReportGeneratorJob.class)
                .withIdentity("QuarterlyReports", "QUARTERLY")
                .build();

        CronTrigger trigger = newTrigger()
                .withIdentity("FirstOfEachMonth", "QUARTERLY")
                .withSchedule(cronSchedule(CRON_JOB_QUARTERLY_EXPRESSION))
                .build();

        sched.scheduleJob(job, trigger);
    }

    /**
     * Start monitor complaints to notify if they were not getting treatment within 24 hours
     * @param sched
     * @throws SchedulerException
     */
    private void startMonitorComplaints(Scheduler sched) throws SchedulerException {
        JobDetail job = newJob(MonitorComplaintsTreatmentJob.class)
                .withIdentity("MonitorComplaints", "SECONDLY")
                .build();

        CronTrigger trigger = newTrigger()
                .withIdentity("EachSecond", "SECONDLY")
                .withSchedule(cronSchedule(CRON_JOB_SECONDLY_EXPRESSION))
                .build();

        sched.scheduleJob(job, trigger);
    }
}
