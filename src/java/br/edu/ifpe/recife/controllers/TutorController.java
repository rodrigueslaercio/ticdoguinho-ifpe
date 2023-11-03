/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.controllers;

import br.edu.ifpe.recife.model.classes.Tutor;
import br.edu.ifpe.recife.model.dao.ManagerDao;
import br.edu.ifpe.recife.utils.PasswordSecurity;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author laerc
 */
@ManagedBean
@SessionScoped
public class TutorController {
    private Tutor cadastro; 
    
    @PostConstruct
    public void init() {
        this.cadastro = new Tutor();
    }
    
    public String insert() {
        String encryptPass = PasswordSecurity.encrypt(cadastro.getSenha());
        this.cadastro.setSenha(encryptPass);
        
        ManagerDao.getCurrentInstance().insert(this.cadastro);
        
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage("Tutor cadastrado com sucesso"));
          
        this.cadastro = new Tutor();
      
        return "index";
    }

    public Tutor getCadastro() {
        return cadastro;
    }

    public void setCadastro(Tutor cadastro) {
        this.cadastro = cadastro;
    }
    
    
}
