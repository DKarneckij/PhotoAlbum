package photos.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import photos.model.Tag;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TagSearchController implements Initializable {

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField firstTagKey;

    @FXML
    private TextField firstTagValue;

    @FXML
    private TextField secondTagKey;

    @FXML
    private TextField secondTagValue;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.getItems().addAll("OR", "AND");
    }

    public ArrayList<Tag> getData() {

        String firstKey = firstTagKey.getText().trim();
        String firstValue = firstTagValue.getText().trim();
        String secondKey = secondTagKey.getText().trim();
        String secondValue = secondTagValue.getText().trim();

        if (firstKey.equals("") || firstValue.equals("")) {
            return null;
        }

        ArrayList<Tag> tagList = new ArrayList<>();

        tagList.add(new Tag(firstKey, firstValue));
        if (choiceBox.getValue() != null) {
            if(secondKey.equals("") || secondValue.equals("")) {
                return null;
            }
            tagList.add(new Tag(secondKey, secondValue));
        }
        return tagList;
    }

    public String getChoiceBox() {
        return choiceBox.getValue();
    }
}
