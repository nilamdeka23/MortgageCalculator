package nilam.project.com.mortgagecalculator.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.List;

import nilam.project.com.mortgagecalculator.R;
import nilam.project.com.mortgagecalculator.model.RecordDao;
import nilam.project.com.mortgagecalculator.utils.DatabaseHelper;
import nilam.project.com.mortgagecalculator.view.OnInfoWindowElemTouchListener;
import nilam.project.com.mortgagecalculator.view.MapWrapperLayout;


public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String ID = "id";

    // custom marker height
    private static final int MARKER_HEIGHT = 37;
    // offset between the default InfoWindow bottom edge and it's content bottom edge
    private static final int OFFSET = 10;

    private static final String TOWNHOUSE = "Townhouse";
    private static final String CONDO = "Condo";

    private GoogleMap googleMap;
    private List<RecordDao> records;
    private DatabaseHelper dbHelper;
    private HashMap<Marker, RecordDao> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        dbHelper = new DatabaseHelper(this);

        fetchRecords();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        fetchRecords();
//        refreshMap();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_wrapper_layout);

        mapWrapperLayout.init(this.googleMap, MARKER_HEIGHT + getPixelsFromDp(OFFSET));

        // to reuse the info window for all the markers, create only one class member instance
        final ViewGroup infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_info_window_marker, null);

        // getting reference to its child views
        final TextView textViewPropertyType = (TextView) infoWindow.findViewById(R.id.textview_property_type);
        final TextView textViewStreetAddress = (TextView) infoWindow.findViewById(R.id.textview_street_address);
        final TextView textViewCity = (TextView) infoWindow.findViewById(R.id.textview_city);
        final TextView textViewLoanAmount = (TextView) infoWindow.findViewById(R.id.textview_loan_amount);
        final TextView textViewApr = (TextView) infoWindow.findViewById(R.id.textview_apr);
        final TextView textViewMonthlyPayment = (TextView) infoWindow.findViewById(R.id.textview_monthly_payment);

        Button buttonEdit = (Button) infoWindow.findViewById(R.id.btnEdit);
        Button buttonDelete = (Button) infoWindow.findViewById(R.id.btnDelete);

        final OnInfoWindowElemTouchListener editBtnClickListener = new OnInfoWindowElemTouchListener(buttonEdit) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent intent = new Intent(GoogleMapsActivity.this, MortgageCalculatorActivity.class);
                intent.putExtra(ID, hashMap.get(marker).getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };

        buttonEdit.setOnTouchListener(editBtnClickListener);

        final OnInfoWindowElemTouchListener deleteBtnClickListener = new OnInfoWindowElemTouchListener(buttonEdit) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                deleteMarker(marker);
            }
        };

        buttonDelete.setOnTouchListener(deleteBtnClickListener);

        this.googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            // Setting up the infoWindow with current's marker info
            @Override
            public View getInfoContents(Marker marker) {

                RecordDao record = hashMap.get(marker);

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

                editBtnClickListener.setMarker(marker);
                deleteBtnClickListener.setMarker(marker);

                // we must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        drawMarkers();

    }

    private void deleteMarker(Marker marker) {
        // remove marker from local data structure
        RecordDao record = hashMap.get(marker);
        records.remove(record);

        // remove record from database
//        dbHelper.deleteRecord(record);

        refreshMap();
    }

    // redraw map markers
    private void refreshMap() {
        // clear map
        googleMap.clear();
        // clear local hashMap
        hashMap.clear();
        drawMarkers();
    }

    // fetch all records
    private void fetchRecords() {
        records = dbHelper.getAllRecords();
    }

    private void drawMarkers() {
        // looping through all records and adding them to the map as markers
        for (int i = 0; i < records.size(); i++) {
            RecordDao record = records.get(i);

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(record.getLatitude(), record.getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .title(String.valueOf(record.getId()))
                    .icon(getIcon(record.getType())));

            if (hashMap == null) {
                hashMap = new HashMap<>();
            }
            hashMap.put(marker, record);

            // move map to the last added mortgage information
            if (i == records.size() - 1)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(record.getLatitude(),
                        record.getLongitude()), 11.0f));


        }
    }

    // returns map icon based on property type
    private BitmapDescriptor getIcon(String propertyType) {
        // set house as default icon
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.house);

        if (propertyType.equals(TOWNHOUSE)) {
            icon = BitmapDescriptorFactory.fromResource(R.mipmap.townhouse);
        } else if (propertyType.equals(CONDO)) {
            icon = BitmapDescriptorFactory.fromResource(R.mipmap.condo);
        }

        return icon;
    }

    private int getPixelsFromDp(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
