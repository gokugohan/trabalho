package com.projetoaplicado.heldermenezes.trabalho.fragmentos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
public class FragmentFecharContas extends Fragment implements IGrupoItemClickListener {

    private RecyclerView recyclerView;
    private FragmentSimular.ContaRecyclerViewAdapter adapter;
    private DatabaseHelper db;

    public FragmentFecharContas() {
        db = DatabaseHelper.newInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_fechar_contas, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.id_recycler_fechar_contas);

        setupRecyclerView(recyclerView);
        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        List<Conta> contas = db.getContas();

        for (Conta conta : contas) {
            Util.Utilities.showLog("Conta", conta.grupo.nome + " : " + conta.valor);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        adapter = new FragmentSimular.ContaRecyclerViewAdapter(getActivity(), contas);

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
        bundle.putInt(Util.PARAMS.ARGS,Util.PARAMS.FECHAR);
        Util.Utilities.startActivity(getActivity(), ActivitySimularDetail.class, bundle);
        getActivity().finish();
    }

}

