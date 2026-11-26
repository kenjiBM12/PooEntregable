package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IUsuarioDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

public class UsuarioFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNombre, txtUsuario;
    @FXML
    private PasswordField txtClave;
    @FXML
    private ComboBox<String> cbRol;

    private Usuario usuario;
    private boolean saved = false;

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();

    @FXML
    public void initialize() {
        cbRol.getItems().addAll("admin", "empleado");
        cbRol.setValue("empleado");
    }

    public boolean isSaved() {
        return saved;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    private boolean validate() {
        if (txtNombre.getText().isBlank()
                || txtUsuario.getText().isBlank()
                || txtClave.getText().isBlank()
                || cbRol.getValue() == null) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.", "ERROR");
            alert.showAndWait();
            return false;
        }
        if (usuarioDao.existeUsuario(txtUsuario.getText().trim())) {
            boolean esElMismo = usuario != null
                    && usuario.getUsuario() != null
                    && usuario.getUsuario().equalsIgnoreCase(txtUsuario.getText().trim());
            if (!esElMismo) {
                utilAlerts.mostrarAlerts("Usuario repetido",
                        "Ya existe una cuenta con ese nombre de usuario.", "WARNING").showAndWait();
                return false;
            }
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        if (!validate()) {
            return;
        }
        if (usuario == null) {
            usuario = new Usuario();
        }
        usuario.setNombre(txtNombre.getText().trim());
        usuario.setUsuario(txtUsuario.getText().trim());
        usuario.setContrasena(txtClave.getText().trim());
        usuario.setRol(cbRol.getValue());
        saved = true;
        cerrar();
    }

    public void setUsuario(Usuario u) {
        this.usuario = u;
        if (u != null) {
            lblTitulo.setText("Editar usuario");
            txtNombre.setText(u.getNombre());
            txtUsuario.setText(u.getUsuario());
            txtClave.setText(u.getContrasena());
            cbRol.setValue(u.getRol());
        }
    }
}
