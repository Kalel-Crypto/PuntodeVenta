package service;

import mx.puntodeventa.dao.UsuarioDAO;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Rol;

import java.util.List;

public class UsuarioService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();


    public void registrarUsuario(Usuario usuario) throws Exception {

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new Exception("La contraseña es obligatoria");
        }

        if (usuario.getRol() == null) {
            throw new Exception("El rol es obligatorio");
        }

        if (!(usuario.getRol() == Rol.ADMINISTRADOR || usuario.getRol() == Rol.CAJERO)) {
            throw new Exception("Rol inválido. Solo ADMINISTRADOR o CAJERO");
        }

        List<Usuario> usuarios = usuarioDAO.listar();

        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(usuario.getNombre())) {
                throw new Exception("El nombre de usuario ya existe");
            }
        }

        usuarioDAO.insertar(usuario);
    }


    public Usuario login(String nombre, String password) throws Exception {

        if (nombre == null || nombre.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new Exception("Credenciales inválidas");
        }

        Usuario usuario = usuarioDAO.login(nombre, password);

        if (usuario == null) {
            throw new Exception("Credenciales inválidas");
        }

        return usuario;
    }


    public List<Usuario> listarUsuarios() throws Exception {
        return usuarioDAO.listar();
    }


    public void eliminarUsuario(int id) throws Exception {
        usuarioDAO.eliminar(id);
    }
}