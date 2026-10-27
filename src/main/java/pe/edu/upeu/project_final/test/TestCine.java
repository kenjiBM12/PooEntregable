package pe.edu.upeu.project_final.test;

import pe.edu.upeu.project_final.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.project_final.interfaces.IPeliculaDao;
import pe.edu.upeu.project_final.modelo.Pelicula;

public class TestCine {
    public static void main(String[] args) {
        IPeliculaDao dao = new PeliculaDaoImpl();

        // Probando insercion rapida
        Pelicula p = new Pelicula(0, "Avengers: Endgame", "Accion", 181, "+14");
        boolean resultado = dao.insert(p);

        if (resultado) {
            System.out.println("Prueba exitosa! Pelicula guardada en SQLite.");
        } else {
            System.out.println("Error al guardar en la prueba.");
        }

        // Listando
        System.out.println("--- LISTA DE PELICULAS ---");
        for (Pelicula pelis : dao.listAll()) {
            System.out.println(pelis.getId() + " - " + pelis.getTitulo() + " (" + pelis.getGenero() + ")");
        }
    }
}
