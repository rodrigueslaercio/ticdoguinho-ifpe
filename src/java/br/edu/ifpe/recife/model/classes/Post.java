/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.model.classes;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author laerc
 */
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @OneToOne
    @JoinColumn(name = "tutor_video_id")
    private TutorVideo tutorVideo;

    @OneToOne
    @JoinColumn(name = "pet_video_id")
    private PetVideo petVideo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME")
    private Date uploadDateTime;

    @ManyToMany
    @JoinTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "pet_id"))
    private List<Pet> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public TutorVideo getTutorVideo() {
        return tutorVideo;
    }

    public void setTutorVideo(TutorVideo tutorVideo) {
        this.tutorVideo = tutorVideo;
    }

    public PetVideo getPetVideo() {
        return petVideo;
    }

    public void setPetVideo(PetVideo petVideo) {
        this.petVideo = petVideo;
    }

    public Date getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(Date uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public List<Pet> getLikes() {
        return likes;
    }

    public void setLikes(List<Pet> likes) {
        this.likes = likes;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    // Para o remove() da List de likes funcionar
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Pet otherPet = (Pet) obj;

        return Objects.equals(this.getId(), otherPet.getCodigo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

}
