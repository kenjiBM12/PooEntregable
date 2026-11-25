package pe.edu.upeu.gestioo_de_cine.controller;

import java.util.Optional;

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
import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

public class SalaController {

    private final UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Sala> tableSala;
    @FXML
    private TableColumn<Sala, Number> ColNum;
    @FXML
    private TableColumn<Sala, Integer> ColNumero;
    @FXML
    private TableColumn<Sala, Integer> ColId;
    @FXML
    private TableColumn<Sala, String> ColtipoSala;
    @FXML
    private TableColumn<Sala, Integer> ColCapacidad;
    @FXML
    private TableColumn<Sala, String> ColEstado;
    @FXML
    private TextField txtBuscar;

    private final ISalaDao salaDao = new SalaDaoImpl();
    private ObservableList<Sala> listaSala;
    //se conserva la lista completa para poder buscar sin perder datos
    private ObservableList<Sala> todosLosSalas;

    @FXML
    public void initialize() {
        ColNum.setCellFactory(columna -> new TableCell<>() {
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

        ColNumero.setCellValueFactory(
                celda -> celda.getValue().numeroProperty().asObject()
        );


        // Coltipo conserva su fx:id, pero ahora apunta a tipoSala.
        ColtipoSala.setCellValueFactory(
                celda -> celda.getValue().tipoSalaProperty()
        );

        ColId.setCellValueFactory(
                celda -> celda.getValue().idsalaProperty().asObject()
        );
        ColCapacidad.setCellValueFactory(
                celda -> celda.getValue().capacidadProperty().asObject()
        );
        ColEstado.setCellValueFactory(
                celda -> celda.getValue().estadoProperty()
        );

        cargarDatos();
    }

    private void cargarDatos() {
        todosLosSalas = FXCollections.observableArrayList(salaDao.listAll());
        listaSala = FXCollections.observableArrayList(todosLosSalas);
        tableSala.setItems(listaSala);
    }

    @FXML
    private void handleBuscar() {
        filtrar();
    }

    //filtra la lista por numero, tipo o estado con un for normal
    private void filtrar() {
        String texto = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        listaSala.clear();
        for (Sala s : todosLosSalas) {
            if (texto.isEmpty()
                    || contiene(String.valueOf(s.getNumero()), texto)
                    || contiene(s.getTipoSala(), texto)
                    || contiene(s.getEstado(), texto)) {
                listaSala.add(s);
            }
        }
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    //el admin puede todo; el empleado solo trabaja en ventas
    private boolean sinPermiso() {
        if (Sesion.esAdmin()) {
            return false;
        }
        utilAlert.mostrarAlerts("Sin permiso",
                "Solo el administrador puede administrar salas.", "WARNING").showAndWait();
        return true;
    }

    @FXML
    private void handleDelete() {
        if (sinPermiso()) return;
        Sala salaSeleccionada =
                tableSala.getSelectionModel().getSelectedItem();

        if (salaSeleccionada == null) {
            utilAlert.mostrarAlerts(
                    "Selección requerida",
                    "Seleccione una sala para eliminar.",
                    "WARNING"
            ).showAndWait();
            return;
        }

        String descripcion = "Sala " + salaSeleccionada.getNumero()
                + " — " + salaSeleccionada.getTipoSala();

        Alert confirmacion = utilAlert.mostrarAlerts(
                "Confirmar eliminación",
                "¿Está seguro de eliminar " + descripcion + "?",
                "CONFIRMATION"
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {
            try {
                if (salaDao.delete(salaSeleccionada)) {
                    listaSala.remove(salaSeleccionada);
                    todosLosSalas.remove(salaSeleccionada);
                } else {
                    utilAlert.mostrarAlerts(
                            "Error",
                            "No se pudo eliminar la sala.",
                            "ERROR"
                    ).showAndWait();
                }
            } catch (RuntimeException e) {
                //si la sala tiene funciones, la base no deja borrarla
                System.out.println("Error al eliminar sala: " + e.getMessage());
                //el DAO ya explica el motivo (por ejemplo: tiene funciones programadas)
                utilAlert.mostrarAlerts("No se pudo eliminar",
                        e.getMessage(), "ERROR").showAndWait();
            }
        }
    }

    @FXML
    private void handleNew() {
        if (sinPermiso()) return;
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        if (sinPermiso()) return;
        Sala salaSeleccionada =
                tableSala.getSelectionModel().getSelectedItem();

        if (salaSeleccionada == null) {
            utilAlert.mostrarAlerts(
                    "Selección requerida",
                    "Seleccione una sala de la tabla para editar.",
                    "WARNING"
            ).showAndWait();
            return;
        }

        //CORREGIDO: si la busqueda fallaba (por ejemplo la base estaba ocupada)
        //la excepcion se perdia y el boton EDITAR parecia no hacer nada
        Sala salaCompleta;
        try {
            salaCompleta = salaDao.searchById(salaSeleccionada.getIdsala());
        } catch (RuntimeException e) {
            System.out.println("Error al buscar la sala: " + e.getMessage());
            utilAlert.mostrarAlerts("Error al abrir la sala",
                    "No se pudo leer la sala de la base de datos.",
                    "ERROR").showAndWait();
            return;
        }

        //CORREGIDO: si la sala ya no existe se abria el formulario como si
        //fuera nueva y al guardar se creaba otra sala; ahora se avisa
        if (salaCompleta == null) {
            utilAlert.mostrarAlerts("Sala no encontrada",
                    "La sala ya no existe en la base de datos.",
                    "WARNING").showAndWait();
            cargarDatos();
            return;
        }

        showForm(salaCompleta);
    }

    private void showForm(Sala salaEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "/pe/edu/upeu/gestioo_de_cine/views/sala-form.fxml"
                    )
            );

            Parent root = loader.load();
            SalaFormController controller = loader.getController();
            controller.setSala(salaEditar);

            Stage stage = new Stage();
            stage.setTitle(
                    salaEditar == null ? "Nueva sala" : "Editar sala"
            );
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                Sala salaProcesada = controller.getSala();
                try {
                    boolean resultado;

                    if (salaEditar == null) {
                        resultado = salaDao.insert(salaProcesada);
                    } else {
                        salaProcesada.setIdsala(salaEditar.getIdsala());
                        resultado = salaDao.update(salaProcesada);
                    }

                    if (resultado) {
                        //CORREGIDO: se avisa que tambien se genero o ajusto la
                        //cuadricula, porque el DAO la guarda en la misma transaccion
                        String mensaje = salaEditar == null
                                ? "Sala creada con sus " + salaProcesada.getCapacidad() + " butacas."
                                : "Sala y cuadricula de butacas actualizadas.";
                        utilAlert.mostrarAlerts(
                                "Correcto",
                                mensaje,
                                "CONFIRMATION"
                        ).showAndWait();
                    } else {
                        utilAlert.mostrarAlerts(
                                "Error",
                                "No se pudieron guardar los datos de la sala.",
                                "ERROR"
                        ).showAndWait();
                    }
                } catch (RuntimeException e) {
                    //por ejemplo: el numero de sala ya existe
                    System.out.println("Error al guardar sala: " + e.getMessage());
                    utilAlert.mostrarAlerts("No se pudo guardar",
                            "Revise los datos de la sala.\nDetalle: " + e.getMessage(),
                            "ERROR").showAndWait();
                }
                cargarDatos();
            }
        } catch (Exception e) {
            System.err.println(
                    "Error al abrir el formulario de sala: "
                            + e.getMessage()
            );
            utilAlert.mostrarAlerts(
                    "Error",
                    "No se pudo abrir el formulario de sala.",
                    "ERROR"
            ).showAndWait();
        }
    }
}