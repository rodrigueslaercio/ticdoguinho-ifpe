/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.controllers;

import br.edu.ifpe.recife.model.classes.Comentario;
import br.edu.ifpe.recife.model.classes.Pet;
import br.edu.ifpe.recife.model.classes.PetVideo;
import br.edu.ifpe.recife.model.classes.Post;
import br.edu.ifpe.recife.model.classes.Tutor;
import br.edu.ifpe.recife.model.classes.TutorVideo;
import br.edu.ifpe.recife.model.dao.ManagerDao;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private int postId;
    private Comentario comentario;
    private Comentario response;

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

    public List<Post> getFollowingPosts() {
        Pet selection = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("petController")).getSelection();

        if (selection != null && !selection.getFollowing().isEmpty()) {
            List<Pet> petsFollowing = selection.getFollowing();
            String jpql = "select distinct p from Post p join p.petVideo pv join pv.pet pet where pet in :petsFollowing order by p.uploadDateTime DESC";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("petsFollowing", petsFollowing);

            List<Post> posts = ManagerDao.getCurrentInstance().read(jpql, Post.class, parameters);
            return posts;
        }

        return null;
    }

    public List<Post> getPostsSearchedPet() {
        Pet searchedPet = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("petController")).fetchSearchedPet();

        List<Post> videos = ManagerDao.getCurrentInstance().read("select p from "
                + "Post p join fetch p.petVideo pv where pv.pet.codigo = " + searchedPet.getCodigo()
                + " order by p.uploadDateTime DESC", Post.class);

        return videos;
    }

    public String doPost(String texto) {
        this.post.setTexto(texto);
        this.post.setUploadDateTime(new Date());

        ManagerDao.getCurrentInstance().insert(this.petVideo);
        ManagerDao.getCurrentInstance().insert(this.tutorVideo);
        ManagerDao.getCurrentInstance().insert(this.post);

        return "indexPet";
    }

    public Post fetchPost() {
        return (Post) ManagerDao.getCurrentInstance().read("select p from Post p where p.id = " + postId, Post.class).get(0);
    }

    public List<Comentario> fetchPostComentarios() {
        List<Comentario> comentarios = ManagerDao.getCurrentInstance().read("select c from Comentario c where c.post.id = " + postId
                + " order by c.data DESC", Comentario.class);

        return comentarios;
    }

    public String doComentario(String texto) {
        Post post = (Post) ManagerDao.getCurrentInstance().read("select p from Post p where p.id = " + postId, Post.class).get(0);
        Post updatedPost = post;
        this.comentario = new Comentario();
        if (!texto.equals("")) {
            Pet selection = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true))
                    .getAttribute("petController")).getSelection();
            this.comentario.setAutor(selection);
            this.comentario.setPost(post);
            this.comentario.setTexto(texto);
            this.comentario.setData(new Date());
            post.getComentarios().add(comentario);

            ManagerDao.getCurrentInstance().insert(this.comentario);
            ManagerDao.getCurrentInstance().update(updatedPost);

            return "postagem.xhtml?faces-redirect=true&postId=" + updatedPost.getId();
        }

        return "postagem.xhtml?faces-redirect=true&postId=" + updatedPost.getId();

    }
    
    public String doResponse(String texto) {
        Post post = (Post) ManagerDao.getCurrentInstance().read("select p from Post p where p.id = " + postId, Post.class).get(0);
        Post updatedPost = post;
        Comentario comentarioForResponse = this.getResponse();
        Comentario response = new Comentario();
        if (!texto.equals("")) {
            Pet selection = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true))
                    .getAttribute("petController")).getSelection();
            response.setAutor(selection);
            response.setData(new Date());
            response.setTexto(texto);
            comentarioForResponse.getRespostas().add(response);
            
            ManagerDao.getCurrentInstance().update(comentarioForResponse);
            ManagerDao.getCurrentInstance().update(post);
                        
            return "postagem.xhtml?faces-redirect=true&postId=" + updatedPost.getId();
        }
        
        return "postagem.xhtml?faces-redirect=true&postId=" + updatedPost.getId();
    }

    public String displayDate(Date date) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDateTime = displayFormat.format(date);

        return "Postado em " + formattedDateTime.replace(" ", " às ");
    }

    public String displayDateComments(Date date) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDateTime = displayFormat.format(date);

        return formattedDateTime.replace(" ", " às ");
    }

    public String formatVideoToSrc(byte[] blob) {
        return blob != null ? Base64.getEncoder().encodeToString(blob) : "";
    }

    public void likeComment(Comentario comentario) {
        Pet selection = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("petController")).getSelection();
        comentario.getLikes().add(selection);
        ManagerDao.getCurrentInstance().update(comentario);
    }

    public void dislikeComment(Comentario comentario) {
        Pet selection = ((PetController) ((HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(true))
                .getAttribute("petController")).getSelection();
        comentario.getLikes().remove(selection);
        ManagerDao.getCurrentInstance().update(comentario);
    }
    
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Comentario getResponse() {
        return response;
    }

    public void setResponse(Comentario response) {
        this.response = response;
    }
    
    

}
