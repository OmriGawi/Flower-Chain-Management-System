package cronjob;

import cronjob.report_generator.ReportFactory;
import cronjob.report_generator.IReportGenerator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import static reports.ReportPeriodEnum.MONTHLY;
import static reports.ReportTypeEnum.*;

/**
 * This Job is being called from [CronJobScheduler] each month and generates all monthly reports
 * @see CronjobScheduler
 */
public class MonthlyReportGeneratorJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        ReportFactory reportFactory = new ReportFactory();

        IReportGenerator reportIncomeMonthly = reportFactory.getReport(INCOME, MONTHLY);
        IReportGenerator reportOrderMonthly = reportFactory.getReport(ORDERS, MONTHLY);
        IReportGenerator reportComplaintMonthly = reportFactory.getReport(COMPLAINTS, MONTHLY);

        reportIncomeMonthly.generate();
        reportOrderMonthly.generate();
        reportComplaintMonthly.generate();
    }
}
