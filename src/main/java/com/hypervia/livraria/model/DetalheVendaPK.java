/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nilton
 */
@Embeddable
public class DetalheVendaPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_produto")
    private int codProduto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_venda")
    private int codVenda;

    public DetalheVendaPK() {
    }

    public DetalheVendaPK(int codProduto, int codVenda) {
        this.codProduto = codProduto;
        this.codVenda = codVenda;
    }

    public int getCodProduto() {
        return codProduto;
    }

    public void setCodProduto(int codProduto) {
        this.codProduto = codProduto;
    }

    public int getCodVenda() {
        return codVenda;
    }

    public void setCodVenda(int codVenda) {
        this.codVenda = codVenda;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) codProduto;
        hash += (int) codVenda;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalheVendaPK)) {
            return false;
        }
        DetalheVendaPK other = (DetalheVendaPK) object;
        if (this.codProduto != other.codProduto) {
            return false;
        }
        if (this.codVenda != other.codVenda) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hypervia.livraria.model.DetalheVendaPK[ codProduto=" + codProduto + ", codVenda=" + codVenda + " ]";
    }
    
}
