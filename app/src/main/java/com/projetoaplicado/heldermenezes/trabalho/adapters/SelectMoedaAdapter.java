package com.projetoaplicado.heldermenezes.trabalho.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.projetoaplicado.heldermenezes.trabalho.R;

import java.util.Map;

/**
 * Created by heldermenezes on 14/06/2015.
 */
public class SelectMoedaAdapter extends ArrayAdapter<String> implements View.OnClickListener {
    private final Context mContext;
    private final Map<Integer,String> mItems;
    private OnMoedaItemClickListener listener;

    public SelectMoedaAdapter(Context context, Map<Integer,String> items) {
        super(context, R.layout.moeda_item_view);
        this.mContext = context;
        this.mItems = items;
    }

    public void setListener(OnMoedaItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public String getItem(int position) {
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
            convertView = View.inflate(mContext, R.layout.moeda_item_view, null);
        TextView tv = ((TextView) convertView.findViewById(R.id.id_tv_grupo_select_moeda));
        String s = mItems.get(position);
        tv.setText(s);
        tv.setTag(position);
        tv.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            int position = (int) v.getTag();
            listener.onMoedaItemClick(position);
        }
    }

    public interface OnMoedaItemClickListener {
        public void onMoedaItemClick(int position);
    }
}
