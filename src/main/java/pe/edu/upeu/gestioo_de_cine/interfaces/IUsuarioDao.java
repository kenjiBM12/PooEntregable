package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;

import java.util.List;

public interface IUsuarioDao {

    //devuelve el usuario si el nombre y la contraseña coinciden, si no devuelve null
    Usuario login(String usuario, String contrasena);

    //lista todos los usuarios creados (admins y empleados)
    List<Usuario> listAll();

    //guarda un usuario nuevo
    boolean insert(Usuario u);

    //actualiza nombre, usuario, contraseña, rol y estado
    boolean update(Usuario u);

    //sirve para no repetir el nombre de usuario
    boolean existeUsuario(String usuario);
}
