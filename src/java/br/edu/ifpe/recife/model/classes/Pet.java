/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpe.recife.model.classes;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

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
}
