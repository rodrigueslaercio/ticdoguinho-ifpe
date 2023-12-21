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
import java.io.IOException;
import java.util.Base64;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author laerc
 */
@ManagedBean
@SessionScoped
public class PetController {

    private Pet cadastro;
    private Pet selection;
    private int petSearchCodigo;
    private Pet searchedPet;

    @PostConstruct
    public void init() {
        this.cadastro = new Pet();
    }

    public String insert() {
        Tutor tutorLogado = tutorLogadoSession();

        TutorPet tutorPet = new TutorPet();
        tutorPet.setPet(this.cadastro);
        tutorPet.setTutor(tutorLogado);

        // checa se o tutor logado já possui um pet com mesmo nome
        if (checkDuplicata(this.cadastro.getNome())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Pet já com esse nome já cadastrado", ""));
            this.cadastro = new Pet();

            return "cadastroPets";
        }

        ManagerDao.getCurrentInstance().insert(this.cadastro);
        ManagerDao.getCurrentInstance().insert(tutorPet);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pet " + this.cadastro.getNome()
                + " cadastrado com sucesso!"));

        this.cadastro = new Pet();

        return "pets";
    }

    public List<Pet> readAllPets() {
        Tutor tutorLogado = tutorLogadoSession();
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

        return "perfilPet";
    }

    public String deletar() {
        List<TutorPet> tutores = ManagerDao.getCurrentInstance().read("select tp from TutorPet tp where tp.pet.codigo = " + this.selection.getCodigo(), TutorPet.class);

        // deleta primeiro o pet na tabela TutorPet
        for (TutorPet tutor : tutores) {
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

            List<TutorPet> pets = ManagerDao.getCurrentInstance().read("select tp from TutorPet tp where tp.pet.codigo = " + pet.getCodigo(), Pet.class);

            if (pets.size() == 2) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Pet já possui dois tutores", ""));
                return "pets";
            }

            Tutor tutorLogado = tutorLogadoSession();

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

    public List<TutorPet> tutoresDoPet() {
        return ManagerDao.getCurrentInstance().read("select tp from TutorPet tp where tp.pet.codigo = " + this.selection.getCodigo(), Pet.class);
    }

    public List<TutorPet> tutoresDoSearchedPet() {
        return ManagerDao.getCurrentInstance().read("select tp from TutorPet tp where tp.pet.codigo = " + fetchSearchedPet().getCodigo(), Pet.class);
    }

    public boolean checkDuplicata(String nome) {
        List<Pet> petsAssociados = ManagerDao.getCurrentInstance().read("select p from Pet p join TutorPet tp where tp.tutor.codigo = " + tutorLogadoSession().getCodigo(), Pet.class);

        String jpql = "select p from Pet p where p.nome = :nome";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nome", nome);
        List<Pet> petsComMesmoNome = ManagerDao.getCurrentInstance().read(jpql, Pet.class, parameters);

        for (Pet petAssociado : petsAssociados) {
            for (Pet petComMesmoNome : petsComMesmoNome) {
                if (petAssociado.getNome().equals(petComMesmoNome.getNome())) {
                    return true;
                }
            }
        }

        return false;
    }

    // faz upload da imagem para o pet
    public void upload(FileUploadEvent e) throws IOException {
        byte[] blob = new byte[(int) e.getFile().getSize()];
        e.getFile().getInputstream().read(blob);
        UploadedFile upFile = e.getFile();

        if (upFile != null && !upFile.getFileName().isEmpty()) {
            this.cadastro.setImagem(blob);
            FacesMessage message = new FacesMessage("Successo!", upFile.getFileName() + " foi salva.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Não foi possível salvar a imagem.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    // upload para o editarPet
    public void uploadChange(FileUploadEvent e) throws IOException {
        byte[] blob = new byte[(int) e.getFile().getSize()];
        e.getFile().getInputstream().read(blob);
        UploadedFile upFile = e.getFile();

        if (upFile != null && !upFile.getFileName().isEmpty()) {
            this.selection.setImagem(blob);
            FacesMessage message = new FacesMessage("Successo!", upFile.getFileName() + " foi salva.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Não foi possível salvar a imagem.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    // retorna a imagem do pet
    public String getImagemPet() {
        byte[] blob = this.selection.getImagem();
        return blob != null ? Base64.getEncoder().encodeToString(blob) : "";
    }

    // retorna a imagem do pet pesquisado
    public String getImagemSearchedPet() {
        byte[] blob = fetchSearchedPet().getImagem();
        return blob != null ? Base64.getEncoder().encodeToString(blob) : "";
    }

    // retorna a imagem do pet no for each do index
    public String formatImagemIndex(byte[] blob) {
        return blob != null ? Base64.getEncoder().encodeToString(blob) : "";
    }

    // fix param value é nulo da tag graphicImage 
    public String getGraphicImage() {
        byte[] blob = this.cadastro.getImagem();
        return blob != null ? Base64.getEncoder().encodeToString(blob) : "";
    }

    public List<Pet> searchPets(String petName) {
        List<Pet> petsFound = ManagerDao.getCurrentInstance().read("select p from Pet p where p.nome = '" + petName + "'", Pet.class);

        return petsFound;
    }

    public Pet fetchSearchedPet() {
        return (Pet) ManagerDao.getCurrentInstance().read("select p from Pet p where p.codigo = " + this.petSearchCodigo, Pet.class).get(0);
    }
    
    public void follow() {
        this.searchedPet = fetchSearchedPet();
        
        if (searchedPet != null) {
            this.selection.getFollowing().add(this.searchedPet);
            this.searchedPet.getFollowers().add(this.selection);
            ManagerDao.getCurrentInstance().update(this.selection);
            ManagerDao.getCurrentInstance().update(this.searchedPet);
        }
        
    }
    
    // TODO
    public void unfollow() {
        
    }
    
    
    public boolean isAlreadyAFollower() {
        this.searchedPet = fetchSearchedPet();
        
        for (Pet pet : this.selection.getFollowing()) {
            if (pet.getCodigo() == this.searchedPet.getCodigo()) {
                return true;
            }
        }
        
        return false;
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


    public int getPetSearchCodigo() {
        return petSearchCodigo;
    }

    public void setPetSearchCodigo(int petSearchCodigo) {
        this.petSearchCodigo = petSearchCodigo;
    }

    public Pet getSearchedPet() {
        return searchedPet;
    }

    public void setSearchedPet(Pet searchedPet) {
        this.searchedPet = searchedPet;
    }
    
    

    private Tutor tutorLogadoSession() {
        return ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("loginController")).getTutorLogado();
    }
}
