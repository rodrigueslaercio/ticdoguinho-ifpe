/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.model.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

/**
 *
 * @author laerc
 */
@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int codigo;
    private String nome;
    private String mesAnoNascimento;
    private String porte;
    private UUID codCompartilhamento = UUID.randomUUID();
    @Lob
    private byte[] imagem;
    @ManyToMany(mappedBy = "following")
    private List<Pet> followers = new ArrayList<>();
    @ManyToMany
    private List<Pet> following = new ArrayList<>();

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMesAnoNascimento() {
        return mesAnoNascimento;
    }

    public void setMesAnoNascimento(String mesAnoNascimento) {
        this.mesAnoNascimento = mesAnoNascimento;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public UUID getCodCompartilhamento() {
        return codCompartilhamento;
    }

    public void setCodCompartilhamento(UUID codCompartilhamento) {
        this.codCompartilhamento = codCompartilhamento;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public List<Pet> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Pet> followers) {
        this.followers = followers;
    }

    public List<Pet> getFollowing() {
        return following;
    }

    public void setFollowing(List<Pet> following) {
        this.following = following;
    }

    
    // Para o remove() da List de followers/following funcionar
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Pet otherPet = (Pet) obj;

        return Objects.equals(this.getCodigo(), otherPet.getCodigo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getCodigo());
    }

}
