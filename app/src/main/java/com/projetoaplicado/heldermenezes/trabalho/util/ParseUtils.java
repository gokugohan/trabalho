package com.projetoaplicado.heldermenezes.trabalho.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.projetoaplicado.heldermenezes.trabalho.model.ParseContaData;

/**
 * Created by heldermenezes on 07/07/2015.
 */
public class ParseUtils {

    public static final String PARSE_APP_ID = "tXfgx0nV5KffY60z09cBSRS97WDcHs3mUzz8dmQi";
    public static final String PARSE_CLIENT_KEY = "Kq3dNgaxnadscrmJiMIwXHvz4NnuJ3rO4o6BayV2";

    public static void initialize(Context context){

        registerParseObject();
        Parse.enableLocalDatastore(context);
        Parse.initialize(context, ParseUtils.PARSE_APP_ID, ParseUtils.PARSE_CLIENT_KEY);

        ParseUser.enableRevocableSessionInBackground();
        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }
    /***
     * Registar os objetos da parse
     */
    private static void registerParseObject(){
        ParseObject.registerSubclass(ParseContaData.class);

    }

    /***
     * Verificar se utilizador esta logado
     * @return true - esta logado, caso contrario false
     */
    public static boolean isRegisteredUser(){
        if(!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
            return true;
        }
        return false;
    }

    /***
     * Criar e guardar a foto antes de enviar os dados para parse (remote server)
     * @param nomedafoto
     * @param url
     * @return ParseFile (a foto guardada)
     */
    public static ParseFile createFoto(String nomedafoto, String url) {
        Bitmap bitmap = Util.Image.getBitmap(url);
        ParseFile foto = Util.Image.parseBitmapToParseFile(bitmap, nomedafoto + ".png");
        try {
            foto.save();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        return foto;
    }
}
