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
import pe.edu.upeu.gestioo_de_cine.modelo.UserSession;

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
    @FXML
    private Button btnReportes;

    private static final long LIMITE_INACTIVIDAD_MS = 15L * 60L * 1000L;
    private long ultimaActividad = System.currentTimeMillis();
    private final AtomicBoolean sesionExpirada = new AtomicBoolean(false);
    private Timeline relojInactividad;

    @FXML
    public void initialize(){
        mostrarSesion();
        aplicarPermisos();
        Platform.runLater(this::instalarAtajosYControlDeSesion);
        if (UserSession.esAdmin()){
            showHome();
        } else {
            loadView("venta-view.fxml");
            marcarActivo(btnVentas);
        }
    }

    private void mostrarSesion(){
        Usuario u = UserSession.getUsuario();
        if (u != null){
            lblUsuario.setText("Bienvenido, " + u.getNombre());
            lblRol.setText("Rol: " + u.getRol());
        }
    }

    private void aplicarPermisos(){
        boolean admin = UserSession.esAdmin();
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
        btnReportes.setVisible(admin);
        btnReportes.setManaged(admin);
        btnInicio.setVisible(admin);
        btnInicio.setManaged(admin);
    }

    private boolean sinPermiso(){
        if (UserSession.esAdmin()){
            return false;
        }
        loadView("venta-view.fxml");
        marcarActivo(btnVentas);
        return true;
    }

    private void marcarActivo(Button activo){
        Button[] botones = {btnInicio, btnClientes, btnPeliculas, btnSalas,
                btnFunciones, btnVentas, btnButacas, btnUsuarios, btnReportes};
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

    @FXML
    private void showReportes(){
        if (sinPermiso()) return;
        loadView("reporte-view.fxml");
        marcarActivo(btnReportes);
    }

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
        Alert aviso = new Alert(Alert.AlertType.WARNING);
        aviso.setTitle("Sesión cerrada");
        aviso.setHeaderText("La sesión se cerró por inactividad");
        aviso.setContentText("Pasaron más de 15 minutos sin uso.\nVuelva a iniciar sesión.");
        aviso.showAndWait();
        handleLogout();
    }

    @FXML
    private void handleLogout(){
        try {
            UserSession.cerrar();
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
            System.out.println("Error al cerrar sesión: " + e.getMessage());
        }
    }

    private void loadView(String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/"+fxml));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Error de carga de vista"+e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error al cargar los datos de la vista " + fxml
                    + ": " + e.getMessage());
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error de base de datos");
            alert.setHeaderText("No se pudieron cargar los datos");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

}
