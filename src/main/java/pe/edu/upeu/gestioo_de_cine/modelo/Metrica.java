package pe.edu.upeu.gestioo_de_cine.modelo;

/**
 * MEJORA 10: numeros rapidos de la pantalla de Inicio.
 *
 * Se llenan con UNA sola consulta SQL (MetricaDaoImpl) para no abrir varias
 * conexiones al entrar al sistema. Son datos de solo lectura, por eso son
 * campos normales con get y set.
 */
public class Metrica {

    private int peliculas;
    private int salas;
    private int salasActivas;
    private int clientes;
    private int funcionesHoy;
    private int boletosHoy;
    private double ingresosHoy;

    public Metrica() {
    }

    public int getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(int peliculas) {
        this.peliculas = peliculas;
    }

    public int getSalas() {
        return salas;
    }

    public void setSalas(int salas) {
        this.salas = salas;
    }

    public int getSalasActivas() {
        return salasActivas;
    }

    public void setSalasActivas(int salasActivas) {
        this.salasActivas = salasActivas;
    }

    public int getClientes() {
        return clientes;
    }

    public void setClientes(int clientes) {
        this.clientes = clientes;
    }

    public int getFuncionesHoy() {
        return funcionesHoy;
    }

    public void setFuncionesHoy(int funcionesHoy) {
        this.funcionesHoy = funcionesHoy;
    }

    public int getBoletosHoy() {
        return boletosHoy;
    }

    public void setBoletosHoy(int boletosHoy) {
        this.boletosHoy = boletosHoy;
    }

    public double getIngresosHoy() {
        return ingresosHoy;
    }

    public void setIngresosHoy(double ingresosHoy) {
        this.ingresosHoy = ingresosHoy;
    }
}
