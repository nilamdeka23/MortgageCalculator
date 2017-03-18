package nilam.project.com.mortgagecalculator.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import nilam.project.com.mortgagecalculator.R;

public class MortgageCalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage_calculator);

        Spinner spinnerUsStates = (Spinner) findViewById(R.id.spinner_us_states);
        Spinner spinnerPropertyType = (Spinner) findViewById(R.id.spinner_property_type);
        Spinner spinnerTerm = (Spinner) findViewById(R.id.spinner_term);
        // Create an ArrayAdapter using the string array and a default spinner activity_mortgage_calculator
        ArrayAdapter<CharSequence> adapterUsStates = ArrayAdapter.createFromResource(this,
                R.array.us_states, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterPropertyType = ArrayAdapter.createFromResource(this,
                R.array.property_type, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterTerm = ArrayAdapter.createFromResource(this,
                R.array.term, android.R.layout.simple_spinner_item);
        // Specify the activity_mortgage_calculator to use when the list of choices appears
        adapterUsStates.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapterPropertyType.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapterTerm.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // Apply the adapter to the spinner
        spinnerUsStates.setAdapter(adapterUsStates);
        spinnerPropertyType.setAdapter(adapterPropertyType);
        spinnerTerm.setAdapter(adapterTerm);

        spinnerUsStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerTerm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}
