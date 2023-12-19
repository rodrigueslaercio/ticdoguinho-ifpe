/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.controllers;

import br.edu.ifpe.recife.model.classes.Pet;
import br.edu.ifpe.recife.model.classes.PetVideo;
import br.edu.ifpe.recife.model.classes.Post;
import br.edu.ifpe.recife.model.classes.Tutor;
import br.edu.ifpe.recife.model.classes.TutorVideo;
import br.edu.ifpe.recife.model.dao.ManagerDao;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author laerc
 */
@ManagedBean
@ViewScoped
public class VideoBean {

    private Post post;
    private PetVideo petVideo;
    private TutorVideo tutorVideo;
    
    @PostConstruct
    public void init() {
        this.post = new Post();
    }

    public void uploadVideoPet(FileUploadEvent e) throws IOException {
        byte[] blob = new byte[(int) e.getFile().getSize()];
        e.getFile().getInputstream().read(blob);
        UploadedFile upFile = e.getFile();

        if (upFile != null && !upFile.getFileName().isEmpty()) {
            this.petVideo = new PetVideo();
            Pet selection = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true))
                    .getAttribute("petController")).getSelection();

            this.petVideo.setPet(selection);
            this.petVideo.setVideo(blob);
            this.post.setPetVideo(petVideo);

            FacesMessage message = new FacesMessage("Successo!", upFile.getFileName() + "Upload realizado.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Não foi possível fazer Upload da imagem.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void uploadVideoTutor(FileUploadEvent e) throws IOException {
        byte[] blob = new byte[(int) e.getFile().getSize()];
        e.getFile().getInputstream().read(blob);
        UploadedFile upFile = e.getFile();

        if (upFile != null && !upFile.getFileName().isEmpty()) {
            this.tutorVideo = new TutorVideo();
            Tutor tutorLogado = ((LoginController) ((HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true))
                    .getAttribute("loginController")).getTutorLogado();

            
            this.tutorVideo.setVideo(blob);
            this.tutorVideo.setTutor(tutorLogado);
            this.post.setTutorVideo(tutorVideo);

            FacesMessage message = new FacesMessage("Successo!", upFile.getFileName() + "Upload realizado.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Não foi possível fazer Upload da imagem.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public List<Post> getPosts() {
        Pet selection = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("petController")).getSelection();
        
        List<Post> videos = ManagerDao.getCurrentInstance().read("select p from "
                + "Post p join fetch p.petVideo pv where pv.pet.codigo = " + selection.getCodigo() 
                + " order by p.uploadDateTime DESC", Post.class);

        return videos;

    }

    public String doPost() {
        this.post.setUploadDateTime(new Date());
        ManagerDao.getCurrentInstance().insert(this.petVideo);
        ManagerDao.getCurrentInstance().insert(this.tutorVideo);
        ManagerDao.getCurrentInstance().insert(this.post);


        return "indexPet";
    }

    public String formatVideoToSrc(byte[] blob) {
        return blob != null ? Base64.getEncoder().encodeToString(blob) : "";
    }
}
