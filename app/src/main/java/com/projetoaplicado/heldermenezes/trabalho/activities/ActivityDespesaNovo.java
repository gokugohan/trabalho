package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.adapters.SelectGrupoAdapter;
import com.projetoaplicado.heldermenezes.trabalho.adapters.SelectMoedaAdapter;
import com.projetoaplicado.heldermenezes.trabalho.adapters.SelectPessoaNoGrupoAdapter;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.RequestApiTaxaDeCambio;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.List;

public class ActivityDespesaNovo extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        SelectMoedaAdapter.OnMoedaItemClickListener,
        SelectGrupoAdapter.OnGrupoItemClickListener,
        SelectPessoaNoGrupoAdapter.OnPessoaNoGrupoItemClickListener {

    private ImageLoader imageLoader;
    private MaterialEditText mNome;
    private MaterialEditText mDescricao;
    private ImageView mImagem;
    private MaterialEditText mMoeda;
    private MaterialBetterSpinner mGrupo;
    private MaterialBetterSpinner mPessoa;
    private MaterialEditText mData;
    private MaterialEditText mImportancia;
    private FloatingActionButton mSaveButton;


    private String nome, descricao, urlDaImagem, nomeDaFoto;
    private Double importancia;
    private Calendar data;
    private Bitmap bitmap;

    private long id = -1;
    private boolean editMode = false;
    private int moeda = -1;

    private DatabaseHelper db;
    private Grupo grupo;
    private Pessoa pessoa;
    private Despesa despesa;
    //private Pagamento pagamento;
    private MaterialDialog mMaterialDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa_novo);

        if (!ParseUtils.isRegisteredUser()) {
            Util.Utilities.startActivity(this, ActivityLogin.class, null);
        }

        db = DatabaseHelper.newInstance();
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_despesa_novo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        this.imageLoader = Util.Image.initImageLoader(this, imageLoader);
        this.mNome = (MaterialEditText) findViewById(R.id.id_et_despesa_nova_nome);
        this.mDescricao = (MaterialEditText) findViewById(R.id.id_et_despesa_nova_descricao);
        this.mImagem = (ImageView) findViewById(R.id.id_iv_despesa_nova_imagem);
        this.mMoeda = (MaterialEditText) findViewById(R.id.id_et_despesa_nova_moeda);
        this.mGrupo = (MaterialBetterSpinner) findViewById(R.id.id_et_despesa_nova_grupo);
        this.mPessoa = (MaterialBetterSpinner) findViewById(R.id.id_et_despesa_nova_pessoa_que_pagou);
        this.mData = (MaterialEditText) findViewById(R.id.id_et_despesa_nova_data);
        this.mImportancia = (MaterialEditText) findViewById(R.id.id_et_despesa_nova_importancia);
        this.mSaveButton = (FloatingActionButton) findViewById(R.id.id_fab_despesa_nova_save);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getLong("id", -1);

            // Receber um Id da despesa
            if (id != -1) {
                editMode = true;
                despesa = db.getDespesa(id);
                grupo = db.getGrupo(despesa.grupo.getId());
                pessoa = db.getPessoa(despesa.pessoa.getId());

                //pagamento = db.getPagamento(despesa);

                importancia = despesa.importancia;
                moeda = despesa.moeda;
                data = Calendar.getInstance();
                data.setTimeInMillis(despesa.data);

                mNome.setText(despesa.tipo);
                mDescricao.setText(despesa.descricao);
                mMoeda.setText(Util.Moeda.getStringMoeda().get(despesa.moeda));
                mGrupo.setText(grupo.nome);
                mPessoa.setText(pessoa.nome);
                mData.setText(Util.Utilities.getDateFormat(despesa.data));

                DecimalFormat df = new DecimalFormat("#.00");
                String sImp = df.format(importancia);

                mImportancia.setText(sImp);
                urlDaImagem = despesa.urlDaFoto;
                setFotoToImageView(urlDaImagem);
            }
            toolbar.setTitle(getString(R.string.despesa));
            toolbar.setSubtitle(getResources().getString(R.string.alterar_despesa));
        } else {
            editMode = false;
            toolbar.setSubtitle(getString(R.string.recolher_nova_despesa));
        }


        this.mData.setOnClickListener(this);
        this.mMoeda.setOnClickListener(this);
        this.mGrupo.setOnClickListener(this);
        this.mPessoa.setOnClickListener(this);
        this.mImagem.setOnClickListener(this);
        this.mSaveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_iv_despesa_nova_imagem:
                getFotoIntent();
                break;

            case R.id.id_et_despesa_nova_data:
                selectData();
                break;
            case R.id.id_et_despesa_nova_moeda:
                selectMoeda();
                break;
            case R.id.id_et_despesa_nova_grupo:
                selectGrupo();
                break;
            case R.id.id_et_despesa_nova_pessoa_que_pagou:

                if (grupo == null) {
                    Util.Utilities.showToast(this, getString(R.string.seleciona_primeiro_o_grupo));
                    return;
                }
                List<Pessoa> pessoas;
                pessoas = db.getPessoasNoGrupo(grupo.getId());

                for (Pessoa p : pessoas) {
                    Util.Utilities.showLog("TAG", p.nome + " " + p.email + " " + p.telefone);
                }

                selectPessoa(grupo.getId());

                break;
            case R.id.id_fab_despesa_nova_save:
                saveDespesa(v);
                break;
        }
    }

    Double valorConvertido = 0.0;

    private void saveDespesa(View v) {

        boolean saved = false, updated = false;
        nome = mNome.getText().toString().trim();
        descricao = mDescricao.getText().toString().trim();


        try {
            importancia = Double.parseDouble(this.mImportancia.getText().toString());
        } catch (Exception e) {
            Util.Utilities.showToast(this,getString(R.string.importancia_erro));
            return;
        }
        if (!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(descricao)) {

            if (grupo != null && pessoa != null && moeda != -1 && data != null) {

                if (moeda != grupo.moeda) {
                    if (!Util.isExisteConexaoAInternet(this)) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                        alertDialog.setTitle(getString(R.string.no_connection));
                        alertDialog.setMessage(getString(R.string.moeda_despesa_e_do_grupo_sao_diferentes));
                        alertDialog.show();
                        return;
                    } else {

                        //Double taxa = Util.Moeda.getValorTaxaDeCambio(this, data, moedaDaDespesa, moedaDoGrupo);

                        final String moedaDoGrupo = Util.Moeda.Simbolos.getSimbolos().get(grupo.moeda);
                        final String moedaDaDespesa = Util.Moeda.Simbolos.getSimbolos().get(moeda);
                        //Snackbar.make(v, "GRUPO: " + moedaDoGrupo + " - DESPESA: " + moedaDaDespesa, Snackbar.LENGTH_SHORT).show();

                        int ano = data.get(Calendar.YEAR);
                        int mes = data.get(Calendar.MONTH);
                        int dia = data.get(Calendar.DAY_OF_MONTH);

                        String oMes = mes < 10 ? 0 + "" + mes : mes + "";
                        String oDia = dia < 10 ? 0 + "" + dia : dia + "";
                        Log.d("DATA", ano + "-" + mes + "-" + dia);
                        String aData = String.format("%d-%s-%s", ano, oMes, oDia);
                        String url = "http://api.fixer.io/" + aData;
                        url += "?base=" + moedaDaDespesa + "&symbols=" + moedaDoGrupo;

                        fazerRequestEGuardarDespesa(moedaDoGrupo, url);
                    }
                }else{
                    fazerRequestEGuardarDespesaAux(0.0);
                }


            } else {
                Util.Utilities.showToast(this, getString(R.string.dados_incompleto));
            }

            mNome.setText("");
            mDescricao.setText("");
            mImportancia.setText("");
            mGrupo.setText("");
            mPessoa.setText("");
            mMoeda.setText("");
            mData.setText("");
            //urlDaImagem = "";
            mImagem.setImageResource(R.drawable.camera);

        } else {
            Util.Utilities.showToast(this, getString(R.string.dados_incompleto));
        }

        setResult(RESULT_OK);
        if (saved) {
            Util.Utilities.showToast(this, getString(R.string.despesa_inserido_com_sucesso));
        } else if (updated) {
            Util.Utilities.showToast(this, getString(R.string.despesa_atualizado_com_sucess));
            finish();
        }
    }

    private void fazerRequestEGuardarDespesa(final String moedaDoGrupo, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resposta) {
                JSONObject jsonObject = resposta;
                try {
                    JSONObject taxa = jsonObject.getJSONObject("rates");
                    Double taxaDeCambio = taxa.getDouble(moedaDoGrupo);
                    fazerRequestEGuardarDespesaAux(taxaDeCambio);
                } catch (JSONException e) {
                    Util.Utilities.showToast(ActivityDespesaNovo.this, getString(R.string.ocorreu_erro_inesperado));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Util.Utilities.showToast(ActivityDespesaNovo.this, getString(R.string.data_muita_antiga));
                Util.Utilities.showToast(ActivityDespesaNovo.this, getString(R.string.ocorreu_erro_inesperado));
            }
        });
        RequestApiTaxaDeCambio.getInstance(ActivityDespesaNovo.this).addToRequestQueue(jsonObjectRequest);
    }

    private void fazerRequestEGuardarDespesaAux(Double taxaDeCambio) {
        if(taxaDeCambio!=0.0){valorConvertido = importancia * taxaDeCambio;
        }else{valorConvertido = importancia;}

        if (urlDaImagem == null) {
            Util.Utilities.showToast(ActivityDespesaNovo.this, getString(R.string.foto_obrigatorio));
            return;
        }
        if (!editMode) {
            db.saveDespesa(nome, descricao, urlDaImagem, grupo, pessoa, valorConvertido, data.getTimeInMillis());
            Util.Utilities.showToast(ActivityDespesaNovo.this, getString(R.string.despesa_inserido_com_sucesso));
        } else if (despesa != null) {
            long idGrupoQuePertenceuADespesa = despesa.grupo.getId();
            despesa.tipo = nome;
            despesa.descricao = descricao;
            despesa.urlDaFoto = urlDaImagem;
            despesa.importancia = valorConvertido;
            despesa.data = data.getTimeInMillis();
            despesa.grupo = grupo;
            despesa.pessoa = pessoa;
            db.updateDespesa(idGrupoQuePertenceuADespesa, despesa);
            Util.Utilities.showToast(ActivityDespesaNovo.this, getString(R.string.despesa_atualizado_com_sucess));
        }
    }


    private void selectPessoa(Long id) {
        // Obter apenas as pessoas que estao no grupo selecionado
        List<Pessoa> gruposParaSelecionar = db.getPessoasNoGrupo(id);

        final SelectPessoaNoGrupoAdapter adapter = new SelectPessoaNoGrupoAdapter(this, gruposParaSelecionar);
        adapter.setListener(this);
        String[] textos = new String[2];
        textos[0] = "Selecionar pessoa";
        textos[1] = "Nova pessoa";

        mMaterialDialog = Util.Dialog.createMaterialDialogForStartActivity(this, adapter, ActivityDespesaNovo.class, false, textos);
        mMaterialDialog.show();
    }

    private void selectGrupo() {
        mPessoa.setText("");
        List<Grupo> gruposParaSelecionar = db.getGrupos();
        final SelectGrupoAdapter adapter = new SelectGrupoAdapter(this, gruposParaSelecionar);
        adapter.setListener(this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] textos = new String[2];
        textos[0] = "Selecionar grupo";
        textos[1] = "Novo grupo";

        mMaterialDialog = Util.Dialog.createMaterialDialogForStartActivity(this, adapter, ActivityGrupoNovo.class, true, textos);

        mMaterialDialog.show();
    }

    private void selectMoeda() {
        SelectMoedaAdapter adapter = new SelectMoedaAdapter(this, Util.Moeda.getStringMoeda());
        adapter.setListener(this);
        String[] textos = new String[1];
        textos[0] = "Selecionar pessoa";
        mMaterialDialog = Util.Dialog.createMaterialDialogForStartActivity(this, adapter, null, false, textos);
        mMaterialDialog.show();
    }

    private void selectData() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void getFotoIntent() {
        AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(this);
        dialog.setTitle("Select option");
        dialog.setNegativeButton("CAMERA", cameraListener);
        dialog.setPositiveButton("GALERRY", galleryListener);
        dialog.show();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (resultCode == RESULT_OK) {

                if (requestCode == Util.Request.REQUEST_IMAGE_GALERY_CODE) {
                    this.urlDaImagem = Util.Image.getImagePath(this, data);
                    Log.e("CAMERA_URL_FOTO", urlDaImagem);
                    this.bitmap = Util.Image.getBitmap(this.urlDaImagem);

                    if (!TextUtils.isEmpty(this.urlDaImagem)) {
                        String tmps[] = this.urlDaImagem.split("/");
                        nomeDaFoto = tmps[tmps.length - 1];
                    }
                }else if(requestCode == Util.Request.REQUEST_IMAGE_CAPTURE_CODE){
                    this.urlDaImagem = Util.Image.getImagePath(this, data);
                    Log.e("CAMERA_URL_FOTO", urlDaImagem);
                }
            }
        }
        if (!TextUtils.isEmpty(this.urlDaImagem)) {
            setFotoToImageView(this.urlDaImagem);
        }

    }


    private void setFotoToImageView(String path) {
        imageLoader.displayImage("file://" + path, mImagem);
    }

    private DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            // http://developer.android.com/training/camera/photobasics.html

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(ActivityDespesaNovo.this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = Util.Image.createImageFile();
                } catch (IOException ex) {
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri uri = Uri.fromFile(photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                    Util.Utilities.showLog("URI_FILE", uri.getPath());
                    urlDaImagem = uri.getPath();
                    startActivityForResult(intent, Util.Request.REQUEST_IMAGE_CAPTURE_CODE);
                }
            }
            dialog.dismiss();
        }
    };
    private DialogInterface.OnClickListener galleryListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Intent intent = new Intent(Util.Request.ACTION_PICK);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(ActivityDespesaNovo.this.getPackageManager()) != null) {
                startActivityForResult(intent, Util.Request.REQUEST_IMAGE_GALERY_CODE);
            }
            dialog.dismiss();
        }
    };

    private void setData(int year, int monthOfYear, int dayOfMonth, Calendar hoje, Calendar diaSelecionada) {
        boolean bDiaMenorQueHoje = diaSelecionada.before(hoje) || diaSelecionada.equals(hoje);

        if (!bDiaMenorQueHoje) {
            return;
        }

        data = diaSelecionada;

        String dia = (dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth);
        String mes = (monthOfYear + 1) < 10 ? "0" + monthOfYear : "" + monthOfYear;
        String dmy = dia + "/" + mes + "/" + year;
        this.mData.setText(dmy);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar hoje = Calendar.getInstance();
        Calendar diaSelecionada = Calendar.getInstance();
        diaSelecionada.set(year, monthOfYear, dayOfMonth);
        setData(year, monthOfYear + 1, dayOfMonth, hoje, diaSelecionada);
    }

    @Override
    public void onMoedaItemClick(int position) {
        this.mMoeda.setText(Util.Moeda.getStringMoeda().get(position));
        this.mMaterialDialog.dismiss();
        this.moeda = position;
    }

    @Override
    public void onGrupoItemClick(long id) {
        grupo = db.getGrupo(id);
        if (grupo != null) {
            this.mGrupo.setText(grupo.nome);
        } else {
            Util.Utilities.showToast(this, "Grupo null");
        }
        this.mMaterialDialog.dismiss();
    }

    @Override
    public void onPessoaNoGrupoItemClick(long id) {
        pessoa = db.getPessoa(id);
        if (pessoa != null) {
            this.mPessoa.setText(pessoa.nome);
        } else {
            Util.Utilities.showToast(this, "@Pessoa null");
        }
        this.mMaterialDialog.dismiss();
    }


}
