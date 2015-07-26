package com.projetoaplicado.heldermenezes.trabalho.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;


import java.util.List;

/**
 * Created by heldermenezes on 10/07/2015.
 */
@ParseClassName("DadosDaConta")
public class ParseContaData extends ParseObject {

    private List<Despesa> despesasDoGrupo;
    private List<Pessoa> pessoasDoGrupo;

    public ParseContaData(List<Despesa> despesasDoGrupo, List<Pessoa> pessoasDoGrupo) {
        this.despesasDoGrupo = despesasDoGrupo;
        this.pessoasDoGrupo = pessoasDoGrupo;
    }

    public ParseContaData() {

    }

    public void setUser(ParseUser user) {
        this.put("user", user);
    }

    public void setNovaConta(Grupo grupo, Pessoa pessoa, Double valorQueDeviaPagar, Double valorQuePagou, Double valorFinal) {
        //this.put("despesa",setDespesa(despesa));
        this.put("grupo", setGrupo(grupo));
        this.put("pessoa", setPessoa(pessoa));
        this.put("despesas", setDespesas());
        this.put("pessoas", setPessoas());
        String moedaUsado = Util.Moeda.getStringMoeda().get(grupo.moeda);
        this.put("moeda", moedaUsado);
        String s = String.format("%.2f", valorQueDeviaPagar);
        this.put("valor_que_devia_pagar", s);
        s = String.format("%.2f", valorQuePagou);
        this.put("valor_que_pagou", s);
        s = String.format("%.2f", valorFinal);
        this.put("valor_final", s);
    }

    private String setGrupo(Grupo grupo) {
        StringBuilder sb = new StringBuilder();
        sb.append(grupo.nome);
        return sb.toString();
    }

    private String setDespesa(Despesa despesa) {
        StringBuilder sb = new StringBuilder();
        sb.append(despesa.tipo).append("(())").append(despesa.descricao).append("(())");
        sb.append(despesa.importancia).append(" ").append(Util.Moeda.getStringMoeda().get(despesa.moeda)).append("(())");
        sb.append(despesa.grupo.nome).append("(())").append(despesa.pessoa.nome).append("(())");
        sb.append(Util.Utilities.getDateFormat(despesa.data)).append("(())");
        String nome_foto = despesa.tipo.replaceAll("\\s", "");
        ParseFile foto = ParseUtils.createFoto(nome_foto, despesa.urlDaFoto);
        sb.append(foto.getUrl());
        return sb.toString();
    }

    private String setDespesas() {
        StringBuilder sb = new StringBuilder();
        for (Despesa despesa : despesasDoGrupo) {
            sb.append(setDespesa(despesa));
            sb.append("___");
        }
        String ret = sb.toString();
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }

    private String setPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder();
        sb.append(pessoa.nome).append("(())");
        sb.append(pessoa.email).append("(())");
        sb.append(pessoa.telefone).append("(())");
        String nome_foto = pessoa.nome.replaceAll("\\s", "_");
        ParseFile foto = ParseUtils.createFoto(nome_foto, pessoa.urlDaFoto);
        sb.append(foto.getUrl());
        return sb.toString();
    }

    private String setPessoas() {
        StringBuilder sb = new StringBuilder();
        for (Pessoa pessoa : pessoasDoGrupo) {
            sb.append(setPessoa(pessoa));
            sb.append("___");
        }
        String ret = sb.toString();
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }
}
