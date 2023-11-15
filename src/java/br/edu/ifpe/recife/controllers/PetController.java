/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.controllers;

import br.edu.ifpe.recife.model.classes.Pet;
import br.edu.ifpe.recife.model.classes.Tutor;
import br.edu.ifpe.recife.model.classes.TutorPet;
import br.edu.ifpe.recife.model.dao.ManagerDao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
public class PetController {

    private Pet cadastro;
    private Pet selection;
    private TutorPet selectTutorPet;

    @PostConstruct
    public void init() {
        this.cadastro = new Pet();
    }

    public String insert() {
        Tutor tutorLogado = ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).getTutorLogado();

        TutorPet tutorPet = new TutorPet();
        tutorPet.setPet(this.cadastro);
        tutorPet.setTutor(tutorLogado);

        ManagerDao.getCurrentInstance().insert(this.cadastro);
        ManagerDao.getCurrentInstance().insert(tutorPet);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pet " + this.cadastro.getNome()
                + " cadastrado com sucesso!"));

        this.cadastro = new Pet();

        return "pets";
    }

    public List<Pet> readAllPets() {
        Tutor tutorLogado = ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).getTutorLogado();
        List<Pet> pets = null;

        String jpql = "select tp.pet from TutorPet tp where tp.tutor = :tutor";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tutor", tutorLogado);

        pets = ManagerDao.getCurrentInstance().read(jpql, Pet.class, parameters);

        return pets;
    }

    public String alterar() {
        ManagerDao.getCurrentInstance().update(this.selection);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Sucesso!", "Pet editado com sucesso"));

        return "pets";
    }

    public String deletar() {
        List<TutorPet> tutores = ManagerDao.getCurrentInstance().read("select tp from TutorPet tp where tp.pet.codigo = " + this.selection.getCodigo(), TutorPet.class);
       
        for(TutorPet tutor : tutores)  {
            ManagerDao.getCurrentInstance().delete(tutor);
        }

        ManagerDao.getCurrentInstance().delete(this.selection);     
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
             "Sucesso!", "Pet deletado com sucesso"));
       
        return "pets";
    }

    public String compartilhar(String codigo) {
        try {
            String jpql = "select p from Pet p where p.codCompartilhamento = :codCompartilhamento";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("codCompartilhamento", UUID.fromString(codigo));
            Pet pet = (Pet) ManagerDao.getCurrentInstance().read(jpql, Pet.class, parameters).get(0);

            Tutor tutorLogado = ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true))
                    .getAttribute("loginController")).getTutorLogado();

            TutorPet tutorPet = new TutorPet();
            tutorPet.setTutor(tutorLogado);
            tutorPet.setPet(pet);
            ManagerDao.getCurrentInstance().insert(tutorPet);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pet compartilhado com sucesso."));

        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Código invalido", ""));
        } catch (ArrayIndexOutOfBoundsException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Pet não existe", ""));
        }

        return "pets";
    }
   
    public Pet getCadastro() {
        return cadastro;
    }

    public void setCadastro(Pet cadastro) {
        this.cadastro = cadastro;
    }

    public Pet getSelection() {
        return selection;
    }

    public void setSelection(Pet selection) {
        this.selection = selection;
    }

    public TutorPet getSelectTutorPet() {
        return selectTutorPet;
    }

    public void setSelectTutorPet(TutorPet selectTutorPet) {
        this.selectTutorPet = selectTutorPet;
    }
}
