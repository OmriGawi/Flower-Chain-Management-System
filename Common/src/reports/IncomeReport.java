package reports;

import java.util.HashMap;
/**
 * Class for Income Reports, contains data from the DB about income reports fetched from the DB
 */
public class IncomeReport extends Report <HashMap<String, Double>>{

    public IncomeReport(int storeId, String year, String period, ReportPeriodEnum periodEnum) {
        super(storeId, year, period, periodEnum);
    }

    @Override
    public ReportTypeEnum getType() {
        return ReportTypeEnum.INCOME;
    }

    @Override
    public void setObject(HashMap<String, Double> object) {
        // HashMap: Month -> Revenue
        // If month not exist will put 0 on revenue
        if(isQuarterly() && storeId != -1){
            for (int i = 1; i < 4; i++) {
                int tempMonth = (Integer.parseInt(getPeriod()) - 1) * 3 + i;
                if (!object.containsKey(String.valueOf(tempMonth)))
                    object.put(String.valueOf(tempMonth), 0.0);
            }
        }
        // HashMap: Day -> Amount
        // If day not exist will put 0 on amount
        if(isMonthly() && storeId != -1) {
            for (int i = 1; i < 31; i++) {
                if (!object.containsKey(String.valueOf(i)))
                    object.put(String.valueOf(i), 0.0);
            }
        }
        super.setObject(object);
    }
}
