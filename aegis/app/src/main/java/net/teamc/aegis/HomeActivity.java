package net.teamc.aegis;

import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Polyline;
import com.jakewharton.threetenabp.AndroidThreeTen;
import net.teamc.aegis.mapservice.CrimeDataService;
import net.teamc.aegis.mapservice.CurrentLocation;
import net.teamc.aegis.mapservice.DirectionService;
import net.teamc.aegis.mapservice.MapMarker;
import net.teamc.aegis.model.ColorCode;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        AndroidThreeTen.init(this.getApplication());

        search = (TextView)findViewById(R.id.search_address);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchint = new Intent(HomeActivity.this, SearchingActivity.class);
                searchint.putExtra(getString(R.string.pref_HomeSearch_search), search.getText().toString());
                startActivityForResult(searchint, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK)
            return;

        String origText = search.getText().toString();
        String dest = data.getStringExtra(getString(R.string.pref_favSearchHome_Address)).trim();
        if (origText.equals(dest) || dest.equals(""))
            return;

        search.setText(data.getStringExtra(getString(R.string.pref_favSearchHome_Address)));
        mMap.clear();
        MapMarker markers = new MapMarker(mMap, this);
        markers.execute(dest);
    }


    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be used. This is where we
     * can add markers or lines, add listeners or move the camera. In this case, we just add a marker near Sydney,
     * Australia. If Google Play services is not installed on the device, the user will be prompted to install it inside
     * the SupportMapFragment. This method will only be triggered once the user has installed Google Play services and
     * returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //get locationManager object from system service
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        final CurrentLocation cl = new CurrentLocation(mMap, locationManager, getApplicationContext(), this);
        location = cl.getLocation();
        //show current location on google map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        //zoom in the google map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        cl.updatesLocation();

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                location = cl.getLocation();
                mMap.clear();

                //execute crime markers on map
                CrimeDataService crimes = new CrimeDataService(mMap, HomeActivity.this, true);
                crimes.execute(location);

                //show current location on google map
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                //zoom in the google map
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                return true;
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                int darkRed = Color.rgb(85, 0, 0);
                int orange = Color.rgb(255, 116, 0);
                int polyColor = polyline.getColor();

                String label = ColorCode.DARK_RED.getLabel();
                if (polyColor == Color.GREEN) label = ColorCode.GREEN.getLabel();
                else if (polyColor == Color.GRAY) label = ColorCode.GRAY.getLabel();
                else if (polyColor == Color.YELLOW) label = ColorCode.YELLOW.getLabel();
                else if (polyColor == orange) label = ColorCode.ORANGE.getLabel();
                else if (polyColor == Color.RED) label = ColorCode.RED.getLabel();

                Toast.makeText(HomeActivity.this, label, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onMenuButtonClick(View view) {
        PopupMenu popup = new PopupMenu(HomeActivity.this, view);
        popup.inflate(R.menu.menu_home_activity);
        popup.show();
    }

    public void menuItem_OnClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_favorites:
                Intent intent = new Intent(this, FavoritesActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

                break;
            case R.id.action_settings:
                startActivities(new Intent[]{
                        new Intent(this, PreferenceActivity.class)});
                break;
            case R.id.action_crime_data_download:
                break;
            default:
                Log.e(clazz, "Unknown menu item: " + item.getItemId());
                break;
        }
    }

    public void buttonGo_OnClick(View view) {
        String origText = search.getText().toString().trim();
        if (origText.equals("")) return;
        if (this.location == null) return;
        DirectionService ds = new DirectionService(mMap, this);
        ds.execute(location, destination);
    }

    public void setDestination(LatLng dest) {
        destination = dest;
    }

    private TextView search;
    private GoogleMap mMap;
    private LatLng location;
    private LatLng destination;
    private LocationManager locationManager;
    private static final int REQUEST_CODE = 100;
    private final String clazz = getClass().getSimpleName();

}
