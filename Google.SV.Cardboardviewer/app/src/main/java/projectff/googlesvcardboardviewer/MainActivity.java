package projectff.googlesvcardboardviewer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity implements View.OnKeyListener{

    /** Member variables **/
    GoogleMap m_googleMap;
    StreetViewPanorama m_StreetView;
    Fragment mapView;
    ActionBar action = null;
    RelativeLayout LocationLayout;
    Button GoButton;
    LatLng location;

    EditText LngFld;
    EditText LatFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMapView();
        createStreetView();
        LocationLayout = (RelativeLayout) findViewById(R.id.locationLayout);
        GoButton = (Button) findViewById(R.id.button);
        LngFld = (EditText) findViewById(R.id.lng);
        LatFld = (EditText) findViewById(R.id.lat);

        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        action = getActionBar();

        GoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double lng = Double.parseDouble(LngFld.getText().toString());
                double lat = Double.parseDouble(LatFld.getText().toString());

                if ( lng != 0 && lat != 0) {
                    location = new LatLng(lng, lat);
                }

                m_StreetView.setPosition(location);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(location)           // Sets the center of the map to Mountain View
                        .zoom(16)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                m_googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                LocationLayout = (RelativeLayout) findViewById(R.id.locationLayout);
                LocationLayout.setVisibility(View.GONE);
            }
        });

        LocationLayout.setVisibility(View.GONE);

        /**
         * Set up the onClickListener that will pass the selected lat/long
         * co-ordinates through to the Street View fragment for loading
         */
        m_googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                /**
                 * Ensure the street view has been initialise correctly and
                 * pass it through the selected lat/long co-ordinates.
                 */
                if (m_StreetView != null) {

                    /**
                     * Hide the map view to expose the street view.
                     */
                    mapView = getFragmentManager().findFragmentById(R.id.mapView);

//                    if (m_googleMap.getMapType()== GoogleMap.MAP_TYPE_HYBRID){
//                        m_googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);}
//                    else{
//                            m_googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                        }


                    m_googleMap.setBuildingsEnabled(true);

                    //getFragmentManager().beginTransaction().hide(mapView).commit();

                    /** Passed the tapped location through to the Street View **/
                    m_StreetView.setPosition(latLng);

                    Toast.makeText(getApplicationContext(),
                    latLng.toString(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    /**
     * Initialises the street view member variable with the appropriate
     * fragment from the FragmentManager
     */
    private void createStreetView() {
        m_StreetView = ((StreetViewPanoramaFragment)
                getFragmentManager().findFragmentById(R.id.streetView))
                .getStreetViewPanorama();

    }

    private Bitmap getStreetviewBitmap(String url){

        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Initialises the mapview
     */
    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(null == m_googleMap){
                m_googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();

//                m_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.928896, 11.584398), 15));
//                m_googleMap.animateCamera(CameraUpdateFactory.zoomIn());
//                m_googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 10, null);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(40.6895,-74.045))      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                m_googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                m_StreetView.setPosition(new LatLng(40.6895,-74.045));
                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == m_googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map",Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }

    /**
     * Adds a marker to the map
     */
    private void addMarker(){

        /** Make sure that the map has been initialised **/
        if(null != m_googleMap){
            m_googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Marker")
                            .draggable(true)
            );
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        Log.d("KeyCode", keyEvent.toString());
        switch(keyEvent.getKeyCode()){

           case KeyEvent.KEYCODE_BACK :
                {
                    getFragmentManager().beginTransaction().show(mapView).commit();
                return true;}

            default : {return true;}

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            LocationLayout.setVisibility(View.VISIBLE);
            return true;
        }
        if (id == R.id.action_normal) {
            m_googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }if (id == R.id.action_satellite) {
            m_googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        }if (id == R.id.action_terrain) {
            m_googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        }if (id == R.id.action_hybrid) {
            m_googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }if (id == R.id.action_cardboard) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
