import mx.puntodeventa.entity.Proveedor;

import java.sql.*;
import java.util.*;

public class ProveedorDAO {

    public void insertar(Proveedor p) throws Exception {
        String sql = "INSERT INTO proveedor(nombre, contacto) VALUES(?,?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getContacto());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Proveedor> listar() throws Exception {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT idproveedor, nombre, contacto FROM proveedor";

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getInt("idproveedor"));
                p.setNombre(rs.getString("nombre"));
                p.setContacto(rs.getString("contacto"));
                lista.add(p);
            }
        }
        return lista;
    }

    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM proveedor WHERE idproveedor=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}