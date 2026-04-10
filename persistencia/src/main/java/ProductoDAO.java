import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;

import java.sql.*;
import java.util.*;

public class ProductoDAO {

    public void insertar(Producto p) throws Exception {
        String sql = "INSERT INTO producto(nombre, precioUnitario, stock, fechaCaducidad, idproveedor) VALUES(?,?,?,?,?)";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setInt(5, p.getProveedor().getId());

            ps.executeUpdate();
        }
    }

    public void actualizar(Producto p) throws Exception {
        String sql = "UPDATE producto SET nombre=?, precioUnitario=?, stock=?, fechaCaducidad=?, idproveedor=? WHERE idProducto=?";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.setInt(5, p.getProveedor().getId());
            ps.setInt(6, p.getId());

            ps.executeUpdate();
        }
    }

    public Producto obtener(int id) throws Exception {
        String sql = "SELECT * FROM producto WHERE idProducto=?";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precioUnitario"));
                p.setStock(rs.getInt("stock"));
                Proveedor prov = new Proveedor();
                prov.setId(rs.getInt("idproveedor"));
                p.setProveedor(prov);

                return p;
            }
        }
        return null;
    }

    public List<Producto> listar() throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precioUnitario"));
                p.setStock(rs.getInt("stock"));
                p.setFechaCaducidad(rs.getDate("fechaCaducidad"));

                Proveedor prov = new Proveedor();
                prov.setId(rs.getInt("idproveedor"));
                p.setProveedor(prov);

                lista.add(p);
            }
        }
        return lista;
    }

    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM producto WHERE idProducto=?";
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}