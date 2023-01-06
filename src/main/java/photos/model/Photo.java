package photos.model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Photo implements Serializable {

    private final String path;
    private final ZonedDateTime z;
    private String caption;
    private ArrayList<Tag> tags;

    public Photo(String path, ZonedDateTime z) {
        this.path = path;
        this.z = z;
        this.caption = "";
        this.tags = new ArrayList<>();
    }

    public Image getThumbnail() {

        File file = new File(path);

        if(file.exists()) {
            return new Image("file:" + path, 400, 0, true, true);
        }
        return null;
    }

    public ZonedDateTime getTime() {
        return z;
    }

    public String getPath() {
        return path;
    }

    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, YYYY");
        return z.format(formatter);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public boolean hasTag(Tag t) {

        if (t.getKey().equals("location") || t.getKey().equals("season")) {
            for (Tag tag : tags) {
                if (tag.getKey().equals(t.getKey())) {
                    return true;
                }
            }
        }

        for(Tag tag : tags) {
            if(tag.getKey().equals(t.getKey()) && tag.getValue().equals(t.getValue())) {
                return true;
            }
        }

        return false;
    }

    public void removeTag(Tag t) {
        tags.remove(t);
    }
}
