package cronjob.report_generator;

import classes.Store;
import database.ReportDBController;
import database.StoreDBController;

import java.util.Calendar;
import java.util.List;
/**
 * Generate and save in DB a new quarterly income report
 */
public class IncomeQuarterlyIReport implements IReportGenerator {
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

        List<Store> storeList = StoreDBController.getAllStores();
        for (Store store : storeList) {
            ReportDBController.saveQuarterlyIncomeReportByStore(
                    ReportDBController.createQuarterlyIncomeReportByStore(store.getId(), quarter, year),
                    store.getId()
            );
        }
        ReportDBController.saveQuarterlyIncomeReport(
                ReportDBController.createQuarterlyIncomeReport(quarter, year)
        );
    }
}
