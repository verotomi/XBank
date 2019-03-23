package tamas.verovszki.xbank;

import java.io.Serializable;
import java.text.DecimalFormat;

public class DataModelSavings implements Serializable {
    private int id;
    private int id_user;
    private int id_bank_account;
    private int id_type;
    private int amount;
    private String expire_date;
    private String status;
    private int reference_number;
    private String arrived_on;
    private String type; // Származtatott mező
    private double rate; // Származtatott mező
    private int duration; // Származtatott mező
    private String currency; // Származtatott mező
    private String bank_account_number; // Származtatott mező
    private double expectedInterest; // Származtatott mező

    public DataModelSavings(int id, int id_user, int id_bank_account, int id_type, int amount, String expire_date, String status, int reference_number, String arrived_on, String type, double rate, int duration, String currency, String bank_account_number) {
        this.id = id;
        this.id_user = id_user;
        this.id_bank_account = id_bank_account;
        this.id_type = id_type;
        this.amount = amount;
        this.expire_date = expire_date;
        this.status = status;
        this.reference_number = reference_number;
        this.arrived_on = arrived_on;
        this.type = type; // Származtatott mező
        this.rate = rate; // Származtatott mező
        this.duration = duration; // Származtatott mező
        this.currency = currency; // Származtatott mező
        this.bank_account_number = bank_account_number; // Származtatott mező

        this.expectedInterest = (this.amount * this.rate * this.duration/365/100);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_bank_account() {
        return id_bank_account;
    }

    public void setId_bank_account(int id_bank_account) {
        this.id_bank_account = id_bank_account;
    }

    public int getId_type() {
        return id_type;
    }

    public void setId_type(int id_type) {
        this.id_type = id_type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReference_number() {
        return reference_number;
    }

    public void setReference_number(int reference_number) {
        this.reference_number = reference_number;
    }

    public String getArrived_on() {
        return arrived_on;
    }

    public void setArrived_on(String arrived_on) {
        this.arrived_on = arrived_on;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBank_account_number() {
        return bank_account_number;
    }

    public String getExpectedInterest() {
        return new DecimalFormat("#,##0.00").format(expectedInterest) + " " + this.currency;
    }
    public String getExpectedAmount() {
        return new DecimalFormat("#,##0.00").format(expectedInterest + (double)amount) + " " + this.currency;
    }
}
