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
import com.hypervia.livraria.model.Usuario;
import com.hypervia.livraria.model.DetalheVenda;
import com.hypervia.livraria.model.Venda;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nilton
 */
public class VendaJpaController implements Serializable {

    public VendaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Venda venda) {
        if (venda.getDetalheVendaList() == null) {
            venda.setDetalheVendaList(new ArrayList<DetalheVenda>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario codUsuario = venda.getCodUsuario();
            if (codUsuario != null) {
                codUsuario = em.getReference(codUsuario.getClass(), codUsuario.getIdusuario());
                venda.setCodUsuario(codUsuario);
            }
            List<DetalheVenda> attachedDetalheVendaList = new ArrayList<DetalheVenda>();
            for (DetalheVenda detalheVendaListDetalheVendaToAttach : venda.getDetalheVendaList()) {
                detalheVendaListDetalheVendaToAttach = em.getReference(detalheVendaListDetalheVendaToAttach.getClass(), detalheVendaListDetalheVendaToAttach.getDetalheVendaPK());
                attachedDetalheVendaList.add(detalheVendaListDetalheVendaToAttach);
            }
            venda.setDetalheVendaList(attachedDetalheVendaList);
            em.persist(venda);
            if (codUsuario != null) {
                codUsuario.getVendaList().add(venda);
                codUsuario = em.merge(codUsuario);
            }
            for (DetalheVenda detalheVendaListDetalheVenda : venda.getDetalheVendaList()) {
                Venda oldVendaOfDetalheVendaListDetalheVenda = detalheVendaListDetalheVenda.getVenda();
                detalheVendaListDetalheVenda.setVenda(venda);
                detalheVendaListDetalheVenda = em.merge(detalheVendaListDetalheVenda);
                if (oldVendaOfDetalheVendaListDetalheVenda != null) {
                    oldVendaOfDetalheVendaListDetalheVenda.getDetalheVendaList().remove(detalheVendaListDetalheVenda);
                    oldVendaOfDetalheVendaListDetalheVenda = em.merge(oldVendaOfDetalheVendaListDetalheVenda);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Venda venda) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venda persistentVenda = em.find(Venda.class, venda.getIdvenda());
            Usuario codUsuarioOld = persistentVenda.getCodUsuario();
            Usuario codUsuarioNew = venda.getCodUsuario();
            List<DetalheVenda> detalheVendaListOld = persistentVenda.getDetalheVendaList();
            List<DetalheVenda> detalheVendaListNew = venda.getDetalheVendaList();
            List<String> illegalOrphanMessages = null;
            for (DetalheVenda detalheVendaListOldDetalheVenda : detalheVendaListOld) {
                if (!detalheVendaListNew.contains(detalheVendaListOldDetalheVenda)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalheVenda " + detalheVendaListOldDetalheVenda + " since its venda field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codUsuarioNew != null) {
                codUsuarioNew = em.getReference(codUsuarioNew.getClass(), codUsuarioNew.getIdusuario());
                venda.setCodUsuario(codUsuarioNew);
            }
            List<DetalheVenda> attachedDetalheVendaListNew = new ArrayList<DetalheVenda>();
            for (DetalheVenda detalheVendaListNewDetalheVendaToAttach : detalheVendaListNew) {
                detalheVendaListNewDetalheVendaToAttach = em.getReference(detalheVendaListNewDetalheVendaToAttach.getClass(), detalheVendaListNewDetalheVendaToAttach.getDetalheVendaPK());
                attachedDetalheVendaListNew.add(detalheVendaListNewDetalheVendaToAttach);
            }
            detalheVendaListNew = attachedDetalheVendaListNew;
            venda.setDetalheVendaList(detalheVendaListNew);
            venda = em.merge(venda);
            if (codUsuarioOld != null && !codUsuarioOld.equals(codUsuarioNew)) {
                codUsuarioOld.getVendaList().remove(venda);
                codUsuarioOld = em.merge(codUsuarioOld);
            }
            if (codUsuarioNew != null && !codUsuarioNew.equals(codUsuarioOld)) {
                codUsuarioNew.getVendaList().add(venda);
                codUsuarioNew = em.merge(codUsuarioNew);
            }
            for (DetalheVenda detalheVendaListNewDetalheVenda : detalheVendaListNew) {
                if (!detalheVendaListOld.contains(detalheVendaListNewDetalheVenda)) {
                    Venda oldVendaOfDetalheVendaListNewDetalheVenda = detalheVendaListNewDetalheVenda.getVenda();
                    detalheVendaListNewDetalheVenda.setVenda(venda);
                    detalheVendaListNewDetalheVenda = em.merge(detalheVendaListNewDetalheVenda);
                    if (oldVendaOfDetalheVendaListNewDetalheVenda != null && !oldVendaOfDetalheVendaListNewDetalheVenda.equals(venda)) {
                        oldVendaOfDetalheVendaListNewDetalheVenda.getDetalheVendaList().remove(detalheVendaListNewDetalheVenda);
                        oldVendaOfDetalheVendaListNewDetalheVenda = em.merge(oldVendaOfDetalheVendaListNewDetalheVenda);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = venda.getIdvenda();
                if (findVenda(id) == null) {
                    throw new NonexistentEntityException("The venda with id " + id + " no longer exists.");
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
            Venda venda;
            try {
                venda = em.getReference(Venda.class, id);
                venda.getIdvenda();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The venda with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetalheVenda> detalheVendaListOrphanCheck = venda.getDetalheVendaList();
            for (DetalheVenda detalheVendaListOrphanCheckDetalheVenda : detalheVendaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venda (" + venda + ") cannot be destroyed since the DetalheVenda " + detalheVendaListOrphanCheckDetalheVenda + " in its detalheVendaList field has a non-nullable venda field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario codUsuario = venda.getCodUsuario();
            if (codUsuario != null) {
                codUsuario.getVendaList().remove(venda);
                codUsuario = em.merge(codUsuario);
            }
            em.remove(venda);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Venda> findVendaEntities() {
        return findVendaEntities(true, -1, -1);
    }

    public List<Venda> findVendaEntities(int maxResults, int firstResult) {
        return findVendaEntities(false, maxResults, firstResult);
    }

    private List<Venda> findVendaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Venda.class));
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

    public Venda findVenda(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Venda.class, id);
        } finally {
            em.close();
        }
    }

    public int getVendaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Venda> rt = cq.from(Venda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
