package reports;

import java.util.HashMap;
/**
 * Class for Complaint's Reports, contains data from the DB about complaint's reports fetched from the DB
 */
public class ComplaintReport extends Report <HashMap<String, Integer>>{

    public ComplaintReport(int storeId, String year, String period, ReportPeriodEnum periodEnum) {
        super(storeId, year, period, periodEnum);
    }

    @Override
    public ReportTypeEnum getType() {
        return ReportTypeEnum.COMPLAINTS;
    }

    @Override
    public void setObject(HashMap<String, Integer> object) {
        // HashMap: Month -> Amount
        // If month not exist will put 0 on revenue
        if(isQuarterly() && storeId != -1){
            for (int i = 1; i < 4; i++) {
                int tempMonth = (Integer.parseInt(getPeriod()) - 1) * 3 + i;
                if (!object.containsKey(String.valueOf(tempMonth)))
                    object.put(String.valueOf(tempMonth), 0);
            }
        }
        // HashMap: Day -> Amount
        // If day not exist will put 0 on amount
        if(isMonthly() && storeId != -1){
            for (int i = 1; i < 31; i++) {
                if (!object.containsKey(String.valueOf(i)))
                    object.put(String.valueOf(i), 0);
            }
        }
        super.setObject(object);
    }
}
