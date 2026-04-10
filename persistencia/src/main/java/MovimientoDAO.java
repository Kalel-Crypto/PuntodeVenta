import mx.puntodeventa.entity.MovimientoInventario;
import mx.puntodeventa.entity.Producto;

import java.sql.*;
import java.util.*;

public class MovimientoDAO {

    public void insertar(MovimientoInventario m) throws Exception {
        String sql = "INSERT INTO inventariomovimientos(tipoMovimiento, fecha, cantidad, idProducto) VALUES(?,?,?,?)";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getTipo());
            ps.setDate(2, new java.sql.Date(m.getFecha().getTime()));
            ps.setInt(3, m.getCantidad());
            ps.setInt(4, m.getProducto().getId());
            ps.executeUpdate();
        }
    }

    public List<MovimientoInventario> listarPorProducto(int productoId) throws Exception {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventariomovimientos WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario();
                m.setId(rs.getInt("idinventarioMovimientos"));
                m.setTipo(rs.getString("tipoMovimiento"));
                m.setFecha(new java.util.Date(rs.getDate("fecha").getTime()));
                m.setCantidad(rs.getInt("cantidad"));

                Producto p = new Producto();
                p.setId(rs.getInt("idProducto"));
                m.setProducto(p);

                lista.add(m);
            }
        }
        return lista;
    }
}