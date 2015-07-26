package com.projetoaplicado.heldermenezes.trabalho;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.projetoaplicado.heldermenezes.trabalho.util.ParseUtils;


/**
 * Created by helder on 07-06-2015.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        ParseUtils.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }




}
