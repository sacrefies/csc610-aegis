package net.teamc.aegis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static net.teamc.aegis.R.id.record1;
import static net.teamc.aegis.R.id.record3;
import static net.teamc.aegis.R.id.record4;
import static net.teamc.aegis.R.id.record5;
import static net.teamc.aegis.R.id.record6;
import static net.teamc.aegis.R.id.record7;
import static net.teamc.aegis.R.id.record8;

/**
 * Created by Mingyuan Li on 2016/12/3.
 */

public class FavoritesActivity extends AppCompatActivity {

    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private Button b6;
    private Button b7;
    private Button b8;
    private Button b10;

    private String addressString;
    private ArrayList<TextView> record = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setupActionBar();
        try {
            record.add(0, (TextView) findViewById(record1));
            record.add(1, (TextView) findViewById(R.id.recrod2));
            record.add(2, (TextView) findViewById(record3));
            record.add(3, (TextView) findViewById(record4));
            record.add(4, (TextView) findViewById(record5));
            record.add(5, (TextView) findViewById(record6));
            record.add(6, (TextView) findViewById(record7));
            record.add(7, (TextView) findViewById(record8));
        }
        catch (IndexOutOfBoundsException e){}


        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        b4 = (Button) findViewById(R.id.b4);
        b5 = (Button) findViewById(R.id.b5);
        b6 = (Button) findViewById(R.id.b6);
        b7 = (Button) findViewById(R.id.b7);
        b8 = (Button) findViewById(R.id.b8);
        b10 = (Button) findViewById(R.id.b10);

        SharedPreferences getSet = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        try {
            addressString = getSet.getString(getString(R.string.pref_fav_records), null);
            String[] addressParts = addressString.split("\\$");
            for (int i = 0; i < addressParts.length; i++){
                record.get(i).setText(addressParts[i]);

            }
        }
        catch (NullPointerException e){}
    }
    private void save() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void delete1(View view){
        record.get(0).setText("");
        addressString = record.get(1).getText().toString()
                + "$" + record.get(2).getText().toString()
                + "$" + record.get(3).getText().toString()
                + "$" + record.get(4).getText().toString()
                + "$" + record.get(5).getText().toString()
                + "$" + record.get(6).getText().toString()
                + "$" + record.get(7).getText().toString()
                + "$" + record.get(0).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }
    public void delete2(View view){
        record.get(1).setText("");
        addressString = record.get(0).getText().toString()
                + "$" + record.get(2).getText().toString()
                + "$" + record.get(3).getText().toString()
                + "$" + record.get(4).getText().toString()
                + "$" + record.get(5).getText().toString()
                + "$" + record.get(6).getText().toString()
                + "$" + record.get(7).getText().toString()
                + "$" + record.get(1).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }
    public void delete3(View view){
        record.get(2).setText("");
        addressString = record.get(0).getText().toString()
                + "$" + record.get(1).getText().toString()
                + "$" + record.get(3).getText().toString()
                + "$" + record.get(4).getText().toString()
                + "$" + record.get(5).getText().toString()
                + "$" + record.get(6).getText().toString()
                + "$" + record.get(7).getText().toString()
                + "$" + record.get(2).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }
    public void delete4(View view){
        record.get(3).setText("");
        addressString = record.get(0).getText().toString()
                + "$" + record.get(1).getText().toString()
                + "$" + record.get(2).getText().toString()
                + "$" + record.get(4).getText().toString()
                + "$" + record.get(5).getText().toString()
                + "$" + record.get(6).getText().toString()
                + "$" + record.get(7).getText().toString()
                + "$" + record.get(3).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }
    public void delete5(View view){
        record.get(4).setText("");
        addressString = record.get(0).getText().toString()
                + "$" + record.get(1).getText().toString()
                + "$" + record.get(2).getText().toString()
                + "$" + record.get(3).getText().toString()
                + "$" + record.get(5).getText().toString()
                + "$" + record.get(6).getText().toString()
                + "$" + record.get(7).getText().toString()
                + "$" + record.get(4).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }
    public void delete6(View view){
        record.get(5).setText("");
        addressString = record.get(0).getText().toString()
                + "$" + record.get(1).getText().toString()
                + "$" + record.get(2).getText().toString()
                + "$" + record.get(3).getText().toString()
                + "$" + record.get(4).getText().toString()
                + "$" + record.get(6).getText().toString()
                + "$" + record.get(7).getText().toString()
                + "$" + record.get(5).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }
    public void delete7(View view){
        record.get(6).setText("");
        addressString = record.get(0).getText().toString()
                + "$" + record.get(1).getText().toString()
                + "$" + record.get(2).getText().toString()
                + "$" + record.get(3).getText().toString()
                + "$" + record.get(4).getText().toString()
                + "$" + record.get(5).getText().toString()
                + "$" + record.get(7).getText().toString()
                + "$" + record.get(6).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }
    public void delete8(View view){
        record.get(7).setText("");
        addressString = record.get(0).getText().toString()
                + "$" + record.get(1).getText().toString()
                + "$" + record.get(2).getText().toString()
                + "$" + record.get(3).getText().toString()
                + "$" + record.get(4).getText().toString()
                + "$" + record.get(5).getText().toString()
                + "$" + record.get(6).getText().toString()
                + "$" + record.get(7).getText().toString();
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getString(R.string.pref_fav_records), addressString);
        editor.apply();
        save();
    }

    public void search1(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(0).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }
    public void search2(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(1).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }
    public void search3(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(2).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }
    public void search4(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(3).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }
    public void search5(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(4).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }
    public void search6(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(5).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }
    public void search7(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(6).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }
    public void search8(View view){
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), record.get(7).getText().toString());
        setResult(RESULT_OK, goint);
        finish();
    }

    public void backToMap(View view){
        Intent backToMap = new Intent(this, HomeActivity.class);
        startActivity(backToMap);
    }
}
