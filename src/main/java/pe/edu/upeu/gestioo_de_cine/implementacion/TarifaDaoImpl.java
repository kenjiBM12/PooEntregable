package pe.edu.upeu.gestioo_de_cine.implementacion;

import pe.edu.upeu.gestioo_de_cine.interfaces.ITarifaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Tarifa;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 4: DAO de tarifas (precio por tipo de sala y dia).
 * El DAO nunca cierra la conexion compartida: solo sus statement.
 */
public class TarifaDaoImpl implements ITarifaDao {

    @Override
    public List<Tarifa> listAll() {
        List<Tarifa> lista = new ArrayList<>();
        String sql = "SELECT idtarifa, tipo_sala, dia_semana, precio FROM tarifa "
                + "ORDER BY tipo_sala, dia_semana";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarifa t = new Tarifa();
                t.setIdtarifa(rs.getInt("idtarifa"));
                t.setTipoSala(rs.getString("tipo_sala"));
                t.setDiaSemana(rs.getInt("dia_semana"));
                t.setPrecio(rs.getDouble("precio"));
                lista.add(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar tarifas: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Busca primero la tarifa del dia exacto (1..7) y, si no existe, usa la
     * tarifa base (dia 0). Devuelve -1 cuando no hay ninguna configurada, para
     * que el formulario deje el precio que ya tenia.
     */
    @Override
    public double precio(String tipoSala, int diaSemana) {
        if (tipoSala == null || tipoSala.isBlank()) {
            return -1;
        }
        String sql = "SELECT precio FROM tarifa WHERE UPPER(tipo_sala)=UPPER(?) "
                + "AND dia_semana IN (?, 0) ORDER BY dia_semana DESC LIMIT 1";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipoSala.trim());
            ps.setInt(2, diaSemana);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la tarifa: " + e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public boolean guardar(String tipoSala, int diaSemana, double precio) {
        //INSERT OR REPLACE usa la restriccion UNIQUE(tipo_sala, dia_semana)
        String sql = "INSERT OR REPLACE INTO tarifa (tipo_sala, dia_semana, precio) "
                + "VALUES (UPPER(?), ?, ?)";
        Connection cn = ConexionDB.getConnection();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipoSala.trim());
            ps.setInt(2, diaSemana);
            ps.setDouble(3, precio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la tarifa: " + e.getMessage(), e);
        }
    }
}
