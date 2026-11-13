package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {

    private static final String QUERY_BASE =
            " SELECT f.idfuncion,f.id_pelicula,f.id_sala,f.horario,f.precio, " +
            " p.nombre AS nombre_pelicula,p.numero AS numero_pelicula,p.duracion, " +
            " p.categoria,p.clasificacion, " +
            " s.nombre AS nombre_sala,s.numero AS numero_sala,s.tipo,s.capacidad,s.estado " +
            " FROM funcion f " +
            " LEFT JOIN pelicula p ON f.id_pelicula = p.idpelicula " +
            " LEFT JOIN sala s ON f.id_sala = s.idsala ";

    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;
        String query = "INSERT INTO funcion(id_pelicula,id_sala,horario,precio)VALUES(?,?,?,?);";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getPelicula().getidpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al insertar funcion:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;
        String query = " UPDATE funcion SET id_pelicula=?, id_sala=?, horario=?, precio=? " +
                " WHERE idfuncion=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getPelicula().getidpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getHorario());
            st.setDouble(4, f.getPrecio());
            st.setInt(5, f.getIdfuncion());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar funcion:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public Funcion searchById(int id) {
        Funcion funcion = null;
        String query = QUERY_BASE + " WHERE f.idfuncion=?;";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    funcion = construirFuncion(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar funcion por Id:" + e.getMessage());
        }
        return funcion;
    }

    @Override
    public boolean delete(Funcion f) {
        boolean flag = false;
        String query = "DELETE FROM funcion WHERE idfuncion=?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar funcion:" + e.getMessage());
        }
        return flag;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();
        String query = QUERY_BASE + " ORDER BY p.nombre, f.horario";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(construirFuncion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar funciones:" + e.getMessage());
        }
        return lista;
    }

    private Funcion construirFuncion(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();
        p.setidpelicula(rs.getInt("id_pelicula"));
        p.setNumero(rs.getInt("numero_pelicula"));
        p.setNombre(rs.getString("nombre_pelicula"));
        p.setDuracion(rs.getString("duracion"));
        p.setCategoria(rs.getString("categoria"));
        p.setClasificacion(rs.getString("clasificacion"));

        Sala s = new Sala();
        s.setIdsala(rs.getInt("id_sala"));
        s.setNumero(rs.getInt("numero_sala"));
        s.setNombre(rs.getString("nombre_sala"));
        s.setTipo(rs.getString("tipo"));
        s.setCapacidad(rs.getInt("capacidad"));
        s.setEstado(rs.getString("estado"));

        Funcion f = new Funcion();
        f.setIdfuncion(rs.getInt("idfuncion"));
        f.setHorario(rs.getString("horario"));
        f.setPrecio(rs.getDouble("precio"));
        f.setPelicula(p);
        f.setSala(s);
        return f;
    }
}
