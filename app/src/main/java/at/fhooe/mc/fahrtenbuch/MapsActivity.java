package at.fhooe.mc.fahrtenbuch;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Innsbruck and move the camera

        LatLng innsbruck = new LatLng(	47.259659, 11.400375);
        mMap.addMarker(new MarkerOptions().position(innsbruck).title("Capital of the alps!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(innsbruck));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(47.273466, 11.241875), 2));
        
        mMap.addPolyline(new PolylineOptions().geodesic(true)
                        .add(new LatLng(47.273466, 11.241875))
                        .add(new LatLng(47.270464, 11.256268))
                        .add(new LatLng(47.265146, 11.274732))
                        .add(new LatLng(47.263839, 11.315825))
                        .add(new LatLng(47.254158, 11.359128))
                        .add(new LatLng(47.256115, 11.381890))
                        .add(new LatLng(47.262558, 11.384723))
        );
    }
}
