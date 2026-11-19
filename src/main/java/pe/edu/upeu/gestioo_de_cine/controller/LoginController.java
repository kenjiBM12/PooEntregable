package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IUsuarioDao;
import pe.edu.upeu.gestioo_de_cine.modelo.UserSeccion;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    private static final UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();

    @FXML
    private void handleLogin() {
        String user = txtUser.getText().trim();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            Alert alert = utilAlerts.mostrarAlerts("Campos Vacíos",
                    "Por favor ingrese el usuario y la contraseña.", "WARNING");
            alert.showAndWait();
            return;
        }

        Usuario u = usuarioDao.login(user, pass);
        if (u != null) {
            String nombreCompleto = u.getNombre() + (u.getApellidos() != null ? " " + u.getApellidos() : "");
            String rol = u.getTipoUser() != null ? " (" + u.getTipoUser() + ")" : "";

            Alert alert = utilAlerts.mostrarAlerts("Acceso Concedido",
                    "¡Bienvenido, " + nombreCompleto + rol + "!", "INFORMATION");
            alert.showAndWait();

            // Guardar usuario en sesión
            UserSeccion.getInstance().setUserLogin(u);
            navigateToHome();
        } else {
            Alert alert = utilAlerts.mostrarAlerts("Acceso Denegado",
                    "Usuario o contraseña incorrectos.", "ERROR");
            alert.showAndWait();
        }
    }

    private void navigateToHome() {
        try {
            URL fxmlLocation = getClass().getResource("/pe/edu/upeu/gestioo_de_cine/views/main-view.fxml");
            if (fxmlLocation == null) {
                System.err.println("No se encontró el archivo FXML: /pe/edu/upeu/gestioo_de_cine/views/main-view.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) txtUser.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Sistema de Cine - Dashboard");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el home: " + e.getMessage());
            e.printStackTrace();
        }
    }
}