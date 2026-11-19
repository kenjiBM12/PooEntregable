package pe.edu.upeu.taller_colaborativo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class
                .getResource("/pe/edu/upeu/taller_colaborativo/views/login-view.fxml"));
        Scene scene = new Scene(loader.load());
        // Asignación de ícono a la ventana principal
        stage.getIcons().add(new Image(getClass().getResourceAsStream(
                "/pe/edu/upeu/taller_colaborativo/img/logo.png")));
        stage.setTitle("Cine UPeU - Iniciar sesión");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
