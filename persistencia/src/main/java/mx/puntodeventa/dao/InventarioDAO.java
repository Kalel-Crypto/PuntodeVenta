package mx.puntodeventa.dao;

import mx.puntodeventa.entity.Inventario;

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
                p.idProveedor,
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
                dto.setNombreProducto(rs.getString("nombre"));

                Integer stock = (Integer) rs.getObject("stock");
                dto.setStock(stock != null ? stock : 0);

                dto.setPrecio(rs.getDouble("precioUnitario"));
                dto.setProveedor(rs.getString("proveedor"));
                dto.setIdProveedor(rs.getInt("idProveedor"));
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

    public void insertar(Inventario inventario) throws Exception {
        String sql = "INSERT INTO inventario (idProducto, stock) VALUES (?, ?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, inventario.getId());
            ps.setInt(2, inventario.getStock());

            ps.executeUpdate();
        }
    }
}