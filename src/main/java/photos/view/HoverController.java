package photos.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import photos.model.Album;
import photos.model.AppUsers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HoverController implements Initializable {

    @FXML private Label openLabel;
    @FXML private Label renameLabel;
    @FXML private Label deleteLabel;

    private AlbumsViewController albumsViewController;
    private StackPane curStack;
    private TilePane curGrid;
    private Album album;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {

            openLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                try {
                    handleOpen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            renameLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                handleRename();
            });

            deleteLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                handleDelete();
            });

        });
    }


    public void handleOpen() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PictureView.fxml"));
        Parent root = loader.load();

        PictureViewController pictureViewController = loader.getController();
        pictureViewController.setData(album, albumsViewController.getUser());

        Stage stage = (Stage) openLabel.getScene().getWindow();
        stage.setScene(new Scene(root));

        AppUsers.writeUsers();

    }

    public  void  handleRename() {

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Rename Album");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Enter New Album Name:");

        Optional<String> albumNameInput = dialog.showAndWait();

        if (albumNameInput.isPresent()) {

            String username = albumNameInput.get().trim();

            if (username.length() > 30) {
                albumsViewController.setError(0);
                return;
            }

            if (!username.isEmpty() && !albumsViewController.getUser().hasAlbum(username)) {

                Label label = (Label) curStack.getChildren().get(0).lookup("#albumName");
                label.setText(username);
                album.setName(username);

            } else {

                System.out.println("Duplicate album");

            }

        }

    }

    public void handleDelete() {

        albumsViewController.getUser().removeAlbum(album);
        curGrid.getChildren().remove(curStack);

    }

    public void setData(StackPane stack, TilePane grid, Album album, AlbumsViewController albumsViewController) {

        this.curStack = stack;
        this.curGrid = grid;
        this.album = album;
        this.albumsViewController = albumsViewController;
    }
}
