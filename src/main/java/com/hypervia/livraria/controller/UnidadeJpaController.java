/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.controller;

import com.hypervia.livraria.controller.exceptions.IllegalOrphanException;
import com.hypervia.livraria.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.hypervia.livraria.model.Produto;
import com.hypervia.livraria.model.Unidade;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nilton
 */
public class UnidadeJpaController implements Serializable {

    public UnidadeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Unidade unidade) {
        if (unidade.getProdutoList() == null) {
            unidade.setProdutoList(new ArrayList<Produto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Produto> attachedProdutoList = new ArrayList<Produto>();
            for (Produto produtoListProdutoToAttach : unidade.getProdutoList()) {
                produtoListProdutoToAttach = em.getReference(produtoListProdutoToAttach.getClass(), produtoListProdutoToAttach.getIdproduto());
                attachedProdutoList.add(produtoListProdutoToAttach);
            }
            unidade.setProdutoList(attachedProdutoList);
            em.persist(unidade);
            for (Produto produtoListProduto : unidade.getProdutoList()) {
                Unidade oldCodUnidadeOfProdutoListProduto = produtoListProduto.getCodUnidade();
                produtoListProduto.setCodUnidade(unidade);
                produtoListProduto = em.merge(produtoListProduto);
                if (oldCodUnidadeOfProdutoListProduto != null) {
                    oldCodUnidadeOfProdutoListProduto.getProdutoList().remove(produtoListProduto);
                    oldCodUnidadeOfProdutoListProduto = em.merge(oldCodUnidadeOfProdutoListProduto);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Unidade unidade) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Unidade persistentUnidade = em.find(Unidade.class, unidade.getIdunidade());
            List<Produto> produtoListOld = persistentUnidade.getProdutoList();
            List<Produto> produtoListNew = unidade.getProdutoList();
            List<String> illegalOrphanMessages = null;
            for (Produto produtoListOldProduto : produtoListOld) {
                if (!produtoListNew.contains(produtoListOldProduto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Produto " + produtoListOldProduto + " since its codUnidade field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Produto> attachedProdutoListNew = new ArrayList<Produto>();
            for (Produto produtoListNewProdutoToAttach : produtoListNew) {
                produtoListNewProdutoToAttach = em.getReference(produtoListNewProdutoToAttach.getClass(), produtoListNewProdutoToAttach.getIdproduto());
                attachedProdutoListNew.add(produtoListNewProdutoToAttach);
            }
            produtoListNew = attachedProdutoListNew;
            unidade.setProdutoList(produtoListNew);
            unidade = em.merge(unidade);
            for (Produto produtoListNewProduto : produtoListNew) {
                if (!produtoListOld.contains(produtoListNewProduto)) {
                    Unidade oldCodUnidadeOfProdutoListNewProduto = produtoListNewProduto.getCodUnidade();
                    produtoListNewProduto.setCodUnidade(unidade);
                    produtoListNewProduto = em.merge(produtoListNewProduto);
                    if (oldCodUnidadeOfProdutoListNewProduto != null && !oldCodUnidadeOfProdutoListNewProduto.equals(unidade)) {
                        oldCodUnidadeOfProdutoListNewProduto.getProdutoList().remove(produtoListNewProduto);
                        oldCodUnidadeOfProdutoListNewProduto = em.merge(oldCodUnidadeOfProdutoListNewProduto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = unidade.getIdunidade();
                if (findUnidade(id) == null) {
                    throw new NonexistentEntityException("The unidade with id " + id + " no longer exists.");
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
            Unidade unidade;
            try {
                unidade = em.getReference(Unidade.class, id);
                unidade.getIdunidade();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The unidade with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Produto> produtoListOrphanCheck = unidade.getProdutoList();
            for (Produto produtoListOrphanCheckProduto : produtoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Unidade (" + unidade + ") cannot be destroyed since the Produto " + produtoListOrphanCheckProduto + " in its produtoList field has a non-nullable codUnidade field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(unidade);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Unidade> findUnidadeEntities() {
        return findUnidadeEntities(true, -1, -1);
    }

    public List<Unidade> findUnidadeEntities(int maxResults, int firstResult) {
        return findUnidadeEntities(false, maxResults, firstResult);
    }

    private List<Unidade> findUnidadeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Unidade.class));
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

    public Unidade findUnidade(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Unidade.class, id);
        } finally {
            em.close();
        }
    }

    public int getUnidadeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Unidade> rt = cq.from(Unidade.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
