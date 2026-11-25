package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IUsuarioDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Usuario> tableUsuario;
    @FXML
    private TableColumn<Usuario, Number> ColNumero;
    @FXML
    private TableColumn<Usuario, String> ColNombre;
    @FXML
    private TableColumn<Usuario, String> ColUsuario;
    @FXML
    private TableColumn<Usuario, String> ColRol;
    @FXML
    private TableColumn<Usuario, String> ColEstado;

    @FXML
    private TextField txtBuscar;

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();
    private ObservableList<Usuario> listaUsuarios;
    private List<Usuario> todosLosUsuarios = new ArrayList<>();

    @FXML
    public void initialize() {
        ColNumero.setCellFactory(colum -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        ColNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        ColUsuario.setCellValueFactory(cellData -> cellData.getValue().usuarioProperty());
        ColRol.setCellValueFactory(cellData -> cellData.getValue().rolProperty());
        ColEstado.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getActivo() == 1 ? "Activo" : "Desactivado"));
        cargarDatos();
    }

    private void cargarDatos() {
        todosLosUsuarios = usuarioDao.listAll();
        listaUsuarios = FXCollections.observableArrayList();
        tableUsuario.setItems(listaUsuarios);
        filtrar();
    }

    @FXML
    private void handleBuscar() {
        filtrar();
    }

    //filtra por nombre, nombre de acceso y rol con un for normal
    private void filtrar() {
        String texto = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        listaUsuarios.clear();
        for (Usuario u : todosLosUsuarios) {
            if (texto.isEmpty() || contiene(u.getNombre(), texto)
                    || contiene(u.getUsuario(), texto) || contiene(u.getRol(), texto)) {
                listaUsuarios.add(u);
            }
        }
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    @FXML
    private void handleNew() {
        if (!Sesion.esAdmin()) {
            utilAlert.mostrarAlerts("Sin permiso",
                    "Solo el administrador puede registrar cuentas.", "WARNING").showAndWait();
            return;
        }
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        if (!Sesion.esAdmin()) {
            utilAlert.mostrarAlerts("Sin permiso",
                    "Solo el administrador puede editar cuentas.", "WARNING").showAndWait();
            return;
        }
        Usuario seleccionado = tableUsuario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un usuario de la tabla para editar.", "WARNING").showAndWait();
            return;
        }
        showForm(seleccionado);
    }

    //desactiva un empleado sin borrarlo, y lo puede volver a activar
    @FXML
    private void handleCambiarEstado() {
        if (!Sesion.esAdmin()) {
            utilAlert.mostrarAlerts("Sin permiso",
                    "Solo el administrador puede activar o desactivar cuentas.", "WARNING").showAndWait();
            return;
        }
        Usuario seleccionado = tableUsuario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un usuario de la tabla.", "WARNING").showAndWait();
            return;
        }
        if (Sesion.getUsuario() != null
                && seleccionado.getIdusuario() == Sesion.getUsuario().getIdusuario()) {
            utilAlert.mostrarAlerts("No permitido",
                    "No puede desactivar la cuenta con la que esta conectado.", "WARNING").showAndWait();
            return;
        }
        int nuevoEstado = seleccionado.getActivo() == 1 ? 0 : 1;
        Alert confirm = utilAlert.mostrarAlerts("Confirmar cambio",
                (nuevoEstado == 1 ? "Activar" : "Desactivar") + " al usuario "
                        + seleccionado.getUsuario() + "?", "CONFIRMATION");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int estadoAnterior = seleccionado.getActivo();
            seleccionado.setActivo(nuevoEstado);
            //CORREGIDO: si la base falla se regresa el estado en memoria, para
            //que la tabla no muestre un cambio que en realidad no se guardo
            try {
                if (usuarioDao.update(seleccionado)) {
                    cargarDatos();
                } else {
                    seleccionado.setActivo(estadoAnterior);
                    utilAlert.mostrarAlerts("Error",
                            "No se pudo cambiar el estado del usuario", "ERROR").showAndWait();
                }
            } catch (RuntimeException e) {
                seleccionado.setActivo(estadoAnterior);
                System.out.println("Error al cambiar el estado del usuario: " + e.getMessage());
                utilAlert.mostrarAlerts("Error",
                        "No se pudo cambiar el estado del usuario.", "ERROR").showAndWait();
            }
        }
    }

    private void showForm(Usuario u) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/usuario-form.fxml"));
            Parent root = loader.load();
            UsuarioFormController controller = loader.getController();
            controller.setUsuario(u);
            Stage stage = new Stage();
            stage.setTitle(u == null ? "Nuevo usuario" : "Editar usuario");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                Usuario usuarioProcesado = controller.getUsuario();
                if (u == null) {
                    if (usuarioDao.insert(usuarioProcesado)) {
                        utilAlert.mostrarAlerts("Success",
                                "Usuario registrado!!!", "CONFIRMATION").showAndWait();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "Error al registrar el usuario!!!", "ERROR").showAndWait();
                    }
                } else {
                    usuarioProcesado.setIdusuario(u.getIdusuario());
                    usuarioProcesado.setActivo(u.getActivo());
                    if (usuarioDao.update(usuarioProcesado)) {
                        utilAlert.mostrarAlerts("Update",
                                "Usuario actualizado!!!", "CONFIRMATION").showAndWait();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "Error al actualizar el usuario!!!", "ERROR").showAndWait();
                    }
                }
                cargarDatos();
            }
        } catch (Exception e) {
            e.printStackTrace();
            utilAlert.mostrarAlerts("Error",
                    "Error al abrir el formulario: " + e.getMessage(), "ERROR").showAndWait();
        }
    }
}
