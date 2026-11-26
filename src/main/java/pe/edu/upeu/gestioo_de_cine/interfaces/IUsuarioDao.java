package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;

import java.util.List;

public interface IUsuarioDao {

    Usuario login(String usuario, String contrasena);

    List<Usuario> listAll();

    boolean insert(Usuario u);

    boolean update(Usuario u);

    boolean existeUsuario(String usuario);
}
