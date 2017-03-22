package nilam.project.com.mortgagecalculator.controller;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

import nilam.project.com.mortgagecalculator.R;
import nilam.project.com.mortgagecalculator.model.Loan;
import nilam.project.com.mortgagecalculator.model.Property;
import nilam.project.com.mortgagecalculator.model.RecordDao;
import nilam.project.com.mortgagecalculator.utils.DatabaseHelper;

import static nilam.project.com.mortgagecalculator.controller.GoogleMapsActivity.ID;

public class MortgageCalculatorActivity extends AppCompatActivity {

    private static final String NOT_SELECTED = "ns";

    private EditText editTxtStrtAddress;
    private EditText editTxtCity;
    private EditText editTxtZipcode;
    private EditText editTxtLoanAmount;
    private EditText editTxtDownPayment;
    private EditText editTxtApr;

    private Spinner spinnerUsStates;
    private Spinner spinnerPropertyType;
    private Spinner spinnerTerm;

    private String state = NOT_SELECTED;
    private String propertyType = NOT_SELECTED;
    private String term = NOT_SELECTED;

    private CardView cardViewMortgageCal;
    private TextView textViewPayment;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String activityTitle;

    private Button btnSave;

    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage_calculator);

        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        id = intent.getIntExtra(ID, -1);
        btnSave.setText(getString(R.string.save));

        if (id >= 0) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);

            RecordDao record = dbHelper.getRecord(id);

            editTxtStrtAddress.setText(record.getStreetAddress());
            editTxtCity.setText(record.getCity());
            editTxtZipcode.setText(record.getZipcode());

            editTxtLoanAmount.setText(record.getAmount());
            editTxtDownPayment.setText(record.getDownPayment());
            editTxtApr.setText(record.getApr());

            state = record.getState();
            term = record.getTerm();
            propertyType = record.getType();

            List<String> states = Arrays.asList(getResources().getStringArray(R.array.us_states));
            List<String> terms = Arrays.asList(getResources().getStringArray(R.array.term));
            List<String> propertyTypes = Arrays.asList(getResources().getStringArray(R.array.property_type));

            if (terms.indexOf(term) >= 0)
                spinnerTerm.setSelection(terms.indexOf(term));

            if (states.indexOf(state) >= 0)
                spinnerUsStates.setSelection(states.indexOf(state));

            if (propertyTypes.indexOf(propertyType) >= 0)
                spinnerPropertyType.setSelection(propertyTypes.indexOf(propertyType));

            spinnerTerm.clearFocus();
            spinnerUsStates.clearFocus();
            spinnerPropertyType.clearFocus();

            // set save button text as update
            btnSave.setText(getString(R.string.update));
        }
        // close the drawer
        drawerLayout.closeDrawer(Gravity.START, true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void init() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        activityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTxtStrtAddress = (EditText) findViewById(R.id.edittext_street_address);
        editTxtCity = (EditText) findViewById(R.id.edittext_city);
        editTxtZipcode = (EditText) findViewById(R.id.edittext_zipcode);

        editTxtLoanAmount = (EditText) findViewById(R.id.edittext_loan_amount);
        editTxtDownPayment = (EditText) findViewById(R.id.edittext_down_payment);
        editTxtApr = (EditText) findViewById(R.id.edittext_apr);

        cardViewMortgageCal = (CardView) findViewById(R.id.cardview_mortgage_cal);
        textViewPayment = (TextView) findViewById(R.id.textview_payment);

        btnSave = (Button) findViewById(R.id.btnSave);

        spinnerUsStates = (Spinner) findViewById(R.id.spinner_us_states);
        spinnerPropertyType = (Spinner) findViewById(R.id.spinner_property_type);
        spinnerTerm = (Spinner) findViewById(R.id.spinner_term);

        setupSpinners();
    }

    /*
     * Button onClick listeners
     */
    public void newCalBtnClkListener(View view) {
        clearForm();
    }

    public void calculateBtnClkListener(View view) {

        if (isLoanInfoValid()) {
            Loan loan = new Loan(editTxtLoanAmount.getText().toString(),
                    editTxtDownPayment.getText().toString(), editTxtApr.getText().toString(), term);

            textViewPayment.setText(String.format(getString(R.string.monthly_payment), loan.calculateMonthlyPayment()));
            // render the view visible
            showMortgageCalculator(true);
            // scroll to top
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview_master);
            scrollView.scrollTo(0, 0);
        } else {

            Toast.makeText(this, R.string.missing_loan_info, Toast.LENGTH_SHORT).show();
        }

    }

    public void saveBtnClkListener(View view) {

        if (isPropertyInfoValid()) {
            Property property = new Property(editTxtStrtAddress.getText().toString(),
                    editTxtCity.getText().toString(), state, editTxtZipcode.getText().toString());
            LatLng latLng = property.getLatLngFromPropertyAddress(this);

            if (latLng != null) {
                String monthlyPayment = "";
                if (!textViewPayment.equals(R.string.monthly_payment)) {
                    monthlyPayment = textViewPayment.getText().toString();
                }

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                RecordDao record = new RecordDao(editTxtStrtAddress.getText().toString(),
                        editTxtCity.getText().toString(), state, editTxtZipcode.getText().toString(),
                        propertyType, editTxtLoanAmount.getText().toString(),
                        editTxtDownPayment.getText().toString(), editTxtApr.getText().toString(),
                        term, latLng.latitude, latLng.longitude, monthlyPayment);

                if (id >= 0) {
                    record.setId(id);
                    // update record
                    dbHelper.updateRecord(record);

                    Toast.makeText(this, R.string.updating, Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, R.string.saving, Toast.LENGTH_SHORT).show();
                    // insert record
                    dbHelper.addRecord(record);
                }

            } else {
                Toast.makeText(this, R.string.invalid_address, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, R.string.missing_property_info, Toast.LENGTH_SHORT).show();

        }
    }

    /*
     * Util Methods
     */
    private boolean isPropertyInfoValid() {
        boolean flag = true;

        if (editTxtStrtAddress.getText().toString().length() <= 0 ||
                editTxtCity.getText().toString().length() <= 0 ||
                editTxtZipcode.getText().toString().length() <= 0 ||
                state.equals(NOT_SELECTED) ||
                propertyType.equals(NOT_SELECTED))
            flag = false;

        return flag;
    }

    private boolean isLoanInfoValid() {
        boolean flag = true;

        if (editTxtLoanAmount.getText().toString().length() <= 0 ||
                editTxtApr.getText().toString().length() <= 0 ||
                editTxtDownPayment.getText().toString().length() <= 0 ||
                term.equals(NOT_SELECTED))
            flag = false;

        return flag;
    }

    private void showMortgageCalculator(boolean flag) {
        if (flag)
            cardViewMortgageCal.setVisibility(View.VISIBLE);
        else
            cardViewMortgageCal.setVisibility(View.GONE);
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(activityTitle);
                invalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void addDrawerItems() {

        RelativeLayout mapActionLayout = (RelativeLayout) findViewById(R.id.layout_map_action);
        mapActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(MortgageCalculatorActivity.this, GoogleMapsActivity.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private void setupSpinners() {
        // set adapter to the spinner
        spinnerUsStates.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.us_states, android.R.layout.simple_spinner_item));
        spinnerPropertyType.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.property_type, android.R.layout.simple_spinner_item));
        spinnerTerm.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.term, android.R.layout.simple_spinner_item));

        spinnerUsStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                state = NOT_SELECTED;
            }
        });

        spinnerPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                propertyType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                propertyType = NOT_SELECTED;
            }
        });


        spinnerTerm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                term = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                term = NOT_SELECTED;
            }
        });

    }

    private void clearForm() {
        showMortgageCalculator(false);

        editTxtStrtAddress.getText().clear();
        editTxtCity.getText().clear();
        editTxtZipcode.getText().clear();

        editTxtLoanAmount.getText().clear();
        editTxtDownPayment.getText().clear();
        editTxtApr.getText().clear();

        spinnerTerm.setSelection(0);
        spinnerTerm.clearFocus();
        spinnerUsStates.setSelection(0);
        spinnerUsStates.clearFocus();
        spinnerPropertyType.setSelection(0);
        spinnerPropertyType.clearFocus();

        state = NOT_SELECTED;
        term = NOT_SELECTED;
        propertyType = NOT_SELECTED;
    }

}
