package mx.puntodeventa.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.io.Serializable;

@Entity
@Table(name = "proveedor")
public class Proveedor{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproveedor")
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "contacto", nullable = false)
    private String contacto;
    @Column(name = "marca", nullable = true)
    private String marca;

    public int getId() {if (id == null) {
        return 0;
    }
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }
}