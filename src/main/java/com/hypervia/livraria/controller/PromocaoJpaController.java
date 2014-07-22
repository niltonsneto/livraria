/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.controller;

import com.hypervia.livraria.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.hypervia.livraria.model.Produto;
import com.hypervia.livraria.model.Promocao;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nilton
 */
public class PromocaoJpaController implements Serializable {

    public PromocaoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Promocao promocao) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto codProduto = promocao.getCodProduto();
            if (codProduto != null) {
                codProduto = em.getReference(codProduto.getClass(), codProduto.getIdproduto());
                promocao.setCodProduto(codProduto);
            }
            em.persist(promocao);
            if (codProduto != null) {
                codProduto.getPromocaoList().add(promocao);
                codProduto = em.merge(codProduto);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Promocao promocao) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Promocao persistentPromocao = em.find(Promocao.class, promocao.getIdpromocao());
            Produto codProdutoOld = persistentPromocao.getCodProduto();
            Produto codProdutoNew = promocao.getCodProduto();
            if (codProdutoNew != null) {
                codProdutoNew = em.getReference(codProdutoNew.getClass(), codProdutoNew.getIdproduto());
                promocao.setCodProduto(codProdutoNew);
            }
            promocao = em.merge(promocao);
            if (codProdutoOld != null && !codProdutoOld.equals(codProdutoNew)) {
                codProdutoOld.getPromocaoList().remove(promocao);
                codProdutoOld = em.merge(codProdutoOld);
            }
            if (codProdutoNew != null && !codProdutoNew.equals(codProdutoOld)) {
                codProdutoNew.getPromocaoList().add(promocao);
                codProdutoNew = em.merge(codProdutoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = promocao.getIdpromocao();
                if (findPromocao(id) == null) {
                    throw new NonexistentEntityException("The promocao with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Promocao promocao;
            try {
                promocao = em.getReference(Promocao.class, id);
                promocao.getIdpromocao();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The promocao with id " + id + " no longer exists.", enfe);
            }
            Produto codProduto = promocao.getCodProduto();
            if (codProduto != null) {
                codProduto.getPromocaoList().remove(promocao);
                codProduto = em.merge(codProduto);
            }
            em.remove(promocao);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Promocao> findPromocaoEntities() {
        return findPromocaoEntities(true, -1, -1);
    }

    public List<Promocao> findPromocaoEntities(int maxResults, int firstResult) {
        return findPromocaoEntities(false, maxResults, firstResult);
    }

    private List<Promocao> findPromocaoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Promocao.class));
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

    public Promocao findPromocao(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Promocao.class, id);
        } finally {
            em.close();
        }
    }

    public int getPromocaoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Promocao> rt = cq.from(Promocao.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
