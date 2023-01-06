package photos.model;

import javafx.scene.image.Image;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Album implements Serializable {

    private String name;
    private ArrayList<Photo> photos;
    private ZonedDateTime earliest;
    private ZonedDateTime latest;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
        this.earliest = null;
        this.latest = null;
    }

    public void addPhoto(Photo photo) {

        photos.add(photo);

        if (earliest == null) {
            earliest = photo.getTime();
            latest = photo.getTime();
            return;
        }

        if (earliest.isAfter(photo.getTime())) {
            earliest = photo.getTime();
        } else if (latest.isBefore(photo.getTime())) {
            latest = photo.getTime();
        }

    }



    public String getDateRange() {

        if (earliest == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, YYYY");
        String start = earliest.format(formatter);
        String end = latest.format(formatter);
        return start + " - " + end;

    }

    public String getName() {
        return name;
    }

    public Image getAlbumThumbnail() {
        if (photos.size() == 0) {
            return new Image("file:src/main/resources/ImagesFXML/NoImageIcon.png", true);
        }
        return new Image("file:" + photos.get(0).getPath(), 292, 0, true, true);
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void resetDates() {

        if (photos.size() == 0) {
            earliest = null;
            latest = null;
            return;
        }

        earliest = photos.get(0).getTime();
        latest = photos.get(0).getTime();

        for (Photo photo : photos) {
            if (earliest.isAfter(photo.getTime())) {
                earliest = photo.getTime();
            } else if (latest.isBefore(photo.getTime())) {
                latest = photo.getTime();
            }
        }
    }

    public boolean hasPhoto(Photo p) {

        String path = p.getPath();

        for(Photo photo : photos) {
            if (photo.getPath().equals(path)) {
                return true;
            }
        }
        return false;
    }

    public void remove(Photo photo) {
        photos.remove(photo);
        resetDates();
    }

    public void remove(int p) {
        photos.remove(p);
        resetDates();
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
        resetDates();
    }

}
