package service;

import mx.puntodeventa.dao.UsuarioDAO;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Rol;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    List<Usuario> lista = new ArrayList<>();

    public Usuario obtenerUsuarioporNombre(String nombre) throws Exception {
        lista = usuarioDAO.listar();
        for(Usuario u: lista){
            if(u.getNombre().equals(nombre)){
                return u;
            }
        }
        return null;
    }

    public void registrarUsuario(Usuario usuario) throws Exception {

        System.out.println("Llegue a UsuarioService: " + usuario.getNombre());
        System.out.println("Llegue a UsuarioService: " + usuario.getPassword());
        System.out.println("Llegue a UsuarioService: " + usuario.getRol());


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

    public void modificarUsuario(Usuario usuario) throws Exception {
        if (usuario == null || usuario.getId() <= 0) {
            throw new Exception("Usuario no válido para modificar");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new Exception("La contraseña es obligatoria");
        }
        if (usuario.getRol() == null) {
            throw new Exception("El rol es obligatorio");
        }

        usuarioDAO.actualizar(usuario);
    }

    public List<Usuario> buscarUsuarios(String busqueda) throws Exception {
        List<Usuario> todos = usuarioDAO.listar();
        List<Usuario> resultados = new ArrayList<>();

        if (busqueda == null || busqueda.trim().isEmpty()) {
            return todos;
        }

        String query = busqueda.toLowerCase().trim();

        for (Usuario u : todos) {
            if ((u.getNombre() != null && u.getNombre().toLowerCase().contains(query)) ||
                    String.valueOf(u.getId()).equals(query)) {
                resultados.add(u);
            }
        }

        return resultados;
    }


    public List<Usuario> listarUsuarios() throws Exception {
        return usuarioDAO.listar();
    }


    public void eliminarUsuario(int id) throws Exception {
        usuarioDAO.eliminar(id);
    }
}