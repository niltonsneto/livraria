/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.view;

import com.hypervia.livraria.controller.CategoriaJpaController;
import com.hypervia.livraria.model.Categoria;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;

/**
 *
 * @author nilton
 */
@ManagedBean
@ViewScoped
public class CategoriaBean {

    private Categoria cat = new Categoria();
    private CategoriaJpaController dao;
    private EntityManager em;
    public CategoriaBean() {
    }
    public void gravar(){
        dao = new CategoriaJpaController(null);
        dao.create(cat);
        this.cat = new Categoria();
    }

    public Categoria getCat() {
        return cat;
    }

    public void setCat(Categoria cat) {
        this.cat = cat;
    }
    
    
}
