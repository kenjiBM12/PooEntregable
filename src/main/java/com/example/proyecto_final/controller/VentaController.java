package com.example.proyecto_final.controller;

import com.example.proyecto_final.utils.ConexionDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class VentaController {

    @FXML
    private ComboBox<String> cbPeliculas;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtPrecio;

    @FXML
    public void initialize() {
        cargarPeliculasEnComboBox();
    }

    private void cargarPeliculasEnComboBox() {
        ObservableList<String> listaPeliculas = FXCollections.observableArrayList();

        String query = "SELECT titulo FROM pelicula";
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                listaPeliculas.add(rs.getString("titulo"));
            }
            cbPeliculas.setItems(listaPeliculas);

        } catch (Exception e) {
            System.err.println("Error al cargar películas en el ComboBox: " + e.getMessage());
        }
    }
}