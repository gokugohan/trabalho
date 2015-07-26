package com.projetoaplicado.heldermenezes.trabalho.repositorio;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.parse.ParseUser;
import com.projetoaplicado.heldermenezes.trabalho.model.AcertoDeConta;
import com.projetoaplicado.heldermenezes.trabalho.model.Conta;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.DespesaDoGrupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.GrupoPessoa;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.model.ParseContaData;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by helder on 07-06-2015.
 */
public class DatabaseHelper {

    private static DatabaseHelper db;

    private DatabaseHelper() {

    }

    public static DatabaseHelper newInstance() {
        if (db == null) {
            return new DatabaseHelper();
        }
        return db;
    }

    public Pessoa savePessoa(String nome, String email, String telefone, String urlDaImagem) {

        Pessoa pessoa = new Pessoa();
        pessoa.nome = nome;
        pessoa.email = email;
        pessoa.telefone = telefone;
        pessoa.urlDaFoto = urlDaImagem;
        pessoa.save();
        return pessoa;
    }

    public Grupo saveGrupo(String nome, int moeda) {
        Grupo grupo = new Grupo();
        grupo.nome = nome;
        grupo.moeda = moeda;
        grupo.save();
        return grupo;
    }

    public GrupoPessoa savePessoaToGrupo(Grupo grupo, Pessoa pessoa) {
        if (grupo == null && pessoa == null) {
            return null;
        }

        GrupoPessoa grupoPessoa = new GrupoPessoa(grupo, pessoa);
        grupoPessoa.save();
        return grupoPessoa;
    }

    public void savePessoaToGrupo(Grupo grupo, List<Pessoa> pessoas) {
        if (grupo == null && pessoas == null) {
            return;
        }

        for (Pessoa pessoa : pessoas) {
            savePessoaToGrupo(grupo, pessoa);
        }
    }

    public void updatePessoaNoGrupo(Grupo grupo, List<Pessoa> pessoas) {
        if (grupo == null && pessoas == null) {
            return;
        }

        List<GrupoPessoa> grupoPessoas = new Select().from(GrupoPessoa.class).where("Grupo=?", grupo.getId()).execute();

        // Remover todas as pessoas que estao la e simplesmente voltar a adiciona-los
        for (GrupoPessoa gp : grupoPessoas) {
            gp.delete();
        }
        savePessoaToGrupo(grupo, pessoas);
    }

    public Despesa saveDespesa(String tipo, String descricao, String urlDaFoto, Grupo grupo, Pessoa pessoa, Double importancia, long data) {
        Despesa despesa = new Despesa();
        despesa.tipo = tipo;
        despesa.descricao = descricao;
        despesa.urlDaFoto = urlDaFoto;
        despesa.importancia = importancia;
        despesa.moeda = grupo.moeda;
        despesa.data = data;
        despesa.grupo = grupo;
        despesa.pessoa = pessoa;
        despesa.save();
        adicionarContaDoGrupo(grupo, despesa, importancia, grupo.moeda);
        return despesa;
    }

    public void updateDespesa(long idGrupoAntigo, Despesa despesa) {
            Conta contaAntigo = new Select().from(Conta.class).where("Grupo=?", idGrupoAntigo).executeSingle();
            if (contaAntigo != null) {
                contaAntigo.grupo = despesa.grupo;
                contaAntigo.despesa = despesa;
                contaAntigo.valor = despesa.importancia;
                contaAntigo.moeda = despesa.moeda;
                contaAntigo.save();
                despesa.save();
            }
            return;

    }

    private Conta adicionarContaDoGrupo(Grupo grupo, Despesa despesa, Double importancia, int moeda) {
        Select select = new Select();
        Conta conta = select.from(Conta.class).where("Grupo=?", grupo.getId()).executeSingle();
        if (conta == null) {
            conta = new Conta();
            conta.grupo = grupo;
            conta.despesa = despesa;
            conta.valor = importancia;
            conta.moeda = moeda;
        }
        conta.save();
        return conta;
    }

    /**
     * Guardar despesa, grupo, importancia e moeda na DespesaDoGrupo
     *
     * @param despesa
     * @param grupo
     */
    public void saveDespesaDoGrupo(Despesa despesa, Grupo grupo, Pessoa pessoa, Double importancia, int moeda, long data) {
        if (despesa == null && grupo == null) {
            return;
        }
        DespesaDoGrupo despesaDoGrupo = new DespesaDoGrupo(grupo, despesa, pessoa, importancia, moeda, data);
        despesaDoGrupo.save();
    }


    /**
     * Eliminar uma pessoa, caso nao esta associado a nenhum grupo
     *
     * @param pessoa
     */
    public void deletePessoa(Pessoa pessoa) {

        Pessoa p = new Select().from(GrupoPessoa.class).where("Pessoa=?", pessoa.getId()).executeSingle();
        if (p == null) {
            pessoa.delete();
        }

    }
    /**
     * Eliminar uma pessoa, caso nao esta associado a nenhum grupo
     *
     * @param idPessoa
     */
    /*public void deletePessoa(long idPessoa) {

        Pessoa pessoa = new Select().from(GrupoPessoa.class).where("Pessoa=?", idPessoa).executeSingle();
        if (pessoa == null) {
            pessoa.delete();
        }

    }

    *//**
     * Eliminar um grupo.
     * Verificar a dependencia do grupo na despesa
     *
     * @param grupo
     *//*
    public void deleteGrupo(Grupo grupo) {

        Grupo g1 = new Select().from(DespesaDoGrupo.class).where("Grupo=?", grupo.getId()).executeSingle();

        if (g1 == null) {
            grupo.delete();
        }

    }

    */

    /**
     * Eliminar um grupo.
     * Verificar a dependencia do grupo na despesa
     *
     * @param
     *//*
    public void deleteGrupo(long Idgrupo) {

        Grupo grupo = new Select().from(DespesaDoGrupo.class).where("Grupo=?", Idgrupo).executeSingle();

        if (grupo == null) {
            grupo.delete();
        }
    }

    */
    public void deleteDespesa(Despesa despesa) {
        Despesa tmp = Model.load(Despesa.class, despesa.getId());
        Conta conta = new Select().from(Conta.class).where("Despesa=?", despesa.getId()).executeSingle();
        if (conta != null) {
            conta.delete();
            if (tmp != null) {
                tmp.delete();
            }
        }

    }

    public void deleteDespesa(long idDespesa) {

        Despesa despesa = Model.load(Despesa.class, idDespesa);
        Conta conta = new Select().from(Conta.class).where("Despesa=?", idDespesa).executeSingle();
        if (despesa != null) {
            despesa.delete();
            conta.delete();
        }
    }

    public void deleteDespesas(List<Despesa> despesas) {
        for (Despesa despesa : despesas) {
            deleteDespesa(despesa);
        }
    }

    public Pessoa getPessoa(long id) {
        return Pessoa.load(Pessoa.class, id);
    }

    public Grupo getGrupo(long id) {
        return Grupo.load(Grupo.class, id);
    }

    public Despesa getDespesa(long id) {
        return Despesa.load(Despesa.class, id);
    }


    public List<Pessoa> getPessoas() {
        return new Select().from(Pessoa.class).orderBy("Nome ASC").execute();
    }

    public List<Grupo> getGrupos() {
        return new Select().from(Grupo.class).orderBy("Nome ASC").execute();
    }

    public List<Despesa> getDespesas() {
        return new Select().from(Despesa.class).orderBy("Tipo ASC").execute();
    }

    /*public List<Pagamento> getPagamentos() {
        return new Select().from(Pagamento.class).execute();
    }*/


    public List<Pessoa> getPessoasNoGrupo(Grupo grupo) {
        return getPessoasNoGrupo(grupo.getId());
    }


    public List<Pessoa> getPessoasNoGrupo(long idGrupo) {

        List<GrupoPessoa> grupoPessoas = new Select().from(GrupoPessoa.class).where("Grupo=?", idGrupo).execute();
        List<Pessoa> pessoas = new ArrayList<>();
        for (GrupoPessoa grupoPessoa : grupoPessoas) {
            Pessoa pessoa = grupoPessoa.pessoa;
            if (pessoa != null) {
                pessoas.add(pessoa);
            }
        }

        return pessoas;
    }

    public List<Grupo> getGrupoQuePessoaPertence(long idPessoa) {
        List<GrupoPessoa> grupoPessoas = new Select().from(GrupoPessoa.class).where("Pessoa=?", idPessoa).execute();
        List<Grupo> grupos = new ArrayList<>();

        for (GrupoPessoa grupoPessoa : grupoPessoas) {
            Grupo grupo = grupoPessoa.grupo;
            if (grupo != null) {
                grupos.add(grupo);
            }
        }

        return grupos;
    }

    public List<Despesa> getDespesasDoGrupo(long idGrupo) {
        return new Select().from(Despesa.class).where("Grupo=?", idGrupo).execute();
    }


    public List<List<Despesa>> getDespesaDaPessoa(long idPessoa) {
        List<Grupo> grupos = getGrupoQuePessoaPertence(idPessoa);
        List<Despesa> despesas;
        List<List<Despesa>> mPessoaDespesa = new ArrayList<>();
        for (Grupo grupo : grupos) {
            despesas = getDespesasDoGrupo(grupo.getId());
            mPessoaDespesa.add(despesas);
        }
        return mPessoaDespesa;
    }

    public List<Conta> getContas() {
        return new Select().from(Conta.class).orderBy("Grupo ASC").execute();
    }

    public Conta getTotalValorDasContasDoGrupo(long idGrupo) {
        return new Select().from(Conta.class).where("Grupo=?", idGrupo).executeSingle();
    }

    public void uploadConta(List<AcertoDeConta> contas, final List<Despesa> despesas, List<Pessoa> pessoasDoGrupo) {


        for (AcertoDeConta conta : contas) {

            ParseContaData contaData = new ParseContaData(despesas, pessoasDoGrupo);
            contaData.setNovaConta(conta.getGrupo(), conta.getPessoa(), conta.getValorQueDeviaPagar(), conta.getValorQuePagou(), conta.getValorFinal());
            contaData.setUser(ParseUser.getCurrentUser());

            contaData.saveEventually();//Upload quando estiver conectado a internet
            /*
            try {
                contaData.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

        }

    }
}
