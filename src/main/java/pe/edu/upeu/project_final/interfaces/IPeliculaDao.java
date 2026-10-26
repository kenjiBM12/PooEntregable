package pe.edu.upeu.project_final.interfaces;

import pe.edu.upeu.project_final.modelo.Pelicula;

import java.util.List;

public interface IPeliculaDao {
    public boolean insert(Pelicula p);

    public boolean update(Pelicula p);

    public Pelicula searchById(int id);

    public boolean delete(Pelicula p);

    public List<Pelicula> listAll();

}
