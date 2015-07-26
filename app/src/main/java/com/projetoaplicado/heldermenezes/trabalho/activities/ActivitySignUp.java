package com.projetoaplicado.heldermenezes.trabalho.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ActivitySignUp extends AppCompatActivity {

    private Button btnSignUp;
    private MaterialEditText eTUserName, eTSenha, eTRepeteSenha, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Toolbar toolbar = (Toolbar) this.findViewById(R.id.id_toolbar);
        //this.setSupportActionBar(toolbar);

        eTUserName = (MaterialEditText) findViewById(R.id.id_et_UserName);
        eTSenha = (MaterialEditText) findViewById(R.id.id_et_password);
        eTRepeteSenha = (MaterialEditText) findViewById(R.id.id_et_password_repeat);
        etEmail = (MaterialEditText) findViewById(R.id.id_et_Email);
        btnSignUp = (Button) findViewById(R.id.id_btn_signup);


        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                signup();
            }
        });

    }

    private void signup() {
        String username = eTUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String senha = eTSenha.getText().toString().trim();
        String repeteSenha = eTRepeteSenha.getText().toString().trim();

        boolean validationError = false;

        StringBuilder validationErrorMessage = new StringBuilder("Error: ");

        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.por_favor_inserir_nome_utilizador)).append("\n");
        }
        if(email.length()==0){
            if(validationError){
                validationErrorMessage.append(getString(R.string.por_favor_inserir_email)).append("\n");
            }
        }
        if(!email.contains("@") && !Util.Utilities.isValidEmail(email)){
            if(validationError){
                validationErrorMessage.append(getString(R.string.por_favor_inserir_email_valido)).append("\n");
            }
        }
        if (senha.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.erro_ao_login)).append("\n");
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.por_favor_inserir_uma_senha)).append("\n");
        }

        if (!senha.equals(repeteSenha)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.erro_ao_fazer_login)).append("\n");
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.as_senhas_nao_sao_iguais)).append("\n");
        }

        if (validationError) {
            Util.Utilities.showToast(this, validationErrorMessage.toString());
            return;
        }

        if(!Util.isExisteConexaoAInternet(this)){
            Util.Utilities.showToast(this,getResources().getString(R.string.no_connection));
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.a_registar));
        progressDialog.show();

        ParseUser novoUser = new ParseUser();
        novoUser.setUsername(username);
        novoUser.setEmail(email);
        novoUser.setPassword(senha);
        //novoUser.put("senha",senha);

        novoUser.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Util.Utilities.showToast(getBaseContext(), getString(R.string.utilizador_registado_com_sucesso));
                    Util.Utilities.startActivity(ActivitySignUp.this, MainActivity.class, null);
                    finish();
                } else {
                    Util.Utilities.showToast(getBaseContext(), getString(R.string.utilizador_ja_existe));
                }
            }
        });
    }

}
