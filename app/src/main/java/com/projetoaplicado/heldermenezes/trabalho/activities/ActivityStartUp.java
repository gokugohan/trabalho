package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;


public class ActivityStartUp extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_start_up);

        init();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.Utilities.startActivity(ActivityStartUp.this,MainActivity.class,null);
                finish();
            }
        }, 3000);

    }

    private void init() {

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.id_rl_main_startup);

        ValueAnimator colorAnim = ObjectAnimator.ofInt(relativeLayout,"backgroundColor",
                getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.color_flat_sun_flower));
        colorAnim.setDuration(2500);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatMode(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }


}
