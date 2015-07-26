package com.projetoaplicado.heldermenezes.trabalho.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;

import java.util.List;

/**
 * Created by heldermenezes on 14/06/2015.
 */
public class GrupoPessoaItemAdapter extends ArrayAdapter<Pessoa> {
    private Context context;
    private List<Pessoa> items;
    private int resource;
    private ImageLoader imageLoader;
    public GrupoPessoaItemAdapter(Context context, List<Pessoa> items, ImageLoader imageLoader) {
        super(context, R.layout.adapter_grupo_pessoa_item, items);
        this.context = context;
        this.items = items;
        this.resource = R.layout.adapter_grupo_pessoa_item;
        this.imageLoader = imageLoader;
    }


    @Override
    public Pessoa getItem(int position) {
        return this.items.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Pessoa pessoa = this.getItem(position);
        viewHolder.tvNome.setText(pessoa.nome);
        setFotoToImageView(pessoa.urlDaFoto, viewHolder.imageView);
        return convertView;
    }

    private void setFotoToImageView(String path, ImageView imageView) {
        imageLoader.displayImage("file://" + path, imageView);
    }
    static class ViewHolder {
        TextView tvNome;
        ImageView imageView;
        public ViewHolder(View view) {
            tvNome = (TextView) view
                    .findViewById(R.id.id_grupo_detail_nome);
            imageView = (ImageView)view.findViewById(R.id.avatar);
        }
    }
}
