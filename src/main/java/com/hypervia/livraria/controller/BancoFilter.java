/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hypervia.livraria.controller;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 *
 * @author nilton
 */
@WebFilter(servletNames = {"Faces Servlet"})
public class BancoFilter implements Filter {
    
  
    private EntityManagerFactory emf;
    
    public BancoFilter() {
    }    
    
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        request.setAttribute("conexao", emf);
    }
       
   

   

    
    public void destroy() {        
    }

   
    public void init(FilterConfig filterConfig) {        
        
        this.emf = Persistence.createEntityManagerFactory("com.hypervia_livraria_war_1.0-SNAPSHOTPU");
    }

   
    
   
}
