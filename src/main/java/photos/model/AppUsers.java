package photos.model;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class AppUsers {

    public static ArrayList<User> users = new ArrayList<>();

    public static boolean hasUser(String username) {
        for(User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        } return false;
    }

    public static User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void addUser(String username) {
        User newUser = new User(username);
        users.add(newUser);
    }

    public static void removeUser(User user) {
        users.remove(user);
    }

    public static void removeUser(String username) {
        removeUser(getUser(username));
    }

    public static void readUsers() throws IOException, ClassNotFoundException {

        File f = new File("data/AppUsers.dat");

        if (f.length() == 0) {
            createStock();
            writeUsers();
            return;
        }

        ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("data/AppUsers.dat"));
        users = (ArrayList<User>) ois.readObject();
        System.out.println("Read all users");
    }

    public static void writeUsers() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("data/AppUsers.dat"));
        oos.writeObject(users);
    }

    public static void printUsers() {
        for(User user : users) {
            System.out.println(user.getUsername());
        }
    }

    public static void createStock() {

        User user = new User("stock");
        users.add(user);

        Album album = new Album("Stock");
        user.addAlbum(album);

        File folder = new File("src/main/Stock");

        for (String fileName : Objects.requireNonNull(folder.list())) {

            String path = "src/main/Stock/" + fileName;

            File file = new File(path);

            Instant i = Instant.ofEpochMilli(file.lastModified());
            ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.of("America/New_York"));

            Photo photo = new Photo(path, z);
            album.addPhoto(photo);

        }

    }

}
