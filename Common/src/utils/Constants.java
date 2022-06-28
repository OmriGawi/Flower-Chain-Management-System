package utils;

import com.sun.source.tree.Tree;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class of constants commonly used
 */
public class Constants {
    public static final int DEFAULT_PORT = 5555;
    public static final String EMAIL_USERNAME = "zerli.g16@gmail.com";
    public static final String EMAIL_PASSWORD = "g16.zerli";
    public static final String TEXT_FIELD_VALID_STYLE = "-fx-background-color: transparent; -fx-border-color: #AFDD93; -fx-border-width: 0px 0px 2px 0px;";
    public static final String TEXT_FIELD_NOT_VALID_STYLE = "-fx-background-color: transparent; -fx-border-color: #EB5234; -fx-border-width: 0px 0px 2px 0px;";
    public static final String TEXT_VALID_STYLE = "-fx-fill: #399400;";
    public static final String TEXT_NOT_VALID_STYLE = "-fx-fill: #EB5234;";
    public static final String TIME_FORMAT_STRING = "HH:mm";
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_STRING);
    public static final String SQL_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final String REPORT_DATE_FORMAT_STRING = "yyyy-MM";
    public static final DateFormat SQL_DATE_FORMAT = new SimpleDateFormat(SQL_DATE_FORMAT_STRING);
    public static final DateFormat REPORT_DATE_FORMAT = new SimpleDateFormat(REPORT_DATE_FORMAT_STRING);
    public static final String DEFAULT_ITEM_ICON_PATH = "/resources/whiteLeafIcon.png";
    public static final String DEFAULT_CUSTOM_ITEM_ICON_PATH = "/resources/whiteLeafIcon-Custom.png";
    public static final String DEFAULT_PRODUCT_ITEM_ICON_PATH = "/resources/whiteLeafIcon-Product.png";
    public static final String DEFAULT_SINGLE_ITEM_ICON_PATH = "/resources/whiteLeafIcon-Single.png";
    public static final String EXPERT_PDF_FILE_NAME = "ExpertPDF.pdf";
    public static String[] SALE_ITEM_COLORS = {"B9E5FF","BDB2FE","FB9AA8","FF8084"};
    public static String[] SALE_ITEM_HOVER_COLORS = {"7B98AA","7E76A9","A76670","AA5558"};
    public static final Set<String> ITEMS_COLORS = new HashSet<>(Arrays.asList(
            "BLACK",
            "WHITE",
            "RED",
            "ORANGE",
            "GREEN",
            "YELLOW",
            "BLUE",
            "PINK",
            "PURPLE",
            "GREY",
            "BROWN",
            "CHAMPAGNE"
    ));

    public static final TreeSet<String> MONTHS = new TreeSet<>(Arrays.asList(
            "01",
            "02",
            "03",
            "04",
            "05",
            "06",
            "07",
            "08",
            "09",
            "10",
            "11",
            "12"
    ));

    public static final TreeSet<String> YEARS = new TreeSet<>(Arrays.asList(
            "2022"
    ));

    public static final TreeSet<String> QUARTERS = new TreeSet<>(Arrays.asList(
            "1",
            "2",
            "3",
            "4"
    ));
}
