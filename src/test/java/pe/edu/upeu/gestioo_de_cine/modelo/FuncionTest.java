package pe.edu.upeu.gestioo_de_cine.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MEJORA 13: la hora de fin sale de sumar la duracion (en minutos) al horario.
 * MEJORA 7: una funcion cancelada se reconoce con isCancelada().
 */
class FuncionTest {

    @Test
    void laHoraDeFinSumaLaDuracionDeLaPelicula() {
        Pelicula p = new Pelicula();
        p.setDuracion("110");
        Funcion f = new Funcion();
        f.setHorario("18:30");
        f.setPelicula(p);
        assertEquals("20:20", f.getHoraFin());
    }

    @Test
    void sinDuracionOHorarioNoHayHoraDeFin() {
        Funcion f = new Funcion();
        f.setHorario("18:30");
        assertEquals("", f.getHoraFin());
        f.setHorario("");
        assertEquals("", f.getHoraFin());
    }

    @Test
    void elEstadoPorDefectoEsActiva() {
        Funcion f = new Funcion();
        assertFalse(f.isCancelada());
        f.setEstado("cancelada");
        assertTrue(f.isCancelada());
        f.setEstado(null);
        assertFalse(f.isCancelada());
    }
}
