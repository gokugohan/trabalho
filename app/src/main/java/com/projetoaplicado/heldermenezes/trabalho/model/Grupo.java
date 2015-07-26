package com.projetoaplicado.heldermenezes.trabalho.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by helder on 07-06-2015.
 */
@Table(name = "Grupo")
public class Grupo extends Model implements Serializable {

    @Column(name = "Nome")
    public String nome;
    @Column(name = "Moeda")
    public int moeda;


    /*@Column(name = "DespesaGrupo")
    public DespesaDoGrupo despesaDoGrupo;*/

    public Grupo(){super();}

    /*public List<Pessoa> getPessoas(){
        return getMany(Pessoa.class,"grupo");
    }*/

   /* public static List<Grupo> getAll(DespesaDoGrupo despesaDoGrupo){
        return new Select()
                .from(Grupo.class)
                .where("DespesaGrupo=?",despesaDoGrupo.getId())
                .orderBy("Nome ASC")
                .execute();
    }*/

}
