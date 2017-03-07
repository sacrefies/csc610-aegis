package net.teamc.aegis;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CrimeJSONParse
{

    //takes in a URL in string form and sets it as a URL
    //and also parses through and creates array list of string json objects
    CrimeJSONParse(String crimeURL) {
        try {
            crimeAPI = new URL(crimeURL);
            arrayCrimeParse(download(crimeAPI));
        } catch(MalformedURLException mue){
            Log.e(logtag,"URL is invalid");
        }
    }

    public static URL crimeAPI;
    String JSONString;
    //returns string version of json
    String download(URL crimeAPI) {
        StringBuffer buff = new StringBuffer();
        try {
            HttpURLConnection conn = (HttpURLConnection) crimeAPI.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                buff.append(line);
            }
            br.close();
            conn.disconnect();
            JSONString = buff.toString();
        }catch(IOException ioe){
        }
        return JSONString;
    }
    final private static String logtag = "CrimeJSONParse";
    List<JSONObject> returnJSON = new ArrayList<JSONObject>();//returned jsonobject arraylist

    //create arraylist of jsonobjects from the json string
    List<JSONObject> arrayCrimeParse(String jsonContext)
    {

        try {
            JSONArray crimeArray = new JSONArray(jsonContext);

            for (int i = 0; i < crimeArray.length(); i++) {
                JSONObject jo = crimeArray.getJSONObject(i);
                returnJSON.add(jo);
            }
            return returnJSON;
        }catch(JSONException js){
            Log.e(logtag,"Unexpected JSONException");
        }return null;
    }
    @NonNull
    static String getAPIshape(double lat, double lang){
        StringBuilder address = new StringBuilder();
        address.append("https://data.phila.gov/resource/sspu-uyfa.json?$where=within_circle(shape,%20"
                +lat+",%20"+lang+",%203000)");
        return address.toString();
    }

    @NonNull
    static String getAPIFull(){
        StringBuilder address = new StringBuilder();
        address.append("https://data.phila.gov/resource/sspu-uyfa.json");
        return address.toString();
    }

    //returns String form of JSON
    String getJSONString()
    {
        return JSONString;
    }

    //return dc_dist number for a single JSON Object in an Array List
    int getdc_dist(List<JSONObject> JSONArraylist,int counter)
    {
        String tagg = "";
        JSONObject jo;
        int result=0;
            try{
                jo = JSONArraylist.get(counter);
                result = jo.getInt("dc_dist");

            } catch (JSONException js) {
                Log.e(tagg, "Unexpected JSONException");
            }
        return result;
    }

    //return dispatch date for a single JSON Object in an Array List
    String getDispatch_date(List<JSONObject> JSONArraylist,int counter)
    {
        String tagg = "";
        JSONObject jo;
        String result="";
        try{
            jo = JSONArraylist.get(counter);
            result = jo.getString("dispatch_date");

        } catch (JSONException js) {
            Log.e(tagg, "Unexpected JSONException");
        }
        return result;
    }

    int getUCR(List<JSONObject> JSONArraylist,int counter)
    {
        String tagg = "";
        JSONObject jo;
        int result=0;
        try{
            jo = JSONArraylist.get(counter);
            result = jo.getInt("ucr_general");

        } catch (JSONException js) {
            Log.e(tagg, "Unexpected JSONException");
        }
        return result;
    }

    int getHour(List<JSONObject> JSONArraylist,int counter)
    {
        String tagg = "";
        JSONObject jo;
        int result=0;
        try{
            jo = JSONArraylist.get(counter);
            result = jo.getInt("hour");

        } catch (JSONException js) {
            Log.e(tagg, "Unexpected JSONException");
        }
        return result;
    }

     double[] getShape(List<JSONObject> JSONArraylist,int counter)
    {
        String tagg = "";
        JSONObject jo;
        JSONArray shape;

        double[] result;
        result = new double[2];
        try{
            jo = JSONArraylist.get(counter);
            shape = jo.getJSONObject("shape").getJSONArray("coordinates");
            double lat = shape.getDouble(0);
            double lon = shape.getDouble(1);

            result[0] = lat;
            result[1] = lon;

        } catch (JSONException js) {
            Log.e(tagg, "Unexpected JSONException");
        }
        return result;
    }

    //Returns new JSON array list with crimes within the shape without the specified URC code
    List<JSONObject> removeByURC(List<JSONObject> shapeJSON, int ucr)
    {
        ListIterator<JSONObject> iterator = shapeJSON.listIterator();
        JSONObject marker;
        List<JSONObject> newJSONArray = new ArrayList<JSONObject>();

        for(int i = 0; iterator.hasNext(); i++)
        {
            try{
            marker = shapeJSON.get(i);
            int shapeJSONucr = marker.getInt("ucr_general");
                if(shapeJSONucr != ucr)
                    newJSONArray.add(marker);
            }catch(JSONException js) {
                Log.e(logtag, "Unexpected JSONException");
            iterator.next();
            }
        }
        return newJSONArray;
    }
}