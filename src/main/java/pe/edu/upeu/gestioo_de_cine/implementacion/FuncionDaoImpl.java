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
                    " f.estado AS estado_funcion, " +
                    " p.titulo AS titulo_pelicula,p.numero AS numero_pelicula,p.duracion, " +
                    " p.categoria,p.clasificacion, " +
                    " s.numero AS numero_sala,s.tipo_sala AS tipo_sala,s.capacidad,s.estado, " +
                    " s.filas AS filas_sala,s.columnas AS columnas_sala " +
                    " FROM funcion f " +
                    " LEFT JOIN pelicula p ON f.id_pelicula = p.idpelicula " +
                    " LEFT JOIN sala s ON f.id_sala = s.idsala ";

    @Override
    public boolean insert(Funcion f) {
        boolean flag = false;
        String query = "INSERT INTO funcion(id_pelicula,id_sala,fecha,horario,precio,estado)"
                + "VALUES(?,?,?,?,?,?);";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
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
            st.setString(6, f.getEstado());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar funcion:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public boolean update(Funcion f) {
        boolean flag = false;
        String query = " UPDATE funcion SET id_pelicula=?, id_sala=?, fecha=?, horario=?, "
                + " precio=?, estado=? WHERE idfuncion=?";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getPelicula().getidpelicula());
            st.setInt(2, f.getSala().getIdsala());
            st.setString(3, f.getFecha());
            st.setString(4, f.getHorario());
            st.setDouble(5, f.getPrecio());
            st.setString(6, f.getEstado());
            st.setInt(7, f.getIdfuncion());
            flag = st.executeUpdate() > 0;
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
                    s.setFilas(rs.getInt("filas_sala"));
                    s.setColumnas(rs.getInt("columnas_sala"));
                    s.setEstado(rs.getString("estado"));

                    funcion = new Funcion();
                    funcion.setIdfuncion(rs.getInt("idfuncion"));
                    funcion.setFecha(rs.getString("fecha"));
                    funcion.setHorario(rs.getString("horario"));
                    funcion.setPrecio(rs.getDouble("precio"));
                    funcion.setEstado(rs.getString("estado_funcion"));
                    funcion.setPelicula(p);
                    funcion.setSala(s);
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

        String queryVentas = "SELECT COUNT(*) FROM venta WHERE id_funcion=?";
        try (PreparedStatement st = cn.prepareStatement(queryVentas)) {
            st.setInt(1, f.getIdfuncion());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("La función tiene " + rs.getInt(1)
                            + " boleto(s) vendido(s) y no se puede eliminar.\n"
                            + "Se conserva el historial de ventas.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al revisar las ventas de la funcion:"
                    + e.getMessage(), e);
        }

        String query = "DELETE FROM funcion WHERE idfuncion=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, f.getIdfuncion());
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar funcion:" + e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public List<Funcion> listAll() {
        List<Funcion> lista = new ArrayList<>();
        String query = QUERY_BASE + " ORDER BY f.fecha, f.horario, f.idfuncion; ";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query);
        ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
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
                s.setFilas(rs.getInt("filas_sala"));
                s.setColumnas(rs.getInt("columnas_sala"));
                s.setEstado(rs.getString("estado"));

                Funcion f = new Funcion();
                f.setIdfuncion(rs.getInt("idfuncion"));
                f.setFecha(rs.getString("fecha"));
                f.setHorario(rs.getString("horario"));
                f.setPrecio(rs.getDouble("precio"));
                f.setEstado(rs.getString("estado_funcion"));
                f.setPelicula(p);
                f.setSala(s);

                lista.add(f);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar funciones: " + e.getMessage(), e);
        }
        return lista;

    }

    @Override
    public boolean cancelar(int idfuncion) {
        boolean flag = false;
        String query = "UPDATE funcion SET estado='cancelada' "
                + "WHERE idfuncion=? AND COALESCE(estado,'activa')<>'cancelada'";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idfuncion);
            flag = st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cancelar la funcion: " + e.getMessage(), e);
        }
        return flag;
    }
}
