package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.fragmentos.FragmentDespesa;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IPessoaItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.List;

public class ActivityGrupoDetail extends AppCompatActivity implements IPessoaItemClickListener {
    private TextView tvNome, tvMoeda,tvPessoas;
    private RecyclerView mrecyclerView;
    private long mIdGrupo = -1;
    private Toolbar mToolbar;
    private DatabaseHelper db;
    private GrupoPessoaDetailAdapter adapter;
    private Bundle bundle;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo_detail);

        // Check if the user is not anonymous user
        // if is the case redirect to the login view
        if(!ParseUtils.isRegisteredUser()){
            Util.Utilities.startActivity(this,ActivityLogin.class,null);
        }

        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        tvNome = (TextView) findViewById(R.id.id_tv_grupo_detail_nome);
        tvMoeda = (TextView) findViewById(R.id.id_tv_grupo_detail_moeda);
        tvPessoas = (TextView)findViewById(R.id.id_tv_grupo_detail_pessoas);

        mrecyclerView = (RecyclerView) findViewById(R.id.id_lista_grupo_detail_pessoas);

        db = DatabaseHelper.newInstance();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            //mToolbar.setTitle("Alterar despesa");
            mIdGrupo = bundle.getLong("id", -1);

            Grupo grupo = db.getGrupo(mIdGrupo);
            if (grupo != null) {
                tvNome.setText(grupo.nome);
                int moeda = grupo.moeda;
                String mMoeda = Util.Moeda.getStringMoeda().get(grupo.moeda);
                tvMoeda.setText(mMoeda);

                setupRecyclerView();

            }
        } else {
            finish();
        }
    }

    private void setupRecyclerView() {


        if (bundle != null) {
            int param = bundle.getInt(Util.PARAMS.ARGS);
            if (param == Util.PARAMS.CONSULTAR) {
                tvPessoas.setText(getString(R.string.despesa_do_grupo));
                imageLoader = Util.Image.initImageLoader(this, imageLoader);
                List<Despesa> despesas = db.getDespesasDoGrupo(mIdGrupo);
                mrecyclerView.setLayoutManager(new GridLayoutManager(mrecyclerView.getContext(), 3));
                FragmentDespesa.DespesaRecyclerViewAdapter adapter = new FragmentDespesa.DespesaRecyclerViewAdapter(this, despesas, imageLoader);
                mrecyclerView.setAdapter(adapter);
                return;
            }
        }

        mrecyclerView.setLayoutManager(new LinearLayoutManager(mrecyclerView.getContext()));

        List<Pessoa> pessoas = db.getPessoasNoGrupo(mIdGrupo);
        adapter = new GrupoPessoaDetailAdapter(this, pessoas);
        adapter.setOnPessoaItemClickListener(this);
        mrecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_grupo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPessoaItemClicked(long id) {
        Pessoa pessoa = db.getPessoa(id);
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Util.Utilities.startActivity(this, ActivityPessoaDetail.class, bundle);
    }


    public static class GrupoPessoaDetailAdapter extends RecyclerView.Adapter<GrupoPessoaDetailAdapter.Holder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Pessoa> pessoas;
        private Context context;
        private ImageLoader imageLoader;
        private IPessoaItemClickListener listener;

        public void setOnPessoaItemClickListener(IPessoaItemClickListener listener) {
            this.listener = listener;
        }


        public Pessoa getValueAt(int position) {
            return pessoas.get(position);
        }

        public GrupoPessoaDetailAdapter(Context context, List<Pessoa> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            pessoas = items;
            this.context = context;
            imageLoader = Util.Image.initImageLoader(context, imageLoader);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_grupo_detail_pessoa, parent, false);
            view.setBackgroundResource(mBackground);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            final Pessoa pessoa = getValueAt(position);
            holder.mBoundString = pessoa.nome;
            holder.tvNome.setText(pessoa.nome);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Snackbar.make(v, pessoa.nome + " " + pessoa.email, Snackbar.LENGTH_LONG).show();
                    if (listener != null) {
                        listener.onPessoaItemClicked(pessoa.getId());
                    }
                }
            });

            if (TextUtils.isEmpty(pessoa.urlDaFoto)) {
                Glide.with(holder.imageView.getContext())
                        .load(R.drawable.no_pessoa)
                        .fitCenter()
                        .into(holder.imageView);
            } else {
                setFotoToImageView(pessoa.urlDaFoto, holder.imageView);
            }

        }

        @Override
        public int getItemCount() {
            return pessoas.size();
        }

        public void removePessoa(Pessoa pessoa) {
            int index = pessoas.indexOf(pessoa);
            notifyItemRemoved(index);
        }


        private void setFotoToImageView(String path, ImageView imageView) {
            if (imageView != null) {
                imageLoader.displayImage("file://" + path, imageView);
            }
        }

        public static class Holder extends RecyclerView.ViewHolder {
            public String mBoundString;
            public final View mView;
            public final TextView tvNome;
            public final ImageView imageView;

            public Holder(View view) {
                super(view);
                mView = view;
                tvNome = (TextView) view
                        .findViewById(R.id.id_tv_item_grupo_detail_nome);
                imageView = (ImageView) view.findViewById(R.id.id_imageView_item_grupo_detail_foto);
            }
        }
    }
}
