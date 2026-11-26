package pe.edu.upeu.gestioo_de_cine.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MEJORA 4: pruebas automaticas de la clase Duracion.
 *
 * Se ejecutan con "mvnw test" (Maven + JUnit 5). Comprueban el error que se
 * corrigio en la MEJORA 1: antes "1:50" se leia como 1 minuto y ahora se lee
 * como 110 minutos, que es lo que usa el cruce de horarios.
 */
class DuracionTest {

    @Test
    void leeHorasYMinutosConDosPuntos() {
        assertEquals(110, Duracion.aMinutos("1:50"));
        assertEquals(90, Duracion.aMinutos("1:30"));
        assertEquals(65, Duracion.aMinutos("01:05"));
    }

    @Test
    void leeMinutosEscritosConTexto() {
        assertEquals(95, Duracion.aMinutos("95 min"));
        assertEquals(120, Duracion.aMinutos("120 minutos"));
        assertEquals(120, Duracion.aMinutos(" 120 "));
    }

    @Test
    void leeHorasConLaLetraH() {
        assertEquals(120, Duracion.aMinutos("2h"));
        assertEquals(110, Duracion.aMinutos("1h 50"));
    }

    @Test
    void normalizaSiempreAMinutos() {
        assertEquals("110", Duracion.normalizar("1:50"));
        assertEquals("95", Duracion.normalizar("95 min"));
    }

    @Test
    void textoVacioONuloDevuelveCero() {
        assertEquals(0, Duracion.aMinutos(null));
        assertEquals(0, Duracion.aMinutos("   "));
        assertEquals(0, Duracion.aMinutos("sin dato"));
    }
}
