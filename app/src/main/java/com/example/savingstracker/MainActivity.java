package com.example.savingstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

////////////////////////////// EACH VALUE HAS A FLOAT //////////////////////////////
    public static float goalFloat;
    public static float bankFloat;
    public static float savingsFloat;
    public static float cashFloat;
    public static float totalFloat; // true total
    public static float displayFloat; // total - cash (for display purposes)
////////////////////////////// EACH VALUE HAS A STRING //////////////////////////////
    public static String goalString;
    public static String bankString;
    public static String savingsString;
    public static String cashString;
    public static String displayString;
    public static String totalString;
///////////////////////////// EACH VALUE HAS A TEXTVIEW /////////////////////////////
    public TextView tVbank;
    public TextView tVsavings;
    public TextView tVdisplayTotal;
    public TextView tVcash;
    public TextView tVtotal;
    public TextView tVgoal;
    public TextView tVgoalAmount;
    public TextView tVgoalReached;
//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// GETTERS FOR THE TOTALS ///////////////////////////////

    float getTotalFloat () {
        return bankFloat + savingsFloat + cashFloat;}

    float getDisplayFloat () {
        return getTotalFloat() - cashFloat;}


//////// THESE UPDATE THE STRINGS EVERY TIME THE VALUE OF THE FLOATS CHANGE ////////

    void updateBankString() {
        bankString = Float.toString(bankFloat);
        if (bankString.contains(".")) {
            int dec = bankString.indexOf(".");
            String sub = bankString.substring(0, dec);
            String tempSub = bankString.substring(dec);
            String sub2;
            if (tempSub.length() > 2) sub2 = bankString.substring(dec, dec + 3);
            else sub2 = tempSub;
            bankString = sub + sub2;}
        tVbank.setText(bankString);}

    void updateSavingsString() {
        savingsString = Float.toString(savingsFloat);
        if (savingsString.contains(".")) {
            int dec = savingsString.indexOf(".");
            String sub = savingsString.substring(0, dec);
            String tempSub = savingsString.substring(dec);
            String sub2;
            if (tempSub.length() > 2) sub2 = savingsString.substring(dec, dec + 3);
            else sub2 = tempSub;
            savingsString = sub + sub2;}
        tVsavings.setText(savingsString);}

    void updateCashString() {
        cashString = Float.toString(cashFloat);
        if (cashString.contains(".")) {
            int dec = cashString.indexOf(".");
            String sub = cashString.substring(0, dec);
            String tempSub = cashString.substring(dec);
            String sub2;
            if (tempSub.length() > 2) sub2 = cashString.substring(dec, dec + 3);
            else sub2 = tempSub;
            cashString = sub + sub2;}
        if (totalFloat >= goalFloat) tVcash.setText(cashString);}

    void updateTotalString() {
        totalFloat = getTotalFloat();
        updateDisplayString();
        totalString = Float.toString(totalFloat);
        if (totalString.contains(".")) {
            int dec = totalString.indexOf(".");
            String sub = totalString.substring(0, dec);
            String tempSub = totalString.substring(dec);
            String sub2;
            if (tempSub.length() > 2) sub2 = totalString.substring(dec, dec + 3);
            else sub2 = tempSub;
            totalString = sub + sub2;}
        if (totalFloat >= goalFloat) {
            tVtotal.setText(totalString);
            whenReachedGoal();}}

    void updateDisplayString() {
        displayFloat = getDisplayFloat();
        displayString = Float.toString(displayFloat);
        if (displayString.contains(".")) {
            int dec = displayString.indexOf(".");
            String sub = displayString.substring(0, dec);
            String tempSub = displayString.substring(dec);
            String sub2;
            if (tempSub.length() > 2) sub2 = displayString.substring(dec, dec + 3);
            else sub2 = tempSub;
            displayString = sub + sub2;}
        if (totalFloat < goalFloat) tVdisplayTotal.setText(displayString);}

///////////////////////////////////////////////////////////////////////////////////

        void whenReachedGoal(){
        // TODO: figure out what happens when we reach our goal
            // new text: "you have reach your goal"
            tVgoal.setVisibility(View.INVISIBLE);
            tVgoalAmount.setVisibility(View.INVISIBLE);
            tVgoalReached.setVisibility(View.VISIBLE);
            updateCashString();

            /** Ask the user if they want to restart the savings or continue from where they are.
             *  If continue: nothing happens.
             *  If restart:
             *      > could restart from 0 and input new savings goal
             *      > could restart from a set amount of $ and input new savings goal
             */

        }



    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tVbank = findViewById(R.id.bankAmountTV);
        tVsavings = findViewById(R.id.savingsAmountTV);
        tVdisplayTotal = findViewById(R.id.totalAmountTV);
        tVcash = findViewById(R.id.cashAmountTV);
        tVtotal = findViewById(R.id.totalAmountTV);
        tVgoal = findViewById(R.id.goalTV);
        tVgoalAmount = findViewById(R.id.goalAmountTV);
        tVgoalReached = findViewById(R.id.goalReachedTV);

        tVcash.setText("???");

        // TODO create some sort of boolean that gets checked onCreate to see if goal reached
        // not needed ASAP

        goalFloat = 20000;
        goalString = getString(R.string.goalAmount); // "$20,000"

        if (getTotalFloat() < goalFloat) tVgoalReached.setVisibility(View.INVISIBLE);
        else {
            tVgoal.setVisibility(View.INVISIBLE);
            tVgoalAmount.setVisibility(View.INVISIBLE);}

        /**
         * if we ask them for their new amount,
         * they input like 20000 (INT NOT FLOAT FOR THE INPUT)
         * and we figure out where the comma would need to go for goal string
         */


        updateCashString();
        updateBankString();
        updateSavingsString();
        updateDisplayString();

        findViewById(R.id.cashButton).setOnClickListener(new HandleClick());
        findViewById(R.id.bankButton).setOnClickListener(new HandleClick());
        findViewById(R.id.savingsButton).setOnClickListener(new HandleClick());}

    private class HandleClick implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        public void onClick(View arg0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
            final EditText userInput = new EditText(arg0.getContext());

            switch (arg0.getId()) {
                case R.id.cashButton:

                    builder.setTitle("Add Cash (Please only enter numbers)");

                    builder.setView(userInput);
                    // set dialog message
                    builder.setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
                        String uIn = userInput.getText().toString();
                        try {
                            float uCash = Float.parseFloat(uIn);
                            cashFloat += uCash;
                            totalFloat = getTotalFloat();
                            updateCashString();
                            updateTotalString();
                            Snackbar.make(getWindow().getDecorView().getRootView(),
                                    "$" + uIn + " added to total! ♥", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } catch (NumberFormatException e) {
                            Snackbar.make(getWindow().getDecorView().getRootView(),"um? thats not a number...",
                                    Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();}})
                            .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                break;

            case R.id.bankButton:
                builder.setTitle("Edit Checking Account (Please only enter numbers)");

                builder.setView(userInput);
                // set dialog message
                builder.setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
                    String uIn = userInput.getText().toString();
                    try {
                        bankFloat = Float.parseFloat(uIn);
                        totalFloat = getTotalFloat();
                        updateBankString();
                        updateTotalString();
                        Snackbar.make(getWindow().getDecorView().getRootView(),
                                "Checking Account set to $" + bankString + ".", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (NumberFormatException e) {
                        Snackbar.make(getWindow().getDecorView().getRootView(),"um? thats not a number...",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();}})
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                break;

            case R.id.savingsButton:
                builder.setTitle("Edit Savings Account (Please only enter numbers)");

                builder.setView(userInput);
                // set dialog message
                builder.setCancelable(false).setPositiveButton("OK", (dialog, id) -> {
                    String uIn = userInput.getText().toString();
                    try {
                        savingsFloat = Float.parseFloat(uIn);
                        totalFloat = getTotalFloat();
                        updateSavingsString();
                        updateTotalString();
                        Snackbar.make(getWindow().getDecorView().getRootView(),
                                "Savings Account set to $" + savingsString + ".", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (NumberFormatException e) {
                        Snackbar.make(getWindow().getDecorView().getRootView(),"um? thats not a number...",
                                Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();}})
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                break;}
            AlertDialog alertDialog = builder.create();
            alertDialog.show();}}}
