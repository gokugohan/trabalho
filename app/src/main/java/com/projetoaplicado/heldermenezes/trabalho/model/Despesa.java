package com.projetoaplicado.heldermenezes.trabalho.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by helder on 07-06-2015.
 */
@Table(name = "Despesa")
public class Despesa extends Model {
    @Column(name = "UrlDaFoto")
    public String urlDaFoto;
    @Column(name = "NomeDaFoto")
    public String nomeDaFoto;
    @Column(name = "Tipo")
    public String tipo;
    @Column(name = "Descricao")
    public String descricao;

    @Column(name = "Importancia")
    public Double importancia;

    @Column(name = "ValorConvertido")
    public Double valorConvertido;
    @Column(name = "Moeda")
    public int moeda;
    @Column(name = "Grupo")
    public Grupo grupo;
    @Column(name = "Pessoa")
    public Pessoa pessoa;

    @Column(name = "Data")
    public Long data;


/*
    @Column(name = "DespesaGrupo")
    public DespesaDoGrupo despesaDoGrupo;*/

    public Despesa() {
        super();
    }

    public List<Grupo> grupos() {
        return getMany(Grupo.class, "Despesa");
    }

    public List<Pessoa> pessoas() {
        return getMany(Pessoa.class, "Despesa");
    }
}
