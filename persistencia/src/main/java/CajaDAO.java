import mx.puntodeventa.entity.Caja;
import java.sql.*;

public class CajaDAO {

    public void abrirCaja(Caja c) throws Exception {
        String sql = "INSERT INTO caja(idUsuario) VALUES(?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, c.getIdUsuario());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setIdcaja(rs.getInt(1));
                }
            }
        }
    }

    public Caja obtenerCajaPorId(int idcaja) throws Exception {
        String sql = "SELECT idcaja, idUsuario FROM caja WHERE idcaja = ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idcaja);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Caja c = new Caja();
                    c.setIdcaja(rs.getInt("idcaja"));
                    c.setIdUsuario(rs.getInt("idUsuario"));
                    return c;
                }
            }
        }
        return null;
    }

    public void actualizarUsuarioDeCaja(Caja c) throws Exception {
        String sql = "UPDATE caja SET idUsuario = ? WHERE idcaja = ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, c.getIdUsuario());
            ps.setInt(2, c.getIdcaja());
            ps.executeUpdate();
        }
    }
}