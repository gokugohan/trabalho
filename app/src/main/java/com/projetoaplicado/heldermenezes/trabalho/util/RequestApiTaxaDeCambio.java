package com.projetoaplicado.heldermenezes.trabalho.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by heldermenezes on 12/06/2015.
 */
public class RequestApiTaxaDeCambio {

    private static final String TAG = "TAXA_DE_CAMBIO";
    private RequestQueue mRequestQueue;
    private static Context context;
    private static RequestApiTaxaDeCambio mTaxaDeCambio;

    private String url = "http://api.fixer.io/latest?";

    private RequestApiTaxaDeCambio(Context context) {
        this.context = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestApiTaxaDeCambio getInstance(Context context) {
        if (mTaxaDeCambio == null) {
            mTaxaDeCambio = new RequestApiTaxaDeCambio(context);
        }
        return mTaxaDeCambio;
    }

    public RequestQueue getRequestQueue() {
        if (mTaxaDeCambio == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }



}
