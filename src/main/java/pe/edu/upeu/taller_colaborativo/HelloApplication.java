package pe.edu.upeu.taller_colaborativo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class
                .getResource("/pe/edu/upeu/taller_colaborativo/views/login-view.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Cine UPeU - Iniciar sesión");
        stage.setScene(scene);
        stage.show();
    }
}
