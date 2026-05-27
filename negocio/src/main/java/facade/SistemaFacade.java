package facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import mx.puntodeventa.dao.MovimientoDAO;
import mx.puntodeventa.dao.ProveedorDAO;
import mx.puntodeventa.entity.*;
import mx.puntodeventa.dao.InventarioDTO;

import service.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SistemaFacade {

    private UsuarioService usuarioService = new UsuarioService();
    private ProductoService productoService = new ProductoService();
    private InventarioService inventarioService = new InventarioService();
    private ProveedorService proveedorService = new ProveedorService();
    private CajaService cajaService = new CajaService();
    private MovimientoInventarioService movimientoInventarioService = new MovimientoInventarioService();

    public boolean existeMarca(Proveedor p) throws Exception {
        for(Proveedor prov: proveedorService.obtenerProveedores()){
            if(prov.getMarca().equalsIgnoreCase(p.getMarca())){
                return true;
            } else {
                return false;
            }
        }
            return false;
    }

    public List<Usuario> buscarUsuarios(String busqueda) throws Exception {
        return usuarioService.buscarUsuarios(busqueda);
    }

    public void modificarUsuario(Usuario usuario) throws Exception {
        usuarioService.modificarUsuario(usuario);
    }


    public Usuario obtenerUsuario(String nombre) throws Exception {
        return usuarioService.obtenerUsuarioporNombre(nombre);
    }


    public boolean existeenVenta(int id) throws SQLException {
    return movimientoInventarioService.conseguirMovimientoExistente(id);
    }


    public void verificarCajaExistente(String nombre) throws Exception {
        cajaService.verificarCajaExistente(nombre);
    }

    public Caja obtenerCajaActual(Usuario u) throws Exception {
        return cajaService.traerCajaActual(u.getId());
    }


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
    public void registrarProveedor(String nombre, String numero, String marca) throws Exception {
        proveedorService.registrar(nombre, numero, marca);
    }

    public void registrarMovimientoSeguro(Usuario usuario, Producto producto, String tipo, int cantidad) {
        try {
            System.out.println("Nombre del usuario: " + usuario.getNombre() +
                    "\n" + "ID: " + usuario.getId());
            MovimientoDAO dao = new MovimientoDAO();
            MovimientoInventario m = new MovimientoInventario();
            m.setFecha(new java.util.Date());
            m.setUsuario(usuario);
            m.setProducto(producto);
            m.setTipo(tipo);
            m.setCantidad(cantidad);

            dao.insertar(m);
        } catch (Exception e) {
            System.err.println("CRÍTICO: Falló el registro de auditoría: " + e.getMessage());
        }
    }

    public List<Producto> buscarProductosPorFiltro(String query) throws Exception {

        List<Producto> todosLosProductos = productoService.listarProductos();
        List<Producto> resultados = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return resultados;
        }

        try {

            int idBusqueda = Integer.parseInt(query.trim());

            for (Producto p : todosLosProductos) {
                if (p.getId() == idBusqueda) {
                    resultados.add(p);
                    break;
                }
            }

        } catch (NumberFormatException e) {

            String textoBusqueda = query.toLowerCase().trim();

            for (Producto p : todosLosProductos) {
                if (p.getNombre() != null && p.getNombre().toLowerCase().contains(textoBusqueda)) {
                    resultados.add(p);
                }
            }
        }

        return resultados;
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

    public Producto obtenerProducto(String nombre) throws Exception {
        List<Producto> lista = productoService.listarProductos();
        for(Producto p: lista){
            if(p.getNombre().equalsIgnoreCase(nombre)){
                return p;
            }
        }
        return null;
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

    public List<MovimientoInventario> listarTodosLosMovimientos() throws Exception {
        MovimientoDAO movimientoDAO = new MovimientoDAO();
        return movimientoDAO.listarTodos();
    }

    public void actualizarStock(int idProducto, int nuevoStock) throws Exception {
        inventarioService.actualizarStock(idProducto, nuevoStock);
    }

    public void eliminarRegistroInventario(int idProducto) throws Exception {
        inventarioService.eliminarRegistro(idProducto);
    }
}