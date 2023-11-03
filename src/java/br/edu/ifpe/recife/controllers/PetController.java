/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.controllers;

import br.edu.ifpe.recife.model.classes.Pet;
import br.edu.ifpe.recife.model.dao.ManagerDao;
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
public class PetController {
    private Pet cadastro;
    
    @PostConstruct
    public void init() {
        this.cadastro = new Pet();
    }
    
    public String insert() {
        ManagerDao.getCurrentInstance().insert(this.cadastro);
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pet " + this.cadastro.getNome() 
                + " cadastrado com sucesso!"));
        
        this.cadastro = new Pet();
        
        return "index";
    }

    public Pet getCadastro() {
        return cadastro;
    }

    public void setCadastro(Pet cadastro) {
        this.cadastro = cadastro;
    }
    
    
}
