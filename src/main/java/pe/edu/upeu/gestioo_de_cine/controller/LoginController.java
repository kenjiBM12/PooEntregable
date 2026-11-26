package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IUsuarioDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.modelo.UserSession;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtClave;
    @FXML
    private Label lblMensaje;

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();

    private static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_MS = 60_000L;
    private static final Map<String, Integer> intentos = new HashMap<>();
    private static final Map<String, Long> bloqueos = new HashMap<>();

    @FXML
    private void handleLogin() {
        String nombreUsuario = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();
        lblMensaje.setText("");

        if (nombreUsuario.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("Complete usuario y contraseña.");
            return;
        }

        String llave = nombreUsuario.toLowerCase();
        Long hasta = bloqueos.get(llave);
        if (hasta != null && hasta > System.currentTimeMillis()) {
            long segundos = (hasta - System.currentTimeMillis()) / 1000 + 1;
            lblMensaje.setText("Demasiados intentos fallidos. Espere " + segundos + " s.");
            return;
        }

        Usuario usuario;
        try {
            usuario = usuarioDao.login(nombreUsuario, clave);
        } catch (RuntimeException e) {
            System.out.println("Error al validar el usuario: " + e.getMessage());
            lblMensaje.setText("No se pudo conectar con la base de datos.");
            return;
        }
        if (usuario == null) {
            int fallidos = intentos.getOrDefault(llave, 0) + 1;
            if (fallidos >= MAX_INTENTOS) {
                intentos.remove(llave);
                bloqueos.put(llave, System.currentTimeMillis() + BLOQUEO_MS);
                lblMensaje.setText("Demasiados intentos fallidos. Espere 60 segundos.");
            } else {
                intentos.put(llave, fallidos);
                lblMensaje.setText("Usuario o contraseña incorrectos. Intento "
                        + fallidos + " de " + MAX_INTENTOS + ".");
            }
            txtClave.clear();
            return;
        }
        intentos.remove(llave);
        bloqueos.remove(llave);

        UserSession.iniciar(usuario);
        abrirPrincipal(usuario);
    }

    private void abrirPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sistema Cine - " + usuario.getNombre());
            stage.setScene(new Scene(root));
            stage.show();
            Stage login = (Stage) txtUsuario.getScene().getWindow();
            login.close();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("No se pudo abrir la ventana principal.");
        }
    }
}
