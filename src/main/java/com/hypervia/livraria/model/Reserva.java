/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nilton
 */
@Entity
@Table(name = "reserva")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reserva.findAll", query = "SELECT r FROM Reserva r"),
    @NamedQuery(name = "Reserva.findByIdreserva", query = "SELECT r FROM Reserva r WHERE r.idreserva = :idreserva"),
    @NamedQuery(name = "Reserva.findByNome", query = "SELECT r FROM Reserva r WHERE r.nome = :nome"),
    @NamedQuery(name = "Reserva.findByDescricao", query = "SELECT r FROM Reserva r WHERE r.descricao = :descricao"),
    @NamedQuery(name = "Reserva.findByDataReserva", query = "SELECT r FROM Reserva r WHERE r.dataReserva = :dataReserva"),
    @NamedQuery(name = "Reserva.findByEstado", query = "SELECT r FROM Reserva r WHERE r.estado = :estado")})
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idreserva")
    private Integer idreserva;
    @Size(max = 255)
    @Column(name = "nome")
    private String nome;
    @Size(max = 255)
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "data_reserva")
    @Temporal(TemporalType.DATE)
    private Date dataReserva;
    @Size(max = 20)
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private Estado estado;
    @JoinColumn(name = "cod_usuario", referencedColumnName = "idusuario")
    @ManyToOne(optional = false)
    private Usuario codUsuario;

    public Reserva() {
    }

    public Reserva(Integer idreserva) {
        this.idreserva = idreserva;
    }

    public Integer getIdreserva() {
        return idreserva;
    }

    public void setIdreserva(Integer idreserva) {
        this.idreserva = idreserva;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Usuario getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(Usuario codUsuario) {
        this.codUsuario = codUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idreserva != null ? idreserva.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reserva)) {
            return false;
        }
        Reserva other = (Reserva) object;
        if ((this.idreserva == null && other.idreserva != null) || (this.idreserva != null && !this.idreserva.equals(other.idreserva))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hypervia.livraria.model.Reserva[ idreserva=" + idreserva + " ]";
    }
    
}
