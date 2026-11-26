package pe.edu.upeu.gestioo_de_cine.modelo;

/**
 * MEJORA 1: cierre de caja con arqueo.
 *
 * Guarda el cierre de UN dia: con cuanto se abrio la caja, cuanto se esperaba
 * por cada forma de pago, cuanto se contó fisicamente y quien cerró. La
 * diferencia (contado - esperado) es lo que permite detectar faltantes o
 * sobrantes el mismo dia.
 *
 * Es una clase simple (sin propiedades de JavaFX) porque la pantalla la usa
 * solo para leer y las columnas se arman con SimpleXProperty al vuelo.
 */
public class CierreCaja {

    private int idcierre;
    private String fecha = "";
    private double montoInicial;
    private double efectivo;
    private double tarjeta;
    private double yape;
    private double otros;
    private double devuelto;
    private double totalEsperado;
    private double totalContado;
    private double diferencia;
    private int idUsuario;
    private String usuario = "";
    private String observacion = "";
    private String fechaRegistro = "";

    public CierreCaja() {
    }

    public int getIdcierre() {
        return idcierre;
    }

    public void setIdcierre(int idcierre) {
        this.idcierre = idcierre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(double montoInicial) {
        this.montoInicial = montoInicial;
    }

    public double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(double efectivo) {
        this.efectivo = efectivo;
    }

    public double getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(double tarjeta) {
        this.tarjeta = tarjeta;
    }

    public double getYape() {
        return yape;
    }

    public void setYape(double yape) {
        this.yape = yape;
    }

    public double getOtros() {
        return otros;
    }

    public void setOtros(double otros) {
        this.otros = otros;
    }

    public double getDevuelto() {
        return devuelto;
    }

    public void setDevuelto(double devuelto) {
        this.devuelto = devuelto;
    }

    public double getTotalEsperado() {
        return totalEsperado;
    }

    public void setTotalEsperado(double totalEsperado) {
        this.totalEsperado = totalEsperado;
    }

    public double getTotalContado() {
        return totalContado;
    }

    public void setTotalContado(double totalContado) {
        this.totalContado = totalContado;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
