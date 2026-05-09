package mx.puntodeventa.dao;

import mx.puntodeventa.entity.DetalleVenta;
import mx.puntodeventa.entity.Venta;
import java.sql.*;
import java.util.List;

public class VentaDAO {

    public void registrarVenta(Venta v, List<DetalleVenta> detalles) throws Exception {

        String sqlVenta = "INSERT INTO venta(idCaja, total) VALUES(?,?)";
        String sqlDetalle = "INSERT INTO detalleventa(cantidad, precioUnitario, idVenta, idProducto) VALUES(?,?,?,?)";
        String sqlActualizarStock = "UPDATE inventario SET stock = stock - ? WHERE idProducto = ? AND stock >= ?";
        String sqlAuditoria = "INSERT INTO inventariomovimientos (idProducto, cantidad, tipoMovimiento, fecha, idusuario) VALUES (?, ?, 'SALIDA', NOW(), ?)";
        Connection con = ConnectionManager.getConnection();

        try {
            con.setAutoCommit(false);


            PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, v.getIdCaja());
            psVenta.setDouble(2, v.getTotal());
            psVenta.executeUpdate();

            ResultSet rs = psVenta.getGeneratedKeys();
            rs.next();
            int ventaId = rs.getInt(1);


            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
            PreparedStatement psStock = con.prepareStatement(sqlActualizarStock);
            PreparedStatement psAudit = con.prepareStatement(sqlAuditoria);

            for (DetalleVenta d : detalles) {

                psDetalle.setInt(1, d.getCantidad());
                psDetalle.setDouble(2, d.getPrecioUnitario());
                psDetalle.setInt(3, ventaId);
                psDetalle.setInt(4, d.getProducto().getId());
                psDetalle.addBatch();


                psStock.setInt(1, d.getCantidad());
                psStock.setInt(2, d.getProducto().getId());
                psStock.setInt(3, d.getCantidad());
                int filasAfectadas = psStock.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new Exception("Stock insuficiente para el producto: " + d.getProducto().getNombre());
                }

                psAudit.setInt(1, d.getProducto().getId());
                psAudit.setInt(2, d.getCantidad());
                psAudit.setInt(3, 1);
                psAudit.addBatch();
            }

            psDetalle.executeBatch();
            psAudit.executeBatch();

            con.commit();

        } catch (Exception e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }
}