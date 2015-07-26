package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnLoginbutton;
    private Button mBtnSignup;
    private String mUsername;
    private String mSenha;
    private MaterialEditText etSenha;
    private EditText etUsername;
    private String mRecuperarSenha;
    private MaterialEditText etRecuperarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        this.mBtnLoginbutton = (Button) findViewById(R.id.id_btn_login);
        this.mBtnSignup = (Button) findViewById(R.id.id_btn_signup);
        this.etUsername = (MaterialEditText) findViewById(R.id.editTextUsername);
        this.etSenha = (MaterialEditText) findViewById(R.id.editTextPassword);

        this.mBtnLoginbutton.setOnClickListener(this);
        this.mBtnSignup.setOnClickListener(this);


    }


    public void forgetPassword(View view){
        forgetpasswordDialog();
    }

    private void forgetpasswordDialog() {

        boolean wrapInScrollView = true;
        View view = getLayoutInflater().inflate(R.layout.dialog_recuperar_senha, null);
        etRecuperarSenha = (MaterialEditText) view.findViewById(R.id.id_et_recuperar_senha);
        MaterialDialog dialog = createMaterialDialog(wrapInScrollView, view);
        dialog.show();
    }

    private MaterialDialog createMaterialDialog(boolean wrapInScrollView, View view) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(ActivityLogin.this)
                .title(getString(R.string.recuperar_senha))
                .customView(view, wrapInScrollView)
                .positiveText(getString(R.string.recuperar))
                .callback(materialDialogButtonListener).build();
        return materialDialog;
    }

    private MaterialDialog.ButtonCallback materialDialogButtonListener = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            dialog.dismiss();
            mRecuperarSenha = etRecuperarSenha.getText().toString();
            ParseUser.requestPasswordResetInBackground(mRecuperarSenha, new RequestPasswordResetCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        mostrarDialogoRecuperarEnviado(getString(R.string.recuperar_senha_mensagem_de_sucesso));
                    }else {
                        mostrarDialogoRecuperarEnviado(getString(R.string.ocorreu_erro));
                    }
                }
            });
        }

        @Override
        public void onNegative(MaterialDialog dialog) {
            super.onNegative(dialog);
        }
    };

    public void mostrarDialogoRecuperarEnviado(String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.recuperar_senha));
        builder.setMessage(mensagem);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn_login:
                login();
                break;
            case R.id.id_btn_signup:
                signup();
                break;
        }
    }

    private void login() {
        mUsername = etUsername.getText().toString().trim();
        mSenha = etSenha.getText().toString().trim();

        if(!Util.isExisteConexaoAInternet(this)){
            Util.Utilities.showToast(this,getResources().getString(R.string.no_connection));
            return;
        }
        if (mUsername.length() > 0 && mSenha.length() > 0) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.loging_in));
            dialog.show();

            ParseUser.logInInBackground(mUsername, mSenha, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    dialog.dismiss();
                    if (parseUser != null) {
                        Util.Utilities.startActivity(ActivityLogin.this, MainActivity.class,null);
                        Util.Utilities.showToast(ActivityLogin.this, getString(R.string.login_sucesso));
                        finish();
                    } else {
                        Util.Utilities.showToast(ActivityLogin.this, getString(R.string.utilizador_ou_senha_errado));
                    }
                }
            });
        }

    }

    private void signup() {
        finish();
        Util.Utilities.startActivity(this, ActivitySignUp.class,null);
    }
}
