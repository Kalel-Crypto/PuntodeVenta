package negocio.service;

import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;
import persistencia.ProductoDAO;
import persistencia.ProveedorDAO;

import java.util.List;

public class ProductoService {

    private ProductoDAO productoDAO = new ProductoDAO();
    private ProveedorDAO proveedorDAO = new ProveedorDAO();


    public void registrarProducto(String nombre, double precio, int idProveedor) throws Exception {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre del producto es obligatorio");
        }

        if (precio <= 0) {
            throw new Exception("El precio debe ser mayor a 0");
        }

        Proveedor proveedor = obtenerProveedorPorId(idProveedor);

        if (proveedor == null) {
            throw new Exception("El proveedor no es válido");
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setProveedor(proveedor);

        productoDAO.insertar(producto);
    }


    public void actualizarProducto(int idProducto, String nombre, double precio, int idProveedor) throws Exception {

        Producto existente = productoDAO.obtener(idProducto);

        if (existente == null) {
            throw new Exception("El producto no existe");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }

        if (precio <= 0) {
            throw new Exception("El precio debe ser mayor a 0");
        }

        Proveedor proveedor = obtenerProveedorPorId(idProveedor);

        if (proveedor == null) {
            throw new Exception("El proveedor no es válido");
        }

        existente.setNombre(nombre);
        existente.setPrecio(precio);
        existente.setProveedor(proveedor);

        productoDAO.actualizar(existente);
    }


    public void eliminarProducto(int idProducto) throws Exception {

        Producto existente = productoDAO.obtener(idProducto);

        if (existente == null) {
            throw new Exception("El producto no existe");
        }

        productoDAO.eliminar(idProducto);
    }


    public List<Producto> listarProductos() throws Exception {
        return productoDAO.listar();
    }


    private Proveedor obtenerProveedorPorId(int idProveedor) throws Exception {

        List<Proveedor> proveedores = proveedorDAO.listar();

        for (Proveedor p : proveedores) {
            if (p.getId() == idProveedor) {
                return p;
            }
        }

        return null;
    }
}