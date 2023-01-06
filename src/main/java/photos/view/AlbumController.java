package photos.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import photos.model.Album;

public class AlbumController {

    @FXML private Label albumDateRange;
    @FXML private Region albumThumbnail;
    @FXML private Label numOfPhotos;
    @FXML public Label albumName;

    public Album album;

    public void setData(Album album) {

        this.album = album;

        albumDateRange.setText(album.getDateRange());
        albumName.setText(album.getName());
        numOfPhotos.setText(album.getPhotos().size() + " photos");

        BackgroundImage bgImage = new BackgroundImage(
                album.getAlbumThumbnail(),                                                 // image
                BackgroundRepeat.NO_REPEAT,                            // repeatX
                BackgroundRepeat.NO_REPEAT,                            // repeatY
                BackgroundPosition.CENTER,                             // position
                new BackgroundSize(-1, -1, false, false, false, true)  // size
        );

        albumThumbnail.setBackground(new Background(bgImage));

    }
}
