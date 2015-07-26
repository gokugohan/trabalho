package com.projetoaplicado.heldermenezes.trabalho.fragmentos;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.activities.ActivityDespesaDetail;
import com.projetoaplicado.heldermenezes.trabalho.activities.ActivityDespesaNovo;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IDespesaItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;


import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDespesa extends Fragment implements OnClickListener, IDespesaItemClickListener {


    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private DatabaseHelper db;
    private List<Despesa> despesas;
    private DespesaRecyclerViewAdapter adapter;
    private ImageLoader imageLoader;
    private int param_passado = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            param_passado = args.getInt(Util.PARAMS.ARGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragmento_despesa, container, false);

        db = DatabaseHelper.newInstance();
        imageLoader = Util.Image.initImageLoader(getActivity(), imageLoader);
        recyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_despesa);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.id_fab_fragment_despesa_add);
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

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        despesas = db.getDespesas();
        adapter = new DespesaRecyclerViewAdapter(getActivity(), despesas, imageLoader);
        adapter.setOnPessoaItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        Snackbar.make(v, "Despesa Clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Util.Utilities.startActivity(getActivity(), ActivityDespesaNovo.class, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView(recyclerView);
    }

    @Override
    public void despesaItemClick(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);

        switch (param_passado) {
            case Util.PARAMS.CONSULTAR:
                Util.Utilities.startActivity(getActivity(), ActivityDespesaDetail.class, bundle);
                break;
            case Util.PARAMS.ALTERAR:
                Util.Utilities.startActivity(getActivity(), ActivityDespesaNovo.class, bundle);
                break;
            default:
                break;
        }
    }


    @Override
    public void despesaItemLongClick(long id, View v) {
        final Despesa despesa = db.getDespesa(id);
        //Snackbar.make(v, despesa.tipo.toUpperCase(), Snackbar.LENGTH_LONG).show();
        showConfirmDialog(despesa);
    }
    private void showConfirmDialog(final Despesa despesa){

        AlertDialogWrapper.Builder alertDialog = new AlertDialogWrapper.Builder(getActivity())
                .setTitle("Eliminar")
                .setMessage("Pretende eliminar a despesa selecionada?")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        db.deleteDespesa(despesa);
                        setupRecyclerView(recyclerView);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    private void alterarDespesa(long id){
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Util.Utilities.startActivity(getActivity(), ActivityDespesaNovo.class, bundle);
    }

    public static class DespesaRecyclerViewAdapter
            extends RecyclerView.Adapter<DespesaRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Despesa> mValues;

        private IDespesaItemClickListener listener;
        private ImageLoader imageLoader;

        public DespesaRecyclerViewAdapter(Context context, List<Despesa> items, ImageLoader imageLoader) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
            this.imageLoader = imageLoader;
        }

        public void setOnPessoaItemClickListener(IDespesaItemClickListener listener) {
            this.listener = listener;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public Despesa getValueAt(int position) {
            return mValues.get(position);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Despesa despesa = mValues.get(position);
            holder.mBoundString = despesa.tipo;
            holder.mTextView.setText(despesa.tipo);

            final String st = despesa.descricao;
            holder.mView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Snackbar.make(v, st, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
    }

}
