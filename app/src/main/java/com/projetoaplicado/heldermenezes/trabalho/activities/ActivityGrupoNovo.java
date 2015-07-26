package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.adapters.GrupoPessoaItemAdapter;
import com.projetoaplicado.heldermenezes.trabalho.adapters.SelectMoedaAdapter;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IPessoaGrupoItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityGrupoNovo extends AppCompatActivity implements SelectMoedaAdapter.OnMoedaItemClickListener, IPessoaGrupoItemClickListener {

    private Toolbar mToolbar;
    private MaterialEditText mNome, mMoedaUsado;
    private MaterialEditText etNome;
    private MaterialEditText etTelefone;
    private MaterialEditText etEmail;
    private ImageView ivFoto;
    private FloatingActionButton btnSave;
    private List<Pessoa> listaDePessoas;
    private ListView mListView;
    private GrupoPessoaItemAdapter mAdapter;
    private DatabaseHelper db;
    private MaterialDialog mDialogSelectGrupo, dialog;
    private int mMoeda = -1;
    private Uri uriContact;
    private String nome, email, telefone, urlDaImagem;

    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private Pessoa pessoa = null;
    private boolean editMode = false;
    private long idGrupo = -1;
    private String nomedafoto = "";
    private MaterialDialog dialogSelectPessoa;
    private int numInitPessoaLista = 0;
    private List<Pessoa> pessoasAdicionadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo_novo);

        // Check if the user is not anonymous user
        // if is the case redirect to the login view
        if (!ParseUtils.isRegisteredUser()) {
            Util.Utilities.startActivity(this, ActivityLogin.class, null);
        }

        imageLoader = Util.Image.initImageLoader(this, imageLoader);
        db = DatabaseHelper.newInstance();

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listaDePessoas == null) {
            listaDePessoas = db.getPessoas();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_grupo_novo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {

        initToolBar();
        this.mNome = (MaterialEditText) this.findViewById(R.id.id_et_grupo_novo_nome);
        this.mMoedaUsado = (MaterialEditText) this.findViewById(R.id.id_et_grupo_novo_moeda_usado);
        this.btnSave = (FloatingActionButton) this.findViewById(R.id.id_btn_grupo_novo_save);

        this.mListView = (ListView) this.findViewById(R.id.id_ll_grupo_novo_lista_pessoas);

        this.btnSave.setOnClickListener(listener);
        this.mMoedaUsado.setOnClickListener(listener);

        if (getIntent().hasExtra("id")) {
            idGrupo = getIntent().getLongExtra("id", -1);
            if (idGrupo != -1 && db != null) {
                editMode = true;
                Grupo grupo = db.getGrupo(idGrupo);
                this.mNome.setText(grupo.nome);
                this.mMoedaUsado.setText(Util.Moeda.getStringMoeda().get(grupo.moeda));
                this.mMoeda = grupo.moeda;
                this.listaDePessoas = db.getPessoasNoGrupo(idGrupo);

                mToolbar.setTitle("Grupo");
                mToolbar.setSubtitle("Alterar grupo");
            }
        } else {
            this.listaDePessoas = new ArrayList<>();
        }

        numInitPessoaLista = listaDePessoas.size();
        initAdapterListview();
    }

    private void initAdapterListview() {

        this.mAdapter = new GrupoPessoaItemAdapter(this, listaDePessoas, imageLoader);

        this.mListView.setAdapter(mAdapter);

        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Util.Utilities.showToast(ActivityGrupoNovo.this, "Item at " + position + " Clicked");

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityGrupoNovo.this);
                alertDialog.setTitle("Remover");
                alertDialog.setMessage("Remover pessoa da lista");
                alertDialog.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (editMode) {

                            removerPessoaNaLista(position);
                        } else {
                            removerPessoaNaLista(position);
                        }


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
        });

    }

    private void removerPessoaNaLista(int position) {
        Pessoa p = listaDePessoas.get(position);
        List<Despesa> despesas = db.getDespesasDoGrupo(idGrupo);

        if (despesas.size() == 0) {
            removerPessoaDaLista(p);
            return;
        }
        for (Despesa despesa : despesas) {
            // So pode remover a pessoa que nao pagou a despesa
            if (p.getId() != despesa.pessoa.getId()) {
                removerPessoaDaLista(p);
            } else {
                Util.Utilities.showToast(this, getString(R.string.nao_pode_remover_pessoa_que_pagou_despesa));
            }
        }
    }

    private void initToolBar() {
        mToolbar = (Toolbar) this.findViewById(R.id.id_toolbar);
        this.setSupportActionBar(mToolbar);
        this.mToolbar.setTitle("Grupo");
        this.mToolbar.setSubtitle(getString(R.string.criar_novo_grupo));
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void adicionarPessoas(View view) {
        //Util.Utilities.showToast(this, "AdicionarPessoas - Clicked!");
        selectContact();
    }

    private void selectContact() {
        AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(this);
        dialog.setTitle(getString(R.string.selecao));
        dialog.setMessage(getString(R.string.selecao_pessoa_mensagem));
        dialog.setNegativeButton(getString(R.string.existente), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectPessoaExistente();
            }
        });
        dialog.setPositiveButton(getString(R.string.nova), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, Util.Request.REQUEST_IMPORTAR_PESSOA_NO_CONTACTO);
            }
        });
        dialog.show();
    }

    private void selectPessoaExistente() {

        SelectPessoaAdapter adapter = new SelectPessoaAdapter(this, imageLoader);
        adapter.setListener(this);
        String[] textos = new String[1];
        textos[0] = getString(R.string.select_pessoa);
        dialogSelectPessoa = Util.Dialog.createMaterialDialogForStartActivity(this, adapter, null, false, textos);
        dialogSelectPessoa.show();
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.id_btn_grupo_novo_save:

                    String nomeDoGrupo = mNome.getText().toString().trim();
                    String moedaUsadoPorGrupo = mMoedaUsado.getText().toString().trim();

                    if (!(TextUtils.isEmpty(nomeDoGrupo) && TextUtils.isEmpty(moedaUsadoPorGrupo))) {
                        saveOrUpdate(nomeDoGrupo);
                        finish();
                    }
                    break;

                case R.id.id_et_grupo_novo_moeda_usado:
                    mostrarDialogoMoeda();
                    break;
            }

        }
    };

    private void saveOrUpdate(String nomeDoGrupo) {
        if (mMoeda != -1) {
            if (!editMode) {
                saveNovoGrupo(nomeDoGrupo);
            } else {
                if (idGrupo != -1) {
                    updateGrupo(nomeDoGrupo);
                }
            }

        } else {
            Util.Utilities.showToast(ActivityGrupoNovo.this, "Uppss");
        }
    }

    private void updateGrupo(String nomeDoGrupo) {

        Grupo grupo = db.getGrupo(idGrupo);
        grupo.nome = nomeDoGrupo;
        grupo.moeda = mMoeda;

        grupo.save();

        if (listaDePessoas != null) {
            db.updatePessoaNoGrupo(grupo, listaDePessoas);
        }
    }

    private void saveNovoGrupo(String nomeDoGrupo) {
        if (checkGrupoNomeExiste(nomeDoGrupo)) {
            Util.Utilities.showToast(ActivityGrupoNovo.this, getString(R.string.o_nome) + nomeDoGrupo + getString(R.string.ja_existe));
            return;
        }
        Grupo grupo = db.saveGrupo(nomeDoGrupo, mMoeda);
        if (listaDePessoas != null) {
            db.savePessoaToGrupo(grupo, listaDePessoas);
        } else {
            //Util.Utilities.showToast(ActivityGrupoNovo.this, "Nao esta adicionar nenhuma pessoa ao grupo\n" + grupo.nome);
        }
    }

    private boolean checkGrupoNomeExiste(String nomeDoGrupo) {
        List<Grupo> grupos = db.getGrupos();
        for (Grupo grupo : grupos) {
            if (grupo.nome.equalsIgnoreCase(nomeDoGrupo)) {
                return true;
            }
        }
        return false;
    }

    private void mostrarDialogoMoeda() {
        SelectMoedaAdapter adapter = new SelectMoedaAdapter(this, Util.Moeda.getStringMoeda());

        adapter.setListener(this);
        mDialogSelectGrupo = new MaterialDialog.Builder(this)
                .title(getString(R.string.select_moeda))
                .adapter(adapter, null)
                .build();
        mDialogSelectGrupo.show();
    }

    @Override
    public void onMoedaItemClick(int position) {
        this.mMoedaUsado.setText(Util.Moeda.getStringMoeda().get(position));
        this.mDialogSelectGrupo.dismiss();
        this.mMoeda = position;
    }

    @Override
    public void onPessoaGrupoItemClick(long id) {
        Pessoa pessoa = db.getPessoa(id);
        if (!jaFoiInseridoNaLista(id)) {
            actualizarLista(pessoa);
        } else {
            Util.Utilities.showToast(this, getString(R.string.ja_foi_adicionado));
        }
        dialogSelectPessoa.dismiss();
    }

    private boolean jaFoiInseridoNaLista(long id) {
        if (this.listaDePessoas != null) {
            for (Pessoa pessoa : listaDePessoas) {
                if (pessoa.getId() == id) {
                    return true;
                }
            }
        }

        return false;
    }

    public void actualizarLista(Pessoa pessoa) {
        this.listaDePessoas.add(pessoa);
        this.pessoasAdicionadas.add(pessoa);
        mAdapter.notifyDataSetInvalidated();
        mAdapter.notifyDataSetChanged();
    }

    public void removerPessoaDaLista(Pessoa pessoa) {
        this.listaDePessoas.remove(pessoa);
        this.pessoasAdicionadas.remove(pessoa);
        mAdapter.notifyDataSetInvalidated();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == Util.Request.REQUEST_IMAGE_GALERY_CODE) {
                this.urlDaImagem = Util.Image.getImagePath(this, data);
                this.bitmap = Util.Image.getBitmap(this.urlDaImagem);
                if (!TextUtils.isEmpty(this.urlDaImagem)) {
                    String tmps[] = this.urlDaImagem.split("/");
                    nomedafoto = tmps[tmps.length - 1];
                }

            } else if (resultCode == Activity.RESULT_OK) {
                if (requestCode == Util.Request.REQUEST_IMPORTAR_PESSOA_NO_CONTACTO) {
                    uriContact = data.getData();
                    nome = Util.Contacto.getNomeDoContacto(this, uriContact);
                    telefone = Util.Contacto.getNumeroContacto(this, uriContact);
                    email = Util.Contacto.getEmailDoContacto(this, uriContact);
                    showDialog();
                }
            }
        }

        if (!TextUtils.isEmpty(this.urlDaImagem)) {
            setFotoToImageView(this.urlDaImagem);
        }
    }


    private void setFotoToImageView(String path) {

        imageLoader.displayImage("file://" + path, ivFoto);
    }

    private void showDialog() {

        boolean wrapInScrollView = true;
        View view = getLayoutInflater().inflate(R.layout.activity_grupo_novo_dialog_selected_contact, null);
        etNome = (MaterialEditText) view.findViewById(R.id.id_et_nome_nova_contacto);
        etTelefone = (MaterialEditText) view.findViewById(R.id.id_et_numero_contacto_nova_contacto);
        etEmail = (MaterialEditText) view.findViewById(R.id.id_et_email_nova_contacto);
        ivFoto = (ImageView) view.findViewById(R.id.id_iv_imagem_nova_contacto);

        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.Utilities.showToast(ActivityGrupoNovo.this, "Clicked");
                getFotoIntent();
            }
        });

        etNome.setText(nome);
        etTelefone.setText(telefone != null ? telefone : "");
        etEmail.setText(email != null ? email : "");
        dialog = createMaterialDialog(wrapInScrollView, view);
        dialog.show();
    }

    private MaterialDialog createMaterialDialog(boolean wrapInScrollView, View view) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title("Pessoa")
                .customView(view, wrapInScrollView)
                .positiveText("ADICIONAR")
                .negativeText("CANCELAR")
                .callback(materialDialogButtonListener).build();
        return materialDialog;
    }

    private MaterialDialog.ButtonCallback materialDialogButtonListener = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            dialog.dismiss();

            nome = etNome.getText().toString().trim();
            telefone = etTelefone.getText().toString().trim();
            email = etEmail.getText().toString().trim();

            if (!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(telefone) && Util.Utilities.isValidEmail(email)) {
                Pessoa pessoa = db.savePessoa(nome, email, telefone, urlDaImagem);
                if (pessoa != null) {
                    String s = pessoa.nome + " " + pessoa.telefone + " " + pessoa.email;
                    //Util.Utilities.showLog("PESSOA_RETURN", s);
                    actualizarLista(pessoa);
                }
            } else {
                Util.Utilities.showToast(ActivityGrupoNovo.this, "Os dados da pessoa sao invalidos");
            }
        }

        @Override
        public void onNegative(MaterialDialog dialog) {
            super.onNegative(dialog);
            dialog.dismiss();
        }
    };

    private void getFotoIntent() {

        AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(this);
        dialog.setTitle("Select option");
        dialog.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // http://developer.android.com/training/camera/photobasics.html
                takePhoto();
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("GALERRY", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Intent intent = new Intent(Util.Request.ACTION_PICK);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, Util.Request.REQUEST_IMAGE_GALERY_CODE);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = Util.Image.createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri uri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        uri);
                Util.Utilities.showLog("URI_FILE", uri.getPath());
                urlDaImagem = uri.getPath();
                startActivityForResult(intent, Util.Request.REQUEST_IMAGE_CAPTURE_CODE);
            }
        }
    }

    public static class SelectPessoaAdapter extends BaseAdapter implements View.OnClickListener {

        private Context mContext;
        private List<Pessoa> mItems = null;
        private IPessoaGrupoItemClickListener listener;
        private DatabaseHelper db;
        private ImageLoader imageLoader;

        public SelectPessoaAdapter(Context context, ImageLoader imageLoader) {
            db = DatabaseHelper.newInstance();
            this.mContext = context;
            mItems = db.getPessoas();
            this.imageLoader = imageLoader;
        }

        public void setListener(IPessoaGrupoItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Pessoa getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.grupo_select_pessoa_list_item, parent, false);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Pessoa pessoa = getItem(position);

            holder.tvNome.setText(pessoa.nome);
            holder.tvNome.setTag(pessoa.getId());
            setFotoToImageView(pessoa.urlDaFoto, holder.imageView);
            holder.tvNome.setOnClickListener(this);
            return convertView;
        }

        private void setFotoToImageView(String path, ImageView imageView) {
            imageLoader.displayImage("file://" + path, imageView);
        }

        static class ViewHolder {

            TextView tvNome;
            ImageView imageView;

            public ViewHolder(View view) {
                tvNome = (TextView) view.findViewById(R.id.id_nome_pessoa_grupo_select);
                imageView = (ImageView) view.findViewById(R.id.avatar);
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                long id = (long) v.getTag();
                listener.onPessoaGrupoItemClick(id);
            }

        }

    }
}
