package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;

public interface IUsuarioDao {
    public Usuario login(String user, String password);
    public boolean insert_user(Usuario u);

}
