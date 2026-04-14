package mx.puntodeventa.dao;

import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;

import java.sql.*;
import java.util.*;

public class ProductoDAO {

    public void insertar(Producto p, int cantidad) throws Exception {
        String sqlProducto = "INSERT INTO producto(nombre, precioUnitario, idProveedor) VALUES (?, ?, ?)";
        String sqlInventario = "INSERT INTO inventario(idProducto, stock) VALUES (?, ?)";

        try (Connection con = ConnectionManager.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psP = con.prepareStatement(sqlProducto, Statement.RETURN_GENERATED_KEYS)) {
                psP.setString(1, p.getNombre());
                psP.setDouble(2, p.getPrecio());
                psP.setInt(3, p.getProveedor().getId());
                psP.executeUpdate();

                int idGenerado = 0;
                try (ResultSet rs = psP.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                        p.setId(idGenerado);
                    }
                }
                try (PreparedStatement psI = con.prepareStatement(sqlInventario)) {
                    psI.setInt(1, idGenerado);
                    psI.setInt(2,cantidad);
                    psI.executeUpdate();
                }
                con.commit();
                System.out.println("Registro exitoso");

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void actualizar(Producto p) throws Exception {
        String sql = "UPDATE producto SET nombre=?, precioUnitario=?, idProveedor=?, stock=?, fechaCaducidad=? WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getProveedor().getId());
            ps.setInt(6, p.getId());

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
        String sql = "SELECT idProducto, nombre, precioUnitario, idProveedor, stock, fechaCaducidad FROM producto";

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
        String sqlInventario = "DELETE FROM inventario WHERE idProducto=?";
        String sqlProducto = "DELETE FROM producto WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection()) {

            con.setAutoCommit(false);
            try {
                try (PreparedStatement psI = con.prepareStatement(sqlInventario)) {
                    psI.setInt(1, id);
                    psI.executeUpdate();
                }
                try (PreparedStatement psP = con.prepareStatement(sqlProducto)) {
                    psP.setInt(1, id);

                    psP.executeUpdate();

                }
                con.commit(); // Confirmamos el borrado en el inventario y el producto
                System.out.println("Eliminacion exitosa de producto e inventario ID: " + id);
            } catch (Exception e) {
                con.rollback();
                throw new Exception("Error al eliminar el producto: " + e.getMessage());
            }
        }
    }
}