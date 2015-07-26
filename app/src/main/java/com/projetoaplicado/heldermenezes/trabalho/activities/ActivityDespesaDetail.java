package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IPessoaItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.List;

public class ActivityDespesaDetail extends AppCompatActivity implements IPessoaItemClickListener {

    private Toolbar mToolbar;
    private DatabaseHelper db;
    private TextView tvTipo, tvDescricao, tvImportancia, tvGrupo, tvPessoa;
    private ImageView imageView, pessoaImageView;
    private Grupo grupo;
    private Pessoa pessoa;
    private ImageLoader imageLoader;
    private RecyclerView recyclerView;
    private PessoaRecyclerViewAdapter adapter;
    private LinearLayout grupoView, pessoaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa_detail);

        // Check if the user is not anonymous user
        // if is the case redirect to the login view
        if (!ParseUtils.isRegisteredUser()) {
            Util.Utilities.startActivity(this, ActivityLogin.class, null);
        }

        init();

        if (getIntent().hasExtra("id")) {
            long id = getIntent().getLongExtra("id", -1);
            //Util.Utilities.showToast(this,"ID DA DESPESA: " + id);
            Despesa despesa = db.getDespesa(id);
            if (despesa != null) {
                grupo = despesa.grupo;
                pessoa = despesa.pessoa;

                setFotoToImageView(despesa.urlDaFoto, imageView);
                tvTipo.setText(despesa.tipo);
                tvDescricao.setText(despesa.descricao);
                String valor = String.format("%.2f %s",despesa.importancia,Util.Moeda.getStringMoeda().get(despesa.moeda));
                tvImportancia.setText(valor);

                //List<Pessoa> pessoas = db.getPessoasNoGrupo(grupo.getId());

                //setupRecyclerView(pessoas);
                tvGrupo.setText(grupo.nome);

                grupoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("id", grupo.getId());
                        Util.Utilities.startActivity(ActivityDespesaDetail.this, ActivityGrupoDetail.class, bundle);
                    }
                });


                tvPessoa.setText(pessoa.nome);
                setFotoToImageView(pessoa.urlDaFoto, pessoaImageView);
                pessoaView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("id", pessoa.getId());
                        Util.Utilities.startActivity(ActivityDespesaDetail.this, ActivityPessoaDetail.class, bundle);
                    }
                });

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_despesa_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_delete) {
            Util.Utilities.showToast(this,"Delete action clicked");
            return true;
        }*/
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initToolBar();
        imageLoader = Util.Image.initImageLoader(this, imageLoader);
        db = DatabaseHelper.newInstance();

        tvTipo = getTextView(R.id.id_tv_detail_despesa_tipo);
        tvDescricao = getTextView(R.id.id_tv_detail_despesa_descricao);
        tvImportancia = getTextView(R.id.id_tv_detail_despesa_importancia);
        tvGrupo = getTextView(R.id.id_tv_detail_despesa_grupo);
        imageView = (ImageView) findViewById(R.id.id_iv_detail_despesa_img);
        grupoView = (LinearLayout) findViewById(R.id.id_linearLayout_despesa_detail_grupo);
        tvPessoa = getTextView(R.id.id_tv_detail_despesa_pessoa);
        pessoaView = (LinearLayout) findViewById(R.id.id_linearLayout_despesa_detail_pessoa);
        pessoaImageView = (ImageView) findViewById(R.id.id_iv_detail_despesa_pessoa_img);
        //recyclerView = (RecyclerView)findViewById(R.id.id_recyclerview_despesa_detail_pessoas);


    }

    private void setupRecyclerView(List<Pessoa> pessoas) {

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new PessoaRecyclerViewAdapter(this, pessoas, imageLoader);
        adapter.setOnPessoaItemClickListener(this);

        recyclerView.setAdapter(adapter);

    }

    public TextView getTextView(int id) {
        return (TextView) this.findViewById(id);
    }

    private void initToolBar() {
        mToolbar = (Toolbar) this.findViewById(R.id.id_toolbar);
        this.setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setFotoToImageView(String path, ImageView imageView) {
        if (imageView != null) {
            imageLoader.displayImage("file://" + path, imageView);
        }
    }

    @Override
    public void onPessoaItemClicked(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Util.Utilities.startActivity(this, ActivityPessoaDetail.class, bundle);
    }


    /* ADAPTER */
    private class PessoaRecyclerViewAdapter
            extends RecyclerView.Adapter<PessoaRecyclerViewAdapter.ViewHolder> {

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

        public PessoaRecyclerViewAdapter(Context context, List<Pessoa> items, ImageLoader imageLoader) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            pessoas = items;
            this.context = context;
            this.imageLoader = imageLoader;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_despesa_detail_pessoa, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mBoundString = pessoas.get(position).nome;
            holder.mTextView.setText(pessoas.get(position).nome);

            final Pessoa pessoa = pessoas.get(position);

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
            return pessoas.size();
        }


        private void setFotoToImageView(String path, ImageView imageView) {
            if (imageView != null) {
                imageLoader.displayImage("file://" + path, imageView);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.id_despesa_detail_pessoa_foto);
                mTextView = (TextView) view.findViewById(R.id.id_despesa_detail_pessoa_nome);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }
    }
}
