/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nilton
 */
@Entity
@Table(name = "detalhe_venda")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DetalheVenda.findAll", query = "SELECT d FROM DetalheVenda d"),
    @NamedQuery(name = "DetalheVenda.findByCodProduto", query = "SELECT d FROM DetalheVenda d WHERE d.detalheVendaPK.codProduto = :codProduto"),
    @NamedQuery(name = "DetalheVenda.findByCodVenda", query = "SELECT d FROM DetalheVenda d WHERE d.detalheVendaPK.codVenda = :codVenda"),
    @NamedQuery(name = "DetalheVenda.findByQtde", query = "SELECT d FROM DetalheVenda d WHERE d.qtde = :qtde")})
public class DetalheVenda implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetalheVendaPK detalheVendaPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "qtde")
    private Double qtde;
    @JoinColumn(name = "cod_venda", referencedColumnName = "idvenda", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Venda venda;
    @JoinColumn(name = "cod_produto", referencedColumnName = "idproduto", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Produto produto;

    public DetalheVenda() {
    }

    public DetalheVenda(DetalheVendaPK detalheVendaPK) {
        this.detalheVendaPK = detalheVendaPK;
    }

    public DetalheVenda(int codProduto, int codVenda) {
        this.detalheVendaPK = new DetalheVendaPK(codProduto, codVenda);
    }

    public DetalheVendaPK getDetalheVendaPK() {
        return detalheVendaPK;
    }

    public void setDetalheVendaPK(DetalheVendaPK detalheVendaPK) {
        this.detalheVendaPK = detalheVendaPK;
    }

    public Double getQtde() {
        return qtde;
    }

    public void setQtde(Double qtde) {
        this.qtde = qtde;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detalheVendaPK != null ? detalheVendaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalheVenda)) {
            return false;
        }
        DetalheVenda other = (DetalheVenda) object;
        if ((this.detalheVendaPK == null && other.detalheVendaPK != null) || (this.detalheVendaPK != null && !this.detalheVendaPK.equals(other.detalheVendaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hypervia.livraria.model.DetalheVenda[ detalheVendaPK=" + detalheVendaPK + " ]";
    }
    
}
