package photos.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import photos.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PictureViewController implements Initializable {

    @FXML private Region pictureThumbnail;
    @FXML private AnchorPane photoLeft;
    @FXML private AnchorPane photoRight;
    @FXML private Label photoCounter;
    @FXML private Label photoDate;
    @FXML private Label albumName;
    @FXML private Label caption;
    @FXML private Label errorLabel;
    @FXML private TableView<Tag> tagTable;
    @FXML private TableColumn<Tag, String> keyColumn;
    @FXML private TableColumn<Tag, String> valueColumn;

    private static final int COPY = -2;
    private static final int MOVE = -1;

    private static final int CAPTIONLENGTH = 0;
    private static final int ONLYONEALBUM = 1;
    private static final int ALBUMHASPHOTO = 2;
    private static final int BADTAG = 3;
    private static final int DUPETAG = 4;
    private static final int DUPEALBUM = 5;
    private static final int ALBUMNAME = 6;

    private Album album;
    private User user;
    private int curPhoto;
    private ObservableList<Tag> tagList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {

            tagTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

            tagList = FXCollections.observableArrayList();

            tagTable.setItems(tagList);

            albumName.setText(album.getName());

            photoLeft.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                handleLeftPhoto();
            });

            photoRight.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                handleRightPhoto();
            });

            setPhoto(curPhoto);

        });
    }

    @FXML
    private void handleAddPhoto() {

        /* PHOTO ALREADY EXISTS ERROR */

        resetError();

        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.bmp", "*.gif");

        fileChooser.getExtensionFilters().add(imageFilter);

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {

            Instant i = Instant.ofEpochMilli(file.lastModified());
            ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.of("America/New_York"));

            album.addPhoto(new Photo(file.toString(), z));

            curPhoto = album.getPhotos().size() - 1;
            setPhoto(curPhoto);

        }
    }

    @FXML
    private void handleSaveAlbum() throws IOException {

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Create New Album");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Enter New Album Name:");

        Optional<String> albumNameInput = dialog.showAndWait();

        if (albumNameInput.isPresent()) {

            String albumName = albumNameInput.get().trim();

            if (albumName.length() > 30) {

                setError(ALBUMNAME);
                return;

            }

            if (!albumName.isEmpty() && !user.hasAlbum(albumName)) {

                album.setName(albumName);
                user.addAlbum(album);
                handleBack();

            } else {
                setError(DUPEALBUM);
            }
        }
    }

    @FXML
    private void handleRemovePhoto() {

        resetError();

        if(album.getPhotos().size() == 0) {
            return;
        }

        album.remove(curPhoto);

        if (album.getPhotos().size() == 0) {
            setNoPhotos();
        } else {
            handleLeftPhoto();
        }
    }

    @FXML
    private void handleEditCaption() {

        if (album.getPhotos().size() == 0) {
            return;
        }

        resetError();

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Edit Caption");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Enter Caption:");

        Optional<String> captionInput = dialog.showAndWait();

        if (captionInput.isPresent()) {

            String captionStr = captionInput.get().trim();

            if (captionStr.length() <= 430) {

                caption.setText(captionStr);
                album.getPhotos().get(curPhoto).setCaption(captionStr);

            } else {
                setError(CAPTIONLENGTH);
            }
        }
    }

    @FXML
    private void handleMove() {

        if (album.getPhotos().size() == 0) {
            return;
        }

        if (user.getAlbums().size() == 1) {
            setError(ONLYONEALBUM);
            return;
        }

        String otherAlbum = dialogMoveCopy(MOVE);
        if(otherAlbum == null) {
            return;
        }

        Photo photo = album.getPhotos().get(curPhoto);

        if (user.getAlbum(otherAlbum).hasPhoto(photo)) {
            setError(ALBUMHASPHOTO);
            return;
        }
        user.getAlbum(otherAlbum).addPhoto(photo);

        album.remove(photo);

        if (album.getPhotos().size() == 0) {
            setNoPhotos();
        } else {
            handleLeftPhoto();
        }
    }

    @FXML
    private void handleCopy() {

        resetError();

        if (album.getPhotos().size() == 0) {
            return;
        }

        if (user.getAlbums().size() == 1) {
            setError(ONLYONEALBUM);
            return;
        }

        String otherAlbum = dialogMoveCopy(COPY);
        if(otherAlbum == null) {
            return;
        }

        Photo photo = album.getPhotos().get(curPhoto);

        if (user.getAlbum(otherAlbum).hasPhoto(photo)) {
            setError(ALBUMHASPHOTO);
            return;
        }

        user.getAlbum(otherAlbum).addPhoto(photo);

    }

    @FXML
    private void handleAddTag() throws IOException {

        resetError();

        if (album.getPhotos().size() == 0) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/AddTagDialog.fxml"));
        dialog.setGraphic(null);
        dialog.setTitle("Add New Tag");

        DialogPane dialogPane = dialogLoader.load();
        dialogPane.setExpandableContent(null);

        dialog.setDialogPane(dialogPane);

        AddTagController controller = dialogLoader.getController();
        controller.setData();


        dialog.showAndWait().filter(ButtonType.OK::equals)
                .ifPresent(button -> {
                    Tag tag = controller.getData();
                    if (tag == null) {
                        setError(BADTAG);
                        return;
                    }
                    if (album.getPhotos().get(curPhoto).hasTag(tag)) {
                        setError(DUPETAG);
                        return;
                    }
                    tagTable.getItems().add(tag);
                    album.getPhotos().get(curPhoto).addTag(tag);
                });
    }

    @FXML
    private void handleRemoveTag() {

        resetError();

        Tag tag = tagTable.getSelectionModel().getSelectedItem();

        if(tag == null) {
            return;
        }

        tagTable.getItems().remove(tag);
        album.getPhotos().get(curPhoto).removeTag(tag);

    }

    @FXML
    private void handleBack() throws IOException {

        AppUsers.writeUsers();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AlbumsView.fxml"));
        Parent root = loader.load();

        AlbumsViewController albumsViewController = loader.getController();
        albumsViewController.setData(user);

        Stage stage = (Stage) pictureThumbnail.getScene().getWindow();
        stage.setScene(new Scene(root));

    }

    @FXML
    private void handleLogout() throws IOException {

        AppUsers.writeUsers();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) pictureThumbnail.getScene().getWindow();
        stage.setScene(new Scene(root));

    }

    private void handleLeftPhoto() {

        resetError();

        if (album.getPhotos().size() == 0) {
            return;
        }

        if (curPhoto == 0) {
            curPhoto = album.getPhotos().size() - 1;
        } else {
            curPhoto--;
        }

        setPhoto(curPhoto);

    }

    private void handleRightPhoto() {

        resetError();

        if (album.getPhotos().size() == 0) {
            return;
        }

        if (album.getPhotos().size() == 1) {
            return;
        }

        if (curPhoto == album.getPhotos().size() - 1) {
            curPhoto = 0;
        } else {
            curPhoto++;
        }

        setPhoto(curPhoto);

    }

    private void setPhoto(int num) {

        Image image;

        if (album.getPhotos().size() == 0) {
            setNoPhotos();
            return;
        }

        image = album.getPhotos().get(num).getThumbnail();

        if(image == null) {
            handleRemovePhoto();
            return;
        }

        pictureThumbnail.setBackground(new Background(getBackground(image)));

        photoCounter.setText((curPhoto + 1) + "/" + album.getPhotos().size());
        photoDate.setText(album.getPhotos().get(curPhoto).getDate());
        caption.setText(album.getPhotos().get(curPhoto).getCaption());

        tagList.clear();
        tagTable.getItems().addAll(album.getPhotos().get(curPhoto).getTags());
    }

    private void setNoPhotos() {

        Image image = new Image("file:src/main/resources/ImagesFXML/NoImageIconPhotos.png", true);
        photoCounter.setText("0/0");
        photoDate.setText("");
        pictureThumbnail.setBackground(new Background(getBackground(image)));
        caption.setText("");

    }

    private BackgroundImage getBackground(Image img) {

        return new BackgroundImage(
                img,                                                 // image
                BackgroundRepeat.NO_REPEAT,                            // repeatX
                BackgroundRepeat.NO_REPEAT,                            // repeatY
                BackgroundPosition.CENTER,                             // position
                new BackgroundSize(-1, -1, false, false, false, true)  // size
        );

    }

    private String dialogMoveCopy(int choice) {

        ArrayList<String> albumNames = user.getAlbums()
                .stream().map(Album::getName)
                .collect(Collectors
                        .toCollection(ArrayList::new));

        albumNames.remove(album.getName());

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>();
        choiceDialog.getItems().addAll(albumNames);

        choiceDialog.setHeaderText(null);
        choiceDialog.setGraphic(null);

        if (choice == MOVE) {

            choiceDialog.setTitle("Move Photo");
            choiceDialog.setContentText("Choose an album:");

        } else if (choice == COPY) {

            choiceDialog.setTitle("Copy Photo");
            choiceDialog.setContentText("Choose an album:");

        }

        Optional<String> albumName = choiceDialog.showAndWait();

        return albumName.orElse(null);
    }

    private void setError(int errorCode)
    {
        errorLabel.setVisible(true);

        switch (errorCode) {
            case CAPTIONLENGTH -> errorLabel.setText("Invalid Caption : Cannot exceed 430 characters");
            case ONLYONEALBUM -> errorLabel.setText("Move/Copy Error : No other albums to move/copy to");
            case ALBUMHASPHOTO -> errorLabel.setText("Invalid Album : Already contains photo");
            case BADTAG -> errorLabel.setText("Invalid Tag Input : Key and/or value is empty");
            case DUPETAG -> errorLabel.setText("Invalid Tag Input : Duplicate tag");
            case DUPEALBUM -> errorLabel.setText("Invalid Album Name: Already have an album with entered name");
            case ALBUMNAME -> errorLabel.setText("Invalid Album Name: Exceeds 30 characters or name is empty");
        }
    }

    private void resetError() {
        errorLabel.setVisible(false);
    }

    public void setData(Album album, User user) {
        this.album = album;
        this.user = user;
        this.curPhoto = 0;
    }
}
