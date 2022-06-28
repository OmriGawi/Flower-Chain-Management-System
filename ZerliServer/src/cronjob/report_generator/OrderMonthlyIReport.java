package cronjob.report_generator;

import classes.Store;
import database.ReportDBController;
import database.StoreDBController;

import java.util.Calendar;
import java.util.List;
/**
 * Generate and save in DB a new monthly order report
 */
public class OrderMonthlyIReport implements IReportGenerator {
    @Override
    public void generate() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        if (month == 0) {
            month = 12;
            c.add(Calendar.YEAR, -1);
        }
        int year = c.get(Calendar.YEAR);

        List<Store> storeList = StoreDBController.getAllStores();
        for (Store store : storeList) {
            ReportDBController.saveMonthlyOrderReportByStore(
                    ReportDBController.createMonthlyOrderReportByStore(store.getId(), month, year),
                    store.getId()
            );
        }
    }
}
