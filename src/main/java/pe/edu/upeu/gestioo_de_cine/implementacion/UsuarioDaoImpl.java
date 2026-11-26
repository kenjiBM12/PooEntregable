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
                    String sal = rs.getString("sal");
                    int iteraciones = rs.getInt("iteraciones");
                    boolean correcta = false;

                    //MEJORA 5: con sal se usa PBKDF2; sin sal, SHA-256 (base vieja)
                    if (Seguridad.verificar(contrasena, guardada, sal, iteraciones)) {
                        correcta = true;
                        //si venia de SHA-256 se migra en este mismo login
                        if (sal == null || sal.isBlank()) {
                            migrarContrasena(cn, rs.getInt("idusuario"), contrasena);
                        }
                    }
                    //caso de una base muy vieja: la clave esta en texto plano
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
        //MEJORA 5: se pasa la clave a PBKDF2 con una sal nueva y se guarda la sal
        String sal = Seguridad.nuevaSal();
        String query = "UPDATE usuario SET contrasena=?, sal=?, iteraciones=? WHERE idusuario=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, Seguridad.pbkdf2(contrasenaPlana, sal, Seguridad.ITERACIONES));
            st.setString(2, sal);
            st.setInt(3, Seguridad.ITERACIONES);
            st.setInt(4, idUsuario);
            st.executeUpdate();
            System.out.println("Contrasena migrada a PBKDF2 para el usuario id " + idUsuario);
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
        String query = "INSERT INTO usuario(nombre,usuario,contrasena,rol,activo,sal,iteraciones)"
                + "VALUES(?,?,?,?,?,?,?)";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            //MEJORA 5: la clave se guarda como hash PBKDF2 con su sal
            String[] clave = claveNueva(u.getContrasena());
            st.setString(1, u.getNombre());
            st.setString(2, u.getUsuario());
            st.setString(3, clave[0]);
            st.setString(4, u.getRol());
            st.setInt(5, 1);
            st.setString(6, clave[1]);
            st.setInt(7, Seguridad.ITERACIONES);
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
        //COALESCE deja la sal anterior si la contraseña no se cambio
        String query = "UPDATE usuario SET nombre=?, usuario=?, contrasena=?, rol=?, activo=?, "
                + "sal=COALESCE(?, sal), iteraciones=COALESCE(?, iteraciones) "
                + "WHERE idusuario=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            String[] clave = claveNueva(u.getContrasena());
            st.setString(1, u.getNombre());
            st.setString(2, u.getUsuario());
            st.setString(3, clave[0]);
            st.setString(4, u.getRol());
            st.setInt(5, u.getActivo());
            st.setString(6, clave[1]);
            if (clave[1] == null) {
                st.setNull(7, java.sql.Types.INTEGER);
            } else {
                st.setInt(7, Seguridad.ITERACIONES);
            }
            st.setInt(8, u.getIdusuario());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario:" + e.getMessage(), e);
        }
        return flag;
    }

    /**
     * Devuelve {hash, sal} para guardar una contraseña.
     * Si el texto YA es una clave guardada (por ejemplo, al editar un usuario
     * sin tocar su contraseña) se deja igual y la sal queda en null, para no
     * hashear dos veces ni perder la sal existente.
     * MEJORA 5: las claves nuevas usan PBKDF2 con sal aleatoria.
     */
    private String[] claveNueva(String contrasena) {
        if (Seguridad.esClaveGuardada(contrasena)) {
            return new String[]{contrasena, null};
        }
        String sal = Seguridad.nuevaSal();
        return new String[]{Seguridad.pbkdf2(contrasena, sal, Seguridad.ITERACIONES), sal};
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
