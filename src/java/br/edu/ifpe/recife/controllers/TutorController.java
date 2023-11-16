/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.controllers;

import br.edu.ifpe.recife.model.classes.Tutor;
import br.edu.ifpe.recife.model.dao.ManagerDao;
import br.edu.ifpe.recife.utils.PasswordSecurity;
import java.util.List;
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
        List<Tutor> verifyTutor = ManagerDao.getCurrentInstance().read("select t from Tutor t where t.email = '" + cadastro.getEmail() + "'", Tutor.class);

        if (!verifyTutor.isEmpty()) {
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "ERRO.", "Email já cadastrado."));

            return "registro_tutor";
        } else {
            String encryptPass = PasswordSecurity.encrypt(cadastro.getSenha());
            this.cadastro.setSenha(encryptPass);

            ManagerDao.getCurrentInstance().insert(this.cadastro);
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Sucesso!", "Tutor cadastrado com sucesso."));
            this.cadastro = new Tutor();

            return "login";
        }
    }

    public String alterarPerfil() {
        Tutor tutorLogado = ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).getTutorLogado();

        ManagerDao.getCurrentInstance().update(tutorLogado);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Sucesso!", "Usuário alterado com sucesso."));

        return "perfil_tutor";
    }

    public void alterarSenha(String senha, String novaSenha, String confirma) {
        Tutor tutorLogado = tutorLogadoSession();

        if (!PasswordSecurity.decrypt(senha, tutorLogado.getSenha())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("A senha digitada está incorreta."));

            return;
        }

        if (!novaSenha.equals(confirma)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("A nova senha não bate com a confirmação."));

            return;
        }

        tutorLogado.setSenha(PasswordSecurity.encrypt(novaSenha));
        ManagerDao.getCurrentInstance().update(tutorLogado);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Senha alterada com sucesso"));

        ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).logout();
    }
    
    private Tutor tutorLogadoSession() {
        return ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).getTutorLogado();
    }

    public Tutor getCadastro() {
        return cadastro;
    }

    public void setCadastro(Tutor cadastro) {
        this.cadastro = cadastro;
    }

}
