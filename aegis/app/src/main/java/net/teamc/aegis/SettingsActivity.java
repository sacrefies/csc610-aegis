package net.teamc.aegis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void backToMapClick(View view){
        Intent backToMap = new Intent(this, HomeActivity.class);
        startActivity(backToMap);
    }
}
