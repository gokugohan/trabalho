package com.projetoaplicado.heldermenezes.trabalho.fragmentos;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.activities.ActivitySimularDetail;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IGrupoItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Conta;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.DividerItemDecoration;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSimular extends Fragment implements IGrupoItemClickListener {
    private RecyclerView recyclerView;
    private ContaRecyclerViewAdapter adapter;
    private DatabaseHelper db;
    private List<Conta> contas;

    public FragmentSimular() {
        db = DatabaseHelper.newInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setRetainInstance(true);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_simular_contas, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.id_recycler_simular_contas);

        setupRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_simular_detail, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setupRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if (id == R.id.id_menu_action_search) {
            Util.Utilities.showToast(getActivity(), "CLICKED");
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        contas = db.getContas();

        for (Conta conta : contas) {
            Util.Utilities.showLog("Conta", conta.grupo.nome + " : " + conta.valor);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        adapter = new ContaRecyclerViewAdapter(getActivity(), contas);


        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setOnGrupoItemClickListener(this);

        recyclerView.setAdapter(adapter);

    }


    @Override
    public void OnGrupoItemClick(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putInt(Util.PARAMS.ARGS, Util.PARAMS.SIMULAR);
        Util.Utilities.startActivity(getActivity(), ActivitySimularDetail.class, bundle);
        getActivity().finish();
    }



    /* ADAPTER */
    public static class ContaRecyclerViewAdapter
            extends RecyclerView.Adapter<ContaRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private Context context;
        private DatabaseHelper db = DatabaseHelper.newInstance();
        private IGrupoItemClickListener listener;

        private List<Conta> contas;

        public void setOnGrupoItemClickListener(IGrupoItemClickListener listener) {
            this.listener = listener;
        }


        public Conta getValueAt(int position) {
            return contas.get(position);
        }

        public ContaRecyclerViewAdapter(Context context, List<Conta> contas) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            //mBackground = mTypedValue.resourceId;
            this.contas = contas;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_acerto_de_contas, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final Conta conta = getValueAt(position);
            holder.tvContaGrupo.setText(conta.grupo.nome);
            String valor = String.format("%.2f %s",conta.valor,Util.Moeda.Simbolos.getSimbolos().get(conta.moeda));
            holder.tvContaValor.setText(valor);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnGrupoItemClick(conta.grupo.getId());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return contas.size();
        }

        public void animateTo(List<Conta> lContas) {
            applyAndAnimateRemovals(lContas);
            applyAndAnimateAdditions(lContas);
            applyAndAnimateMovedItems(lContas);
        }

        private void applyAndAnimateRemovals(List<Conta> newContas) {
            for (int i = contas.size() - 1; i >= 0; i--) {
                final Conta conta = contas.get(i);
                if (!newContas.contains(conta)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<Conta> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final Conta model = newModels.get(i);
                if (!contas.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Conta> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final Conta model = newModels.get(toPosition);
                final int fromPosition = contas.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        public Conta removeItem(int position) {
            final Conta model = contas.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, Conta model) {
            contas.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final Conta model = contas.remove(fromPosition);
            contas.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {

            public String mBoundString;
            public final View mView;
            public final TextView tvContaGrupo, tvContaValor;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvContaGrupo = (TextView) view.findViewById(R.id.id_tv_acerto_de_contas_grupo);
                tvContaValor = (TextView) view.findViewById(R.id.id_tv_acerto_de_contas_valor_total);

            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                return super.toString() + " XXX ";
            }
        }
    }

}
