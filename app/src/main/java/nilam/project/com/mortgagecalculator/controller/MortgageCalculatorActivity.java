package nilam.project.com.mortgagecalculator.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import nilam.project.com.mortgagecalculator.R;
import nilam.project.com.mortgagecalculator.model.Loan;
import nilam.project.com.mortgagecalculator.model.Property;
import nilam.project.com.mortgagecalculator.model.RecordDao;
import nilam.project.com.mortgagecalculator.utils.DatabaseHelper;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage_calculator);

        init();
    }

    private void init() {
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

                DatabaseHelper db = new DatabaseHelper(this);
                // insert record
                db.addRecord(new RecordDao(mEditTxtStrtAddress.getText().toString(),
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

}
