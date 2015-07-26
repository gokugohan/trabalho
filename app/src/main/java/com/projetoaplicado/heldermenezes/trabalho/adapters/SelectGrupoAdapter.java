package com.projetoaplicado.heldermenezes.trabalho.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.model.Grupo;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.List;

/**
 * Created by heldermenezes on 14/06/2015.
 */
public class SelectGrupoAdapter extends ArrayAdapter<Grupo> implements View.OnClickListener{
    private final Context mContext;
    private final List<Grupo> mItems;
    private OnGrupoItemClickListener listener;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoader;

    public SelectGrupoAdapter(Context context, List<Grupo> items) {
        super(context, R.layout.dialog_select_grupo_despesa, items);
        this.mContext = context;
        this.mItems = items;
        imageLoader = Util.Image.initImageLoader(context, imageLoader);
    }

    public void setListener(OnGrupoItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Grupo getItem(int position) {
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
        if (convertView == null)
            convertView = View.inflate(mContext, R.layout.dialog_select_grupo_despesa, null);
        TextView tv = ((TextView) convertView.findViewById(R.id.id_tv_despesa_select_grupo_dialog));
        Grupo grupo = mItems.get(position);
        tv.setText(grupo.nome);
        tv.setTag(grupo.getId());
        tv.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            long id = (long) v.getTag();
            listener.onGrupoItemClick(id);
        }
    }

    public interface OnGrupoItemClickListener {
        void onGrupoItemClick(long id);
    }
}
