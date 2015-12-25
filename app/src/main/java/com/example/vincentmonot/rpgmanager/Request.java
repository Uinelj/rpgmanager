package com.example.vincentmonot.rpgmanager;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {
    String request; //requete pas envoyee
    String jsonAnswer; //resultat en plain json
    JSONObject jo; //objet json recu apres parsage
    Map<String, String> result = new HashMap<String, String>(); //hashmap de ce qui a ete recu

    Context context;

    public Request(String request, Context context){
        this.request = request;
        jsonAnswer = makeHttpRequest();
        populateResult();
        if(isJSONValid(result.get("user"))){
            setJsonAnswer(result.get("user"));
            populateResult();
        }
        this.context = context;
        //System.out.println(result.);
    }

    public void setJsonAnswer(String jsonAnswer){
        this.jsonAnswer = jsonAnswer;
    }
    public String getValue(String key){
        return result.get(key);
    }
    public Map<String, String> getResult(){
        return result;
    }
    public void populateResult(){
        try {
            jo = new JSONObject(jsonAnswer);
            Iterator<String> keysItr = jo.keys();
            while(keysItr.hasNext()){
                String key = keysItr.next();
                String value = jo.get(key).toString();
                result.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String makeHttpRequest(){
        String content="";
        try {
            URL queryUrl = new URL(request);
            BufferedReader br = new BufferedReader(new InputStreamReader(queryUrl.openStream()));
            String buffer;
            while((buffer = br.readLine()) != null){
                content += buffer;
            }
        } catch(IOException e){
            //e.printStackTrace();
            Toast.makeText(context, "Connexion impossible", Toast.LENGTH_SHORT).show();
        }
        return content;
    }
    public ArrayList<String> getKeys(){
        return new ArrayList<String>(result.keySet());
    }
    public boolean isJSONValid(String str){
        if(str == null){
            return false;
        }
        try {
            new JSONObject(str);
        } catch (JSONException ex) {
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(str);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}