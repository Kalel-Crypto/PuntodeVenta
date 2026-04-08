package mx.puntodeventa.entity;

import jakarta.persistence.*;



@Entity
@Table(name = "caja")

public class Caja{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcaja", nullable = false)
    private Integer idcaja;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;


    public Integer getIdcaja() {
        return idcaja;
    }

    public void setIdcaja(Integer idcaja) {
        this.idcaja = idcaja;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}