package nilam.project.com.mortgagecalculator.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import nilam.project.com.mortgagecalculator.R;
import nilam.project.com.mortgagecalculator.model.RecordDao;
import nilam.project.com.mortgagecalculator.utils.DatabaseHelper;

/**
 * Created by nilamdeka on 3/19/17.
 */

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<RecordDao> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        fetchRecords();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // use default infoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // getting view from the xml layout file
                View v = getLayoutInflater().inflate(R.layout.custom_info_window_marker, null);

                for (RecordDao record : records) {
                    if (record.getId() != Integer.parseInt(marker.getTitle()))
                        continue;

                    // getting reference to the textViews
                    TextView textViewPropertyType = (TextView) v.findViewById(R.id.textview_property_type);
                    TextView textViewStreetAddress = (TextView) v.findViewById(R.id.textview_street_address);
                    TextView textViewCity = (TextView) v.findViewById(R.id.textview_city);
                    TextView textViewLoanAmount = (TextView) v.findViewById(R.id.textview_loan_amount);
                    TextView textViewApr = (TextView) v.findViewById(R.id.textview_apr);
                    TextView textViewMonthlyPayment = (TextView) v.findViewById(R.id.textview_monthly_payment);

                    // setting the values
                    textViewPropertyType.setText(String.format(getString(R.string.info_label_property_type),
                            record.getType()));
                    textViewStreetAddress.setText(String.format(getString(R.string.info_label_street_address),
                            record.getStreetAddress()));
                    textViewCity.setText(String.format(getString(R.string.info_label_city),
                            record.getCity()));
                    textViewLoanAmount.setText(String.format(getString(R.string.info_label_loan_amount),
                            record.getAmount()));
                    textViewApr.setText(String.format(getString(R.string.info_label_apr),
                            record.getApr()));
                    textViewMonthlyPayment.setText(record.getMonthlyPayment());
                    break;
                }

                // returning the view containing InfoWindow contents
                return v;

            }
        });

        drawMarkers();
    }

    private void deleteMarker(Marker marker) {
        for (RecordDao record : records) {
            if (record.getId() != Integer.parseInt(marker.getTitle()))
                continue;
            records.remove(record);
            break;
        }

        mMap.clear();

        drawMarkers();
    }

    // fetch all user records
    private void fetchRecords() {

        DatabaseHelper db = new DatabaseHelper(this);
        records = db.getAllRecords();
    }

    private void drawMarkers() {
        // looping through all records and adding them to the map as markers
        for (int i = 0; i < records.size(); i++) {
            RecordDao record = records.get(i);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(record.getLatitude(), record.getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .title(String.valueOf(record.getId()))
                    .icon(getIcon(record.getType())));

            // move map to the last added mortgage information
            if (i == records.size() - 1)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(record.getLatitude(),
                        record.getLongitude())));

        }
    }

    // returns map icon based on property type
    private BitmapDescriptor getIcon(String propertyType) {
        // set house as default icon
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.house);

        if (propertyType.equals("Townhouse")) {
            icon = BitmapDescriptorFactory.fromResource(R.mipmap.townhouse);
        } else if (propertyType.equals("Condo")) {
            icon = BitmapDescriptorFactory.fromResource(R.mipmap.condo);
        }

        return icon;
    }

}
