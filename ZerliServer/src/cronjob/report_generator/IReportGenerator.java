package cronjob.report_generator;

/**
 * Interface for reports being generated automatically by background tasks (cron jobs)
 */
public interface IReportGenerator {
    /**
     * Generates a report and saves it in DB
     */
    void generate();
}