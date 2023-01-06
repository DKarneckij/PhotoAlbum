package photos.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import photos.model.Tag;

public class AddTagController {

    @FXML private ComboBox<String> tagKey;
    @FXML private TextField tagValue;

    public void setData() {
        tagKey.getItems().addAll("location", "season", "person", "food");
    }

    public Tag getData() {
        if (tagKey.getValue() == null || tagKey.getValue().trim().equals("") || tagValue.getText().trim().equals("")) {
            return null;
        }
        return new Tag(tagKey.getValue().trim().toLowerCase(), tagValue.getText().trim());
    }
}
