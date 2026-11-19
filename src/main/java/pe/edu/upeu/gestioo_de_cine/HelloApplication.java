package pe.edu.upeu.gestioo_de_cine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL fxmlUrl = HelloApplication.class
                .getResource("/pe/edu/upeu/gestioo_de_cine/views/Login.view.fxml");
        if (fxmlUrl == null) {
            throw new IOException("No se encontró Login.view.fxml en views");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sistema Cine!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}