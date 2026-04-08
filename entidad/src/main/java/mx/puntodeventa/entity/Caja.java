package mx.puntodeventa.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List; // Importante para la asociación

@Entity
@Table(name = "caja")
public class Caja implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcaja")
    private int id;

    @Temporal(TemporalType.DATE)
    private Date fecha;

    private double totalVentas;

    @OneToMany(mappedBy = "caja")
    private List<Venta> ventas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
}