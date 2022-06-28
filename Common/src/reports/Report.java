package reports;

import java.io.Serializable;

/**
 * Abstract from which Report classes will inherit.
 */
public abstract class Report<T extends Serializable> implements Serializable {
    protected final int storeId;
    protected final String year;
    protected final String period;
    protected T object;
    private final ReportPeriodEnum durationType;

    public Report(int storeId, String year, String period, ReportPeriodEnum durationType) {
        this.storeId = storeId;
        this.year = year;
        this.period = period;
        this.durationType = durationType;
    }

    public abstract ReportTypeEnum getType();

    public void setObject(T object){
        this.object = object;
    }
    public T getObject(){
        return object;
    }

    public String getPeriod() {
        return period;
    }
    public String getYear() {
        return year;
    }

    public int getStoreId() {
        return storeId;
    }

    public ReportPeriodEnum getDurationType() {
        return durationType;
    }

    public boolean isMonthly(){
        return durationType == ReportPeriodEnum.MONTHLY;
    }

    public boolean isQuarterly(){
        return durationType == ReportPeriodEnum.QUARTERLY;
    }

    @Override
    public String toString() {
        return "StoreID " + storeId + "," +
                "Year " + year + "," +
                "Period " + period + "," +
                "Duration " + durationType.toString();
    }
}
