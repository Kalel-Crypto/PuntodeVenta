package mx.puntodeventa.dao;

import mx.puntodeventa.entity.MovimientoInventario;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Usuario;

import java.sql.*;
import java.util.*;

public class MovimientoDAO {

    public void insertar(MovimientoInventario m) throws Exception {

        String sql = "INSERT INTO inventariomovimientos(fecha, idUsuario, idProducto, tipoMovimiento, cantidad) VALUES(?,?,?,?,?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            ps.setTimestamp(1, new java.sql.Timestamp(m.getFecha().getTime()));
            ps.setInt(2, m.getUsuario().getId());
            ps.setInt(3, m.getProducto().getId());
            ps.setString(4, m.getTipo());


            ps.setInt(5, m.getCantidad());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getInt(1));
                }
            }
        }
    }


    public List<MovimientoInventario> listarTodos() throws Exception {
        List<MovimientoInventario> lista = new ArrayList<>();


        String sql = "SELECT m.idinventarioMovimientos, m.fecha, m.idUsuario, m.idProducto, m.tipoMovimiento, m.cantidad, " +
                "u.nombre AS nombreUsuario, p.nombre AS nombreProducto " +
                "FROM inventariomovimientos m " +
                "INNER JOIN usuario u ON m.idUsuario = u.idusuario " +
                "INNER JOIN producto p ON m.idProducto = p.idProducto " +
                "ORDER BY m.fecha DESC";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario();
                m.setId(rs.getInt("idinventarioMovimientos"));
                m.setFecha(new java.util.Date(rs.getTimestamp("fecha").getTime()));
                m.setTipo(rs.getString("tipoMovimiento"));
                m.setCantidad(rs.getInt("cantidad"));

                Producto p = new Producto();
                p.setId(rs.getInt("idProducto"));

                p.setNombre(rs.getString("nombreProducto"));
                m.setProducto(p);

                Usuario u = new Usuario();
                u.setId(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombreUsuario"));
                m.setUsuario(u);

                lista.add(m);
            }
        }
        return lista;
    }

    public List<MovimientoInventario> listarPorProducto(int productoId) throws Exception {
        List<MovimientoInventario> lista = new ArrayList<>();

        String sql = "SELECT idinventarioMovimientos, fecha, idUsuario, idProducto, tipoMovimiento, cantidad FROM inventariomovimientos WHERE idProducto=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoInventario m = new MovimientoInventario();
                    m.setId(rs.getInt("idinventarioMovimientos"));


                    m.setFecha(new java.util.Date(rs.getTimestamp("fecha").getTime()));
                    m.setTipo(rs.getString("tipoMovimiento"));
                    m.setCantidad(rs.getInt("cantidad")); // Recuperamos la cantidad

                    Producto p = new Producto();
                    p.setId(rs.getInt("idProducto"));
                    m.setProducto(p);

                    Usuario u = new Usuario();
                    u.setId(rs.getInt("idUsuario"));
                    m.setUsuario(u);

                    lista.add(m);
                }
            }
        }
        return lista;
    }
}