package nilam.project.com.mortgagecalculator.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Property {

    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;

    public Property(String streetAddress, String city, String state, String zipcode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public LatLng getLatLngFromPropertyAddress(Context context) {
        LatLng latLng = null;

        // TODO: test case "731 Market St, San Francisco, CA 94103"
        StringBuilder sbAddress = new StringBuilder(streetAddress).append(", ");
        sbAddress.append(city).append(", ");
        sbAddress.append(state).append(", ");
        sbAddress.append(zipcode);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List addressList = geocoder.getFromLocationName(sbAddress.toString(), 5);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);

                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e("Error", "Unable to connect to Geocoder", e);
        }

        return latLng;
    }

}