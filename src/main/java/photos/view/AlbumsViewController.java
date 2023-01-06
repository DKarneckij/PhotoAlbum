package photos.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import photos.model.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class AlbumsViewController implements Initializable {

    private static final int ALBUMNAME = 0;
    private static final int DUPEALBUM = 1;
    private static final int BADTAGSEARCH = 2;
    private static final int NOSEARCHRESULT = 3;
    private static final int DATESBAD = 4;

    @FXML private TilePane grid;
    @FXML private Button logoutBtn;
    @FXML private Label errorLabel;
    @FXML private AnchorPane addButton;

    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {

            grid.setMinWidth(Region.USE_COMPUTED_SIZE);
            grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
            grid.setMaxWidth(Region.USE_PREF_SIZE);

            grid.setMinHeight(Region.USE_COMPUTED_SIZE);
            grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
            grid.setMaxHeight(Region.USE_PREF_SIZE);

            addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                try {
                    addNewAlbum();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            try {
                for (Album album : user.getAlbums()) {

                    StackPane stack = createAlbumStack(album);

                    grid.getChildren().add(stack);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addNewAlbum() throws IOException {

        resetError();

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Create New Album");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Enter Album Name:");

        Optional<String> albumNameInput = dialog.showAndWait();

        if (albumNameInput.isPresent()) {

            String albumName = albumNameInput.get();

            if (albumName.length() > 40) {
                setError(ALBUMNAME);
                return;
            }

            if (!albumName.isEmpty() && !user.hasAlbum(albumName)) {

                Album newAlbum = new Album((albumName));
                user.getAlbums().add(0,newAlbum);

                grid.getChildren().add(0, createAlbumStack(newAlbum));

            } else {

                setError(DUPEALBUM);

            }
        }
    }

    @FXML
    private void handleTagSearch() throws IOException {

        resetError();

        if (user.getAlbums().size() == 0) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setGraphic(null);
        dialog.setTitle("Search for Tag/s");

        FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/TagSearchDialog.fxml"));
        DialogPane dialogPane = dialogLoader.load();

        dialogPane.setExpandableContent(null);
        dialog.setDialogPane(dialogPane);

        TagSearchController controller = dialogLoader.getController();

        dialog.showAndWait().filter(ButtonType.OK::equals)
                .ifPresent(button -> {

                    ArrayList<Tag> tags = controller.getData();

                    if (tags == null) {
                        setError(BADTAGSEARCH);
                        return;
                    }

                    String choice = controller.getChoiceBox();

                    ArrayList<Photo> photoList = new ArrayList<>();

                    if (choice == null || choice.equals("OR")) {
                        for(Album album : user.getAlbums()) {
                            for(Photo photo : album.getPhotos()) {
                                for(Tag photoTag : photo.getTags()) {
                                    for(Tag tag : tags) {
                                        if(tag.equals(photoTag) && !photoList.contains(photo)) {
                                            photoList.add(photo);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for(Album album : user.getAlbums()) {
                            for(Photo photo : album.getPhotos()) {
                                if (!photoList.contains(photo)) {
                                    Tag firstTag = tags.get(0);
                                    Tag secondTag = tags.get(1);
                                    for(Tag photoTag : photo.getTags()) {
                                        if (firstTag.equals(photoTag)) {
                                            for (Tag photoTagg: photo.getTags()) {
                                                if (secondTag.equals(photoTagg)) {
                                                    photoList.add(photo);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (photoList.size() == 0) {
                        setError(NOSEARCHRESULT);
                        return;
                    }

                    Album album = new Album("Search Results");
                    album.setPhotos(photoList);

                    try {
                        switchSearchView(album);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
    }

    @FXML
    private void handleDateSearch() throws IOException {
        resetError();

        if (user.getAlbums().size() == 0) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setGraphic(null);
        dialog.setTitle("Search for Date/s");

        FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/DateSearchDialog.fxml"));
        DialogPane dialogPane = dialogLoader.load();

        dialogPane.setExpandableContent(null);
        dialog.setDialogPane(dialogPane);

        DateSearchController controller = dialogLoader.getController();

        dialog.showAndWait().filter(ButtonType.OK::equals)
                .ifPresent(button -> {

                    ArrayList<ZonedDateTime> dates = controller.getData();

                    if (dates == null) {
                        setError(DATESBAD);
                        return;
                    }

                    ZonedDateTime start = dates.get(0);
                    ZonedDateTime end = dates.get(1);

                    ArrayList<Photo> photoList = new ArrayList<>();

                    for (Album album : user.getAlbums()) {
                        for(Photo photo : album.getPhotos()) {
                            if(!photoList.contains(photo)) {
                                if (start.isBefore(photo.getTime()) && end.isAfter(photo.getTime())) {
                                    photoList.add(photo);
                                }
                            }
                        }
                    }

                    if (photoList.size() == 0) {
                        setError(NOSEARCHRESULT);
                        return;
                    }

                    Album album = new Album("Search Results");
                    album.setPhotos(photoList);

                    try {
                        switchSearchView(album);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
    }

    private void switchSearchView(Album album) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PictureView.fxml"));
        Parent root = loader.load();
        root.lookup("#removeBtn").setVisible(false);
        root.lookup("#moveBtn").setVisible(false);
        root.lookup("#copyBtn").setVisible(false);
        root.lookup("#addPhotoBtn").setVisible(false);
        root.lookup("#saveAlbumBtn").setVisible(true);

        PictureViewController pictureViewController = loader.getController();
        pictureViewController.setData(album, user);

        Stage stage = (Stage) grid.getScene().getWindow();
        stage.setScene(new Scene(root));

        AppUsers.writeUsers();

    }

    @FXML
    private void handleLogout() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.setScene(new Scene(root));

        AppUsers.writeUsers();
    }

    private StackPane createAlbumStack(Album album) throws IOException {

        StackPane stack = new StackPane();

        FXMLLoader albumLoader = new FXMLLoader(getClass().getResource("/Album.fxml"));
        AnchorPane anchorPane = albumLoader.load();
        AlbumController albumController = albumLoader.getController();
        albumController.setData(album);

        FXMLLoader hoverLoader = new FXMLLoader(getClass().getResource("/Hover.fxml"));
        AnchorPane anchorPaneHover = hoverLoader.load();
        HoverController hoverController = hoverLoader.getController();
        hoverController.setData(stack, grid, album, this);
        anchorPaneHover.setVisible(false);


        stack.getChildren().add(anchorPane);
        stack.getChildren().add(anchorPaneHover);
        stack.setAlignment(Pos.BOTTOM_LEFT);

        stack.hoverProperty().addListener((observable, oldValue, newValue) -> {
            anchorPaneHover.setVisible(newValue);
        });

        return stack;
    }

    public User getUser() {
        return user;
    }

    public void setError(int error) {

        errorLabel.setVisible(true);

        switch(error) {
            case ALBUMNAME -> errorLabel.setText("Invalid Album Name: Exceeds 40 characters");
            case DUPEALBUM -> errorLabel.setText("Invalid Album Name: User already has album with the same name");
            case BADTAGSEARCH -> errorLabel.setText("Invalid Tag Search: Input doesn't match required criteria");
            case NOSEARCHRESULT -> errorLabel.setText("Invalid Search: No photos match search criteria");
            case DATESBAD -> errorLabel.setText("Invalid Search: Incorrect date input");
        }
    }

    private void resetError() {
        errorLabel.setVisible(false);
    }

    public void setData(User user) {
        this.user = user;
    }

}
