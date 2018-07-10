package com.pixzen.mortgagecalculatorapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    /** Called when the activity is first created. */
    // constants used when saving/restoring state

    private static final String LOAN_AMOUNT = "LOAN_AMOUNT";
    private static final String CUSTOM_LOAN_TERM = "CUSTOM_LOAN_DURATION";
    private static final String INTEREST_RATE = "INTEREST_RATE";
    private static final String DOWN_PAYMENT = "DOWN_PAYMENT";

    private double purchasePrice; // purchase price of home
    private EditText purchasepriceEditText; // accepts user input for the loan amount

    private double downPayment; // down payment entered by the user
    private EditText downpaymentEditText; // accepts user input for the down payment

    private double interestRate; // interest rate entered by the user
    private EditText interestrateEditText; // accepts user input for the rate

    private int currentLoanTerm = 10; // loan duration set with the SeekBar
    private TextView loanTermTextView; // text view for loan term

    private double currentMontlyPayment; // monthly payment
    private EditText monthlyPaymentEditText; // current monthly payment

    private static final NumberFormat currencyFormat =
           NumberFormat.getCurrencyInstance();

    private static final NumberFormat percentFormat =
            NumberFormat.getNumberInstance() .getPercentInstance();




    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); // call superclass's version
        setContentView(R.layout.activity_main); // inflate the GUI

        // check if app just started or is being restored from memory
        if ( savedInstanceState == null ) // the app just started running
        {
            purchasePrice = 0.00; // initialize the purchase price to 0
            downPayment = 0.00; // initialize the downPayment amount to 0
            interestRate = 0.00; // initialize the rate to 0.00%
            currentLoanTerm = 10; // initialize the loan term to 10
            currentMontlyPayment = 0.00; // initialized the mth payment to 0
        } // end if
        else // app is being restored from memory, not executed from scratch
        {
            // initialize the loan amount to saved amount
            purchasePrice = savedInstanceState.getDouble(LOAN_AMOUNT);

            // initialize the interest rate to saved rate
            interestRate = savedInstanceState.getInt(INTEREST_RATE);

            // initialize the down payment to saved down payment amount
            downPayment = savedInstanceState.getDouble(DOWN_PAYMENT);

            // initialize the loan term to saved term
            currentLoanTerm = savedInstanceState.getInt(CUSTOM_LOAN_TERM);
        }

        // get the interestrateEditText
        interestrateEditText = (EditText) findViewById(R.id.interestrateEditText);
        // set to default or saved value
        interestrateEditText.setText(percentFormat.format(interestRate));
        // interestrateEditTextWatcher handles interestrateEditText's onTextChanged event
        interestrateEditText.addTextChangedListener(interestRateEditTextWatcher);

        // get the purchasepriceEditText
        purchasepriceEditText = (EditText) findViewById(R.id.purchasePriceEditText);
        purchasepriceEditText.setText(currencyFormat.format(purchasePrice));
        // purchasepriceEditTextWatcher handles loanEditText's onTextChanged event
        purchasepriceEditText.addTextChangedListener(purchasePriceEditTextWatcher);

        // get the downpaymentEditText
        downpaymentEditText = (EditText) findViewById(R.id.downpaymentEditText);
        downpaymentEditText.setText(currencyFormat.format(downPayment));
        // downpaymentEditTextWatcher handles downpaymentEditText's onTextChanged event
        downpaymentEditText.addTextChangedListener(downpaymentEditTextWatcher);

        // get the monthly payment EditText
        monthlyPaymentEditText = (EditText) findViewById(R.id.monthlyPaymentEditText);

        // get the loan term TextView and set text to default of 10
        loanTermTextView = (TextView) findViewById(R.id.loandurationTextView);

        // get the SeekBar used to set the loan term
        SeekBar customSeekBar = (SeekBar) findViewById(R.id.termSeekBar);
        customSeekBar.setOnSeekBarChangeListener(customSeekBarListener);

    }

    // calculate monthly payment
    private double formula(double purchasePrice, double downPayment, double interestRate, double currentLoanTerm)
    {
        double loan = purchasePrice - downPayment;
        double c = interestRate/100/12.;
        double n = currentLoanTerm *12 ;
        return loan * (c * Math.pow(1 + c, n )) / ( Math.pow(1 + c,n)-1);
    }

    // updates monthly payment EditTexts
    private void updateMonthlyPayment()
    {
        // calculate monthly payment
        double monthlyPayment =
                formula(purchasePrice, downPayment, interestRate, currentLoanTerm);
        // set TextViews
        monthlyPaymentEditText.setText(currencyFormat.format(monthlyPayment));
        // TODO check to make sure the text displays correctly

    }

    // updates the loanTermTextView and monthly payment EditTexts
    private void updateLoanTerm(int currentLoanTerm)
    {
        // set loanTermTextView's text to match the position of the SeekBar
        loanTermTextView.setText(currentLoanTerm + " years");
        updateMonthlyPayment();

    }

    // save values of loanEditText and customSeekBar
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putDouble(LOAN_AMOUNT, purchasePrice);
        outState.putInt(CUSTOM_LOAN_TERM, currentLoanTerm);
    }

    // called when the user changes the position of SeekBar
    private OnSeekBarChangeListener customSeekBarListener =
            new OnSeekBarChangeListener()
            {
                // update currentLoanDuration, then call updateLoanDuration
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser)
                {
                    try {
                        // get currentLoanDuration from the position of the SeekBar's thumb
                        currentLoanTerm = seekBar.getProgress();
                        updateLoanTerm(currentLoanTerm);
                        updateMonthlyPayment(); // update monthlyPayment EditTexts

                    }catch (Exception e){
                        System.out.print(e.getLocalizedMessage());
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar)
                {}

                public void onStopTrackingTouch(SeekBar seekBar)
                {}
            };

    // event-handling object that responds to loanEditText's events
    private TextWatcher purchasePriceEditTextWatcher = new TextWatcher()
    {
        // called when the user enters a number

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            // convert purchasepriceEditText's text to a double
            try
            {
                String purchasePriceToString = s.toString().replace("$", "");
                purchasePrice = Double.parseDouble(purchasePriceToString);
            }
            catch (NumberFormatException e)
            {
                purchasePrice = 0.0; // default if an exception occurs
                // better error handling could be added
            }

            // update the Monthly Payment
            updateMonthlyPayment(); // update the monthly payment EditTexts
        }

        public void afterTextChanged(Editable s)
        {}

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after)
        {}
    };

    // event-handling object that responds to loanEditText's events
    private TextWatcher downpaymentEditTextWatcher = new TextWatcher()
    {
        // called when the user enters a number

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            // convert loanEditText's text to a double
            try
            {
                String downPaymentToString = s.toString().replace("$", "");
                downPayment = Double.parseDouble(downPaymentToString);
            }
            catch (NumberFormatException e)
            {
                downPayment = 0.0; // default if an exception occurs
            }

            // update the Monthly Payment
            updateMonthlyPayment(); // update the monthly payment EditTexts
        }

        public void afterTextChanged(Editable s)
        {}

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after)
        {}
    };

    // event-handling object that responds to loanEditText's events
    private TextWatcher interestRateEditTextWatcher = new TextWatcher()
    {
        // called when the user enters a number

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            // convert loanEditText's text to a double
            try
            {
                String interestRateToString = s.toString().replace("%", "");
                interestRate = Double.parseDouble(interestRateToString);
            }
            catch (NumberFormatException e)
            {
                interestRate = 0.0; // default if an exception occurs
            }

            // update the Monthly Payment
            updateMonthlyPayment(); // update the monthly payment EditTexts
        }

        public void afterTextChanged(Editable s)
        {}

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after)
        {}
    };
}
