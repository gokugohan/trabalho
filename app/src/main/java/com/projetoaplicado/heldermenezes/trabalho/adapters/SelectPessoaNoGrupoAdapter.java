package com.projetoaplicado.heldermenezes.trabalho.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.List;

/**
 * Created by heldermenezes on 14/06/2015.
 */
public class SelectPessoaNoGrupoAdapter extends ArrayAdapter<Pessoa> implements View.OnClickListener {
    private Context context;
    private int resource;
    private List<Pessoa> pessoas;
    private OnPessoaNoGrupoItemClickListener listener;
    private ImageLoader imageLoader;

    public SelectPessoaNoGrupoAdapter(Context context,List<Pessoa> pessoas) {
        super(context, R.layout.fragment_pessoa_select_grupo, pessoas);
        this.context = context;
        this.resource = R.layout.fragment_pessoa_select_grupo;
        this.pessoas = pessoas;
        imageLoader = Util.Image.initImageLoader(context, imageLoader);
    }

    public void setListener(OnPessoaNoGrupoItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return pessoas.size();
    }

    @Override
    public Pessoa getItem(int position) {
        return pessoas.get(position);
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
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pessoa pessoa = getItem(position);

        holder.tvNome.setText(pessoa.nome);
        setFotoToImageView(pessoa.urlDaFoto, holder.imageView);
        holder.linearLayout.setTag(pessoa.getId());
        holder.linearLayout.setOnClickListener(this);
        return convertView;
    }
    private void setFotoToImageView(String path, ImageView imageView) {
        if (imageView != null) {
            imageLoader.displayImage("file://" + path, imageView);
        }
    }

    static class ViewHolder {

        TextView tvNome;
        LinearLayout linearLayout;
        ImageView imageView;

        public ViewHolder(View view) {
            tvNome = (TextView) view.findViewById(R.id.title);
            imageView = (ImageView) view.findViewById(R.id.id_pessoa_list_item_foto);
            linearLayout = (LinearLayout) view.findViewById(R.id.id_pessoa_list_item_linearLayout);
        }
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            long id = (long) v.getTag();
            listener.onPessoaNoGrupoItemClick(id);
        }
    }

    public interface OnPessoaNoGrupoItemClickListener {
        void onPessoaNoGrupoItemClick(long id);
    }
}
