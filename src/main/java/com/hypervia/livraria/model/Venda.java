/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nilton
 */
@Entity
@Table(name = "venda")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Venda.findAll", query = "SELECT v FROM Venda v"),
    @NamedQuery(name = "Venda.findByIdvenda", query = "SELECT v FROM Venda v WHERE v.idvenda = :idvenda"),
    @NamedQuery(name = "Venda.findByDataVenda", query = "SELECT v FROM Venda v WHERE v.dataVenda = :dataVenda")})
public class Venda implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idvenda")
    private Integer idvenda;
    @Column(name = "data_venda")
    @Temporal(TemporalType.DATE)
    private Date dataVenda;
    @JoinColumn(name = "cod_usuario", referencedColumnName = "idusuario")
    @ManyToOne(optional = false)
    private Usuario codUsuario;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venda")
    private List<DetalheVenda> detalheVendaList;

    public Venda() {
    }

    public Venda(Integer idvenda) {
        this.idvenda = idvenda;
    }

    public Integer getIdvenda() {
        return idvenda;
    }

    public void setIdvenda(Integer idvenda) {
        this.idvenda = idvenda;
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Usuario getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(Usuario codUsuario) {
        this.codUsuario = codUsuario;
    }

    @XmlTransient
    public List<DetalheVenda> getDetalheVendaList() {
        return detalheVendaList;
    }

    public void setDetalheVendaList(List<DetalheVenda> detalheVendaList) {
        this.detalheVendaList = detalheVendaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idvenda != null ? idvenda.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Venda)) {
            return false;
        }
        Venda other = (Venda) object;
        if ((this.idvenda == null && other.idvenda != null) || (this.idvenda != null && !this.idvenda.equals(other.idvenda))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hypervia.livraria.model.Venda[ idvenda=" + idvenda + " ]";
    }
    
}
