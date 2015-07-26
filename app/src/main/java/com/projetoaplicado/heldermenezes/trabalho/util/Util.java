package com.projetoaplicado.heldermenezes.trabalho.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by heldermenezes on 12/06/2015.
 */
public class Util {
    public static class Utilities {

        public static String getDateFormat(long milliseconds) {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            return formatter.format(calendar.getTime());
        }

        public static void showToast(Context context, String mensagem) {
            Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
        }

        public static void showLog(final String TAG, String mensagem) {
            Log.i(TAG, mensagem);
        }

        public static boolean isValidEmail(CharSequence target) {
            if (TextUtils.isEmpty(target)) {
                return false;
            } else {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            }
        }

        public static void startActivity(Activity activity, Class cls, Bundle bundle) {
            Intent intent = new Intent(activity, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }

            activity.startActivity(intent);
        }

        public static void startActivityForResult(Activity activity, Class cls, int requestCode, Bundle bundle) {
            Intent intent = new Intent(activity, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static class Image {
        private static Context ctx;

        public static File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName,
                    ".jpg",
                    storageDir
            );

            // Save a file: path for use with ACTION_VIEW intents
            String mCurrentPhotoPath = "file:" + image.getAbsolutePath();
            return image;
        }

        public static ImageLoader initImageLoader(Context context, ImageLoader imageLoader) {
            ctx = context;
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                    .defaultDisplayImageOptions(defaultOptions).memoryCache(new WeakMemoryCache());

            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
            return imageLoader;
        }

        public static String getImagePath(Context context, Intent data) {
            ctx = context;
            String single_path;
            Uri uri;
            uri = data.getData();
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            single_path = cursor.getString(columnIndex);
            cursor.close();
            return single_path;
        }

        public static Bitmap getBitmap(String path) {
            if (path == null || path.isEmpty()) {
                //Bitmap icon = BitmapFactory.decodeResource()
                return null;
            }
            Bitmap bitmap = scaleBitmap(path);
            return bitmap;
        }

        private static Bitmap scaleBitmap(String filePath) {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / 250, photoH / 250);
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

            return bitmap;
        }

        public static ParseFile parseBitmapToParseFile(Bitmap bitmap, String title) {

            ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();
            // compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, mByteArrayOutputStream);
            byte[] image = mByteArrayOutputStream.toByteArray();

            // Create the ParseFile
            ParseFile mParseFile = new ParseFile(title, image);
            return mParseFile;
        }

    }

    public static class Dialog {

        public static MaterialDialog createMaterialDialogForStartActivity(final Activity activity, BaseAdapter adapter, final Class className, boolean includeButton, String[] text) {
            MaterialDialog dialog;
            if (includeButton) {
                dialog = new MaterialDialog.Builder(activity)
                        .title(text[0])
                        .adapter(adapter, null)
                        .positiveText(text[1])
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Utilities.startActivity(activity, className, null);
                                dialog.dismiss();
                            }
                        }).build();
            } else {
                dialog = new MaterialDialog.Builder(activity)

                        .title(text[0])
                        .adapter(adapter, null)
                        .build();
            }


            return dialog;
        }
    }

    public static class Contacto {
        public static String getNumeroContacto(Context context, Uri uriContact) {
            String contactNumber = null;
            // getting contacts ID
            Cursor cursorID = context.getContentResolver().query(uriContact,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);

            String contactID = "";
            if (cursorID.moveToFirst()) {
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }
            cursorID.close();
            Log.i("TAG", "Contact ID: " + contactID);
            // Using the contact ID now we will get contact phone number
            Cursor cursorPhone = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    new String[]{contactID},
                    null);
            if (cursorPhone.moveToFirst()) {
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            cursorPhone.close();
            Log.i("TAG", "Contact Phone Number: " + contactNumber);
            return contactNumber;
        }

        public static String getNomeDoContacto(Context context, Uri uriContact) {
            String contactName = null;
            Cursor cursor = context.getContentResolver().query(uriContact, null, null, null, null);
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }
            cursor.close();
            Log.i("TAG", "Contact Name: " + contactName);
            return contactName;
        }

        public static String getEmailDoContacto(Context context, Uri uriContact) {
            String contactEmail = null;
            Cursor cursorID = context.getContentResolver().query(uriContact,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);
            String contactID = "";
            if (cursorID.moveToFirst()) {
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }
            cursorID.close();
            Log.i("TAG", "Contact ID: " + contactID);
            // Using the contact ID now we will get contact phone number
            Cursor cursorEmail = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Email.DATA},
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ? ",
                    new String[]{contactID},
                    null);
            if (cursorEmail.moveToFirst()) {
                contactEmail = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
            cursorEmail.close();
            Log.i("TAG", "Contact email: " + contactEmail);
            return contactEmail;
        }
    }

    /**
     * source : http://www.androidhive.info/2012/07/android-detect-internet-connection-status/
     *
     * @param context
     * @return
     */
    public static boolean isExisteConexaoAInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null) {
                for (int i = 0; i < networkInfos.length; i++) {
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public static final String EMPTY = "";

    public static class Request {

        public static final int EDIT = 0;
        public static final int ADD = 1;
        public static final int DEFAULT = 2;
        public static final int DETAIL = 3;
        public static final String SELECT = "Clica para selecionar";
        public static final int REQUEST_IMAGE_GALERY_CODE = 100;
        public static final int REQUEST_IMAGE_CAPTURE_CODE = 200;
        public static final int REQUEST_IMPORTAR_PESSOA_NO_CONTACTO = 300;
        public static final int REQUEST_ACTIVITY_FOR_PESSOA = 4;
        public static final int REQUEST_ACTIVITY_FOR_GRUPO = 5;

    }

    public static class PARAMS {
        public static final String ARGS = "alt_cons";
        public static final int CONSULTAR = 2014;
        public static final int ALTERAR = 2015;
        public static final int SIMULAR = 666;
        public static final int FECHAR = 777;
        public static final int UPLOAD_FAILED = 8888;
        public static final int UPLOAD_SUCESSO = 9999;

    }


    public static class Moeda {


        public static Double conversorMoeda(Double importancia, int moeda, Double taxaDeCambio) {

            if (importancia != null && moeda != -1 && taxaDeCambio != null) {
                return importancia * taxaDeCambio;
            }
            return 0.0;
        }


        public static class Simbolos {

            public static Map<Integer, String> getSimbolos() {
                Map<Integer, String> simbolos = new HashMap<>();
                simbolos.put(0, EURO);
                simbolos.put(1, DOLLAR_AMERICANO);
                simbolos.put(2, POUND);
                simbolos.put(3, REAL_BRASIL);
                simbolos.put(4, YEN_JAPONES);


                return simbolos;
            }

            public static final String EURO = "EUR";
            public static final String DOLLAR_AMERICANO = "USD";
            public static final String POUND = "GBP";
            public static final String REAL_BRASIL = "BRL";
            public static final String YEN_JAPONES = "JPY";

        }

        public static Map<Integer, String> getStringMoeda() {

            Map<Integer, String> moedas = new HashMap<>();
            moedas.put(0, "EURO");
            moedas.put(1, "DOLLAR AMERICANO");
            moedas.put(2, "LIBRA");
            moedas.put(3, "REAL");
            moedas.put(4, "YEN JAPONES");
            return moedas;
        }

    }
}
