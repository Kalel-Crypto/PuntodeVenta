import mx.puntodeventa.entity.MovimientoInventario;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Usuario;

import java.sql.*;
import java.util.*;

public class MovimientoDAO {

    public void insertar(MovimientoInventario m) throws Exception {
        String sql = "INSERT INTO inventariomovimientos(fecha, idUsuario, idProducto, tipoMovimiento) VALUES(?,?,?,?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, new java.sql.Date(m.getFecha().getTime()));
            ps.setInt(2, m.getUsuario().getId());
            ps.setInt(3, m.getProducto().getId());
            ps.setString(4, m.getTipo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<MovimientoInventario> listarPorProducto(int productoId) throws Exception {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT idinventarioMovimientos, fecha, idUsuario, idProducto, tipoMovimiento FROM inventariomovimientos WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoInventario m = new MovimientoInventario();
                    m.setId(rs.getInt("idinventarioMovimientos"));
                    m.setFecha(new java.util.Date(rs.getDate("fecha").getTime()));
                    m.setTipo(rs.getString("tipoMovimiento"));

                    Producto p = new Producto();
                    p.setId(rs.getInt("idProducto"));
                    m.setProducto(p);

                    Usuario u = new Usuario();
                    u.setId(rs.getInt("idUsuario"));
                    m.setUsuario(u);

                    lista.add(m);
                }
            }
        }
        return lista;
    }
}