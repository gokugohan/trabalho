package com.projetoaplicado.heldermenezes.trabalho.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by heldermenezes on 12/06/2015.
 */
@Table(name = "GrupoPessoa")
public class GrupoPessoa extends Model {


    @Column(name = "Grupo",onDelete = Column.ForeignKeyAction.CASCADE)
    public Grupo grupo;
    @Column(name = "Pessoa",onDelete = Column.ForeignKeyAction.CASCADE)
    public Pessoa pessoa;

    public GrupoPessoa(){
        super();
    }

    public GrupoPessoa(Grupo grupo, Pessoa pessoa) {
        super();
        this.grupo = grupo;
        this.pessoa = pessoa;
    }

    public List<Pessoa> pessoas(){
        return getMany(Pessoa.class,"GrupoPessoa");
    }

    public List<Grupo> grupos(){
        return getMany(Grupo.class,"GrupoPessoa");
    }

}
