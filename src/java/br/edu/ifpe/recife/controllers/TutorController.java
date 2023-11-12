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
import javax.servlet.http.HttpSession;

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
      
        return "login";
    }
    
    public void alterarSenha(String senha, String novaSenha, String confirma) {
        Tutor tutorLogado = ((LoginController)((HttpSession)FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).getTutorLogado();
        
        if(!PasswordSecurity.decrypt(senha, tutorLogado.getSenha())) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage("A senha digitada está incorreta."));
            
            return;
        }
        
        if(!novaSenha.equals(confirma)) {
            FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage("A nova senha não bate com a confirmação."));
            
            return;
        }
        
        tutorLogado.setSenha(PasswordSecurity.encrypt(novaSenha));
        ManagerDao.getCurrentInstance().update(tutorLogado);
        // TODO Message does not reach growl because function has no explicit return
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage("Senha alterada com sucesso"));
        
        ((LoginController)((HttpSession)FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).logout();
    }

    public Tutor getCadastro() {
        return cadastro;
    }

    public void setCadastro(Tutor cadastro) {
        this.cadastro = cadastro;
    }
    
    
}
