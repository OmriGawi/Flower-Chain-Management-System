package cronjob;

import cronjob.report_generator.ReportFactory;
import cronjob.report_generator.IReportGenerator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import static reports.ReportPeriodEnum.QUARTERLY;
import static reports.ReportTypeEnum.*;

/**
 * This Job is being called from [CronJobScheduler] each quarter and generates all quarterly reports
 * @see CronjobScheduler
 */
public class QuarterlyReportGeneratorJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        ReportFactory reportFactory = new ReportFactory();

        IReportGenerator reportIncomeQuarterly = reportFactory.getReport(INCOME, QUARTERLY);
        IReportGenerator reportOrderQuarterly = reportFactory.getReport(ORDERS, QUARTERLY);
        IReportGenerator reportComplaintQuarterly = reportFactory.getReport(COMPLAINTS, QUARTERLY);

        reportIncomeQuarterly.generate();
        reportOrderQuarterly.generate();
        reportComplaintQuarterly.generate();
    }
}
