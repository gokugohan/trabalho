package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IUploadListener;
import com.projetoaplicado.heldermenezes.trabalho.model.AcertoDeConta;
import com.projetoaplicado.heldermenezes.trabalho.model.Despesa;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ActivityEnviarDados extends AppCompatActivity {


    private DatabaseHelper db;
    private List<Despesa> despesas;

    private TextView tvUploadText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_dados);

        db = DatabaseHelper.newInstance();

        final ArrayList<AcertoDeConta> acerto_final = (ArrayList<AcertoDeConta>) getIntent().getSerializableExtra("contas");

        Long idGrupo = getIntent().getLongExtra("idGrupo", -1);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout3);

        final List<Pessoa> pessoas = getPessoasConta(acerto_final);
        if(idGrupo!=-1){
            despesas = db.getDespesasDoGrupo(idGrupo);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    db.uploadConta(acerto_final, despesas, pessoas);
                    db.deleteDespesas(despesas);
                    setResult(RESULT_OK);
                    linearLayout.setVisibility(View.GONE);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityEnviarDados.this);
                    alertDialog.setTitle(getString(R.string.envio_de_dados));
                    alertDialog.setMessage(getString(R.string.dados_enviado_com_sucesso));
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            },1000);

        }else{
            Util.Utilities.showToast(this, getString(R.string.ocorreu_erro));
            finish();
        }
    }



    @Override
    public void onBackPressed() {
        Util.Utilities.showToast(this,getString(R.string.nao_pode_interomper_o_processo));
    }

    private List<Pessoa> getPessoasConta(List<AcertoDeConta> acerto_final) {
        List<Pessoa> pessoas = new ArrayList<>();
        for (int i = 0; i < acerto_final.size(); i++) {
            AcertoDeConta ac = acerto_final.get(i);
            pessoas.add(ac.getPessoa());
        }
        return pessoas;
    }
}
