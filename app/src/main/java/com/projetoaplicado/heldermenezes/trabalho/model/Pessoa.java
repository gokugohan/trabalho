package com.projetoaplicado.heldermenezes.trabalho.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by helder on 07-06-2015.
 */
@Table(name = "Pessoa")
public class Pessoa extends Model implements Serializable {

    @Column(name = "Nome")
    public String nome;

    @Column(name = "Email")
    public String email;
    @Column(name = "Telefone")
    public String telefone;

    @Column(name = "UrlDaFoto")
    public String urlDaFoto;

//    @Column(name = "Grupo")
//    public Grupo grupo;
//    @Column(name = "NomeDaFoto")
//    public String nomeDaFoto;

    public Pessoa() {
        super();
    }

    public static List<Pessoa> getAll(Grupo grupo) {
        return new Select()
                .from(Pessoa.class)
                .where("Grupo=?", grupo.getId())
                .orderBy("Nome ASC")
                .execute();
    }
}
