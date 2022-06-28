package database;

import classes.Survey;
import classes.SurveyQuestion;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import communication.Answer;
import communication.Message;
import utils.Functions;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static utils.Constants.EXPERT_PDF_FILE_NAME;
/**
 * This class defines the methods that send query's to the DB and receive the data back from the DB
 * and return it to the relevant controllers and classes that need to use that information
 */
public class SurveyDBController {

    /**
     * Get all surveys from the DV
     * @param message
     */
    public static void getAllSurveys(Message message) {
        String query = "SELECT * FROM zerli.survey;";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(Survey.createSurveyListFromResultSet(rs));
        }
    }

    /**
     * Get all survey questions for a specific survey from the DB
     * @param message
     */
    public static void getSurveyQuestion(Message message) {
        int surveyId = (int) message.getObject();
        String query = "SELECT *\n" +
                "FROM zerli.questions_in_survey\n" +
                "where survey_id = "+surveyId+";";
        ResultSet rs = DatabaseController.getInstance().executeQuery(query);
        if (rs == null)
            message.setAnswer(Answer.FAILED);
        else {
            message.setAnswer(Answer.SUCCEED);
            message.setObject(SurveyQuestion.createSurveyQuestionFromResultSet(rs));
        }
    }

    /**
     * Update the answers for each question to the DB
     * @param message
     */
    public static void updateAnswers(Message message) {
        List<SurveyQuestion> questions = (List<SurveyQuestion>) message.getObject();
        String query;
        for (SurveyQuestion question:questions) {
            query = "UPDATE zerli.questions_in_survey \n" +
                    "SET amount_participants = amount_participants + 1, rating_avg = " + question.getAvgRating() + "\n" +
                    "WHERE question_id = " + question.getId() + ";";

            int result = DatabaseController.getInstance().executeUpdate(query);
            if (result == -1) {
                message.setAnswer(Answer.NO_ROWS_AFFECTED);
                break;
            }
        }
        message.setAnswer(Answer.SUCCEED);
    }

    /**
     * Update conclusion for a survey in the DB
     * @param message
     */
    public static void updateConclusion(Message message) {
        Survey survey = (Survey)message.getObject();
        String query = "UPDATE zerli.survey \n" +
                "SET conclusions = '"+survey.getConclusion()+"',pdf = ? \n" +
                "WHERE id = "+survey.getId()+";";
        PreparedStatement prepareStatement = null;
        try {
            generatePdf(survey);
            File fBlob = new File (EXPERT_PDF_FILE_NAME);
            FileInputStream is = new FileInputStream(fBlob);
            prepareStatement = DatabaseController.getInstance().getConnection().prepareStatement(query);
            prepareStatement.setBinaryStream(1, is, (int) fBlob.length());
            int result = DatabaseController.getInstance().executeUpdate(prepareStatement);
            if (result == -1) {
                message.setAnswer(Answer.NO_ROWS_AFFECTED);
                throw new IllegalArgumentException("Report was not created!");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a new PDF with a survey conclusion and data
     * @param survey
     */
    public static void generatePdf(Survey survey)  {
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(EXPERT_PDF_FILE_NAME));

            // Open PDF
            document.open();
            // Add PDF content
            Font font = FontFactory.getFont(FontFactory.COURIER, 18, BaseColor.BLACK);
            Chunk title1 = new Chunk(survey.getDescription(), font);
            document.add(new Paragraph(title1));
            Chunk titleQuestion = new Chunk("\nQuestion :", font);
            PdfPTable table = new PdfPTable(2);
            addTableHeader(table);
            addRows(table,survey);
            document.add(titleQuestion);
            document.add(table);
            Chunk conclusionTitle = new Chunk("\nConclusion:\n\n", font);
            Font font2 = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk conclusionText = new Chunk(survey.getConclusion(), font2);
            document.add(conclusionTitle);
            document.add(new Paragraph(conclusionText));
            document.addTitle("Survey Conclusions");

            // Close PDF
            document.close();
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a header to the PDF
     * @param table
     */
    private static void addTableHeader(PdfPTable table) {
        Stream.of("Questions", "Average Rating")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    /**
     * Add rows of data to the PDF
     * @param table
     * @param survey
     */
    private static void addRows(PdfPTable table, Survey survey) {
        for(SurveyQuestion question:survey.getQuestions()) {
            table.addCell(question.getQuestion());
            table.addCell(String.valueOf(Functions.round(question.getAvgRating(), 2)));
        }
    }
}
