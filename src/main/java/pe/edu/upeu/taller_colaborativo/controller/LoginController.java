package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.taller_colaborativo.utils.UtilAlerts;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtClave;

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();

        // Primera validacion: que no deje los campos vacios
        if (usuario.isEmpty() || clave.isEmpty()) {
            Alert alert = UtilAlerts.mostrarAlerts("Campos Vacios",
                    "porfavor ingrese el usuario y la clave", "WARNING");
            alert.showAndWait();
            return;
        }

        // Aqui se valida el usuario contra la base de datos.
        // Cuando el equipo tenga listo UsuarioDaoImpl se cambia por:
        // Usuario u = usuarioDao.login(usuario, clave);
        Alert alert = UtilAlerts.mostrarAlerts("Acceso Concedido",
                "¡Bienvenido, " + usuario + "!", "INFORMATION");
        alert.showAndWait();

        abrirMenuPrincipal(usuario);
    }

    private void abrirMenuPrincipal(String usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/taller_colaborativo/views/main-view.fxml"));
            Parent root = loader.load();

            // Le pasamos el nombre del usuario a la cabecera del menu
            MainController controlador = loader.getController();
            controlador.setUsuario(usuario);

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Cine UPeU - Venta de entradas");
            stage.show();
        } catch (IOException e) {
            System.err.println("No se pudo abrir el menu principal: " + e.getMessage());
        }
    }
}
