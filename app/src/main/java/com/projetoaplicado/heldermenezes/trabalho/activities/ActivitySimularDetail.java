package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IDespesaItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IPessoaItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.AcertoDeConta;
import com.projetoaplicado.heldermenezes.trabalho.model.Conta;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;


import java.util.ArrayList;
import java.util.List;

public class ActivitySimularDetail extends AppCompatActivity implements View.OnClickListener,
        IDespesaItemClickListener,
        IPessoaItemClickListener {

    private Toolbar mToolbar;
    private DatabaseHelper db;
    private TextView tvNomeDoGrupo, tvTotalValor;
    private RecyclerView recyclerViewDespesas, recyclerViewPessoas;
    private List<Despesa> despesas;
    private DespesaRecyclerViewAdapter despesaAdapter;
    private PessoaValorPagoRecyclerViewAdapter pessoaAdapter;
    private ImageLoader imageLoader;
    private ArrayList<AcertoDeConta> acertos;
    private Double total;
    private Grupo grupo;
    private Button btnSimular, btnFechar;
    private AdapterValorAPagar adapterValorAPagar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simular_detail);

        if (!ParseUtils.isRegisteredUser()) {
            Util.Utilities.startActivity(this, ActivityLogin.class, null);
        }
        init();

        getExtrasValue();
    }

    private void getExtrasValue() {
        if (getIntent().hasExtra("id")) {
            long id = getIntent().getLongExtra("id", -1);
            int param = getIntent().getIntExtra(Util.PARAMS.ARGS, -1);
            toogleButton(param);

            grupo = db.getGrupo(id);
            if (grupo != null) {
                tvNomeDoGrupo.setText(grupo.nome);
                Conta conta = db.getTotalValorDasContasDoGrupo(grupo.getId());
                total = conta.valor;
                tvTotalValor.setText(String.format("%.2f %s", total, Util.Moeda.getStringMoeda().get(conta.moeda)));
                configContas(id);
                setUpAdapter(acertos, param);
            }

        } else {
            Util.Utilities.showToast(this, "Upss");
            finish();
        }
    }

    private void init() {
        initToolBar();
        tvNomeDoGrupo = (TextView) findViewById(R.id.id_tv_simular_detail_nome_do_grupo);
        tvTotalValor = (TextView) findViewById(R.id.id_tv_simular_detail_valor_total);

        recyclerViewDespesas = (RecyclerView) findViewById(R.id.id_recycler_simular_detail_despesas);
        recyclerViewPessoas = (RecyclerView) findViewById(R.id.id_recycler_simular_detail_pessoas);

        btnSimular = (Button) findViewById(R.id.id_btn_simular);
        btnFechar = (Button) findViewById(R.id.id_btn_fechar);

        btnSimular.setOnClickListener(this);
        btnFechar.setOnClickListener(this);


        imageLoader = Util.Image.initImageLoader(this, imageLoader);
        db = DatabaseHelper.newInstance();
    }


    private void toogleButton(int param) {
        if (param == Util.PARAMS.FECHAR) {
            btnSimular.setVisibility(View.GONE);
        } else {
            btnFechar.setVisibility(View.GONE);
        }
    }

    private void configContas(long id) {
        despesas = db.getDespesasDoGrupo(id);
        List<Pessoa> pessoasNoGrupo = db.getPessoasNoGrupo(grupo);
        acertos = new ArrayList<>();
        boolean pagou = false;
        int moeda = 0;
        int countPessoa = 0;
        AcertoDeConta acertoDeConta;

        for (Pessoa pessoa : pessoasNoGrupo) {

            for (Despesa despesa : despesas) {

                if (pessoa.getId() == despesa.pessoa.getId()) {

                    moeda = despesa.moeda;

                    if (!acertos.isEmpty() && containPessoa(pessoa)) {

                        acertoDeConta = jaContemAPessoa(pessoa);

                        if (null != acertoDeConta) {
                            Double tmp = acertoDeConta.getValorQuePagou() + despesa.importancia;
                            acertoDeConta.setValorQuePagou(tmp);

                            updateConta(acertoDeConta);
                        }
                    } else {
                        acertoDeConta = new AcertoDeConta(despesa.pessoa, despesa.importancia, moeda);
                        acertoDeConta.setGrupo(grupo);

                        acertos.add(acertoDeConta);
                    }

                    pagou = true;
                } else {
                    pagou = false;
                }
            }
            if (!pagou && !containPessoa(pessoa)) {
                acertoDeConta = new AcertoDeConta(pessoa, 0.0, moeda);
                acertoDeConta.setGrupo(grupo);

                acertos.add(acertoDeConta);
            }
        }
    }

    private void updateConta(AcertoDeConta conta) {
        for (AcertoDeConta ac : acertos) {
            if (ac.getPessoa().getId() == conta.getPessoa().getId()) {
                ac.setValorQuePagou(conta.getValorQuePagou());
            }
        }
    }

    public AcertoDeConta jaContemAPessoa(Pessoa pessoa) {

        for (AcertoDeConta conta : acertos) {
            if (containPessoa(pessoa)) {
                return conta;
            }
        }
        return null;
    }

    public boolean containPessoa(Pessoa pessoa) {

        for (AcertoDeConta conta : acertos) {
            if (conta.getPessoa().getId() == pessoa.getId()) {
                return true;
            }
        }

        return false;
    }


    public void simular(View view) {
        showSimulacao();
    }


    private void initToolBar() {
        mToolbar = (Toolbar) this.findViewById(R.id.id_toolbar);
        this.setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }


    private void setUpAdapter(List<AcertoDeConta> acertoDeContas, int param) {
        setupRecyclerViewDespesa(param);
        setupRecyclerViewPessoasValorPago(acertoDeContas, param);
        adapterValorAPagar = new AdapterValorAPagar(this, acertos, despesas, total, param);
    }

    private void setupRecyclerViewDespesa(int param) {

        recyclerViewDespesas.setLayoutManager(new LinearLayoutManager(this));
        if (despesas == null) {
            despesas = db.getDespesas();
        }
        despesaAdapter = new DespesaRecyclerViewAdapter(this, despesas, imageLoader);
        despesaAdapter.setOnDespesaItemClickListener(this);
        recyclerViewDespesas.setAdapter(despesaAdapter);

    }

    private void setupRecyclerViewPessoasValorPago(List<AcertoDeConta> acertoDeContas, int param) {

        recyclerViewPessoas.setLayoutManager(new LinearLayoutManager(this));
        pessoaAdapter = new PessoaValorPagoRecyclerViewAdapter(this, acertoDeContas, grupo, imageLoader);
        pessoaAdapter.setOnPessoaItemClickListener(this);
        recyclerViewPessoas.setAdapter(pessoaAdapter);


    }


    private void showSimulacao() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_valor_pagar_por_pessoa, null);


        String[] textos = new String[2];
        textos[0] = getString(R.string.title_activity_activity_simular_detail);

        MaterialDialog mMaterialDialog = Util.Dialog.createMaterialDialogForStartActivity(this, adapterValorAPagar, ActivitySimularDetail.class, false, textos);
        mMaterialDialog.show();
    }

    private MaterialDialog getDialogSimulacao(boolean wrapInScrollView, View view) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .customView(view, wrapInScrollView)
                .build();
        return materialDialog;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_simular_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            Util.Utilities.showToast(this,"CLICKED");
            Util.Utilities.startActivity(this, MainActivity.class, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Util.Utilities.showToast(this, "CLICKED");
        Util.Utilities.startActivity(this, MainActivity.class, null);
    }

    @Override
    public void despesaItemClick(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Util.Utilities.startActivity(this, ActivityDespesaDetail.class, bundle);
    }

    @Override
    public void despesaItemLongClick(long id, View v) {

    }

    @Override
    public void onPessoaItemClicked(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Util.Utilities.startActivity(this, ActivityPessoaDetail.class, bundle);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.id_btn_simular) {
            showSimulacao();
        } else if (id == R.id.id_btn_fechar) {
            if (!Util.isExisteConexaoAInternet(this)) {
                Snackbar.make(v, getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (ParseUtils.isRegisteredUser()) {
                confirmarFechoDaConta();

            }
        }
    }


    private void confirmarFechoDaConta() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivitySimularDetail.this);
        alertDialog.setTitle(getString(R.string.fechar_conta));
        alertDialog.setMessage(getString(R.string.fechar_mesmo_as_contas));
        alertDialog.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enviarDadosParaParse();
            }
        });

        alertDialog.setNegativeButton(getString(R.string.nao), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void enviarDadosParaParse() {

        ArrayList<AcertoDeConta> acerto_final = getAcertoDeContasFinal();
        long idGrupo = acerto_final.get(0).getGrupo().getId();
        Bundle bundle = new Bundle();
        bundle.putSerializable("contas", acerto_final);
        bundle.putLong("idGrupo", Long.valueOf(idGrupo));
        Util.Utilities.startActivityForResult(this, ActivityEnviarDados.class, 1507, bundle);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        Util.Utilities.startActivity(this, MainActivity.class,null);
    }

    /*ADAPTER DESPESAS*/
    public static class DespesaRecyclerViewAdapter
            extends RecyclerView.Adapter<DespesaRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private List<Despesa> mValues;

        private IDespesaItemClickListener listener;
        private ImageLoader imageLoader;

        //private ParseDatabaseHelper parseDb;

        public DespesaRecyclerViewAdapter(Context context, List<Despesa> items, ImageLoader imageLoader) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mValues = items;
            this.imageLoader = imageLoader;
            //this.parseDb = ParseDatabaseHelper.newInstance();
        }

        public void setOnDespesaItemClickListener(IDespesaItemClickListener listener) {
            this.listener = listener;
        }


        public Despesa getValueAt(int position) {
            return mValues.get(position);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_simular_detail_despesas, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Despesa despesa = mValues.get(position);
            holder.mBoundString = despesa.tipo;
            holder.mTextView.setText(despesa.tipo.toUpperCase());
            String symMoeda = Util.Moeda.Simbolos.getSimbolos().get(despesa.moeda);

            String valor = String.format("%.2f",despesa.importancia, symMoeda);

            holder.mTvValor.setText(valor);

            final String st = despesa.descricao;
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        listener.despesaItemClick(despesa.getId());
                    }
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.despesaItemLongClick(despesa.getId(), v);
                    }
                    return true;
                }
            });

            if (TextUtils.isEmpty(despesa.urlDaFoto)) {
                Glide.with(holder.mImageView.getContext())
                        .load(R.drawable.no_media)
                        .fitCenter()
                        .into(holder.mImageView);
            } else {
                setFotoToImageView(despesa.urlDaFoto, holder.mImageView);
            }

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        private void setFotoToImageView(String path, ImageView imageView) {
            if (imageView != null) {
                imageLoader.displayImage("file://" + path, imageView);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView, mTvValor;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mTvValor = (TextView) view.findViewById(R.id.id_tv_item_simular_detail_valor);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }
    }


    /* ADAPTER PESSOAS*/
    public static class PessoaValorPagoRecyclerViewAdapter
            extends RecyclerView.Adapter<PessoaValorPagoRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<AcertoDeConta> mAcertoDeConta;
        private Context context;
        private ImageLoader imageLoader;
        private IPessoaItemClickListener listener;
        private Grupo grupo;

        public PessoaValorPagoRecyclerViewAdapter(Context context, List<AcertoDeConta> items, Grupo grupo, ImageLoader imageLoader) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mAcertoDeConta = items;
            this.context = context;
            this.imageLoader = imageLoader;
            this.grupo = grupo;
        }

        public void setOnPessoaItemClickListener(IPessoaItemClickListener listener) {
            this.listener = listener;
        }


        public AcertoDeConta getValueAt(int position) {
            return mAcertoDeConta.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_simular_detail_pessoas, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            AcertoDeConta acertoDeConta = getValueAt(position);
            final Pessoa pessoa = acertoDeConta.getPessoa();
            holder.mBoundString = pessoa.nome;
            holder.mTextView.setText(pessoa.nome);
            String strMoeda = Util.Moeda.Simbolos.getSimbolos().get(acertoDeConta.getMoeda());
            String valor = String.format("%.2f", acertoDeConta.getValorQuePagou(), strMoeda);
            holder.tvValorQuePagou.setText(valor);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Snackbar.make(v, pessoa.nome + " " + pessoa.email, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    if (listener != null) {
                        listener.onPessoaItemClicked(pessoa.getId());
                    }
                }
            });

            if (TextUtils.isEmpty(pessoa.urlDaFoto)) {
                Glide.with(holder.mImageView.getContext())
                        .load(R.drawable.no_media)
                        .fitCenter()
                        .into(holder.mImageView);
            } else {
                setFotoToImageView(pessoa.urlDaFoto, holder.mImageView);
            }


        }

        @Override
        public int getItemCount() {
            return mAcertoDeConta.size();
        }

        private void setFotoToImageView(String path, ImageView imageView) {
            if (imageView != null) {
                imageLoader.displayImage("file://" + path, imageView);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView, tvValorQuePagou;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                tvValorQuePagou = (TextView) view.findViewById(R.id.id_tv_item_simular_detail_valor_que_pagou);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }
    }


    /*ADAPTER PESSOAS COM VALOR A PAG*/

    public static class AdapterValorAPagar extends ArrayAdapter<AcertoDeConta> {

        private Context context;
        private ArrayList<AcertoDeConta> contas;
        private Double totalConta;
        private List<Despesa> despesas;
        private int param;

        public AdapterValorAPagar(Context context, ArrayList<AcertoDeConta> contas, List<Despesa> despesas, Double totalConta, int param) {
            super(context, R.layout.item_valor_pagar_por_pessoa);
            this.context = context;
            this.contas = contas;
            this.totalConta = totalConta;
            this.despesas = despesas;
            this.param = param;
            fazerCalculo();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_valor_pagar_por_pessoa, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            AcertoDeConta conta = getItem(position);
            Pessoa pessoa = conta.getPessoa();
            String strMoeda = Util.Moeda.Simbolos.getSimbolos().get(conta.getMoeda());

            holder.mNome.setText(pessoa.nome);
            holder.mValorDeviaPagar.setText(String.format("%.2f %s", conta.getValorQueDeviaPagar(), strMoeda));

            holder.mValorQuePagou.setText(String.format("%.2f %s", conta.getValorQuePagou(), strMoeda));
            String desc="";
            Double vfTemp = conta.getValorFinal();
            if(vfTemp<0){
                desc = context.getString(R.string.tem_que_pagar);
            }else if(vfTemp>=0){
                desc = context.getString(R.string.tem_que_receber);
            }
            holder.mDescVFinal.setText(desc);
            holder.mValorFinal.setText(String.format("%.2f %s", conta.getValorFinal(), strMoeda));

            return convertView;
        }

        @Override
        public int getCount() {
            return contas.size();
        }

        @Override
        public AcertoDeConta getItem(int position) {
            return contas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        static class Holder {

            View mView;
            TextView mNome, mValorDeviaPagar, mValorQuePagou, mValorFinal, mDescVFinal;

            public Holder(View view) {
                mView = view;
                mNome = (TextView) view.findViewById(R.id.id_tv_item_valor_pagar_nome_da_pessoa);
                mValorDeviaPagar = (TextView) view.findViewById(R.id.id_tv_item_valor_pagar_valor_devia_pagar);
                mValorQuePagou = (TextView) view.findViewById(R.id.id_tv_item_valor_pagar_valor_que_pagou);
                mValorFinal = (TextView) view.findViewById(R.id.id_tv_item_valor_pagar_valor_final);
                mDescVFinal = (TextView) view.findViewById(R.id.id_tv_item_valor_pagar_valor_final_desc);

            }
        }

        public void fazerCalculo() {

            Double valorDeviaPagar = totalConta / contas.size();
            //Util.Utilities.showToast(context, "O que devia ser pago - " + valorDeviaPagar);

            Double valorPagou = 0.0;
            Double valorFinal = 0.0;

            for (AcertoDeConta conta : contas) {

                valorPagou = conta.getValorQuePagou();
                valorFinal = valorDeviaPagar - valorPagou;
                conta.setValorQueDeviaPagar(valorDeviaPagar);
                conta.setValorQuePagou(valorPagou);
                conta.setValorFinal(valorFinal);

            }

            ActivitySimularDetail.setAcertosFinais(contas);

        }

    }


    private static ArrayList<AcertoDeConta> acertoDeContasFinal;

    public static void setAcertosFinais(ArrayList<AcertoDeConta> acertos) {
        acertoDeContasFinal = acertos;
    }

    public ArrayList<AcertoDeConta> getAcertoDeContasFinal() {
        return ActivitySimularDetail.acertoDeContasFinal;
    }

}
