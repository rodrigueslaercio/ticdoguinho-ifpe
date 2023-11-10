/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.controllers;

import br.edu.ifpe.recife.model.classes.Tutor;
import br.edu.ifpe.recife.model.dao.ManagerDao;
import br.edu.ifpe.recife.utils.PasswordSecurity;
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
public class LoginController {
    private Tutor tutorLogado;

    public String realizarLogin(String email, String senha) {

        try {
            String password = (String) ManagerDao.getCurrentInstance().read("select t.senha from Tutor t where t.email = '" + email + "'", Tutor.class).get(0);

            boolean isPassCorrect = PasswordSecurity.decrypt(senha, password);

            if (isPassCorrect) {
                Tutor tLogin = (Tutor) ManagerDao.getCurrentInstance().read("select t from Tutor t where t.email = '" + email + "'", Tutor.class).get(0);
                this.tutorLogado = tLogin;

                return "index_tutor";
            } else {
                FacesContext.getCurrentInstance()
                    .addMessage(null, 
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro ao Logar","Senha incorreta"));
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance()
                    .addMessage(null, 
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erro ao Logar","Email incorreto"));
            return null;
        }

    }
    
    public String alterarPerfilSessao() {
        ManagerDao.getCurrentInstance().update(this.tutorLogado);
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
        "Sucesso!", "Usuário alterado com sucesso."));
        
        return "perfil_tutor";
    }
    
    public String logout() {
        this.tutorLogado = null;
        
        return "login";
    }

    public Tutor getTutorLogado() {
        return tutorLogado;
    }

    public void setTutorLogado(Tutor tutorlogado) {
        this.tutorLogado = tutorlogado;
    }

}
