package facade;

import mx.puntodeventa.dao.ProveedorDAO;
import mx.puntodeventa.entity.Proveedor;
import mx.puntodeventa.entity.Usuario;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.dao.InventarioDTO;

import service.ProveedorService;
import service.UsuarioService;
import service.ProductoService;
import service.InventarioService;

import java.util.List;

public class SistemaFacade {

    private UsuarioService usuarioService = new UsuarioService();
    private ProductoService productoService = new ProductoService();
    private InventarioService inventarioService = new InventarioService();
    private ProveedorService proveedorService = new ProveedorService();

    public List<Proveedor> listarProveedores() throws Exception {
        ProveedorDAO proveedorDao = new ProveedorDAO();
        return proveedorDao.listar();
    }

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
    public void registrarProveedor(String nombre, String numero) throws Exception {
        proveedorService.registrar(nombre, numero);
    }


    public void registrarProducto(String nombre, double precio, int idProveedor, int stock) throws Exception {
        productoService.registrarProducto(nombre, precio, idProveedor, stock);
    }

    public void actualizarProducto(int idProducto, String nombre, double precio, int idProveedor) throws Exception {
        productoService.actualizarProducto(idProducto, nombre, precio, idProveedor);
    }

    public void eliminarProducto(int idProducto) throws Exception {
        productoService.eliminarProducto(idProducto);
    }

    public List<Producto> listarProductos() throws Exception {
        return productoService.listarProductos();
    }

    public void modificarProveedor(Proveedor p) throws Exception {
        ProveedorDAO dao = new ProveedorDAO();
        dao.actualizar(p);
    }

    public void eliminarProveedor(int id) throws Exception {
        ProveedorDAO dao = new ProveedorDAO();
        dao.eliminar(id);
    }

    public List<Proveedor> buscarProveedores(String busqueda) throws Exception {
        ProveedorDAO dao = new ProveedorDAO();
        return dao.buscar(busqueda);
    }

    public List<InventarioDTO> listarInventario() throws Exception {
        return inventarioService.listarInventario();
    }

    public List<InventarioDTO> buscarInventarioPorNombre(String nombre) throws Exception {
        return inventarioService.buscarPorNombre(nombre);
    }

    public List<InventarioDTO> buscarInventarioPorId(int idProducto) throws Exception {
        return inventarioService.buscarPorId(idProducto);
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