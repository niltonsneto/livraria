/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author nilton
 */
public class JPAUtil {
    private EntityManagerFactory emf;

    public EntityManagerFactory getEmf() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpServletRequest req = (HttpServletRequest) ec.getRequest();
        return emf = (EntityManagerFactory) req.getAttribute("conexao");
    }
    
}
