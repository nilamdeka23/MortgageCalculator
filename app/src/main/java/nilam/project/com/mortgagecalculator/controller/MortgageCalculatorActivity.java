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
import android.widget.EditText;
import android.widget.RelativeLayout;
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

    private EditText mEditTxtStrtAddress;
    private EditText mEditTxtCity;
    private EditText mEditTxtZipcode;
    private EditText mEditTxtLoanAmount;
    private EditText mEditTxtDownPayment;
    private EditText mEditTxtApr;

    private Spinner spinnerUsStates;
    private Spinner spinnerPropertyType;
    private Spinner spinnerTerm;

    private String state = NOT_SELECTED;
    private String propertyType = NOT_SELECTED;
    private String term = NOT_SELECTED;

    private CardView mCardViewMortgageCal;
    private TextView mTextViewPayment;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

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

        if (id >= 0) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);

            RecordDao record = dbHelper.getRecord(id);

            mEditTxtStrtAddress.setText(record.getStreetAddress());
            mEditTxtCity.setText(record.getCity());
            mEditTxtZipcode.setText(record.getZipcode());

            mEditTxtLoanAmount.setText(record.getAmount());
            mEditTxtDownPayment.setText(record.getDownPayment());
            mEditTxtApr.setText(record.getApr());

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
        }
        // close the drawer
        mDrawerLayout.closeDrawer(Gravity.START, true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void init() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTxtStrtAddress = (EditText) findViewById(R.id.edittext_street_address);
        mEditTxtCity = (EditText) findViewById(R.id.edittext_city);
        mEditTxtZipcode = (EditText) findViewById(R.id.edittext_zipcode);

        mEditTxtLoanAmount = (EditText) findViewById(R.id.edittext_loan_amount);
        mEditTxtDownPayment = (EditText) findViewById(R.id.edittext_down_payment);
        mEditTxtApr = (EditText) findViewById(R.id.edittext_apr);

        mCardViewMortgageCal = (CardView) findViewById(R.id.cardview_mortgage_cal);
        mTextViewPayment = (TextView) findViewById(R.id.textview_payment);

        spinnerUsStates = (Spinner) findViewById(R.id.spinner_us_states);
        spinnerPropertyType = (Spinner) findViewById(R.id.spinner_property_type);
        spinnerTerm = (Spinner) findViewById(R.id.spinner_term);

        setupSpinners();
    }

    /*
     * Button onClick listeners
     */
    public void newCalBtnClkListener(View view) {
        showMortgageCalculator(false);

        mEditTxtStrtAddress.getText().clear();
        mEditTxtCity.getText().clear();
        mEditTxtZipcode.getText().clear();

        mEditTxtLoanAmount.getText().clear();
        mEditTxtDownPayment.getText().clear();
        mEditTxtApr.getText().clear();

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

    public void calculateBtnClkListener(View view) {

        if (isLoanInfoValid()) {
            Loan loan = new Loan(mEditTxtLoanAmount.getText().toString(),
                    mEditTxtDownPayment.getText().toString(), mEditTxtApr.getText().toString(), term);

            mTextViewPayment.setText(String.format(getString(R.string.monthly_payment), loan.calculateMonthlyPayment()));
            // render the view visible
            showMortgageCalculator(true);
        } else {

            Toast.makeText(this, R.string.missing_loan_info, Toast.LENGTH_SHORT).show();
        }

    }

    public void saveBtnClkListener(View view) {

        if (isPropertyInfoValid()) {
            Property property = new Property(mEditTxtStrtAddress.getText().toString(),
                    mEditTxtCity.getText().toString(), state, mEditTxtZipcode.getText().toString());
            LatLng latLng = property.getLatLngFromPropertyAddress(this);

            if (latLng != null) {
                String monthlyPayment = "";
                if (!mTextViewPayment.equals(R.string.monthly_payment)) {
                    monthlyPayment = mTextViewPayment.getText().toString();
                }

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                // insert record
                dbHelper.addRecord(new RecordDao(mEditTxtStrtAddress.getText().toString(),
                        mEditTxtCity.getText().toString(), state, mEditTxtZipcode.getText().toString(),
                        propertyType, mEditTxtLoanAmount.getText().toString(),
                        mEditTxtDownPayment.getText().toString(), mEditTxtApr.getText().toString(),
                        term, latLng.latitude, latLng.longitude, monthlyPayment));

                Toast.makeText(this, R.string.saving, Toast.LENGTH_SHORT).show();

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

        if (mEditTxtStrtAddress.getText().toString().length() <= 0 ||
                mEditTxtCity.getText().toString().length() <= 0 ||
                mEditTxtZipcode.getText().toString().length() <= 0 ||
                state.equals(NOT_SELECTED) ||
                propertyType.equals(NOT_SELECTED))
            flag = false;

        return flag;
    }

    private boolean isLoanInfoValid() {
        boolean flag = true;

        if (mEditTxtLoanAmount.getText().toString().length() <= 0 ||
                mEditTxtApr.getText().toString().length() <= 0 ||
                mEditTxtDownPayment.getText().toString().length() <= 0 ||
                term.equals(NOT_SELECTED))
            flag = false;

        return flag;
    }

    private void showMortgageCalculator(boolean flag) {
        if (flag)
            mCardViewMortgageCal.setVisibility(View.VISIBLE);
        else
            mCardViewMortgageCal.setVisibility(View.GONE);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
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

}
