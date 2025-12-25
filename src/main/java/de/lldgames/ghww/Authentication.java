package de.lldgames.ghww;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Authentication {
    final static String FILE_PATH = "./auth.json";
    private static JSONObject auths;


    public static void loadFromFile(){
        File f = new File(FILE_PATH);
        if(!f.exists()) {
            writeFile(f, "{}");
        }
        String content = readFile(f);
        JSONObject loaded = new JSONObject(content);
        auths = loaded;
    }

    public static void writeToFile(){
        File f = new File(FILE_PATH);
        writeFile(f, auths.toString());
    }

    //perm funcs
    public static boolean hasAccess(String token, String repo){
        if(!isGoodToken(token)) return false;
        JSONObject tokenConfig = auths.getJSONObject(token);
        JSONArray allowed = tokenConfig.getJSONArray("allowed");
        for(int i = 0; i<allowed.length(); ++i){
            if(allowed.getString(i).equals(repo) || allowed.getString(i).equals("*")){
                return true;
            }
        }
        return false;
    }

    public static boolean isGoodToken(String token){
        return auths.has(token);/* &&
                !(auths.getJSONObject(token).has("disabled")
                                && auths.getJSONObject(token).getBoolean("disabled")
                )
                ;*/
    }

    public static String readFile(File f){
        try(FileInputStream fis = new FileInputStream(f)){
            return new String(fis.readAllBytes());
        }catch (Exception e){
            return "";
        }
    }

    public static boolean writeFile(File f, String content){
        try{
            if(!f.exists())f.createNewFile();
        }catch (Exception e){ return false;}
        try(FileOutputStream fos = new FileOutputStream(f)){
            fos.write(content.getBytes());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
