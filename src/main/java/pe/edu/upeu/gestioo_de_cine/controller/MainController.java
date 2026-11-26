package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;

import java.io.IOException;

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

    @FXML
    public void initialize(){
        mostrarSesion();
        aplicarPermisos();
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

    //MEJORA 3: abre el cierre de caja por rango de fechas
    @FXML
    private void showReportes(){
        if (sinPermiso()) return;
        loadView("reporte-view.fxml");
        marcarActivo(btnReportes);
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
            System.err.println("Error al cerrar sesion" + e.getMessage());
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
            System.err.println("Error al cargar los datos de la vista"+e.getMessage());
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error de base de datos");
            alert.setHeaderText("No se pudieron cargar los datos");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}
