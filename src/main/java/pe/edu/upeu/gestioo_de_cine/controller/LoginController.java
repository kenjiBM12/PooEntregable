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
import pe.edu.upeu.gestioo_de_cine.utils.Bitacora;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtClave;
    @FXML
    private Label lblMensaje;

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();

    @FXML
    private void handleLogin() {
        String nombreUsuario = txtUsuario.getText().trim();
        String clave = txtClave.getText().trim();
        lblMensaje.setText("");

        //no se permiten campos vacios
        if (nombreUsuario.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("Complete usuario y contraseña.");
            return;
        }

        //CORREGIDO: si la base de datos falla, la excepcion salia del evento
        //y la ventana de login quedaba trabada sin ningun aviso al usuario
        Usuario usuario;
        try {
            usuario = usuarioDao.login(nombreUsuario, clave);
        } catch (RuntimeException e) {
            System.out.println("Error al validar el usuario: " + e.getMessage());
            lblMensaje.setText("No se pudo conectar con la base de datos.");
            return;
        }
        if (usuario == null) {
            //MEJORA 9: los intentos fallidos tambien quedan en la auditoria
            Bitacora.registrar("LOGIN FALLIDO", "Intento con el usuario " + nombreUsuario);
            lblMensaje.setText("Usuario o contraseña incorrectos.");
            txtClave.clear();
            return;
        }

        //MEJORA 9: se registra el ingreso correcto antes de abrir la ventana.
        //CORREGIDO: la sesion se inicia ANTES de escribir en la bitacora. Antes
        //se llamaba primero a Bitacora y Sesion todavia estaba vacia, asi que
        //la auditoria guardaba "sistema" en lugar del usuario que acababa de
        //entrar. Ahora Bitacora ya encuentra al usuario en Sesion.
        Sesion.iniciar(usuario);
        Bitacora.registrar("LOGIN", "Ingreso del usuario " + usuario.getUsuario());
        abrirPrincipal(usuario);
    }

    private void abrirPrincipal(Usuario usuario) {
        try {
            //la sesion ya quedo iniciada en handleLogin, antes de la auditoria
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sistema Cine - " + usuario.getNombre());
            stage.setScene(new Scene(root));
            stage.show();
            //se cierra la ventana del login
            Stage login = (Stage) txtUsuario.getScene().getWindow();
            login.close();
        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("No se pudo abrir la ventana principal.");
        }
    }
}
