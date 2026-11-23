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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl implements IFuncionDao {

    private static final String QUERY_BASE =
            " SELECT f.idfuncion,f.id_pelicula,f.id_sala,f.fecha,f.horario,f.precio, " +
            " p.titulo AS titulo_pelicula,p.numero AS numero_pelicula,p.duracion, " +
            " p.categoria,p.clasificacion, " +
            " s.numero AS numero_sala,s.tipo_sala AS tipo_sala,s.capacidad,s.estado " +
            " FROM funcion f " +
            " LEFT JOIN pelicula p ON f.id_pelicula = p.idpelicula " +
            " LEFT JOIN sala s ON f.id_sala = s.idsala ";

    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;
        //fecha es obligatoria en la tabla funcion, no se puede omitir
        String query = "INSERT INTO funcion(id_pelicula,id_sala,fecha,horario,precio)VALUES(?,?,?,?,?);";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            //si la funcion no trae fecha se usa la de hoy, y se guarda en el objeto
            String fecha = f.getFecha();
            if (fecha == null || fecha.isBlank()) {
                fecha = LocalDate.now().toString();
                f.setFecha(fecha);
            }
            st.setInt(1, f.getPelicula().getidpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, fecha);
            st.setString(4, f.getHorario());
            st.setDouble(5, f.getPrecio());
            st.executeUpdate();
            //true solo si se inserto una fila
            flag = true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar funcion:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;
        String query = " UPDATE funcion SET id_pelicula=?, id_sala=?, fecha=?, horario=?, precio=? " +
                " WHERE idfuncion=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getPelicula().getidpelicula());
            st.setInt(2, f.getSala().getIdsala());
            //la fecha tambien se actualiza: antes se quedaba con la vieja
            st.setString(3, f.getFecha());
            st.setString(4, f.getHorario());
            st.setDouble(5, f.getPrecio());
            st.setInt(6, f.getIdfuncion());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar funcion:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public Funcion searchById(int id) {
        Funcion funcion = null;
        String query = QUERY_BASE + " WHERE f.idfuncion=?;";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    funcion = construirFuncion(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar funcion por Id:" + e.getMessage(), e);
        }
        return funcion;
    }

    @Override
    public boolean delete(Funcion f) {
        boolean flag = false;
        Connection cn = ConexionDB.getConnection();

        //una funcion con ventas no se borra: se perderia el historial de la compra
        String queryVentas = "SELECT COUNT(*) FROM venta WHERE id_funcion=?";
        try (PreparedStatement st = cn.prepareStatement(queryVentas)) {
            st.setInt(1, f.getIdfuncion());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("La función tiene " + rs.getInt(1)
                            + " boleto(s) vendido(s) y no se puede eliminar.\n"
                            + "Anule esas ventas desde el módulo Ventas.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al revisar las ventas de la funcion:"
                    + e.getMessage(), e);
        }

        String query = "DELETE FROM funcion WHERE idfuncion=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar funcion:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();
        //el tipo de sala pertenece a sala, no a pelicula
        String query = QUERY_BASE + " ORDER BY s.numero, f.fecha, f.horario";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                lista.add(construirFuncion(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar funciones:" + e.getMessage(), e);
        }
        return lista;
    }

    private Funcion construirFuncion(ResultSet rs) throws SQLException {
        Pelicula p = new Pelicula();
        p.setidpelicula(rs.getInt("id_pelicula"));
        p.setNumero(rs.getInt("numero_pelicula"));
        p.setTitulo(rs.getString("titulo_pelicula"));
        p.setDuracion(rs.getString("duracion"));
        p.setCategoria(rs.getString("categoria"));
        p.setClasificacion(rs.getString("clasificacion"));

        Sala s = new Sala();
        s.setIdsala(rs.getInt("id_sala"));
        s.setNumero(rs.getInt("numero_sala"));
        s.setTipoSala(rs.getString("tipo_sala"));
        s.setCapacidad(rs.getInt("capacidad"));
        s.setEstado(rs.getString("estado"));

        Funcion f = new Funcion();
        f.setIdfuncion(rs.getInt("idfuncion"));
        f.setFecha(rs.getString("fecha"));
        f.setHorario(rs.getString("horario"));
        f.setPrecio(rs.getDouble("precio"));
        f.setPelicula(p);
        f.setSala(s);
        return f;
    }
}
