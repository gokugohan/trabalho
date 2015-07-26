package com.projetoaplicado.heldermenezes.trabalho.model;

import java.io.Serializable;

/**
 * Created by heldermenezes on 25/06/2015.
 */
public class AcertoDeConta implements Serializable {

    private Grupo grupo;
    private Pessoa pessoa;
    private Double valorQueDeviaPagar;
    private int moeda;
    private Double valorQuePagou, valorFinal;

    public AcertoDeConta() {
        this(null, 0.0, 0);
    }

    public AcertoDeConta(Pessoa pessoa, Double valorQuePagou, int moeda) {
        this.pessoa = pessoa;
        this.valorQuePagou = valorQuePagou;
        this.moeda = moeda;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Double getValorQueDeviaPagar() {
        return valorQueDeviaPagar;
    }

    public void setValorQueDeviaPagar(Double vDeviaPagar) {this.valorQueDeviaPagar = vDeviaPagar;}

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Double getValorQuePagou() {
        return valorQuePagou;
    }

    public void setValorQuePagou(Double valorQuePagou) {
        this.valorQuePagou = valorQuePagou;
    }

    public Double getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Double valorFinal) {
        this.valorFinal = valorFinal;
    }

    public int getMoeda() {
        return moeda;
    }

    public void setMoeda(int moeda) {
        this.moeda = moeda;
    }

    @Override
    public String toString() {
        return pessoa != null ? pessoa.nome + " : " + valorQueDeviaPagar : "@null";
    }
}
