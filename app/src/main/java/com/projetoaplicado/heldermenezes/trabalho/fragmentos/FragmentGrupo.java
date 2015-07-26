package com.projetoaplicado.heldermenezes.trabalho.fragmentos;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.activities.ActivityGrupoDetail;
import com.projetoaplicado.heldermenezes.trabalho.activities.ActivityGrupoNovo;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IGrupoItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGrupo extends Fragment implements View.OnClickListener, IGrupoItemClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private GrupoRecyclerViewAdapter adapter;
    private DatabaseHelper db;
    private int param_passado = 0;

    public FragmentGrupo() {
        db = DatabaseHelper.newInstance();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            param_passado = args.getInt(Util.PARAMS.ARGS);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragmento_grupo, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_main_fragmento_grupo);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.id_fab_fragment_grupo_add);
        floatingActionButton.setOnClickListener(this);
        switch (param_passado) {
            case Util.PARAMS.CONSULTAR:
                floatingActionButton.setVisibility(View.GONE);
                break;
            case Util.PARAMS.ALTERAR:
                floatingActionButton.setVisibility(View.GONE);
                break;
            default:
                floatingActionButton.setVisibility(View.VISIBLE);
                break;
        }
        setupRecyclerView(recyclerView);
        return view;
    }


    private void setupRecyclerView(RecyclerView recyclerView) {

        //recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3));
        List<Grupo> grupos = db.getGrupos();
        adapter = new GrupoRecyclerViewAdapter(getActivity(),R.layout.list_item,grupos);
        adapter.setOnPessoaItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Util.Utilities.startActivity(getActivity(), ActivityGrupoNovo.class, null);
    }

    @Override
    public void OnGrupoItemClick(long id) {

        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        switch (param_passado) {
            case Util.PARAMS.CONSULTAR:
                bundle.putInt(Util.PARAMS.ARGS,Util.PARAMS.CONSULTAR);
                Util.Utilities.startActivity(getActivity(), ActivityGrupoDetail.class, bundle);
                List<Despesa> despesas = db.getDespesasDoGrupo(id);
                for(Despesa despesa:despesas){
                    Log.e("DESPESA",despesa.tipo);
                }
                break;
            case Util.PARAMS.ALTERAR:
                Util.Utilities.startActivity(getActivity(), ActivityGrupoNovo.class, bundle);
                break;
            default:
                Util.Utilities.startActivity(getActivity(), ActivityGrupoDetail.class, bundle);
                break;
        }
    }

    public void showOptionEditDetail(final long id){

        final Grupo grupo = db.getGrupo(id);
        final Bundle bundle = new Bundle();
        bundle.putLong("id", id);

        AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(getActivity());
        dialog.setTitle("Option");
        dialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Util.Utilities.startActivity(getActivity(), ActivityGrupoNovo.class, bundle);

            }
        });
        dialog.setPositiveButton("Detail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Util.Utilities.startActivity(getActivity(),ActivityGrupoNovo.class,bundle);
                showDetailGrupo(grupo);
            }
        });
        dialog.show();
    }

    private void showDetailGrupo(Grupo grupo) {

        boolean wrapInScrollView = true;
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_grupo_detail, null);
        TextView tvNome = (TextView) view.findViewById(R.id.id_tv_grupo_dialog_nome);
        TextView tvMoeda = (TextView) view.findViewById(R.id.id_tv_grupo_dialog_moeda);
        TextView tvPessoas = (TextView)view.findViewById(R.id.id_tv_grupo_dialog_pessoas);

        tvNome.setText(grupo.nome);
        tvMoeda.setText(Util.Moeda.getStringMoeda().get(grupo.moeda));
        List<Pessoa> pessoas = db.getPessoasNoGrupo(grupo);
        StringBuilder sb = new StringBuilder("");
        int contador=1;
        for (Pessoa p:pessoas){
            sb.append(contador<10?"0"+contador:contador).append(". ").append(p.nome).append("\n");
            contador++;
        }

        tvPessoas.setText(sb.toString());

        MaterialDialog dialog = createMaterialDialog(wrapInScrollView, view);
        dialog.show();
    }

    private MaterialDialog createMaterialDialog(boolean wrapInScrollView, View view) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .title("Grupo")
                .customView(view, wrapInScrollView)
                .positiveText("EXIT")
                .build();
        return materialDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView(recyclerView);
    }

    /* ADAPTER */
    public static class GrupoRecyclerViewAdapter
            extends RecyclerView.Adapter<GrupoRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Grupo> grupos;
        private Context context;
        private int resource;
        private IGrupoItemClickListener listener;

        public void setOnPessoaItemClickListener(IGrupoItemClickListener listener) {
            this.listener = listener;
        }


        public Grupo getValueAt(int position) {
            return grupos.get(position);
        }

        public GrupoRecyclerViewAdapter(Context context,int resource, List<Grupo> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            //mBackground = mTypedValue.resourceId;
            grupos = items;
            this.resource = resource;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(resource, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            //holder.mBoundString = grupos.get(position).nome;
            holder.mTextView.setText(grupos.get(position).nome);
            Glide.with(holder.mImageView.getContext())
                    .load(R.drawable.no_grupo)
                    .fitCenter()
                    .into(holder.mImageView);
            final Grupo grupo = grupos.get(position);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Snackbar.make(v, pessoa.nome + " " + pessoa.email, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    if (listener != null) {
                        listener.OnGrupoItemClick(grupo.getId());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return grupos.size();
        }

        public void deleteGrupo(Grupo grupo){
            if(grupo!=null){
                int position = grupos.indexOf(grupo);
                notifyItemRemoved(position);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final TextView mTextView;
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mImageView = (ImageView)view.findViewById(R.id.avatar);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }
    }
}
