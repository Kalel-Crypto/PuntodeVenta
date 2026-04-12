package mx.puntodeventa.dao;

import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;

import java.sql.*;
import java.util.*;

public class ProductoDAO {

    public void insertar(Producto p) throws Exception {
        String sqlProducto = "INSERT INTO producto(nombre, precioUnitario, idProveedor, fechaCaducidad) VALUES(?,?,?,?)";
        String sqlInventario = "INSERT INTO inventario(idProducto, stock) VALUES(?,?)";

        try (Connection con = ConnectionManager.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psP = con.prepareStatement(sqlProducto, Statement.RETURN_GENERATED_KEYS)) {
                psP.setString(1, p.getNombre());
                psP.setDouble(2, p.getPrecio());
                psP.setInt(3, p.getProveedor().getId());

            if (p.getCaducidad() != null) {
                psP.setDate(4, new java.sql.Date(p.getCaducidad().getTime()));
            } else {
                psP.setNull(4, Types.DATE);
            }
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
                    psI.setInt(2, p.getStock());
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
            ps.setInt(4, p.getStock());
            if (p.getCaducidad() != null) {
                ps.setDate(5, new java.sql.Date(p.getCaducidad().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setInt(6, p.getId());

            ps.executeUpdate();
        }
    }

    public Producto obtener(int id) throws Exception {
        String sql = "SELECT idProducto, nombre, precioUnitario, idProveedor, stock, fechaCaducidad FROM producto WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("idProducto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precioUnitario"));
                    p.setStock(rs.getInt("stock"));
                    p.setCaducidad(rs.getDate("fechaCaducidad"));

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
                p.setStock(rs.getInt("stock"));
                p.setCaducidad(rs.getDate("fechaCaducidad"));

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