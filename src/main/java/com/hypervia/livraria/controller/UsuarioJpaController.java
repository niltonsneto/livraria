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
import com.hypervia.livraria.model.Venda;
import java.util.ArrayList;
import java.util.List;
import com.hypervia.livraria.model.Reserva;
import com.hypervia.livraria.model.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author nilton
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getVendaList() == null) {
            usuario.setVendaList(new ArrayList<Venda>());
        }
        if (usuario.getReservaList() == null) {
            usuario.setReservaList(new ArrayList<Reserva>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Venda> attachedVendaList = new ArrayList<Venda>();
            for (Venda vendaListVendaToAttach : usuario.getVendaList()) {
                vendaListVendaToAttach = em.getReference(vendaListVendaToAttach.getClass(), vendaListVendaToAttach.getIdvenda());
                attachedVendaList.add(vendaListVendaToAttach);
            }
            usuario.setVendaList(attachedVendaList);
            List<Reserva> attachedReservaList = new ArrayList<Reserva>();
            for (Reserva reservaListReservaToAttach : usuario.getReservaList()) {
                reservaListReservaToAttach = em.getReference(reservaListReservaToAttach.getClass(), reservaListReservaToAttach.getIdreserva());
                attachedReservaList.add(reservaListReservaToAttach);
            }
            usuario.setReservaList(attachedReservaList);
            em.persist(usuario);
            for (Venda vendaListVenda : usuario.getVendaList()) {
                Usuario oldCodUsuarioOfVendaListVenda = vendaListVenda.getCodUsuario();
                vendaListVenda.setCodUsuario(usuario);
                vendaListVenda = em.merge(vendaListVenda);
                if (oldCodUsuarioOfVendaListVenda != null) {
                    oldCodUsuarioOfVendaListVenda.getVendaList().remove(vendaListVenda);
                    oldCodUsuarioOfVendaListVenda = em.merge(oldCodUsuarioOfVendaListVenda);
                }
            }
            for (Reserva reservaListReserva : usuario.getReservaList()) {
                Usuario oldCodUsuarioOfReservaListReserva = reservaListReserva.getCodUsuario();
                reservaListReserva.setCodUsuario(usuario);
                reservaListReserva = em.merge(reservaListReserva);
                if (oldCodUsuarioOfReservaListReserva != null) {
                    oldCodUsuarioOfReservaListReserva.getReservaList().remove(reservaListReserva);
                    oldCodUsuarioOfReservaListReserva = em.merge(oldCodUsuarioOfReservaListReserva);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdusuario());
            List<Venda> vendaListOld = persistentUsuario.getVendaList();
            List<Venda> vendaListNew = usuario.getVendaList();
            List<Reserva> reservaListOld = persistentUsuario.getReservaList();
            List<Reserva> reservaListNew = usuario.getReservaList();
            List<String> illegalOrphanMessages = null;
            for (Venda vendaListOldVenda : vendaListOld) {
                if (!vendaListNew.contains(vendaListOldVenda)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Venda " + vendaListOldVenda + " since its codUsuario field is not nullable.");
                }
            }
            for (Reserva reservaListOldReserva : reservaListOld) {
                if (!reservaListNew.contains(reservaListOldReserva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reserva " + reservaListOldReserva + " since its codUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Venda> attachedVendaListNew = new ArrayList<Venda>();
            for (Venda vendaListNewVendaToAttach : vendaListNew) {
                vendaListNewVendaToAttach = em.getReference(vendaListNewVendaToAttach.getClass(), vendaListNewVendaToAttach.getIdvenda());
                attachedVendaListNew.add(vendaListNewVendaToAttach);
            }
            vendaListNew = attachedVendaListNew;
            usuario.setVendaList(vendaListNew);
            List<Reserva> attachedReservaListNew = new ArrayList<Reserva>();
            for (Reserva reservaListNewReservaToAttach : reservaListNew) {
                reservaListNewReservaToAttach = em.getReference(reservaListNewReservaToAttach.getClass(), reservaListNewReservaToAttach.getIdreserva());
                attachedReservaListNew.add(reservaListNewReservaToAttach);
            }
            reservaListNew = attachedReservaListNew;
            usuario.setReservaList(reservaListNew);
            usuario = em.merge(usuario);
            for (Venda vendaListNewVenda : vendaListNew) {
                if (!vendaListOld.contains(vendaListNewVenda)) {
                    Usuario oldCodUsuarioOfVendaListNewVenda = vendaListNewVenda.getCodUsuario();
                    vendaListNewVenda.setCodUsuario(usuario);
                    vendaListNewVenda = em.merge(vendaListNewVenda);
                    if (oldCodUsuarioOfVendaListNewVenda != null && !oldCodUsuarioOfVendaListNewVenda.equals(usuario)) {
                        oldCodUsuarioOfVendaListNewVenda.getVendaList().remove(vendaListNewVenda);
                        oldCodUsuarioOfVendaListNewVenda = em.merge(oldCodUsuarioOfVendaListNewVenda);
                    }
                }
            }
            for (Reserva reservaListNewReserva : reservaListNew) {
                if (!reservaListOld.contains(reservaListNewReserva)) {
                    Usuario oldCodUsuarioOfReservaListNewReserva = reservaListNewReserva.getCodUsuario();
                    reservaListNewReserva.setCodUsuario(usuario);
                    reservaListNewReserva = em.merge(reservaListNewReserva);
                    if (oldCodUsuarioOfReservaListNewReserva != null && !oldCodUsuarioOfReservaListNewReserva.equals(usuario)) {
                        oldCodUsuarioOfReservaListNewReserva.getReservaList().remove(reservaListNewReserva);
                        oldCodUsuarioOfReservaListNewReserva = em.merge(oldCodUsuarioOfReservaListNewReserva);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdusuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdusuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Venda> vendaListOrphanCheck = usuario.getVendaList();
            for (Venda vendaListOrphanCheckVenda : vendaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Venda " + vendaListOrphanCheckVenda + " in its vendaList field has a non-nullable codUsuario field.");
            }
            List<Reserva> reservaListOrphanCheck = usuario.getReservaList();
            for (Reserva reservaListOrphanCheckReserva : reservaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Reserva " + reservaListOrphanCheckReserva + " in its reservaList field has a non-nullable codUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
