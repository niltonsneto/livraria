/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.controller;

import com.hypervia.livraria.controller.exceptions.IllegalOrphanException;
import com.hypervia.livraria.controller.exceptions.NonexistentEntityException;
import com.hypervia.livraria.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.hypervia.livraria.model.Unidade;
import com.hypervia.livraria.model.Categoria;
import com.hypervia.livraria.model.DetalheVenda;
import com.hypervia.livraria.model.Produto;
import java.util.ArrayList;
import java.util.List;
import com.hypervia.livraria.model.Promocao;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nilton
 */
public class ProdutoJpaController implements Serializable {

    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produto produto) throws PreexistingEntityException, Exception {
        if (produto.getDetalheVendaList() == null) {
            produto.setDetalheVendaList(new ArrayList<DetalheVenda>());
        }
        if (produto.getPromocaoList() == null) {
            produto.setPromocaoList(new ArrayList<Promocao>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Unidade codUnidade = produto.getCodUnidade();
            if (codUnidade != null) {
                codUnidade = em.getReference(codUnidade.getClass(), codUnidade.getIdunidade());
                produto.setCodUnidade(codUnidade);
            }
            Categoria codCategoria = produto.getCodCategoria();
            if (codCategoria != null) {
                codCategoria = em.getReference(codCategoria.getClass(), codCategoria.getIdcategoria());
                produto.setCodCategoria(codCategoria);
            }
            List<DetalheVenda> attachedDetalheVendaList = new ArrayList<DetalheVenda>();
            for (DetalheVenda detalheVendaListDetalheVendaToAttach : produto.getDetalheVendaList()) {
                detalheVendaListDetalheVendaToAttach = em.getReference(detalheVendaListDetalheVendaToAttach.getClass(), detalheVendaListDetalheVendaToAttach.getDetalheVendaPK());
                attachedDetalheVendaList.add(detalheVendaListDetalheVendaToAttach);
            }
            produto.setDetalheVendaList(attachedDetalheVendaList);
            List<Promocao> attachedPromocaoList = new ArrayList<Promocao>();
            for (Promocao promocaoListPromocaoToAttach : produto.getPromocaoList()) {
                promocaoListPromocaoToAttach = em.getReference(promocaoListPromocaoToAttach.getClass(), promocaoListPromocaoToAttach.getIdpromocao());
                attachedPromocaoList.add(promocaoListPromocaoToAttach);
            }
            produto.setPromocaoList(attachedPromocaoList);
            em.persist(produto);
            if (codUnidade != null) {
                codUnidade.getProdutoList().add(produto);
                codUnidade = em.merge(codUnidade);
            }
            if (codCategoria != null) {
                codCategoria.getProdutoList().add(produto);
                codCategoria = em.merge(codCategoria);
            }
            for (DetalheVenda detalheVendaListDetalheVenda : produto.getDetalheVendaList()) {
                Produto oldProdutoOfDetalheVendaListDetalheVenda = detalheVendaListDetalheVenda.getProduto();
                detalheVendaListDetalheVenda.setProduto(produto);
                detalheVendaListDetalheVenda = em.merge(detalheVendaListDetalheVenda);
                if (oldProdutoOfDetalheVendaListDetalheVenda != null) {
                    oldProdutoOfDetalheVendaListDetalheVenda.getDetalheVendaList().remove(detalheVendaListDetalheVenda);
                    oldProdutoOfDetalheVendaListDetalheVenda = em.merge(oldProdutoOfDetalheVendaListDetalheVenda);
                }
            }
            for (Promocao promocaoListPromocao : produto.getPromocaoList()) {
                Produto oldCodProdutoOfPromocaoListPromocao = promocaoListPromocao.getCodProduto();
                promocaoListPromocao.setCodProduto(produto);
                promocaoListPromocao = em.merge(promocaoListPromocao);
                if (oldCodProdutoOfPromocaoListPromocao != null) {
                    oldCodProdutoOfPromocaoListPromocao.getPromocaoList().remove(promocaoListPromocao);
                    oldCodProdutoOfPromocaoListPromocao = em.merge(oldCodProdutoOfPromocaoListPromocao);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProduto(produto.getIdproduto()) != null) {
                throw new PreexistingEntityException("Produto " + produto + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produto produto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto persistentProduto = em.find(Produto.class, produto.getIdproduto());
            Unidade codUnidadeOld = persistentProduto.getCodUnidade();
            Unidade codUnidadeNew = produto.getCodUnidade();
            Categoria codCategoriaOld = persistentProduto.getCodCategoria();
            Categoria codCategoriaNew = produto.getCodCategoria();
            List<DetalheVenda> detalheVendaListOld = persistentProduto.getDetalheVendaList();
            List<DetalheVenda> detalheVendaListNew = produto.getDetalheVendaList();
            List<Promocao> promocaoListOld = persistentProduto.getPromocaoList();
            List<Promocao> promocaoListNew = produto.getPromocaoList();
            List<String> illegalOrphanMessages = null;
            for (DetalheVenda detalheVendaListOldDetalheVenda : detalheVendaListOld) {
                if (!detalheVendaListNew.contains(detalheVendaListOldDetalheVenda)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalheVenda " + detalheVendaListOldDetalheVenda + " since its produto field is not nullable.");
                }
            }
            for (Promocao promocaoListOldPromocao : promocaoListOld) {
                if (!promocaoListNew.contains(promocaoListOldPromocao)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Promocao " + promocaoListOldPromocao + " since its codProduto field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codUnidadeNew != null) {
                codUnidadeNew = em.getReference(codUnidadeNew.getClass(), codUnidadeNew.getIdunidade());
                produto.setCodUnidade(codUnidadeNew);
            }
            if (codCategoriaNew != null) {
                codCategoriaNew = em.getReference(codCategoriaNew.getClass(), codCategoriaNew.getIdcategoria());
                produto.setCodCategoria(codCategoriaNew);
            }
            List<DetalheVenda> attachedDetalheVendaListNew = new ArrayList<DetalheVenda>();
            for (DetalheVenda detalheVendaListNewDetalheVendaToAttach : detalheVendaListNew) {
                detalheVendaListNewDetalheVendaToAttach = em.getReference(detalheVendaListNewDetalheVendaToAttach.getClass(), detalheVendaListNewDetalheVendaToAttach.getDetalheVendaPK());
                attachedDetalheVendaListNew.add(detalheVendaListNewDetalheVendaToAttach);
            }
            detalheVendaListNew = attachedDetalheVendaListNew;
            produto.setDetalheVendaList(detalheVendaListNew);
            List<Promocao> attachedPromocaoListNew = new ArrayList<Promocao>();
            for (Promocao promocaoListNewPromocaoToAttach : promocaoListNew) {
                promocaoListNewPromocaoToAttach = em.getReference(promocaoListNewPromocaoToAttach.getClass(), promocaoListNewPromocaoToAttach.getIdpromocao());
                attachedPromocaoListNew.add(promocaoListNewPromocaoToAttach);
            }
            promocaoListNew = attachedPromocaoListNew;
            produto.setPromocaoList(promocaoListNew);
            produto = em.merge(produto);
            if (codUnidadeOld != null && !codUnidadeOld.equals(codUnidadeNew)) {
                codUnidadeOld.getProdutoList().remove(produto);
                codUnidadeOld = em.merge(codUnidadeOld);
            }
            if (codUnidadeNew != null && !codUnidadeNew.equals(codUnidadeOld)) {
                codUnidadeNew.getProdutoList().add(produto);
                codUnidadeNew = em.merge(codUnidadeNew);
            }
            if (codCategoriaOld != null && !codCategoriaOld.equals(codCategoriaNew)) {
                codCategoriaOld.getProdutoList().remove(produto);
                codCategoriaOld = em.merge(codCategoriaOld);
            }
            if (codCategoriaNew != null && !codCategoriaNew.equals(codCategoriaOld)) {
                codCategoriaNew.getProdutoList().add(produto);
                codCategoriaNew = em.merge(codCategoriaNew);
            }
            for (DetalheVenda detalheVendaListNewDetalheVenda : detalheVendaListNew) {
                if (!detalheVendaListOld.contains(detalheVendaListNewDetalheVenda)) {
                    Produto oldProdutoOfDetalheVendaListNewDetalheVenda = detalheVendaListNewDetalheVenda.getProduto();
                    detalheVendaListNewDetalheVenda.setProduto(produto);
                    detalheVendaListNewDetalheVenda = em.merge(detalheVendaListNewDetalheVenda);
                    if (oldProdutoOfDetalheVendaListNewDetalheVenda != null && !oldProdutoOfDetalheVendaListNewDetalheVenda.equals(produto)) {
                        oldProdutoOfDetalheVendaListNewDetalheVenda.getDetalheVendaList().remove(detalheVendaListNewDetalheVenda);
                        oldProdutoOfDetalheVendaListNewDetalheVenda = em.merge(oldProdutoOfDetalheVendaListNewDetalheVenda);
                    }
                }
            }
            for (Promocao promocaoListNewPromocao : promocaoListNew) {
                if (!promocaoListOld.contains(promocaoListNewPromocao)) {
                    Produto oldCodProdutoOfPromocaoListNewPromocao = promocaoListNewPromocao.getCodProduto();
                    promocaoListNewPromocao.setCodProduto(produto);
                    promocaoListNewPromocao = em.merge(promocaoListNewPromocao);
                    if (oldCodProdutoOfPromocaoListNewPromocao != null && !oldCodProdutoOfPromocaoListNewPromocao.equals(produto)) {
                        oldCodProdutoOfPromocaoListNewPromocao.getPromocaoList().remove(promocaoListNewPromocao);
                        oldCodProdutoOfPromocaoListNewPromocao = em.merge(oldCodProdutoOfPromocaoListNewPromocao);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produto.getIdproduto();
                if (findProduto(id) == null) {
                    throw new NonexistentEntityException("The produto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto produto;
            try {
                produto = em.getReference(Produto.class, id);
                produto.getIdproduto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetalheVenda> detalheVendaListOrphanCheck = produto.getDetalheVendaList();
            for (DetalheVenda detalheVendaListOrphanCheckDetalheVenda : detalheVendaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produto (" + produto + ") cannot be destroyed since the DetalheVenda " + detalheVendaListOrphanCheckDetalheVenda + " in its detalheVendaList field has a non-nullable produto field.");
            }
            List<Promocao> promocaoListOrphanCheck = produto.getPromocaoList();
            for (Promocao promocaoListOrphanCheckPromocao : promocaoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produto (" + produto + ") cannot be destroyed since the Promocao " + promocaoListOrphanCheckPromocao + " in its promocaoList field has a non-nullable codProduto field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Unidade codUnidade = produto.getCodUnidade();
            if (codUnidade != null) {
                codUnidade.getProdutoList().remove(produto);
                codUnidade = em.merge(codUnidade);
            }
            Categoria codCategoria = produto.getCodCategoria();
            if (codCategoria != null) {
                codCategoria.getProdutoList().remove(produto);
                codCategoria = em.merge(codCategoria);
            }
            em.remove(produto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produto> findProdutoEntities() {
        return findProdutoEntities(true, -1, -1);
    }

    public List<Produto> findProdutoEntities(int maxResults, int firstResult) {
        return findProdutoEntities(false, maxResults, firstResult);
    }

    private List<Produto> findProdutoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Produto findProduto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Produto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
