import mx.puntodeventa.entity.Caja;

import java.sql.*;
import java.time.LocalDate;

public class CajaDAO {

    public void abrirCaja(Caja c) throws Exception {
        String sql = "INSERT INTO caja(fecha, totalVentas) VALUES(?,?)";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(c.getFecha().getTime()));
            ps.setDouble(2, c.getTotalVentas());
            ps.executeUpdate();
        }
    }

    public Caja obtenerCajaHoy() throws Exception {
        String sql = "SELECT * FROM caja WHERE fecha=?";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Caja c = new Caja();
                c.setFecha(new java.util.Date(rs.getDate("fecha").getTime()));
                c.setTotalVentas(rs.getDouble("totalVentas"));
                return c;
            }
        }
        return null;
    }

    public void actualizarTotal(double total) throws Exception {
        String sql = "UPDATE caja SET totalVentas=? WHERE fecha=?";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, total);
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
        }
    }
}