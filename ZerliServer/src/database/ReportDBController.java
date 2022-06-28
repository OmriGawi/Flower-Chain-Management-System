package database;

import classes.Item;
import communication.Answer;
import communication.Message;
import javafx.util.Pair;
import reports.*;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static utils.Constants.REPORT_DATE_FORMAT;
import static utils.Constants.SQL_DATE_FORMAT;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class ReportDBController {

    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ INCOME REPORT @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

    /**
     * Creates a new query for selecting all orders for specific store id for a given month and year.<p>
     * will put the result set in HashMap.<p>
     * Key-day of month | value-total price
     *
     * @param storeId store id
     * @param month month
     * @param year year
     * @return HashMap: Key-day | Value-total price
     */
    public static HashMap<String, Double> createMonthlyIncomeReportByStore(int storeId, int month, int year){
        String query = "SELECT DAYOFMONTH(creation_date) as day_of_month, SUM(total_price) as total_price\n" +
                "FROM zerli.orders\n" +
                "WHERE store_id=" + storeId + " AND month(creation_date)=" + month + " AND year(creation_date)=" + year + "\n" +
                "GROUP BY DAYOFMONTH(creation_date);";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        HashMap<String, Double> dayIncomeData = new HashMap<>();
        if (rs == null)
            return dayIncomeData;
        try {
            while (rs.next()) {
                dayIncomeData.put(
                        rs.getString("day_of_month"),
                        rs.getDouble("total_price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dayIncomeData;
    }

    /**
     * Creates a new query for inserting a new monthly income report into database report table.<p>
     * the given HashMap will be converted into blob value.
     *
     * @param daysIncomeData  HashMap : Key-day | Value-total price
     * @param storeId store id
     */
    public static void saveMonthlyIncomeReportByStore(HashMap<String, Double> daysIncomeData, int storeId) {
        String query = "INSERT INTO `zerli`.`reports` (`store_id`, `creation_date`, `type`, `duration_type`, `content`)" +
                "VALUES ('STORE_ID', 'CREATION_DATE', 'TYPE', 'DURATION_TYPE', ?);";
        String tempQuery = query
                .replace("'STORE_ID'", String.valueOf(storeId))
                .replace("'CREATION_DATE'", "'" + SQL_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "'")
                .replace("'TYPE'", "'INCOME'")
                .replace("'DURATION_TYPE'", "'MONTHLY'");
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(tempQuery);
            prepareStatement.setBlob(1, convertObjectToBlob(daysIncomeData));
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1)
                throw new IllegalArgumentException("Report was not created!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new query for inserting a new quarterly income report into database report table.
     * @param storeId store ID
     * @param quarter quarter of year
     * @param year year
     * @return HashMap: Key-month | Value-total price
     */
    public static HashMap<String, Double> createQuarterlyIncomeReportByStore(int storeId, int quarter, int year){
        Pair<Integer,Integer> quarterRange = calculateMonthRangeOfQuarter(quarter);
        int fromMonth = quarterRange.getKey();
        int toMonth = quarterRange.getValue();

        String query = String.format(
                        "SELECT MONTH(creation_date) as month, SUM(total_price) as total_price\n" +
                        "FROM zerli.orders\n" +
                        "WHERE store_id = %s AND MONTH(creation_date) BETWEEN %s AND %s AND YEAR(creation_date) = %s\n" +
                        "GROUP BY MONTH(creation_date)\n" +
                        "ORDER BY month;",
                         storeId, fromMonth, toMonth, year
        );
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        HashMap<String, Double> monthIncomeData = new HashMap<>();
        if (rs == null)
            return monthIncomeData;
        try {
            while (rs.next()) {
                monthIncomeData.put(
                        rs.getString("month"),
                        rs.getDouble("total_price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthIncomeData;
    }

    /**
     * Creates a new query for inserting a new quarterly income report into database report table.<p>
     * the given HashMap will be converted into blob value.
     * @param monthIncomeData data
     * @param storeId store id
     */
    public static void saveQuarterlyIncomeReportByStore(HashMap<String, Double> monthIncomeData, int storeId) {
        String query = "INSERT INTO `zerli`.`reports` (`store_id`, `creation_date`, `type`, `duration_type`, `content`)" +
                "VALUES ('STORE_ID', 'CREATION_DATE', 'TYPE', 'DURATION_TYPE', ?);";
        String tempQuery = query
                .replace("'STORE_ID'", String.valueOf(storeId))
                .replace("'CREATION_DATE'", "'" + SQL_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "'")
                .replace("'TYPE'", "'INCOME'")
                .replace("'DURATION_TYPE'", "'QUARTERLY'");
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(tempQuery);
            prepareStatement.setBlob(1, convertObjectToBlob(monthIncomeData));
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1)
                throw new IllegalArgumentException("Report was not created!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates a new query for inserting a new quarterly income report into database report table,
     * used to create a report that calculate all stores income and group by store
     * @param quarter quarter of year
     * @param year year
     * @return HashMap: Key-Store | Value-total price
     */
    public static HashMap<String, Double> createQuarterlyIncomeReport(int quarter, int year){
        Pair<Integer,Integer> quarterRange = calculateMonthRangeOfQuarter(quarter);
        int fromMonth = quarterRange.getKey();
        int toMonth = quarterRange.getValue();

        String query = String.format(
                "WITH totalAmount AS (\n" +
                        "\tSELECT\n" +
                        "\t\tstore_id,\n" +
                        "\t\tSUM(total_price) as total_price\n" +
                        "\tFROM zerli.orders\n" +
                        "\tWHERE MONTH(creation_date) BETWEEN %s AND %s AND YEAR(creation_date) = %s\n" +
                        "\tGROUP BY store_id\n" +
                        ")\n" +
                        "SELECT \n" +
                        "\tname AS store_name,\n" +
                        "    IFNULL (total_price, 0) as total_price\n" +
                        "FROM stores\n" +
                        "LEFT JOIN totalAmount\n" +
                        "\tON store_id = id;",
                fromMonth, toMonth, year
        );
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        HashMap<String, Double> data = new HashMap<>();
        if (rs == null)
            return data;
        try {
            while (rs.next()) {
                data.put(
                        rs.getString("store_name"),
                        rs.getDouble("total_price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Creates a new query for inserting a new quarterly income report into database report table.
     * The store_id column in database will be null because it belongs to all stores.
     * <p>
     * the given HashMap will be converted into blob value.
     * @param data data
     */
    public static void saveQuarterlyIncomeReport(HashMap<String, Double> data) {
        String query = "INSERT INTO `zerli`.`reports` (`creation_date`, `type`, `duration_type`, `content`)" +
                "VALUES ('CREATION_DATE', 'TYPE', 'DURATION_TYPE', ?);";
        String tempQuery = query
                .replace("'CREATION_DATE'", "'" + SQL_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "'")
                .replace("'TYPE'", "'INCOME'")
                .replace("'DURATION_TYPE'", "'QUARTERLY'");
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(tempQuery);
            prepareStatement.setBlob(1, convertObjectToBlob(data));
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1)
                throw new IllegalArgumentException("Report was not created!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ORDERS REPORT @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/


    /**
     * Creates a new query for selecting the amount of items sold in specific store, month and year group by item type.<p>
     * @param storeId store id
     * @param month month
     * @param year year
     * @return Pair <p> Key - HashMap: Item Type --> Amount <p>
     *                 Value - Item: the Best seller item
     */
    public static Pair<HashMap<String, Integer>, Item> createMonthlyOrderReportByStore(int storeId, int month, int year){
        String query = String.format("WITH itemsWithAmount AS (\n" +
                "\tSELECT\n" +
                "\t\titems.*,\n" +
                "\t\tSUM(amount) as item_amount\n" +
                "\tFROM ( SELECT * \n" +
                "\t\t\tFROM orders\n" +
                "\t\t\tWHERE store_id = %s AND MONTH(creation_date) = %s AND YEAR(creation_date) = %s) AS tblOrders\n" +
                "\tJOIN items_in_orders\n" +
                "\t\tON tblOrders.id = items_in_orders.order_id\n" +
                "\tJOIN items\n" +
                "\t\tON items_in_orders.item_id = items.id\n" +
                "\tGROUP BY item_id\n" +
                "    ORDER BY item_amount DESC\n" +
                ")\n" +
                "SELECT \n" +
                "\tSUM(CASE WHEN isProduct = 0 THEN item_amount ELSE 0 END) AS single_item,\n" +
                "\tSUM(CASE WHEN isProduct = 1 AND account_id IS NULL THEN item_amount ELSE 0 END) AS product,\n" +
                "    SUM(CASE WHEN isProduct = 1 AND account_id IS NOT NULL THEN item_amount ELSE 0 END) AS custom_item,\n" +
                "\tid, name, price, color, discount_percentage, img, isProduct, account_id\n" +
                "FROM itemsWithAmount;",
                storeId, month, year
        );
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        HashMap<String, Integer > dataMap = new HashMap<>();
        Item item = null;
        if (rs == null)
            return null;
        try {
            while (rs.next()) {
                dataMap.put("Single Item", rs.getInt("single_item"));
                dataMap.put("Product", rs.getInt("product"));
                dataMap.put("Custom Item", rs.getInt("custom_item"));
                item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("color"),
                        rs.getInt("discount_percentage"),
                        rs.getString("img"),
                        rs.getInt("account_id") != 0 ? rs.getInt("account_id") : null,
                        rs.getBoolean("isProduct"),
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Pair<>(dataMap, item);
    }

    /**
     * Creates a new query for inserting a new monthly order report into database report table.<p>
     * the given Pair will be converted into blob value.
     * @param pair <p> Key - HashMap: Item Type --> Amount <p>
     *                 Value - Item: the Best seller item
     * @param storeId store id
     */
    public static void saveMonthlyOrderReportByStore(Pair<HashMap<String, Integer>, Item> pair, int storeId) {
        String query = "INSERT INTO `zerli`.`reports` (`store_id`, `creation_date`, `type`, `duration_type`, `content`)" +
                "VALUES ('STORE_ID', 'CREATION_DATE', 'TYPE', 'DURATION_TYPE', ?);";
        String tempQuery = query
                .replace("'STORE_ID'", String.valueOf(storeId))
                .replace("'CREATION_DATE'", "'" + SQL_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "'")
                .replace("'TYPE'", "'ORDERS'")
                .replace("'DURATION_TYPE'", "'MONTHLY'");
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(tempQuery);
            prepareStatement.setBlob(1, convertObjectToBlob(pair));
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1)
                throw new IllegalArgumentException("Report was not created!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates a new query for selecting the amount of items sold in specific store, quarter and year group by item type.<p>
     * @param storeId store id
     * @param quarter quarter of year
     * @param year year
     * @return Pair <p> Key - HashMap: Item Type --> Amount <p>
     *                 Value - Item: the Best seller item
     */
    public static Pair<HashMap<String, Integer>, Item> createQuarterlyOrderReportByStore(int storeId, int quarter, int year){
        Pair<Integer,Integer> quarterRange = calculateMonthRangeOfQuarter(quarter);
        int fromMonth = quarterRange.getKey();
        int toMonth = quarterRange.getValue();

        String query = String.format("WITH itemsWithAmount AS (\n" +
                        "\tSELECT\n" +
                        "\t\titems.*,\n" +
                        "\t\tSUM(amount) as item_amount\n" +
                        "\tFROM ( SELECT * \n" +
                        "\t\t\tFROM orders\n" +
                        "\t\t\tWHERE store_id = %s AND MONTH(creation_date) BETWEEN %s AND %s AND YEAR(creation_date) = %s) AS tblOrders\n" +
                        "\tJOIN items_in_orders\n" +
                        "\t\tON tblOrders.id = items_in_orders.order_id\n" +
                        "\tJOIN items\n" +
                        "\t\tON items_in_orders.item_id = items.id\n" +
                        "\tGROUP BY item_id\n" +
                        "    ORDER BY item_amount DESC\n" +
                        ")\n" +
                        "SELECT \n" +
                        "\tSUM(CASE WHEN isProduct = 0 THEN item_amount ELSE 0 END) AS single_item,\n" +
                        "\tSUM(CASE WHEN isProduct = 1 AND account_id IS NULL THEN item_amount ELSE 0 END) AS product,\n" +
                        "    SUM(CASE WHEN isProduct = 1 AND account_id IS NOT NULL THEN item_amount ELSE 0 END) AS custom_item,\n" +
                        "\tid, name, price, color, discount_percentage, img, isProduct, account_id\n" +
                        "FROM itemsWithAmount;",
                storeId, fromMonth, toMonth, year
        );
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        HashMap<String, Integer > dataMap = new HashMap<>();
        Item item = null;
        if (rs == null)
            return null;
        try {
            while (rs.next()) {
                dataMap.put("Single Item", rs.getInt("single_item"));
                dataMap.put("Product", rs.getInt("product"));
                dataMap.put("Custom Item", rs.getInt("custom_item"));
                item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("color"),
                        rs.getInt("discount_percentage"),
                        rs.getString("img"),
                        rs.getInt("account_id") != 0 ? rs.getInt("account_id") : null,
                        rs.getBoolean("isProduct"),
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Pair<>(dataMap, item);
    }

    /**
     * Creates a new query for inserting a new quarterly order report into database report table.<p>
     * @param pair <p> Key - HashMap: Item Type --> Amount <p>
     *                 Value - Item: the Best seller item
     * @param storeId store id
     */
    public static void saveQuarterlyOrderReportByStore(Pair<HashMap<String, Integer>, Item> pair, int storeId) {
        String query = "INSERT INTO `zerli`.`reports` (`store_id`, `creation_date`, `type`, `duration_type`, `content`)" +
                "VALUES ('STORE_ID', 'CREATION_DATE', 'TYPE', 'DURATION_TYPE', ?);";
        String tempQuery = query
                .replace("'STORE_ID'", String.valueOf(storeId))
                .replace("'CREATION_DATE'", "'" + SQL_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "'")
                .replace("'TYPE'", "'ORDERS'")
                .replace("'DURATION_TYPE'", "'QUARTERLY'");
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(tempQuery);
            prepareStatement.setBlob(1, convertObjectToBlob(pair));
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1)
                throw new IllegalArgumentException("Report was not created!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ COMPLAINTS REPORT @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    /**
     * Creates a new query for selecting all complaints for specific store id for a given month and year.<p>
     * will put the result set in HashMap.<p>
     * Key-day of month | value-total price
     *
     * @param storeId store id
     * @param month month
     * @param year year
     * @return HashMap: Key-day | Value-amount
     */
    public static HashMap<String, Integer> createMonthlyComplaintReportByStore(int storeId, int month, int year){
        String query = String.format(
                "SELECT\n" +
                        "\tDAYOFMONTH(date) as day_of_month,\n" +
                        "\tCOUNT(complaint_id) as amount\n" +
                        "FROM zerli.complaints\n" +
                        "WHERE store_id = %s AND month(date) = %s AND year(date) = %s\n" +
                        "GROUP BY DAYOFMONTH(date);",
                storeId, month, year
        );
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        HashMap<String, Integer> data = new HashMap<>();
        if (rs == null)
            return data;
        try {
            while (rs.next()) {
                data.put(
                        rs.getString("day_of_month"),
                        rs.getInt("amount")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Creates a new query for inserting a new monthly complaint report into database report table.<p>
     * the given HashMap will be converted into blob value.
     *
     * @param data  HashMap : Key-day | Value-amount
     * @param storeId store id
     */
    public static void saveMonthlyComplaintReportByStore(HashMap<String, Integer> data, int storeId) {
        String query = "INSERT INTO `zerli`.`reports` (`store_id`, `creation_date`, `type`, `duration_type`, `content`)" +
                "VALUES ('STORE_ID', 'CREATION_DATE', 'TYPE', 'DURATION_TYPE', ?);";
        String tempQuery = query
                .replace("'STORE_ID'", String.valueOf(storeId))
                .replace("'CREATION_DATE'", "'" + SQL_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "'")
                .replace("'TYPE'", "'COMPLAINTS'")
                .replace("'DURATION_TYPE'", "'MONTHLY'");
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(tempQuery);
            prepareStatement.setBlob(1, convertObjectToBlob(data));
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1)
                throw new IllegalArgumentException("Report was not created!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates a new query for selecting all complaints for specific store id for a given quarter and year.<p>
     * @param storeId store id
     * @param quarter quarter
     * @param year year
     * @return HashMap: Key-month | Value-amount
     */
    public static HashMap<String, Integer> createQuarterlyComplaintReportByStore(int storeId, int quarter, int year){
        Pair<Integer,Integer> quarterRange = calculateMonthRangeOfQuarter(quarter);
        int fromMonth = quarterRange.getKey();
        int toMonth = quarterRange.getValue();

        String query = String.format(
                "SELECT\n" +
                        "\tMONTH(date) as month,\n" +
                        "\tCOUNT(complaint_id) as amount\n" +
                        "FROM zerli.complaints\n" +
                        "WHERE store_id = %s AND month(date) BETWEEN %s AND %s AND year(date) = %s\n" +
                        "GROUP BY month;",
                storeId, fromMonth, toMonth,  year
        );
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        HashMap<String, Integer> data = new HashMap<>();
        if (rs == null)
            return data;
        try {
            while (rs.next()) {
                data.put(
                        rs.getString("month"),
                        rs.getInt("amount")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Creates a new query for inserting a new monthly complaint report into database report table.<p>
     * the given HashMap will be converted into blob value.
     *
     * @param data  HashMap : Key-month | Value-amount
     * @param storeId store id
     */
    public static void saveQuarterlyComplaintReportByStore(HashMap<String, Integer> data, int storeId) {
        String query = "INSERT INTO `zerli`.`reports` (`store_id`, `creation_date`, `type`, `duration_type`, `content`)" +
                "VALUES ('STORE_ID', 'CREATION_DATE', 'TYPE', 'DURATION_TYPE', ?);";
        String tempQuery = query
                .replace("'STORE_ID'", String.valueOf(storeId))
                .replace("'CREATION_DATE'", "'" + SQL_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "'")
                .replace("'TYPE'", "'COMPLAINTS'")
                .replace("'DURATION_TYPE'", "'QUARTERLY'");
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(tempQuery);
            prepareStatement.setBlob(1, convertObjectToBlob(data));
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1)
                throw new IllegalArgumentException("Report was not created!");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CALCULATIONS METHODS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    /**
     * Calculates the months range of a given quarter.
     * @param quarter quarter of a year (1-4)
     * @return Pair of the range of a quarter.<p>
     *          Key - first month of a quarter <p>
     *          Value - last month of a quarter
     */
    private static Pair<Integer, Integer> calculateMonthRangeOfQuarter(int quarter){
        int fromMonth = -1, toMonth = -1;
        switch (quarter){
            case 1:
                fromMonth = 1;
                toMonth = 3;
                break;
            case 2:
                fromMonth = 4;
                toMonth = 6;
                break;
            case 3:
                fromMonth = 7;
                toMonth = 9;
                break;
            case 4:
                fromMonth = 10;
                toMonth = 12;
                break;
            default:
                throw new IllegalArgumentException("quarter should be 1-4 only");
        }
        return new Pair<>(fromMonth, toMonth);
    }

    /**
     * Calculates the DATE required for selecting an appropriate report from database asked by the user.
     * @return Calender
     */
    private static <T extends Serializable> Calendar calculateReportCreationDate(Report<T> report) {
        Date d = null;
        try {
            if(report.isMonthly()) {
                d = REPORT_DATE_FORMAT.parse(report.getYear() + "-" + report.getPeriod());
            }
            else if (report.isQuarterly()){
                int month = Integer.parseInt(report.getPeriod()) * 3;
                int year = Integer.parseInt(report.getYear());
                String monthStr = String.valueOf(month).length() == 1 ? "0" + month : String.valueOf(month);
                d = REPORT_DATE_FORMAT.parse(year + "-" + monthStr);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.MONTH, 1);
        d.setTime(c.getTime().getTime());
        return c;
    }


    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CONVERT METHODS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    /**
     * Convert a given T to blob value.
     * @param object object extends serializable
     * @return blob
     */
    private static <T extends Serializable> Blob convertObjectToBlob(T object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        Blob blob = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            byte[] bytes = baos.toByteArray();
            blob = new SerialBlob(bytes);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        return blob;
    }

    /**
     * Convert a given blob to T
     * @param blob blob
     */
    private static <T extends Serializable> T convertBlobToObject(Blob blob) {
        ByteArrayInputStream bais = null;
        ObjectInputStream in = null;
        try {
            bais = new ByteArrayInputStream(blob.getBytes(1, Math.toIntExact(blob.length())));
            in = new ObjectInputStream(bais);
            return (T) in.readObject();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ GET REPORT FROM REPORT TABLE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    /**
     * Creates a new query for selecting a report required by user<p>
     * It will convert the blob came from database into T and will put it into report's T variable
     * @param message Object - Report
     */
    public static <T extends Serializable> void getSelectedReport(Message message){
        Report<T> report = (Report<T>) message.getObject();
        Calendar calendar = calculateReportCreationDate(report);

        String storeId = String.valueOf(report.getStoreId());
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String type = String.valueOf(report.getType());
        String durationType = String.valueOf(report.getDurationType());
        Blob blob = null;
        String query;

        // Selected report is for all stores -> search for store_id is null (used by CEO user)
        if(report.getStoreId() == -1)
             query = String.format("SELECT * FROM zerli.reports WHERE store_id is NULL AND type = '%s' AND duration_type = '%s' AND YEAR(creation_date) = '%s' AND MONTH(creation_date) = '%s'",
                    type, durationType, year, month);
        // Selected report is for specific store -> search for store_id value
        else{
             query = String.format("SELECT * FROM zerli.reports WHERE store_id = %s AND type = '%s' AND duration_type = '%s' AND YEAR(creation_date) = '%s' AND MONTH(creation_date) = '%s'",
                    storeId, type, durationType, year, month);
        }

        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else{
            try{
                if(!rs.next()){
                    message.setAnswer(Answer.SELECTED_REPORT_NOT_EXIST);
                    return;
                }
                else {
                    rs.first();
                    blob = rs.getBlob("content");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            T object = convertBlobToObject(blob);
            report.setObject(object);
            message.setAnswer(Answer.SUCCEED);
        }
    }

    /**
     * Creates a new query for selecting two reports required by user<p>
     * It will convert the blob came from database into T and will put it into report's T variable
     * @param message Object - Report[]
     * @implNote Use this function when you want to select 2 reports from DB.
     */
    public static <T extends Serializable> void getTwoSelectedReport(Message message){
        Report[] report = (Report[]) message.getObject();
        for(int i = 0; i < 2; i++){
            Calendar calendar = calculateReportCreationDate(report[i]);

            String storeId = String.valueOf(report[i].getStoreId());
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String type = String.valueOf(report[i].getType());
            String durationType = String.valueOf(report[i].getDurationType());
            Blob blob = null;
            String query;

            query = String.format("SELECT * FROM zerli.reports WHERE store_id = %s AND type = '%s' AND duration_type = '%s' AND YEAR(creation_date) = '%s' AND MONTH(creation_date) = '%s'",
                    storeId, type, durationType, year, month);

            ResultSet rs = DatabaseController.getInstance().executeQuery(query);
            if (rs == null)
                message.setAnswer(Answer.FAILED);
            else{
                try{
                    if(!rs.next()){
                        message.setAnswer(Answer.SELECTED_REPORT_NOT_EXIST);
                        return;
                    }
                    else {
                        rs.first();
                        blob = rs.getBlob("content");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                T object = convertBlobToObject(blob);
                report[i].setObject(object);
                message.setAnswer(Answer.SUCCEED);
            }
        }
        }

}
