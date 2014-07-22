/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.model;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nilton
 */
@Entity
@Table(name = "promocao")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Promocao.findAll", query = "SELECT p FROM Promocao p"),
    @NamedQuery(name = "Promocao.findByIdpromocao", query = "SELECT p FROM Promocao p WHERE p.idpromocao = :idpromocao"),
    @NamedQuery(name = "Promocao.findByPreco", query = "SELECT p FROM Promocao p WHERE p.preco = :preco"),
    @NamedQuery(name = "Promocao.findByEstado", query = "SELECT p FROM Promocao p WHERE p.estado = :estado")})
public class Promocao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idpromocao")
    private Integer idpromocao;
    @Column(name = "preco")
    private Long preco;
    @Size(max = 20)
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private Estado estado;    
    @JoinColumn(name = "cod_produto", referencedColumnName = "idproduto")
    @ManyToOne(optional = false)
    private Produto codProduto;

    public Promocao() {
    }

    public Promocao(Integer idpromocao) {
        this.idpromocao = idpromocao;
    }

    public Integer getIdpromocao() {
        return idpromocao;
    }

    public void setIdpromocao(Integer idpromocao) {
        this.idpromocao = idpromocao;
    }

    public Long getPreco() {
        return preco;
    }

    public void setPreco(Long preco) {
        this.preco = preco;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Produto getCodProduto() {
        return codProduto;
    }

    public void setCodProduto(Produto codProduto) {
        this.codProduto = codProduto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idpromocao != null ? idpromocao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Promocao)) {
            return false;
        }
        Promocao other = (Promocao) object;
        if ((this.idpromocao == null && other.idpromocao != null) || (this.idpromocao != null && !this.idpromocao.equals(other.idpromocao))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hypervia.livraria.model.Promocao[ idpromocao=" + idpromocao + " ]";
    }
    
}
