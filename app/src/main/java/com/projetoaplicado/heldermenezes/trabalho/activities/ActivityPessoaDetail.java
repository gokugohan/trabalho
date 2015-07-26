package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentDespesa;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentGrupo;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IGrupoItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ActivityPessoaDetail extends AppCompatActivity implements IGrupoItemClickListener {

    private Toolbar mToolbar;
    private DatabaseHelper db;
    private Pessoa pessoa;
    private TextView tvNome, tvEmail, tvTelefone;
    private ImageView imageView;
    private ImageLoader imageLoader;
    private RecyclerView recyclerView;
    private FragmentGrupo.GrupoRecyclerViewAdapter adapter;
    private List<Grupo> grupos;
    private String email, telefone;
    private long id;
    private TextView tvGrupos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pessoa_detail);
        // Check if the user is not anonymous user
        // if is the case redirect to the login view
        if(!ParseUtils.isRegisteredUser()){
            Util.Utilities.startActivity(this,ActivityLogin.class,null);
        }
        init();

        if (getIntent().hasExtra("id")) {
            id = getIntent().getLongExtra("id", -1);

            db = DatabaseHelper.newInstance();
            pessoa = db.getPessoa(id);
            if (pessoa != null) {
                imageLoader = Util.Image.initImageLoader(this, imageLoader);
                //Util.Utilities.showToast(this,pessoa.nome);
                email = pessoa.email;
                telefone = pessoa.telefone;
                this.tvNome.setText(pessoa.nome);
                this.tvEmail.setText(email);
                this.tvTelefone.setText(telefone);
                setFotoToImageView(pessoa.urlDaFoto, this.imageView);
                grupos = db.getGrupoQuePessoaPertence(id);
                setupRecyclerView(grupos);
            }
        }
    }

    private void init() {
        initToolBar();
        this.imageView = (ImageView) findViewById(R.id.id_imageView_pessoa_detail_foto);
        this.tvNome = (TextView) findViewById(R.id.id_tv_pessoa_detail_nome);
        this.tvEmail = (TextView) findViewById(R.id.id_tv_pessoa_detail_email);
        this.tvTelefone = (TextView) findViewById(R.id.id_tv_pessoa_detail_telefone);
        this.tvGrupos = (TextView) findViewById(R.id.id_tv_pessoa_detail_grupos);
        this.recyclerView = (RecyclerView) findViewById(R.id.id_recycler_pessoa_detail_grupos);

    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        this.setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setFotoToImageView(String path, ImageView imageView) {
        if (imageView != null) {
            imageLoader.displayImage("file://" + path, imageView);
        }
    }

    private void setupRecyclerView(List<Grupo> grupos) {

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int param = bundle.getInt(Util.PARAMS.ARGS);
            if (param == Util.PARAMS.CONSULTAR) {
                List<Despesa> despesas = getDespesas();
                tvGrupos.setText(getResources().getString(R.string.pertenceu_a_despesa));

                FragmentDespesa.DespesaRecyclerViewAdapter adapter = new FragmentDespesa.DespesaRecyclerViewAdapter(this,despesas,imageLoader);
                recyclerView.setAdapter(adapter);
                return;
            }
        }

        adapter = new FragmentGrupo.GrupoRecyclerViewAdapter(this, R.layout.item_pessoa_detail, grupos);
        adapter.setOnPessoaItemClickListener(this);

        recyclerView.setAdapter(adapter);

    }

    private List<Despesa> getDespesas() {
        List<List<Despesa>> despesaDaPessoa = db.getDespesaDaPessoa(id);
        List<Despesa> tempDespesa;
        List<Despesa> despesas = new ArrayList<>();
        if(despesaDaPessoa !=null){
            for(int i=0;i<despesaDaPessoa.size();i++){
                tempDespesa = despesaDaPessoa.get(i);
                for(int j=0;j<tempDespesa.size();j++){
                    despesas.add(tempDespesa.get(j));
                }
            }
        }
        return despesas;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_pessoa_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.id_menu_enviar_email:
                enviarEmail();
                break;
            case R.id.id_menu_telefonar:
                fazerChamada();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void enviarEmail() {
        if (Util.Utilities.isValidEmail(email)) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:"));
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.conta));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.conta_da_despesa_do_grupo));

            try {
                startActivity(Intent.createChooser(intent, getString(R.string.enviar_email)));

            } catch (android.content.ActivityNotFoundException ex) {
                Util.Utilities.showToast(this, getString(R.string.cliente_nao_instalado));
            }
        } else {
            Util.Utilities.showToast(this, getString(R.string.email_invalido));
        }
    }

    private void fazerChamada() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + telefone));
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Util.Utilities.showToast(this, "Error");
        }
    }

    @Override
    public void OnGrupoItemClick(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Util.Utilities.startActivity(this, ActivityGrupoDetail.class, bundle);
    }
}
