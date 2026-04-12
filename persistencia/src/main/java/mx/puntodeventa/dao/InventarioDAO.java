package mx.puntodeventa.dao;

import mx.puntodeventa.entity.Producto;

import java.sql.*;
import java.util.*;

public class InventarioDAO {

    public List<InventarioDTO> listar() throws Exception {

        List<InventarioDTO> lista = new ArrayList<>();

        String sql = """
            SELECT 
                p.idProducto,
                p.nombre,
                i.stock,
                p.precioUnitario,
                pr.nombre AS proveedor
            FROM producto p
            LEFT JOIN inventario i ON p.idProducto = i.idProducto
            LEFT JOIN proveedor pr ON p.idProveedor = pr.idProveedor
        """;

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                InventarioDTO dto = new InventarioDTO();

                dto.setIdProducto(rs.getInt("idProducto"));
                dto.setNombre(rs.getString("nombre"));
                dto.setStock(rs.getInt("stock"));
                dto.setPrecio(rs.getDouble("precioUnitario"));
                dto.setProveedor(rs.getString("proveedor"));

                lista.add(dto);
            }
        }

        return lista;
    }

    public void actualizarStock(int idProducto, int stock) throws Exception {

        String sql = "UPDATE inventario SET stock=? WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, stock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    public void eliminar(int idProducto) throws Exception {

        String sql = "DELETE FROM inventario WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }

    public void registrarNuevo(Producto p, int stockInicial) throws Exception {
        String sqlProducto = "INSERT INTO producto (nombre, precioUnitario, idProveedor) VALUES (?, ?, ?)";
        String sqlInventario = "INSERT INTO inventario (idProducto, stock) VALUES (?, ?)";

        try (Connection con = ConnectionManager.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psP = con.prepareStatement(sqlProducto, Statement.RETURN_GENERATED_KEYS)) {
                psP.setString(1, p.getNombre());
                psP.setDouble(2, p.getPrecio());
                psP.setInt(3, p.getProveedor().getId());
                psP.executeUpdate();

                // Obtenemos el ID generado para el producto
                int idGenerado = 0;
                try (ResultSet rs = psP.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                    }
                }

                // Insertamos el stock inicial en la tabla de inventario
                try (PreparedStatement psI = con.prepareStatement(sqlInventario)) {
                    psI.setInt(1, idGenerado);
                    psI.setInt(2, stockInicial);
                    psI.executeUpdate();
                }

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }
}