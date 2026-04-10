import mx.puntodeventa.entity.DetalleVenta;
import mx.puntodeventa.entity.Venta;

import java.sql.*;
import java.util.List;

public class VentaDAO {

    public void registrarVenta(Venta v, List<DetalleVenta> detalles) throws Exception {

        String sqlVenta = "INSERT INTO venta(fecha, total, idusuario) VALUES(?,?,?)";
        String sqlDetalle = "INSERT INTO detalle_venta(cantidad, precioUnitario, idventa, idproducto) VALUES(?,?,?,?)";

        Connection con = ConnectionManager.getConnection();

        try {
            con.setAutoCommit(false);

            PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setTimestamp(1, new Timestamp(v.getFecha().getTime()));
            psVenta.setDouble(2, v.getTotal());
            psVenta.setInt(3, v.getUsuario().getId());
            psVenta.executeUpdate();

            ResultSet rs = psVenta.getGeneratedKeys();
            rs.next();
            int ventaId = rs.getInt(1);

            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);

            for (DetalleVenta d : detalles) {
                psDetalle.setInt(1, d.getCantidad());
                psDetalle.setDouble(2, d.getPrecioUnitario());
                psDetalle.setInt(3, ventaId);
                psDetalle.setInt(4, d.getProducto().getId());
                psDetalle.addBatch();
            }

            psDetalle.executeBatch();

            con.commit();

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }
}