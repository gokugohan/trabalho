package com.projetoaplicado.heldermenezes.trabalho.fragmentos;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.projetoaplicado.heldermenezes.trabalho.R;
import com.projetoaplicado.heldermenezes.trabalho.activities.ActivityPessoaDetail;
import com.projetoaplicado.heldermenezes.trabalho.interfaces.IPessoaItemClickListener;
import com.projetoaplicado.heldermenezes.trabalho.model.Pessoa;
import com.projetoaplicado.heldermenezes.trabalho.repositorio.DatabaseHelper;
import com.projetoaplicado.heldermenezes.trabalho.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPessoa extends Fragment implements OnClickListener, IPessoaItemClickListener {

    private FloatingActionButton fab;
    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private String urlDaImagem;
    private Bitmap bitmap;
    private String nomedafoto;
    private Uri uriContact;
    private String nome;
    private String telefone;
    private String email;
    private ImageLoader imageLoader;
    private ImageView ivFoto;
    private MaterialEditText etNome;
    private MaterialEditText etTelefone;
    private MaterialEditText etEmail;
    private PessoaRecyclerViewAdapter adapter;
    private boolean editMode = false;
    private long pessoa_id = -1;
    private List<Pessoa> pessoas;
    private MaterialDialog dialog;
    private int param_passado = 0;

    public FragmentPessoa() {
        db = DatabaseHelper.newInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = Util.Image.initImageLoader(getActivity(), imageLoader);
        Bundle args = this.getArguments();
        if (args != null) {
            param_passado = args.getInt(Util.PARAMS.ARGS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmento_pessoa, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_main_fragmento_pessoa);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.id_fab_fragment_pessoa_add);
        floatingActionButton.setOnClickListener(this);

        switch (param_passado) {
            case Util.PARAMS.CONSULTAR:
                floatingActionButton.setVisibility(View.GONE);
                break;
            case Util.PARAMS.ALTERAR:
                floatingActionButton.setVisibility(View.GONE);
                break;
            default:
                floatingActionButton.setVisibility(View.VISIBLE);
                break;
        }
        setupRecyclerView(recyclerView);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView(recyclerView);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        //recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3));
        pessoas = db.getPessoas();


        adapter = new PessoaRecyclerViewAdapter(getActivity(), pessoas, imageLoader);
        adapter.setOnPessoaItemClickListener(this);

        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        //Snackbar.make(v, "Pessoa clicked", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        if (v.getId() == R.id.id_fab_fragment_pessoa_add) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, Util.Request.REQUEST_IMPORTAR_PESSOA_NO_CONTACTO);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == Activity.RESULT_OK) {

                if (requestCode == Util.Request.REQUEST_IMAGE_CAPTURE_CODE) {
                    Log.d("CAMERA","");
                    /*
                    this.urlDaImagem = Util.Image.getImagePath(getActivity(), data);
                    Log.e("CAMERA_URL_FOTO", urlDaImagem);
                    this.bitmap = Util.Image.getBitmap(this.urlDaImagem);

                    if (!TextUtils.isEmpty(this.urlDaImagem)) {
                        String tmps[] = this.urlDaImagem.split("/");
                        nomedafoto = tmps[tmps.length - 1];
                    }
                    */
                }

                if (requestCode == Util.Request.REQUEST_IMAGE_GALERY_CODE) {
                    this.urlDaImagem = Util.Image.getImagePath(getActivity(), data);
                    this.bitmap = Util.Image.getBitmap(this.urlDaImagem);
                    if (!TextUtils.isEmpty(this.urlDaImagem)) {
                        String tmps[] = this.urlDaImagem.split("/");
                        nomedafoto = tmps[tmps.length - 1];
                    }

                } else if (requestCode == Util.Request.REQUEST_IMPORTAR_PESSOA_NO_CONTACTO) {
                    uriContact = data.getData();
                    nome = Util.Contacto.getNomeDoContacto(getActivity(), uriContact);
                    telefone = Util.Contacto.getNumeroContacto(getActivity(), uriContact);
                    email = Util.Contacto.getEmailDoContacto(getActivity(), uriContact);
                    showEditAddPessoa();
                }
            }
        }

        if (!TextUtils.isEmpty(this.urlDaImagem)) {
            setFotoToImageView(this.urlDaImagem);
        }

    }

    private void setFotoToImageView(String path) {

        imageLoader.displayImage("file://" + path, ivFoto);
    }

    private void showEditAddPessoa() {

        boolean wrapInScrollView = true;
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_grupo_novo_dialog_selected_contact, null);
        etNome = (MaterialEditText) view.findViewById(R.id.id_et_nome_nova_contacto);
        etTelefone = (MaterialEditText) view.findViewById(R.id.id_et_numero_contacto_nova_contacto);
        etEmail = (MaterialEditText) view.findViewById(R.id.id_et_email_nova_contacto);
        ivFoto = (ImageView) view.findViewById(R.id.id_iv_imagem_nova_contacto);

        ivFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.Utilities.showToast(getActivity(), "Clicked");
                getFotoIntent();
            }
        });

        etNome.setText(nome);
        etTelefone.setText(telefone != null ? telefone : "");
        etEmail.setText(email != null ? email : "");
        if (!TextUtils.isEmpty(urlDaImagem)) setFotoToImageView(urlDaImagem);

        dialog = createMaterialDialog(wrapInScrollView, view);
        dialog.show();
    }

    private MaterialDialog createMaterialDialog(boolean wrapInScrollView, View view) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .title(getActivity().getString(R.string.pessoa))
                .customView(view, wrapInScrollView)
                .positiveText(editMode ? getActivity().getString(R.string.alterar) : getActivity().getString(R.string.adicionar))
                .negativeText(getActivity().getString(R.string.cancelar))
                .callback(materialDialogButtonListener).build();
        return materialDialog;
    }

    private MaterialDialog.ButtonCallback materialDialogButtonListener = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            dialog.dismiss();
            nome = etNome.getText().toString().trim();
            telefone = etTelefone.getText().toString().trim();
            email = etEmail.getText().toString().trim();

            if (!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(telefone) && Util.Utilities.isValidEmail(email)) {
                if (editMode) {
                    Pessoa pessoa = db.getPessoa(pessoa_id);
                    pessoa.nome = nome;
                    pessoa.telefone = telefone;
                    pessoa.email = email;
                    pessoa.urlDaFoto = urlDaImagem;
                    pessoa.save();
                    setupRecyclerView(recyclerView);

                } else if (!pessoaJaExisteComEsseNome(nome)) {
                    Pessoa pessoa = db.savePessoa(nome, email, telefone, urlDaImagem);
                    adapter.addItemPessoa(pessoa);
                } else {
                    Util.Utilities.showToast(getActivity(), getString(R.string.ja_existe));
                }
            } else {
                Util.Utilities.showToast(getActivity(), getString(R.string.dados_incompleto));
            }
        }
        @Override
        public void onNegative(MaterialDialog dialog) {
            super.onNegative(dialog);
            dialog.dismiss();
        }
    };

    private boolean pessoaJaExisteComEsseNome(String nome) {
        List<Pessoa> pessoas = db.getPessoas();
        for (Pessoa pessoa : pessoas) {
            if (pessoa.nome.equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    private void getFotoIntent() {

        AlertDialogWrapper.Builder dialog = new AlertDialogWrapper.Builder(getActivity());
        dialog.setTitle("Select option");
        dialog.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // http://developer.android.com/training/camera/photobasics.html
                takePhoto();
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("GALERRY", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Intent intent = new Intent(Util.Request.ACTION_PICK);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, Util.Request.REQUEST_IMAGE_GALERY_CODE);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void takePhoto() {
        // Android Camera Sample page
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = Util.Image.createImageFile();
            } catch (IOException ex) {
                Util.Utilities.showToast(getActivity(),ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri uri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        uri);
                Util.Utilities.showLog("URI_FILE", uri.getPath());
                urlDaImagem = uri.getPath();
                startActivityForResult(intent, Util.Request.REQUEST_IMAGE_CAPTURE_CODE);
            }
        }

    }

    @Override
    public void onPessoaItemClicked(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        //Util.Utilities.startActivity(getActivity(), ActivityPessoaDetail.class, bundle);
        switch (param_passado) {
            case Util.PARAMS.CONSULTAR:
                bundle.putInt(Util.PARAMS.ARGS, Util.PARAMS.CONSULTAR);
                Util.Utilities.startActivity(getActivity(), ActivityPessoaDetail.class, bundle);
                break;
            case Util.PARAMS.ALTERAR:
                editMode = true;
                Pessoa pessoa = db.getPessoa(id);
                nome = pessoa.nome;
                email = pessoa.email;
                telefone = pessoa.telefone;
                urlDaImagem = pessoa.urlDaFoto;
                pessoa_id = id;
                showEditAddPessoa();
                break;
            default:
                Util.Utilities.startActivity(getActivity(), ActivityPessoaDetail.class, bundle);
                break;
        }
    }


    /* ADAPTER */
    public static class PessoaRecyclerViewAdapter
            extends RecyclerView.Adapter<PessoaRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Pessoa> pessoas;
        private Context context;
        private ImageLoader imageLoader;
        private IPessoaItemClickListener listener;

        public void setOnPessoaItemClickListener(IPessoaItemClickListener listener) {
            this.listener = listener;
        }

        public Pessoa getValueAt(int position) {
            return pessoas.get(position);
        }

        public PessoaRecyclerViewAdapter(Context context, List<Pessoa> items, ImageLoader imageLoader) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            pessoas = items;
            this.context = context;
            this.imageLoader = imageLoader;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mBoundString = getValueAt(position).nome;
            holder.mTextView.setText(getValueAt(position).nome);

            final Pessoa pessoa = pessoas.get(position);

            holder.mView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPessoaItemClicked(pessoa.getId());
                    }
                }
            });


            if (TextUtils.isEmpty(pessoa.urlDaFoto)) {
                Glide.with(holder.mImageView.getContext())
                        .load(R.drawable.no_pessoa)
                        .fitCenter()
                        .into(holder.mImageView);
            } else {
                setFotoToImageView(pessoa.urlDaFoto, holder.mImageView);
            }


        }

        @Override
        public int getItemCount() {
            return pessoas.size();
        }

        public void addItemPessoa(Pessoa pessoa) {
            pessoas.add(0, pessoa);
            notifyItemInserted(0);
        }


        private void setFotoToImageView(String path, ImageView imageView) {
            if (imageView != null) {
                imageLoader.displayImage("file://" + path, imageView);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }
    }
}
