package net.teamc.aegis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchingActivity extends Activity {


    private EditText address;
    private ArrayList<String> addressList = new ArrayList<>(1);
    private ArrayList<TextView> hisAddress = new ArrayList<>();
    private Button save1;
    private Button save2;
    private Button save3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        address = (EditText) findViewById(R.id.search_address);
        try {
            hisAddress.add(0, (TextView) findViewById(R.id.history1));
            hisAddress.add(1, (TextView) findViewById(R.id.history2));
            hisAddress.add(2, (TextView) findViewById(R.id.history3));
        }
        catch (IndexOutOfBoundsException e){}

        save1 = (Button) findViewById(R.id.save1);
        save2 = (Button) findViewById(R.id.save2);
        save3 = (Button) findViewById(R.id.save3);

        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        try {
            String addressString = sp.getString(getString(R.string.pref_search_hisAddress), "");
            String[] addressParts = addressString.split("\\$");
            for (int i = 0; i < addressParts.length; i++){
                hisAddress.get(i).setText(addressParts[i]);

            }
        }
        catch (IndexOutOfBoundsException e){}



        addressList.add(0,address.getText().toString());

        Intent intent = getIntent();
        String previousAddress = intent.getStringExtra(getString(R.string.pref_HomeSearch_search));
        address.setText(previousAddress);
    }

    public void historySearch1(View view){
        Intent historySearch1 = new Intent(this, HomeActivity.class);
        historySearch1.putExtra(getString(R.string.pref_favSearchHome_Address), hisAddress.get(0).getText().toString());
        setResult(RESULT_OK, historySearch1);
        finish();
    }
    public void historySearch2(View view){
        Intent historySearch1 = new Intent(this, HomeActivity.class);
        historySearch1.putExtra(getString(R.string.pref_favSearchHome_Address), hisAddress.get(1).getText().toString());
        setResult(RESULT_OK, historySearch1);
        finish();
    }
    public void historySearch3(View view){
        Intent historySearch1 = new Intent(this, HomeActivity.class);
        historySearch1.putExtra(getString(R.string.pref_favSearchHome_Address), hisAddress.get(2).getText().toString());
        setResult(RESULT_OK, historySearch1);
        finish();
    }
    private boolean checkDuplicate (String s){
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        String string = sp.getString(getString(R.string.pref_fav_records), "");
        try {
            String[] addressParts = string.split("\\$");
            for (int i = 0; i < addressParts.length; i++) {
                if (addressParts[i].equalsIgnoreCase(s))
                    return true;
            }
        }catch (NullPointerException e){}
        return false;
    }

    public void save1(View view){
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String addressFromPreference = sp.getString(getString(R.string.pref_fav_records), "");
        if (!checkDuplicate(hisAddress.get(0).getText().toString())) {
            editor.putString(getString(R.string.pref_fav_records), hisAddress.get(0).getText().toString() + "$" + addressFromPreference);
        }
        editor.apply();
    }
    public void save2(View view){
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String addressFromPreference = sp.getString(getString(R.string.pref_fav_records), "");
        if (!checkDuplicate(hisAddress.get(1).getText().toString())) {
            editor.putString(getString(R.string.pref_fav_records), hisAddress.get(1).getText().toString() + "$" + addressFromPreference);
        }
        editor.apply();
    }
    public void save3(View view){
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String addressFromPreference = sp.getString(getString(R.string.pref_fav_records), "");
        if (!checkDuplicate(hisAddress.get(2).getText().toString())) {
            editor.putString(getString(R.string.pref_fav_records), hisAddress.get(2).getText().toString() + "$" + addressFromPreference);
        }
        editor.apply();
    }

    public void backToMapClick(View view) {
        Intent backToMap = new Intent(this, HomeActivity.class);
        backToMap.putExtra(getString(R.string.pref_favSearchHome_Address), addressList.get(0));
        setResult(RESULT_OK, backToMap);
        finish();
    }

    public void goClick(View view) {
        Intent goint = new Intent(this, HomeActivity.class);
        goint.putExtra(getString(R.string.pref_favSearchHome_Address), address.getText().toString());
        setResult(RESULT_OK, goint);
        finish();
        addressList.add(0,address.getText().toString());
        saveAddressToSharedPreferences();
    }

    private void saveAddressToSharedPreferences() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String addressFromPreference = sp.getString(getString(R.string.pref_search_hisAddress), "");
        editor.putString(getString(R.string.pref_search_hisAddress),  address.getText().toString() + "$" + addressFromPreference );
        editor.apply();
    }


}
