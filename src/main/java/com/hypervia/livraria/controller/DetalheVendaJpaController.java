/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.controller;

import com.hypervia.livraria.controller.exceptions.NonexistentEntityException;
import com.hypervia.livraria.controller.exceptions.PreexistingEntityException;
import com.hypervia.livraria.model.DetalheVenda;
import com.hypervia.livraria.model.DetalheVendaPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.hypervia.livraria.model.Venda;
import com.hypervia.livraria.model.Produto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nilton
 */
public class DetalheVendaJpaController implements Serializable {

    public DetalheVendaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalheVenda detalheVenda) throws PreexistingEntityException, Exception {
        if (detalheVenda.getDetalheVendaPK() == null) {
            detalheVenda.setDetalheVendaPK(new DetalheVendaPK());
        }
        detalheVenda.getDetalheVendaPK().setCodProduto(detalheVenda.getProduto().getIdproduto());
        detalheVenda.getDetalheVendaPK().setCodVenda(detalheVenda.getVenda().getIdvenda());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venda venda = detalheVenda.getVenda();
            if (venda != null) {
                venda = em.getReference(venda.getClass(), venda.getIdvenda());
                detalheVenda.setVenda(venda);
            }
            Produto produto = detalheVenda.getProduto();
            if (produto != null) {
                produto = em.getReference(produto.getClass(), produto.getIdproduto());
                detalheVenda.setProduto(produto);
            }
            em.persist(detalheVenda);
            if (venda != null) {
                venda.getDetalheVendaList().add(detalheVenda);
                venda = em.merge(venda);
            }
            if (produto != null) {
                produto.getDetalheVendaList().add(detalheVenda);
                produto = em.merge(produto);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDetalheVenda(detalheVenda.getDetalheVendaPK()) != null) {
                throw new PreexistingEntityException("DetalheVenda " + detalheVenda + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DetalheVenda detalheVenda) throws NonexistentEntityException, Exception {
        detalheVenda.getDetalheVendaPK().setCodProduto(detalheVenda.getProduto().getIdproduto());
        detalheVenda.getDetalheVendaPK().setCodVenda(detalheVenda.getVenda().getIdvenda());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalheVenda persistentDetalheVenda = em.find(DetalheVenda.class, detalheVenda.getDetalheVendaPK());
            Venda vendaOld = persistentDetalheVenda.getVenda();
            Venda vendaNew = detalheVenda.getVenda();
            Produto produtoOld = persistentDetalheVenda.getProduto();
            Produto produtoNew = detalheVenda.getProduto();
            if (vendaNew != null) {
                vendaNew = em.getReference(vendaNew.getClass(), vendaNew.getIdvenda());
                detalheVenda.setVenda(vendaNew);
            }
            if (produtoNew != null) {
                produtoNew = em.getReference(produtoNew.getClass(), produtoNew.getIdproduto());
                detalheVenda.setProduto(produtoNew);
            }
            detalheVenda = em.merge(detalheVenda);
            if (vendaOld != null && !vendaOld.equals(vendaNew)) {
                vendaOld.getDetalheVendaList().remove(detalheVenda);
                vendaOld = em.merge(vendaOld);
            }
            if (vendaNew != null && !vendaNew.equals(vendaOld)) {
                vendaNew.getDetalheVendaList().add(detalheVenda);
                vendaNew = em.merge(vendaNew);
            }
            if (produtoOld != null && !produtoOld.equals(produtoNew)) {
                produtoOld.getDetalheVendaList().remove(detalheVenda);
                produtoOld = em.merge(produtoOld);
            }
            if (produtoNew != null && !produtoNew.equals(produtoOld)) {
                produtoNew.getDetalheVendaList().add(detalheVenda);
                produtoNew = em.merge(produtoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                DetalheVendaPK id = detalheVenda.getDetalheVendaPK();
                if (findDetalheVenda(id) == null) {
                    throw new NonexistentEntityException("The detalheVenda with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DetalheVendaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DetalheVenda detalheVenda;
            try {
                detalheVenda = em.getReference(DetalheVenda.class, id);
                detalheVenda.getDetalheVendaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalheVenda with id " + id + " no longer exists.", enfe);
            }
            Venda venda = detalheVenda.getVenda();
            if (venda != null) {
                venda.getDetalheVendaList().remove(detalheVenda);
                venda = em.merge(venda);
            }
            Produto produto = detalheVenda.getProduto();
            if (produto != null) {
                produto.getDetalheVendaList().remove(detalheVenda);
                produto = em.merge(produto);
            }
            em.remove(detalheVenda);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DetalheVenda> findDetalheVendaEntities() {
        return findDetalheVendaEntities(true, -1, -1);
    }

    public List<DetalheVenda> findDetalheVendaEntities(int maxResults, int firstResult) {
        return findDetalheVendaEntities(false, maxResults, firstResult);
    }

    private List<DetalheVenda> findDetalheVendaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalheVenda.class));
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

    public DetalheVenda findDetalheVenda(DetalheVendaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalheVenda.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalheVendaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalheVenda> rt = cq.from(DetalheVenda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
