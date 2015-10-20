package com.murach.tipcalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Schedule extends Activity
implements OnEditorActionListener {

    private TableLayout tlSchedule;
    private TableRow displayRow;
    private DecimalFormat df;
    double currentBal;
    int yearsToRepay;
    double contribToPrincipal;
    int numberOfPayments, tempPayments;
    double montlyPayment;
    double montlyInterestRate;

    int paymentNum;
    TextView scMonthlyPaymentAmount;
    EditText addedMonthlyPayment;
    NumberFormat currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity);

        Intent i = getIntent();
        tlSchedule = (TableLayout) findViewById(R.id.tlschedule);

        Mortgage mor = (Mortgage) i.getSerializableExtra("MortgageCalculator");

        currentBal = mor.loanAmount;
        yearsToRepay = mor.totalYearsToRepay;
        contribToPrincipal = mor.totalPayBack();
        numberOfPayments = mor.numberOfPayments;
        montlyPayment = mor.monthlyPayment();
        montlyInterestRate = mor.monthlyInterestRate();
        paymentNum = 1;

        currency = NumberFormat.getCurrencyInstance();
        currency.setMaximumFractionDigits(2);
        currency.setMinimumFractionDigits(2);

        scMonthlyPaymentAmount = (TextView) findViewById(R.id.schMonthlyPaymentAmount);
        addedMonthlyPayment = (EditText) findViewById(R.id.addedMonthlyPayment);

        scMonthlyPaymentAmount.setText(currency.format(montlyPayment));

        addedMonthlyPayment.setOnEditorActionListener(this);

        df = new DecimalFormat("#.00");
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        displayTable();
    }

    protected void displayTable(){
        double tempContrib, tempBalance, interest, tempMonPay;

        tempContrib = contribToPrincipal;
        tempPayments = numberOfPayments;
        paymentNum = 1;
        tempBalance = currentBal;
        tempMonPay = montlyPayment;
        //interest = montlyInterestRate*currentBal;

        while (tempPayments > 0 && tempBalance > 0)
        {
            interest = montlyInterestRate*tempBalance;
            tempContrib = tempMonPay - interest;

            tempBalance -= tempContrib;

            if(tempBalance < 0)
            {
                tempMonPay += tempBalance;
                tempContrib += tempBalance;
                tempBalance = 0;
            }

            TextView tvPmtNo = new TextView(this);
            TextView tvPmtAmt = new TextView(this);
            TextView tvInterest = new TextView(this);
            TextView tvContribToPrin = new TextView(this);
            TextView tvCurrBal = new TextView(this);

            displayRow = new TableRow(this);

            tvPmtNo.setText(String.valueOf(paymentNum));
            tvPmtAmt.setText(df.format(tempMonPay));
            tvInterest.setText(df.format(interest));
            tvContribToPrin.setText(df.format(tempContrib));
            tvCurrBal.setText(df.format(tempBalance));

            displayRow.addView(tvPmtNo);
            displayRow.addView(tvPmtAmt);
            displayRow.addView(tvInterest);
            displayRow.addView(tvContribToPrin);
            displayRow.addView(tvCurrBal);

            tlSchedule.addView(displayRow);

            paymentNum++;
            tempPayments--;
        }
    }
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        String addedMonthlyPayString = "";

        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
        {
            addedMonthlyPayString = addedMonthlyPayment.getText().toString();

            if (addedMonthlyPayString.equals(""))
                addedMonthlyPayment.setText("0.00");
            else {
                double addedPay = Double.parseDouble(addedMonthlyPayString);

                if (addedPay + montlyPayment > currentBal)
                    addedPay = currentBal - montlyPayment;

                montlyPayment += addedPay;

                tlSchedule.removeViews(1, paymentNum-1);
                displayTable();
                montlyPayment -= addedPay;
            }
        }
        return false;
     }
}
