package photos.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private ArrayList<Album> albums;

    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public boolean hasAlbum(String username) {
        for (Album album : albums) {
            if (album.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public Album getAlbum(String albumName) {
        for(Album album : albums) {
            if(album.getName().equals(albumName)) {
                return album;
            }
        }
        return null;
    }
}
