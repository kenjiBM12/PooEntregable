package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;

import java.util.List;

public interface IPeliculaDao {
    boolean insert(Pelicula p);
    boolean update(Pelicula p);
    Pelicula searchById(int id);
    Pelicula searchByNum(int num);
    boolean delete(Pelicula p);
    List<Pelicula> listAll();
}
