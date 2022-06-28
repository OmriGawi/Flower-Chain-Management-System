package cronjob.report_generator;

import database.ReportDBController;

import java.util.Calendar;

//TODO should we delete this report??
public class IncomeAllStoresQuarterlyIReport implements IReportGenerator {
    @Override
    public void generate() {
        Calendar c = Calendar.getInstance();
        int quarter;
        int month = c.get(Calendar.MONTH);
        if (month != Calendar.JANUARY) {
            quarter = month / 3;
        } else {
            quarter = 4;
            c.add(Calendar.YEAR, -1);
        }
        int year = c.get(Calendar.YEAR);
        ReportDBController.saveQuarterlyIncomeReport(
                ReportDBController.createQuarterlyIncomeReport(quarter, year)
        );
    }
}
