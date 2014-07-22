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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author nilton
 */
@Entity
@Table(name = "produto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Produto.findAll", query = "SELECT p FROM Produto p"),
    @NamedQuery(name = "Produto.findByIdproduto", query = "SELECT p FROM Produto p WHERE p.idproduto = :idproduto"),
    @NamedQuery(name = "Produto.findByNome", query = "SELECT p FROM Produto p WHERE p.nome = :nome"),
    @NamedQuery(name = "Produto.findByValorUnitario", query = "SELECT p FROM Produto p WHERE p.valorUnitario = :valorUnitario"),
    @NamedQuery(name = "Produto.findByQtde", query = "SELECT p FROM Produto p WHERE p.qtde = :qtde"),
    @NamedQuery(name = "Produto.findByQtdeLimite", query = "SELECT p FROM Produto p WHERE p.qtdeLimite = :qtdeLimite"),
    @NamedQuery(name = "Produto.findByDataRegistro", query = "SELECT p FROM Produto p WHERE p.dataRegistro = :dataRegistro"),
    @NamedQuery(name = "Produto.findByEstado", query = "SELECT p FROM Produto p WHERE p.estado = :estado")})
public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idproduto")
    private Integer idproduto;
    @Size(max = 255)
    @Column(name = "nome")
    private String nome;
    @Lob
    @Size(max = 65535)
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "valor_unitario")
    private Long valorUnitario;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "qtde")
    private Double qtde;
    @Column(name = "qtde_limite")
    private Double qtdeLimite;
    @Column(name = "data_registro")
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Size(max = 20)
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private Estado estado;
    @JoinColumn(name = "cod_unidade", referencedColumnName = "idunidade")
    @ManyToOne(optional = false)
    private Unidade codUnidade;
    @JoinColumn(name = "cod_categoria", referencedColumnName = "idcategoria")
    @ManyToOne(optional = false)
    private Categoria codCategoria;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "produto")
    private List<DetalheVenda> detalheVendaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codProduto")
    private List<Promocao> promocaoList;

    public Produto() {
    }

    public Produto(Integer idproduto) {
        this.idproduto = idproduto;
    }

    public Integer getIdproduto() {
        return idproduto;
    }

    public void setIdproduto(Integer idproduto) {
        this.idproduto = idproduto;
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

    public Long getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Long valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getQtde() {
        return qtde;
    }

    public void setQtde(Double qtde) {
        this.qtde = qtde;
    }

    public Double getQtdeLimite() {
        return qtdeLimite;
    }

    public void setQtdeLimite(Double qtdeLimite) {
        this.qtdeLimite = qtdeLimite;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Unidade getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(Unidade codUnidade) {
        this.codUnidade = codUnidade;
    }

    public Categoria getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(Categoria codCategoria) {
        this.codCategoria = codCategoria;
    }

    @XmlTransient
    public List<DetalheVenda> getDetalheVendaList() {
        return detalheVendaList;
    }

    public void setDetalheVendaList(List<DetalheVenda> detalheVendaList) {
        this.detalheVendaList = detalheVendaList;
    }

    @XmlTransient
    public List<Promocao> getPromocaoList() {
        return promocaoList;
    }

    public void setPromocaoList(List<Promocao> promocaoList) {
        this.promocaoList = promocaoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idproduto != null ? idproduto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Produto)) {
            return false;
        }
        Produto other = (Produto) object;
        if ((this.idproduto == null && other.idproduto != null) || (this.idproduto != null && !this.idproduto.equals(other.idproduto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hypervia.livraria.model.Produto[ idproduto=" + idproduto + " ]";
    }
    
}
