package facade;

import mx.puntodeventa.dao.ProveedorDAO;
import mx.puntodeventa.entity.Proveedor;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.dao.InventarioDTO;

import service.UsuarioService;
import service.ProductoService;
import service.InventarioService;

import java.util.Date;
import java.util.List;

public class SistemaFacade {

    private UsuarioService usuarioService = new UsuarioService();
    private ProductoService productoService = new ProductoService();
    private InventarioService inventarioService = new InventarioService();



    public void registrarUsuario(Usuario usuario) throws Exception {
        usuarioService.registrarUsuario(usuario);
    }

    public Usuario login(String nombre, String password) throws Exception {
        return usuarioService.login(nombre, password);
    }

    public List<Usuario> listarUsuarios() throws Exception {
        return usuarioService.listarUsuarios();
    }

    public void eliminarUsuario(int id) throws Exception {
        usuarioService.eliminarUsuario(id);
    }



    public void registrarProducto(String nombre, double precio, int idProveedor, int stock, Date caducidad) throws Exception {
        productoService.registrarProducto(nombre, precio, idProveedor, stock, caducidad);
    }

    public void actualizarProducto(int idProducto, String nombre, double precio, int idProveedor, int stock, Date caducidad) throws Exception {
        productoService.actualizarProducto(idProducto, nombre, precio, idProveedor, stock, caducidad);
    }

    public void eliminarProducto(int idProducto) throws Exception {
        productoService.eliminarProducto(idProducto);
    }

    public List<Producto> listarProductos() throws Exception {
        return productoService.listarProductos();
    }
    public List<Proveedor> listarProveedores() throws Exception {
        ProveedorDAO proveedorDao = new ProveedorDAO();
        return proveedorDao.listar();
    }

    public List<InventarioDTO> listarInventario() throws Exception {
        return inventarioService.listarInventario();
    }

    public List<InventarioDTO> buscarInventarioPorNombre(String nombre) throws Exception {
        return inventarioService.buscarPorNombre(nombre);
    }

    public List<InventarioDTO> buscarInventarioExacto(int idProducto, String nombre) throws Exception {
        return inventarioService.buscarExacto(idProducto, nombre);
    }

    public void actualizarStock(int idProducto, int nuevoStock) throws Exception {
        inventarioService.actualizarStock(idProducto, nuevoStock);
    }

    public void eliminarRegistroInventario(int idProducto) throws Exception {
        inventarioService.eliminarRegistro(idProducto);
    }
}