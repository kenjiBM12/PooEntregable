package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.utils.Bitacora;
import pe.edu.upeu.gestioo_de_cine.utils.Log;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblRol;
    @FXML
    private Button btnInicio;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnPeliculas;
    @FXML
    private Button btnSalas;
    @FXML
    private Button btnFunciones;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnButacas;
    @FXML
    private Button btnUsuarios;
    //MEJORA 3: boton para la pantalla de reportes / cierre de caja
    @FXML
    private Button btnReportes;
    //MEJORA 9: boton para la pantalla de auditoria
    @FXML
    private Button btnAuditoria;

    //MEJORA 6: la sesion se cierra sola si la PC queda sin uso
    private static final long LIMITE_INACTIVIDAD_MS = 15L * 60L * 1000L;
    private long ultimaActividad = System.currentTimeMillis();
    private final AtomicBoolean sesionExpirada = new AtomicBoolean(false);
    private Timeline relojInactividad;

    @FXML
    public void initialize(){
        mostrarSesion();
        aplicarPermisos();
        //los atajos y el control de inactividad necesitan la escena ya montada
        Platform.runLater(this::instalarAtajosYControlDeSesion);
        if (Sesion.esAdmin()){
            showHome();
        } else {
            //el rol empleado trabaja con la venta de entradas
            loadView("venta-view.fxml");
            marcarActivo(btnVentas);
        }
    }

    //muestra arriba el nombre y el rol de quien esta conectado
    private void mostrarSesion(){
        Usuario u = Sesion.getUsuario();
        if (u != null){
            lblUsuario.setText("Bienvenido, " + u.getNombre());
            lblRol.setText("Rol: " + u.getRol());
        }
    }

    //el admin ve todo, el empleado solo ve ventas y butacas
    private void aplicarPermisos(){
        boolean admin = Sesion.esAdmin();
        btnUsuarios.setVisible(admin);
        btnUsuarios.setManaged(admin);
        btnClientes.setVisible(admin);
        btnClientes.setManaged(admin);
        btnPeliculas.setVisible(admin);
        btnPeliculas.setManaged(admin);
        btnSalas.setVisible(admin);
        btnSalas.setManaged(admin);
        btnFunciones.setVisible(admin);
        btnFunciones.setManaged(admin);
        //los reportes muestran dinero: solo el admin los ve
        btnReportes.setVisible(admin);
        btnReportes.setManaged(admin);
        //la auditoria muestra quien hizo cada movimiento: solo el admin
        btnAuditoria.setVisible(admin);
        btnAuditoria.setManaged(admin);
        //el empleado solo ve ventas, por eso tampoco tiene Inicio
        btnInicio.setVisible(admin);
        btnInicio.setManaged(admin);
    }

    //devuelve true si el usuario no puede abrir esa vista; deja la venta abierta
    private boolean sinPermiso(){
        if (Sesion.esAdmin()){
            return false;
        }
        loadView("venta-view.fxml");
        marcarActivo(btnVentas);
        return true;
    }

    //resalta en el menu la pantalla que se esta viendo
    private void marcarActivo(Button activo){
        Button[] botones = {btnInicio, btnClientes, btnPeliculas, btnSalas,
                btnFunciones, btnVentas, btnButacas, btnUsuarios, btnReportes, btnAuditoria};
        for (Button b : botones){
            if (b != null){
                b.getStyleClass().remove("nav-button-activo");
            }
        }
        if (activo != null && !activo.getStyleClass().contains("nav-button-activo")){
            activo.getStyleClass().add("nav-button-activo");
        }
    }

    @FXML
    private  void showHome(){
        if (sinPermiso()) return;
        loadView("home-view.fxml");
        marcarActivo(btnInicio);
    }
    @FXML
    private void showCliente(){
        if (sinPermiso()) return;
        loadView("cliente-view.fxml");
        marcarActivo(btnClientes);
    }
    @FXML
    private void showPeli(){
        if (sinPermiso()) return;
        loadView("pelicula-view.fxml");
        marcarActivo(btnPeliculas);
    }
    @FXML
    private void showSal(){
        if (sinPermiso()) return;
        loadView("sala-view.fxml");
        marcarActivo(btnSalas);
    }
    @FXML
    private void showFuncion(){
        if (sinPermiso()) return;
        loadView("funcion-view.fxml");
        marcarActivo(btnFunciones);
    }
    @FXML
    private void showVenta(){
        loadView("venta-view.fxml");
        marcarActivo(btnVentas);
    }
    @FXML
    private void showButacas(){
        loadView("butaca-view.fxml");
        marcarActivo(btnButacas);
    }
    @FXML
    private void showUsuarios(){
        if (sinPermiso()) return;
        loadView("usuario-view.fxml");
        marcarActivo(btnUsuarios);
    }

    //MEJORA 3: abre el cierre de caja por rango de fechas
    @FXML
    private void showReportes(){
        if (sinPermiso()) return;
        loadView("reporte-view.fxml");
        marcarActivo(btnReportes);
    }

    //MEJORA 9: abre la bitacora de movimientos del sistema
    @FXML
    private void showAuditoria(){
        if (sinPermiso()) return;
        loadView("auditoria-view.fxml");
        marcarActivo(btnAuditoria);
    }

    /**
     * MEJORA 6 y MEJORA 12:
     *  - reinicia el contador de inactividad con cada tecla o movimiento de mouse;
     *  - cierra la sesion a los 15 minutos sin uso (boleteria compartida);
     *  - atajos: F1 Inicio, F2 Ventas, F3 Funciones, F4 Reportes.
     * Si la sesion se cierra sola se avisa antes y se registra en la auditoria.
     */
    private void instalarAtajosYControlDeSesion() {
        Scene scene = contentArea.getScene();
        if (scene == null) {
            return;
        }
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_MOVED,
                e -> ultimaActividad = System.currentTimeMillis());
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                e -> ultimaActividad = System.currentTimeMillis());
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED,
                e -> ultimaActividad = System.currentTimeMillis());

        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F1, KeyCombination.SHORTCUT_DOWN), this::showHome);
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F2, KeyCombination.SHORTCUT_DOWN), this::showVenta);
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F3, KeyCombination.SHORTCUT_DOWN), this::showFuncion);
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F4, KeyCombination.SHORTCUT_DOWN), this::showReportes);

        relojInactividad = new Timeline(new KeyFrame(Duration.seconds(30), e -> revisarInactividad()));
        relojInactividad.setCycleCount(Timeline.INDEFINITE);
        relojInactividad.play();
    }

    private void revisarInactividad() {
        long inactivo = System.currentTimeMillis() - ultimaActividad;
        if (inactivo < LIMITE_INACTIVIDAD_MS || !sesionExpirada.compareAndSet(false, true)) {
            return;
        }
        if (relojInactividad != null) {
            relojInactividad.stop();
        }
        Bitacora.registrar("SESION", "Sesión cerrada por inactividad");
        Alert aviso = new Alert(Alert.AlertType.WARNING);
        aviso.setTitle("Sesión cerrada");
        aviso.setHeaderText("La sesión se cerró por inactividad");
        aviso.setContentText("Pasaron más de 15 minutos sin uso.\nVuelva a iniciar sesión.");
        aviso.showAndWait();
        handleLogout();
    }

    //cierra la sesion y vuelve al login
    @FXML
    private void handleLogout(){
        try {
            Sesion.cerrar();
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sistema Cine - Login");
            stage.setScene(new Scene(root));
            stage.show();
            Stage actual = (Stage) contentArea.getScene().getWindow();
            actual.close();
        } catch (IOException e) {
            Log.error("Error al cerrar sesión", e);
        }
    }

    //metodo que llama y busca a las vistas
    private void loadView(String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/"+fxml));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Error de carga de vista"+e.getMessage());
        } catch (RuntimeException e) {
            //aqui caen los errores de base de datos, se avisa en pantalla que fallo
            Log.error("Error al cargar los datos de la vista " + fxml, e);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error de base de datos");
            alert.setHeaderText("No se pudieron cargar los datos");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}
