/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.model.classes;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author laerc
 */
@Entity
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Pet autor;

    @Column(length = 500)
    private String texto;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pet> likes;

    private List<Comentario> respostas;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME")
    private Date data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pet getAutor() {
        return autor;
    }

    public void setAutor(Pet autor) {
        this.autor = autor;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<Pet> getLikes() {
        return likes;
    }

    public void setLikes(List<Pet> likes) {
        this.likes = likes;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public List<Comentario> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<Comentario> respostas) {
        this.respostas = respostas;
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
