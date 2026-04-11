package mx.puntodeventa.dao;

import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;

import java.sql.*;
import java.util.*;

public class ProductoDAO {

    public void insertar(Producto p) throws Exception {
        String sql = "INSERT INTO producto(nombre, precioUnitario, idProveedor) VALUES(?,?,?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getProveedor().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Producto p) throws Exception {
        String sql = "UPDATE producto SET nombre=?, precioUnitario=?, idProveedor=? WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getProveedor().getId());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        }
    }

    public Producto obtener(int id) throws Exception {
        String sql = "SELECT idProducto, nombre, precioUnitario, idProveedor FROM producto WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("idProducto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precioUnitario"));

                    Proveedor prov = new Proveedor();
                    prov.setId(rs.getInt("idProveedor"));
                    p.setProveedor(prov);

                    return p;
                }
            }
        }
        return null;
    }

    public List<Producto> listar() throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT idProducto, nombre, precioUnitario, idProveedor FROM producto";

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precioUnitario"));

                Proveedor prov = new Proveedor();
                prov.setId(rs.getInt("idProveedor"));
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