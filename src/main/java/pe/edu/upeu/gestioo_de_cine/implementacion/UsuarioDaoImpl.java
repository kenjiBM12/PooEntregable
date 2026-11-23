package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IUsuarioDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuarioDao {

    @Override
    public Usuario login(String usuario, String contrasena) {
        Usuario u = null;
        String query = "SELECT * FROM usuario WHERE usuario=? AND contrasena=? AND activo=1";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, usuario);
            st.setString(2, contrasena);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    u = construirUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar el usuario:" + e.getMessage(), e);
        }
        return u;
    }

    @Override
    public List<Usuario> listAll() {
        List<Usuario> lista = new ArrayList<>();
        String query = "SELECT * FROM usuario ORDER BY idusuario";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(construirUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios:" + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public boolean insert(Usuario u) {
        boolean flag = false;
        String query = "INSERT INTO usuario(nombre,usuario,contrasena,rol,activo)VALUES(?,?,?,?,?)";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, u.getNombre());
            st.setString(2, u.getUsuario());
            st.setString(3, u.getContrasena());
            st.setString(4, u.getRol());
            st.setInt(5, 1);
            //true solo si se guardo la fila
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar usuario:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public boolean update(Usuario u) {
        boolean flag = false;
        String query = "UPDATE usuario SET nombre=?, usuario=?, contrasena=?, rol=?, activo=? " +
                "WHERE idusuario=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, u.getNombre());
            st.setString(2, u.getUsuario());
            st.setString(3, u.getContrasena());
            st.setString(4, u.getRol());
            st.setInt(5, u.getActivo());
            st.setInt(6, u.getIdusuario());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public boolean existeUsuario(String usuario) {
        String query = "SELECT COUNT(*) FROM usuario WHERE lower(usuario)=lower(?)";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, usuario);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario repetido:" + e.getMessage(), e);
        }
        return false;
    }

    private Usuario construirUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdusuario(rs.getInt("idusuario"));
        u.setNombre(rs.getString("nombre"));
        u.setUsuario(rs.getString("usuario"));
        u.setContrasena(rs.getString("contrasena"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getInt("activo"));
        return u;
    }
}
