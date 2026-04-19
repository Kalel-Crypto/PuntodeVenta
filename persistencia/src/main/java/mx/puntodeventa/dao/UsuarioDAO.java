package mx.puntodeventa.dao;

import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Rol;

import java.sql.*;
import java.util.*;

public class UsuarioDAO {

    public void insertar(Usuario u) throws Exception {
        String sql = "INSERT INTO usuario(nombre, password, rol) VALUES(?,?,?)";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getPassword());
            String rolParaDb = (u.getRol() == Rol.ADMINISTRADOR) ? "admin" : "cajero";
            ps.setString(3, rolParaDb);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setId(rs.getInt(1));
                }
            }
        }
    }

    public Usuario login(String nombre, String password) throws Exception {
        String sql = "SELECT idusuario, nombre, password, rol FROM usuario WHERE nombre=? AND password=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("idusuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setPassword(rs.getString("password"));
                    String rolBD = rs.getString("rol");
                    if (rolBD.equalsIgnoreCase("admin")) {
                        u.setRol(Rol.ADMINISTRADOR);
                    } else {
                        u.setRol(Rol.CAJERO);
                    }
                    return u;
                }
            }
        }
        return null;
    }

    public List<Usuario> listar() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT idusuario, nombre, password, rol FROM usuario";

        try (Connection con = ConnectionManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("idusuario"));
                u.setNombre(rs.getString("nombre"));
                u.setPassword(rs.getString("password"));
                u.setRol(Rol.valueOf(rs.getString("rol")));
                lista.add(u);
            }
        }
        return lista;
    }

    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM usuario WHERE idusuario=?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    private Rol mapearRol(String rolBD) {
        if (rolBD == null) return null;

        if (rolBD.equalsIgnoreCase("admin")) {
            return Rol.ADMINISTRADOR;
        } else if (rolBD.equalsIgnoreCase("cajero")) {
            return Rol.CAJERO;
        }
        return null;
    }
}