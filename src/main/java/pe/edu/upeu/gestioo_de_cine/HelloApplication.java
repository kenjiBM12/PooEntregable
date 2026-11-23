package pe.edu.upeu.gestioo_de_cine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class
                .getResource("/pe/edu/upeu/gestioo_de_cine/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sistema Cine!");
        stage.setScene(scene);
        stage.show();
    }

    //al cerrar la aplicación se cierra también la conexión con la base de datos
    @Override
    public void stop() {
        ConexionDB.cerrar();
    }

    public static void main(String[] args) {
        launch();
    }
}
