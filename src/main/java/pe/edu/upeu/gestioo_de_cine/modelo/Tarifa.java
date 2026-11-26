package pe.edu.upeu.gestioo_de_cine.modelo;

/**
 * MEJORA 4: tarifa de entrada por tipo de sala y dia de la semana.
 *
 * diaSemana = 0 significa "todos los dias" (tarifa base). Los valores 1..7
 * son lunes..domingo y permiten una promocion, por ejemplo martes mas barato.
 * El codigo del sistema usa esta tabla para proponer el precio de la funcion
 * en lugar de escribirlo a mano en cada una.
 */
public class Tarifa {

    private int idtarifa;
    private String tipoSala = "";
    private int diaSemana;
    private double precio;

    public Tarifa() {
    }

    public Tarifa(int idtarifa, String tipoSala, int diaSemana, double precio) {
        this.idtarifa = idtarifa;
        this.tipoSala = tipoSala;
        this.diaSemana = diaSemana;
        this.precio = precio;
    }

    public int getIdtarifa() {
        return idtarifa;
    }

    public void setIdtarifa(int idtarifa) {
        this.idtarifa = idtarifa;
    }

    public String getTipoSala() {
        return tipoSala;
    }

    public void setTipoSala(String tipoSala) {
        this.tipoSala = tipoSala;
    }

    public int getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(int diaSemana) {
        this.diaSemana = diaSemana;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /** Nombre del dia para mostrarlo en una tabla. */
    public String getDiaTexto() {
        switch (diaSemana) {
            case 1: return "Lunes";
            case 2: return "Martes";
            case 3: return "Miércoles";
            case 4: return "Jueves";
            case 5: return "Viernes";
            case 6: return "Sábado";
            case 7: return "Domingo";
            default: return "Todos los días";
        }
    }

    @Override
    public String toString() {
        return tipoSala + " - " + getDiaTexto() + " - S/ " + String.format("%.2f", precio);
    }
}
