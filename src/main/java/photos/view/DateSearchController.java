package photos.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.lang.reflect.Array;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DateSearchController implements Initializable {

    @FXML private TextField endDayField;
    @FXML private ChoiceBox<String> endMonthChoice;
    @FXML private TextField endYearField;
    @FXML private TextField startDayField;
    @FXML private ChoiceBox<String> startMonthChoice;
    @FXML private TextField startYearField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        startMonthChoice.getItems().addAll(months);
        endMonthChoice.getItems().addAll(months);
    }

    public ArrayList<ZonedDateTime> getData() {

        ArrayList<ZonedDateTime> dateList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dateStr;
        LocalDateTime dateTime;

        String startMonth = startMonthChoice.getValue();
        String startDay = startDayField.getText().trim();
        String startYear = startYearField.getText().trim();

        dateStr = startYear + "-" + startMonth + "-" + startDay + " 00:00";

        try {
            dateTime = LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }

        ZonedDateTime startDate = dateTime.atZone(ZoneId.of("America/New_York"));

        String endMonth = endMonthChoice.getValue();
        String endDay = endDayField.getText().trim();
        String endYear = endYearField.getText().trim();

        dateStr = endYear + "-" + endMonth + "-" + endDay + " 23:59";

        try {
            dateTime = LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }

        ZonedDateTime endDate = dateTime.atZone(ZoneId.of("America/New_York"));

        if (endDate.isBefore(startDate)) {
            return null;
        }

        dateList.add(startDate);
        dateList.add(endDate);

        return dateList;
    }
}
