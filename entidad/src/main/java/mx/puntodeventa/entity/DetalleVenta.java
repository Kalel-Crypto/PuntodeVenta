package mx.puntodeventa.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "detalleventa")
public class DetalleVenta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddetalleVenta")
    private Integer id;

    @Column(name = "idVenta", nullable = false)
    private Integer idventa;

    @Column(name = "idProducto", nullable = false)
    private Integer idProducto;

    @Column(name = "precioUnitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getIdventa() {
        return idventa;
    }

    public void setIdventa(Integer idventa) {
        this.idventa = idventa;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}