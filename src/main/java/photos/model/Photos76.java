package photos.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Photos76 extends Application {

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {

        AppUsers.readUsers();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Photos76");
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest(e -> {
            e.consume();
            try {
                closeProgram(stage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void closeProgram(Stage stage) throws IOException {
        AppUsers.writeUsers();
        stage.close();
    }

}