package com.example.savingstracker;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;

public class Account {
    private float _bankAmount, _savingAmount, _goal, _cashAmount, _totalSaved;
    private int _id;
    public Account(){
        _id = 1;
        _bankAmount = 0.0f;
        _savingAmount = 0.0f;
        _goal = 0.0f;
        _cashAmount = 0.0f;
        _totalSaved = 0.0f;
    }

    public Account(Cursor data){
        if(!verifyData(data)){
            throw new CursorIndexOutOfBoundsException("Cursor does not have the proper data");
        }
        _id = Integer.parseInt(data.getString(0));
        _bankAmount = Float.parseFloat(data.getString(1));
        _savingAmount = Float.parseFloat(data.getString(2));
        _goal = Float.parseFloat(data.getString(3));
        _cashAmount = Float.parseFloat(data.getString(4));
        calcTotal();
    }

    public Account(float bank, float saving, float cash, float total, float goal){
        _id = 1;
        _bankAmount = bank;
        _savingAmount = saving;
        _cashAmount = cash;
        _goal = goal;
        _totalSaved = total;
    }

    // Returns true if @data has info
    private boolean verifyData(Cursor data){ return data.getCount() != 0; }

    private void calcTotal(){ _totalSaved = _bankAmount + _savingAmount + _cashAmount; }

    private float round(float r){ return Math.round(r*100.0f)/100.0f; }

    @Override
    public String toString() {
        return "Account{" +
                "_bankAmount=" + _bankAmount +
                ", \n_savingAmount=" + _savingAmount +
                ", \n_goal=" + _goal +
                ", \n_cashAmount=" + _cashAmount +
                ", \n_totalSaved=" + _totalSaved +
                ", \n_id=" + _id +
                '}';
    }

    public void set_bankAmount(float bank){
        _bankAmount = round(bank);
        calcTotal();
    }

    public void set_goal(float goal){ _goal = round(goal); }

    public void set_savingAmount(float saving){
        _savingAmount = round(saving);
        calcTotal();
    }

    public float add_cashAmount(float cashAmount){
        float add = round(cashAmount);
        _cashAmount += add;
        calcTotal();
        return add;
    }

    public float get_bankAmount(){ return _bankAmount; }

    public float get_savingAmount(){ return _savingAmount; }

    public float get_goal(){ return _goal; }

    public float get_cashAmount(){ return _cashAmount; }

    public float get_totalSaved(){ return _totalSaved; }

    public float get_displayAmount(){ return _totalSaved - _cashAmount; }

    public int get_id(){ return _id; }
}