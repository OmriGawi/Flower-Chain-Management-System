package cronjob.report_generator;

import reports.ReportPeriodEnum;
import reports.ReportTypeEnum;

import static reports.ReportPeriodEnum.MONTHLY;
import static reports.ReportPeriodEnum.QUARTERLY;

/**
 * This class handles reports generation
 */
public class ReportFactory {
    /**
     * Get report by the specified enums
     * @param reportTypeEnum
     * @param reportPeriodEnum
     * @return
     */
    public IReportGenerator getReport(ReportTypeEnum reportTypeEnum, ReportPeriodEnum reportPeriodEnum) {
        if (reportTypeEnum == null || reportPeriodEnum == null) {
            return null;
        }
        switch (reportTypeEnum) {
            case INCOME:
                if (MONTHLY == reportPeriodEnum) return new IncomeMonthlyIReport();
                if (QUARTERLY == reportPeriodEnum) return new IncomeQuarterlyIReport();
                break;
            case COMPLAINTS:
                if (MONTHLY == reportPeriodEnum) return new ComplaintMonthlyIReport();
                if (QUARTERLY == reportPeriodEnum) return new ComplaintQuarterlyIReport();
                break;
            case ORDERS:
                if (MONTHLY == reportPeriodEnum) return new OrderMonthlyIReport();
                if (QUARTERLY == reportPeriodEnum) return new OrderQuarterlyIReport();
                break;
        }
        return null;
    }
}
