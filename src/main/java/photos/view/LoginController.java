package photos.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import photos.model.AppUsers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private String username;

    @FXML TextField usernameField;
    @FXML Label errorLabel;
    @FXML Button loginBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        usernameField.setOnKeyPressed( event -> {

            if( event.getCode() == KeyCode.ENTER ) {

                try {
                    handleLogin();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }

        } );
    }

    @FXML
    public void handleLogin() throws IOException, ClassNotFoundException {

        username = usernameField.getText();

        if(username.equals("admin")) {

            switchAdminView();

        } else if (AppUsers.hasUser(username)) {

            switchAlbumView();

        } else {

            displayWrongUser();

        }
    }

    private void switchAdminView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void switchAlbumView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AlbumsView.fxml"));
        Parent root = loader.load();

        AlbumsViewController albumsViewController = loader.getController();
        albumsViewController.setData(AppUsers.getUser(username));

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));

    }

    public void displayWrongUser() {
        errorLabel.setVisible(true);
    }
}