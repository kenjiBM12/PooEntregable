package pe.edu.upeu.gestioo_de_cine.modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * MEJORA 1: cierre de caja con arqueo.
 *
 * Guarda el cierre de UN dia: con cuanto se abrio la caja, cuanto se esperaba
 * por cada forma de pago, cuanto se conto fisicamente y quien cerro. La
 * diferencia (contado - esperado) es lo que permite detectar faltantes o
 * sobrantes el mismo dia.
 *
 * Sigue el mismo patron que los demas modelos del sistema: los campos son
 * propiedades de JavaFX y se accede con getX/setX o con xProperty(), asi una
 * tabla puede enlazar sus columnas sin crear propiedades al vuelo.
 */
public class CierreCaja {

    private final IntegerProperty idcierre = new SimpleIntegerProperty();
    private final StringProperty fecha = new SimpleStringProperty();
    private final DoubleProperty montoInicial = new SimpleDoubleProperty();
    private final DoubleProperty efectivo = new SimpleDoubleProperty();
    private final DoubleProperty tarjeta = new SimpleDoubleProperty();
    private final DoubleProperty yape = new SimpleDoubleProperty();
    private final DoubleProperty otros = new SimpleDoubleProperty();
    private final DoubleProperty devuelto = new SimpleDoubleProperty();
    private final DoubleProperty totalEsperado = new SimpleDoubleProperty();
    private final DoubleProperty totalContado = new SimpleDoubleProperty();
    private final DoubleProperty diferencia = new SimpleDoubleProperty();
    private final IntegerProperty idUsuario = new SimpleIntegerProperty();
    private final StringProperty usuario = new SimpleStringProperty();
    private final StringProperty observacion = new SimpleStringProperty();
    private final StringProperty fechaRegistro = new SimpleStringProperty();

    public CierreCaja() {
    }

    public final int getIdcierre() {
        return idcierre.get();
    }

    public final void setIdcierre(int value) {
        idcierre.set(value);
    }

    public final IntegerProperty idcierreProperty() {
        return idcierre;
    }

    public final String getFecha() {
        return fecha.get();
    }

    public final void setFecha(String value) {
        fecha.set(value);
    }

    public final StringProperty fechaProperty() {
        return fecha;
    }

    public final double getMontoInicial() {
        return montoInicial.get();
    }

    public final void setMontoInicial(double value) {
        montoInicial.set(value);
    }

    public final DoubleProperty montoInicialProperty() {
        return montoInicial;
    }

    public final double getEfectivo() {
        return efectivo.get();
    }

    public final void setEfectivo(double value) {
        efectivo.set(value);
    }

    public final DoubleProperty efectivoProperty() {
        return efectivo;
    }

    public final double getTarjeta() {
        return tarjeta.get();
    }

    public final void setTarjeta(double value) {
        tarjeta.set(value);
    }

    public final DoubleProperty tarjetaProperty() {
        return tarjeta;
    }

    public final double getYape() {
        return yape.get();
    }

    public final void setYape(double value) {
        yape.set(value);
    }

    public final DoubleProperty yapeProperty() {
        return yape;
    }

    public final double getOtros() {
        return otros.get();
    }

    public final void setOtros(double value) {
        otros.set(value);
    }

    public final DoubleProperty otrosProperty() {
        return otros;
    }

    public final double getDevuelto() {
        return devuelto.get();
    }

    public final void setDevuelto(double value) {
        devuelto.set(value);
    }

    public final DoubleProperty devueltoProperty() {
        return devuelto;
    }

    public final double getTotalEsperado() {
        return totalEsperado.get();
    }

    public final void setTotalEsperado(double value) {
        totalEsperado.set(value);
    }

    public final DoubleProperty totalEsperadoProperty() {
        return totalEsperado;
    }

    public final double getTotalContado() {
        return totalContado.get();
    }

    public final void setTotalContado(double value) {
        totalContado.set(value);
    }

    public final DoubleProperty totalContadoProperty() {
        return totalContado;
    }

    public final double getDiferencia() {
        return diferencia.get();
    }

    public final void setDiferencia(double value) {
        diferencia.set(value);
    }

    public final DoubleProperty diferenciaProperty() {
        return diferencia;
    }

    public final int getIdUsuario() {
        return idUsuario.get();
    }

    public final void setIdUsuario(int value) {
        idUsuario.set(value);
    }

    public final IntegerProperty idUsuarioProperty() {
        return idUsuario;
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

    public final String getObservacion() {
        return observacion.get();
    }

    public final void setObservacion(String value) {
        observacion.set(value);
    }

    public final StringProperty observacionProperty() {
        return observacion;
    }

    public final String getFechaRegistro() {
        return fechaRegistro.get();
    }

    public final void setFechaRegistro(String value) {
        fechaRegistro.set(value);
    }

    public final StringProperty fechaRegistroProperty() {
        return fechaRegistro;
    }
}
