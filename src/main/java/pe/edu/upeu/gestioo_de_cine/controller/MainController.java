package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Label lblUsuario;

    @FXML
    public void initialize(){

        showHome();
    }
    @FXML
    private  void showHome(){
        loadView("home-view.fxml");
    }
    @FXML
    private void showCliente(){
        loadView("cliente-view.fxml");
    }
    @FXML
    private void showPeli(){
        loadView("pelicula-view.fxml");
    }
    @FXML
    private void showSal(){
        loadView("sala-view.fxml");
    }
    @FXML
    private void showFuncion(){
        loadView("funcion-view.fxml");
    }
    @FXML
    private void showVenta(){
        loadView("venta-view.fxml");
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
        }
    }


}
