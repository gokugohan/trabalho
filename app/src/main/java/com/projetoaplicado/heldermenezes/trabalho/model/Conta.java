package com.projetoaplicado.heldermenezes.trabalho.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by heldermenezes on 18/06/2015.
 */
@Table(name = "Conta")
public class Conta extends Model{

    @Column(name = "Grupo")
    public Grupo grupo;

    @Column(name = "Despesa")
    public Despesa despesa;

//    @Column(name = "Conta_pago_por")
//    public String pessoa;

    @Column(name = "Valor")
    public Double valor;

    @Column(name = "Moeda")
    public int moeda;

    public Conta(){super();}

}
