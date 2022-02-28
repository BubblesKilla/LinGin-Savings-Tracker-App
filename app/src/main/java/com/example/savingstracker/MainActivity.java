package com.example.savingstracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

///////////////////////////// EACH VALUE HAS A TEXTVIEW /////////////////////////////
    public TextView tVbank;
    public TextView tVsavings;
    public TextView tVdisplayTotal;
    public TextView tVcash;
    public TextView tVgoal;
    public TextView tVgoalAmount;
    public TextView tVgoalReached;
//////////////////////////////// DATABASE VARIABLES //////////////////////////////////
    public DBServices dbServices;
    public Account account;
//////////////////////////////////// FORMATTER ///////////////////////////////////////
    private final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.CANADA);

//////////////////////////////////////////////////////////////////////////////////////

//////// THESE UPDATE THE STRINGS EVERY TIME THE VALUE OF THE FLOATS CHANGE ////////

    void updateBankString() { tVbank.setText(CURRENCY_FORMAT.format(account.get_bankAmount())); }

    void updateSavingsString() { tVsavings.setText(CURRENCY_FORMAT.format(account.get_savingAmount())); }

    void updateCashString() {
        if (account.get_totalSaved() >= account.get_goal()) tVcash.setText(CURRENCY_FORMAT.format(account.get_cashAmount()));
    }

    void updateTotalString() {
        if (account.get_totalSaved() >= account.get_goal()) {
            tVdisplayTotal.setText(CURRENCY_FORMAT.format(account.get_totalSaved()));
            whenReachedGoal();}
        else{
            tVdisplayTotal.setText(CURRENCY_FORMAT.format(account.get_displayAmount()));
        }
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        tVbank = findViewById(R.id.bankAmountTV);
        tVsavings = findViewById(R.id.savingsAmountTV);
        tVdisplayTotal = findViewById(R.id.totalAmountTV);
        tVcash = findViewById(R.id.cashAmountTV);
        tVgoal = findViewById(R.id.goalTV);
        tVgoalAmount = findViewById(R.id.goalAmountTV);
        tVgoalReached = findViewById(R.id.goalReachedTV);

        tVcash.setText("???");

        // TODO create some sort of boolean that gets checked onCreate to see if goal reached
        // not needed ASAP

        /**
        * Database initializations
        */
        dbServices = new DBServices(this);
        if(dbServices.getMainAccount().getCount() == 0){
            // Initialize account
            initGoalAlert("Set a Goal!!");
        }else{
            account = new Account(dbServices.getMainAccount());
            initDisplay();
        }
        findViewById(R.id.cashButton).setOnClickListener(new HandleClick());
        findViewById(R.id.bankButton).setOnClickListener(new HandleClick());
        findViewById(R.id.savingsButton).setOnClickListener(new HandleClick());
    }

    @Override
    protected void onPause() {
        // Save account to database
        super.onPause();
        dbServices.updateAccount(account);
    }

    private void initGoalAlert(String title){
        EditText userInput = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setView(userInput)
                .setCancelable(false)
                .setNeutralButton("Enter", (dialog, id)->{
                    String in = userInput.getText().toString();
                    if(!isFloat(in)) {
                        initGoalAlert("Please input a number as your goal!");
                    }else{
                        float goal = Math.round(Float.parseFloat(in)*100.0f)/100.0f;
                        dbServices.insertInitialAccount(goal);
                        account = new Account(dbServices.getMainAccount());
                        initDisplay();
                    }
                }).show();
    }

    private void initDisplay(){
        if (account.get_totalSaved() < account.get_goal()) {
            tVgoalReached.setVisibility(View.INVISIBLE);
            tVgoalAmount.setText(CURRENCY_FORMAT.format(account.get_goal()));
        }
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
        updateTotalString();
    }

    private boolean isFloat(String in){
        boolean isFloat;
        try{
            Float.parseFloat(in);
            isFloat = true;
        }catch (Exception e){
            isFloat = false;
        }
        return isFloat;
    }

    public void printAccount(View view) {
        Toast.makeText(this,dbServices.toStringTransaction(),Toast.LENGTH_LONG).show();
    }

    private class HandleClick implements View.OnClickListener {
        private void umNotANumber(){
            Snackbar.make(getWindow().getDecorView().getRootView(),"um? thats not a number...",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        @SuppressLint("NonConstantResourceId")
        public void onClick(View arg0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
            final EditText userInput = new EditText(arg0.getContext());

            switch (arg0.getId()) {
                case R.id.cashButton:
                    builder.setTitle("Add Cash (Please only enter numbers)")
                            .setView(userInput)
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, id) -> {
                                String uIn = userInput.getText().toString();
                                if (isFloat(uIn)) {
                                    float added = account.add_cashAmount(Float.parseFloat(uIn));
                                    updateCashString();
                                    updateTotalString();
                                    Snackbar.make(getWindow().getDecorView().getRootView(),
                                            CURRENCY_FORMAT.format(added) + " added to total! ♥", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    // Transaction Part
                                    dbServices.insertTransaction(account.get_id(),added);
                                } else {
                                    umNotANumber();
                                }
                            }).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                break;

            case R.id.bankButton:
                builder.setTitle("Edit Checking Account (Please only enter numbers)")
                        .setView(userInput)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            String uIn = userInput.getText().toString();
                            if (isFloat(uIn)) {
                                account.set_bankAmount(Float.parseFloat(uIn));
                                updateBankString();
                                updateTotalString();
                                Snackbar.make(getWindow().getDecorView().getRootView(),
                                        "Checking Account set to " + CURRENCY_FORMAT.format(account.get_bankAmount())
                                                + ".", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                umNotANumber();
                            }
                        }).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                break;

            case R.id.savingsButton:
                builder.setTitle("Edit Savings Account (Please only enter numbers)")
                        .setView(userInput)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> {
                            String uIn = userInput.getText().toString();
                            if (isFloat(uIn)) {
                                account.set_savingAmount(Float.parseFloat(uIn));
                                updateSavingsString();
                                updateTotalString();
                                Snackbar.make(getWindow().getDecorView().getRootView(),
                                        "Savings Account set to " +
                                                CURRENCY_FORMAT.format(account.get_savingAmount()) +
                                                ".", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                umNotANumber();
                            }
                }).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                break;}
            AlertDialog alertDialog = builder.create();
            alertDialog.show();}}}
