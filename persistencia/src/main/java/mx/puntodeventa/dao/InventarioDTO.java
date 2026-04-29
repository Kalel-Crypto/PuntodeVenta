package mx.puntodeventa.dao;

public class InventarioDTO {

    private int idProducto;
    private String nombreProducto;
    private int stock;
    private double precio;
    private String proveedor;
    private int idProveedor;
    private int cantidadOperacion;

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {

        return nombreProducto;
    }

    public int getCantidadOperacion() {
        return cantidadOperacion;
    }

    public void setCantidadOperacion(int cantidadOperacion) {
        this.cantidadOperacion = cantidadOperacion;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }


}