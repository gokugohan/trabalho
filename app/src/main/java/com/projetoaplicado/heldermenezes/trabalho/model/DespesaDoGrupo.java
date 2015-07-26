package com.projetoaplicado.heldermenezes.trabalho.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Calendar;
import java.util.List;

/**
 * Created by helder on 07-06-2015.
 */
@Table(name = "DespesaGrupo")
public class DespesaDoGrupo extends Model {

    @Column(name = "Grupo",onDelete = Column.ForeignKeyAction.CASCADE)
    public Grupo grupo;

    @Column(name = "Despesa",onDelete = Column.ForeignKeyAction.CASCADE)
    public Despesa despesa;

    @Column(name = "pessoa") // pessoa que pagou a despesa
    public Pessoa pessoa;

    @Column(name = "importancia")
    public Double importancia;//Valor pago pela pessoa

    @Column(name = "moeda")
    public int moeda; //Moeda que foi usado para o pagamento da despesa

    @Column(name = "data_pagamento")
    public long data_pagamento; //Data do pagamento realizado

    public DespesaDoGrupo(Grupo grupo,Despesa despesa,Pessoa pessoa,Double importancia,int moeda,long data_pagamento) {
        super();
        this.grupo = grupo;
        this.despesa = despesa;
        this.pessoa = pessoa;
        this.importancia = importancia;
        this.moeda = moeda;
        this.data_pagamento = data_pagamento;
    }


    public List<Grupo> grupos(){
        return getMany(Grupo.class,"DespesaGrupo");
    }

    public List<Despesa> despesas(){
        return getMany(Despesa.class,"DespesaGrupo");
    }


}
