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
import pe.edu.upeu.gestioo_de_cine.utils.DatosDemo;
import pe.edu.upeu.gestioo_de_cine.utils.Log;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

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

    /**
     * MEJORA 6: limite de intentos fallidos.
     * Despues de 5 intentos con el mismo usuario, se bloquea el ingreso 60
     * segundos. Se guarda en memoria (se reinicia al cerrar el programa), que
     * es suficiente para una PC de boleteria y no ensucia la base.
     */
    private static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_MS = 60_000L;
    private static final Map<String, Integer> intentos = new HashMap<>();
    private static final Map<String, Long> bloqueos = new HashMap<>();

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

        //MEJORA 6: si el usuario esta bloqueado se avisa cuanto falta
        String llave = nombreUsuario.toLowerCase();
        Long hasta = bloqueos.get(llave);
        if (hasta != null && hasta > System.currentTimeMillis()) {
            long segundos = (hasta - System.currentTimeMillis()) / 1000 + 1;
            lblMensaje.setText("Demasiados intentos fallidos. Espere " + segundos + " s.");
            return;
        }

        //CORREGIDO: si la base de datos falla, la excepcion salia del evento
        //y la ventana de login quedaba trabada sin ningun aviso al usuario
        Usuario usuario;
        try {
            usuario = usuarioDao.login(nombreUsuario, clave);
        } catch (RuntimeException e) {
            Log.error("Error al validar el usuario", e);
            lblMensaje.setText("No se pudo conectar con la base de datos.");
            return;
        }
        if (usuario == null) {
            //MEJORA 9: los intentos fallidos tambien quedan en la auditoria
            Bitacora.registrar("LOGIN FALLIDO", "Intento con el usuario " + nombreUsuario);
            //MEJORA 6: se cuentan los intentos y se bloquea al llegar al limite
            int fallidos = intentos.getOrDefault(llave, 0) + 1;
            if (fallidos >= MAX_INTENTOS) {
                intentos.remove(llave);
                bloqueos.put(llave, System.currentTimeMillis() + BLOQUEO_MS);
                Bitacora.registrar("LOGIN BLOQUEADO",
                        "Usuario " + nombreUsuario + " bloqueado por intentos fallidos");
                lblMensaje.setText("Demasiados intentos fallidos. Espere 60 segundos.");
            } else {
                intentos.put(llave, fallidos);
                lblMensaje.setText("Usuario o contraseña incorrectos. Intento "
                        + fallidos + " de " + MAX_INTENTOS + ".");
            }
            txtClave.clear();
            return;
        }
        //ingreso correcto: se limpia el contador de ese usuario
        intentos.remove(llave);
        bloqueos.remove(llave);

        //MEJORA 9: se registra el ingreso correcto antes de abrir la ventana.
        //CORREGIDO: la sesion se inicia ANTES de escribir en la bitacora. Antes
        //se llamaba primero a Bitacora y Sesion todavia estaba vacia, asi que
        //la auditoria guardaba "sistema" en lugar del usuario que acababa de
        //entrar. Ahora Bitacora ya encuentra al usuario en Sesion.
        Sesion.iniciar(usuario);
        Bitacora.registrar("LOGIN", "Ingreso del usuario " + usuario.getUsuario());
        abrirPrincipal(usuario);
    }

    /**
     * MEJORA 15: modo demostracion.
     * Cambia la carpeta de datos a una base aparte (SistemaCineUPeU_demo),
     * carga datos de ejemplo y entra como administrador. La base real no se
     * toca: al cerrar y volver a abrir el programa se trabaja con la de siempre.
     */
    @FXML
    private void handleDemo() {
        try {
            java.io.File carpeta = DatosDemo.preparar();
            Usuario demo = usuarioDao.login("admin", "admin123");
            if (demo == null) {
                lblMensaje.setText("No se pudo preparar la demostración.");
                return;
            }
            Sesion.iniciar(demo);
            Bitacora.registrar("LOGIN DEMO", "Ingreso en modo demostración: " + carpeta);
            new UtilAlerts().mostrarAlerts("Modo demostración",
                    "Se abrió una base de ejemplo en:\n" + carpeta.getAbsolutePath()
                            + "\n\nLa base real no se modificó. Al cerrar el programa se vuelve a la normal.",
                    "INFORMATION").showAndWait();
            abrirPrincipal(demo);
        } catch (RuntimeException e) {
            Log.error("Error al preparar el modo demostración", e);
            lblMensaje.setText("No se pudo preparar el modo demostración.");
        }
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
