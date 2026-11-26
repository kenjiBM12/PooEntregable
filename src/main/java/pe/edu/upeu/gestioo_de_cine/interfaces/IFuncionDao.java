package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;

import java.util.List;

public interface IFuncionDao {
    boolean insert(Funcion f);
    boolean update(Funcion f);
    Funcion searchById(int id);
    boolean delete(Funcion f);
    List<Funcion> listAll();

    /**
     * MEJORA 7: marca la funcion como 'cancelada' sin borrarla.
     * Se usa cuando falla el proyector: se avisa a los compradores y se
     * conservan sus boletos para anularlos y devolver el dinero.
     */
    boolean cancelar(int idfuncion);
}
