package reports;

import classes.Item;
import javafx.util.Pair;

import java.util.HashMap;

/**
 * Class for Order's Reports, contains data from the DB about order's reports fetched from the DB
 */
public class OrderReport extends Report<Pair<HashMap<String,Integer>, Item>>{

    public OrderReport(int storeId, String year, String period, ReportPeriodEnum periodEnum) {
        super(storeId, year, period, periodEnum);
    }

    @Override
    public ReportTypeEnum getType() {
        return ReportTypeEnum.ORDERS;
    }
}
