package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IUsuarioDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;
import pe.edu.upeu.gestioo_de_cine.utils.Seguridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuarioDao {

    /**
     * MEJORA 7: login con contraseñas hasheadas y migracion automatica.
     *
     * Ya no se compara la contraseña dentro del SQL (WHERE contrasena=?)
     * porque el valor guardado es un hash. Ahora:
     *   1. se busca el usuario activo por su nombre
     *   2. se compara el hash de lo escrito con el hash guardado
     *   3. si la base todavia tiene la contraseña vieja en texto plano
     *      (por ejemplo "admin123"), se acepta UNA vez y se cambia por su
     *      hash en ese mismo momento. Asi nadie tiene que cambiar su clave.
     */
    @Override
    public Usuario login(String usuario, String contrasena) {
        Usuario u = null;
        String query = "SELECT * FROM usuario WHERE usuario=? AND activo=1";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, usuario);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    String guardada = rs.getString("contrasena");
                    boolean correcta = false;

                    //caso normal: la base ya guarda un hash
                    if (guardada != null && guardada.equals(Seguridad.hash(contrasena))) {
                        correcta = true;
                    }
                    //caso de una base vieja: la clave esta en texto plano
                    if (!correcta && guardada != null && guardada.equals(contrasena)) {
                        correcta = true;
                        migrarContrasena(cn, rs.getInt("idusuario"), contrasena);
                    }

                    if (correcta) {
                        u = construirUsuario(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar el usuario:" + e.getMessage(), e);
        }
        return u;
    }

    /**
     * Cambia una contraseña en texto plano por su hash.
     * Se llama solo cuando el login fue correcto con la clave vieja.
     */
    private void migrarContrasena(Connection cn, int idUsuario, String contrasenaPlana) {
        String query = "UPDATE usuario SET contrasena=? WHERE idusuario=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, Seguridad.hash(contrasenaPlana));
            st.setInt(2, idUsuario);
            st.executeUpdate();
            System.out.println("Contrasena migrada a SHA-256 para el usuario id " + idUsuario);
        } catch (SQLException e) {
            //si falla la migracion el login ya fue correcto: solo se avisa
            System.out.println("No se pudo migrar la contrasena: " + e.getMessage());
        }
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
            //MEJORA 7: nunca se guarda la clave escrita, solo su hash
            st.setString(3, claveParaGuardar(u.getContrasena()));
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
            st.setString(3, claveParaGuardar(u.getContrasena()));
            st.setString(4, u.getRol());
            st.setInt(5, u.getActivo());
            st.setInt(6, u.getIdusuario());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario:" + e.getMessage(), e);
        }
        return flag;
    }

    /**
     * Si el texto YA es un hash (por ejemplo, al editar un usuario sin tocar
     * su contraseña) se guarda tal cual; si es una clave nueva, se hashea.
     * Asi nunca se guarda dos veces el hash de un hash.
     */
    private String claveParaGuardar(String contrasena) {
        if (Seguridad.esHash(contrasena)) {
            return contrasena;
        }
        return Seguridad.hash(contrasena);
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
