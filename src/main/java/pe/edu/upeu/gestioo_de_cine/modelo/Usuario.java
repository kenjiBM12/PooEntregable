package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {
    private final IntegerProperty idusuario = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty usuario = new SimpleStringProperty();
    private final StringProperty contrasena = new SimpleStringProperty();
    private final StringProperty rol = new SimpleStringProperty();
    private final IntegerProperty activo = new SimpleIntegerProperty();

    public Usuario() {
    }

    public Usuario(int idusuario, String nombre, String usuario, String contrasena, String rol, int activo) {
        setIdusuario(idusuario);
        setNombre(nombre);
        setUsuario(usuario);
        setContrasena(contrasena);
        setRol(rol);
        setActivo(activo);
    }

    public final int getIdusuario() {
        return idusuario.get();
    }

    public final void setIdusuario(int value) {
        idusuario.set(value);
    }

    public final IntegerProperty idusuarioProperty() {
        return idusuario;
    }

    public final String getNombre() {
        return nombre.get();
    }

    public final void setNombre(String value) {
        nombre.set(value);
    }

    public final StringProperty nombreProperty() {
        return nombre;
    }

    public final String getUsuario() {
        return usuario.get();
    }

    public final void setUsuario(String value) {
        usuario.set(value);
    }

    public final StringProperty usuarioProperty() {
        return usuario;
    }

    public final String getContrasena() {
        return contrasena.get();
    }

    public final void setContrasena(String value) {
        contrasena.set(value);
    }

    public final StringProperty contrasenaProperty() {
        return contrasena;
    }

    public final String getRol() {
        return rol.get();
    }

    public final void setRol(String value) {
        rol.set(value);
    }

    public final StringProperty rolProperty() {
        return rol;
    }

    public final int getActivo() {
        return activo.get();
    }

    public final void setActivo(int value) {
        activo.set(value);
    }

    public final IntegerProperty activoProperty() {
        return activo;
    }

    @Override
    public String toString() {
        return getUsuario() + " (" + getRol() + ")";
    }
}
