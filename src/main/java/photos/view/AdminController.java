package photos.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import photos.model.AppUsers;
import photos.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminController implements Initializable {

    static class XCell extends ListCell<String> {

        ImageView profilePic = new ImageView(new Image("file:src/main/resources/ImagesFXML/ProfilePicture.png"));
        ImageView trashCan = new ImageView(new Image("file:src/main/resources/ImagesFXML/TrashCan.png"));

        StackPane imgPane = new StackPane();
        HBox hbox = new HBox();
        Label label = new Label("");
        Label edit  = new Label("Edit");
        Pane pane = new Pane();

        public XCell() {

            super();

            profilePic.setFitHeight(30);
            profilePic.setFitWidth(30);
            trashCan.setFitHeight(22);
            trashCan.setFitWidth(22);

            imgPane.getChildren().add(trashCan);
            imgPane.setPadding(new Insets(0, 0, 2, 0));
            label.setPadding(new Insets(7, 0, 0, 0));
            edit.setPadding(new Insets(7, 10, 0, 0));

            label.setStyle("-fx-font-weight: bold");

            hbox.getChildren().addAll(profilePic, label, pane, edit, imgPane);
            HBox.setHgrow(pane, Priority.ALWAYS);

            edit.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                getListView().getScene().lookup("#errorLabel").setVisible(false);

                TextInputDialog dialog = new TextInputDialog();

                dialog.setTitle("Edit User");
                dialog.setHeaderText(null);
                dialog.setGraphic(null);
                dialog.setContentText("Enter New Username:");

                Optional<String> usernameInput = dialog.showAndWait();

                /* Add user if username not empty, and username is not a duplicate
                 * */
                if (usernameInput.isPresent()) {

                    String username = usernameInput.get().trim();
                    if (!username.isEmpty() && !AppUsers.hasUser(username)) {
                        Objects.requireNonNull(AppUsers.getUser(getItem())).setUsername(username);
                        getListView().getItems().remove(getItem());
                        getListView().getItems().add(username);

                    } else {
                        getListView().getScene().lookup("#errorLabel").setVisible(true);
                    }
                    getListView().getSelectionModel().clearSelection();
                }
                event.consume();
            });

            /* Event Handler for clicking trash can icon */
            trashCan.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                AppUsers.removeUser(getItem());
                getListView().getItems().remove(getItem());
                getListView().getSelectionModel().clearSelection();
                event.consume();
            });
        }

        @Override
        public void updateItem(String name, boolean empty) {
            super.updateItem(name, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                label.setText(name);
                setGraphic(hbox);
            }
        }

    }

    @FXML Button logoutBtn;
    @FXML ListView<String> lvUsernames;
    @FXML Label errorLabel;

    private ObservableList<String> usernames;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        lvUsernames.setEditable(true);

        usernames = AppUsers.users
                .stream().map(User::getUsername)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        lvUsernames.setItems(usernames);

        lvUsernames.setCellFactory(param -> new XCell());

    }

    @FXML
    public void handleAddNew() {

        errorLabel.setVisible(false);

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Create New User");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Enter Username:");

        Optional<String> usernameInput = dialog.showAndWait();

        if (usernameInput.isPresent()) {


            String username = usernameInput.get().trim();
            if (!username.isEmpty() && !AppUsers.hasUser(username)) {

                AppUsers.addUser(username);

                usernames.add(username);

            } else {

                errorLabel.setVisible(true);

            }
        }
        lvUsernames.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleLogout() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.setScene(new Scene(root));

        AppUsers.writeUsers();

    }
}
