package service;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import mx.puntodeventa.dao.ProductoDAO;
import mx.puntodeventa.dao.ProveedorDAO;
import mx.puntodeventa.dao.InventarioDAO;
import mx.puntodeventa.entity.Producto;
import mx.puntodeventa.entity.Proveedor;
import mx.puntodeventa.entity.Inventario;

import java.util.List;

public class ProductoService {

    private ProductoDAO productoDAO = new ProductoDAO();
    private ProveedorDAO proveedorDAO = new ProveedorDAO();
    //private InventarioDAO inventarioDAO = new InventarioDAO();



    public void registrarProducto(String nombre, double precio, int idProveedor, int stock) throws Exception {

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

        if(stock < 0){
            throw new Exception("El Stock debe ser numero positivo");
        }
        if (productoDAO.existeProductoPorNombre(nombre)) {
            throw new Exception("El nombre del producto ya existe en el sistema.");
        }
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setProveedor(proveedor);
        productoDAO.insertar(producto,stock);


        /*
        ESTO ES INNECESARIO, ES REDUNDANTE PORQUE EN EL PRODUCTODAO TAMBIEN
        DA DE ALTA EL PRODUCTO EN EL INVENTARIO.... ATTE: KALEL
        */
        /*Inventario inventario = new Inventario();
        inventario.setId(producto.getId());
        inventario.setStock(stock);
       // inventarioDAO.insertar(inventario);*/
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