package tamas.verovszki.xbank;

import java.io.Serializable;

public class DataModelRecurringTransfers implements Serializable {
    int id;
    int id_user;
    int id_bank_account_number;
    String name;
    String type;
    String direction;
    int reference_number;
    String currency;
    double amount;
    String partner_name;
    String partner_account_number;
    String comment;
    String arrived_on;
    String value_date;
    String status;
    String last_fulfilled;
    String frequency;
    String days;

    public DataModelRecurringTransfers(int id, int id_user, int id_bank_account_number, String name, String type, String direction, int reference_number, String currency, double amount, String partner_name, String partner_account_number, String comment, String arrived_on, String value_date, String status, String last_fulfilled, String frequency, String days) {
        this.id = id;
        this.id_user = id_user;
        this.id_bank_account_number = id_bank_account_number;
        this.name = name;
        this.type = type;
        this.direction = direction;
        this.reference_number = reference_number;
        this.currency = currency;
        this.amount = amount;
        this.partner_name = partner_name;
        this.partner_account_number = partner_account_number;
        this.comment = comment;
        this.arrived_on = arrived_on;
        this.value_date = value_date;
        this.status = status;
        this.last_fulfilled = last_fulfilled;
        this.frequency = frequency;
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public int getId_user() {
        return id_user;
    }

    public int getId_bank_account_number() {
        return id_bank_account_number;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDirection() {
        return direction;
    }

    public int getReference_number() {
        return reference_number;
    }

    public String getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public String getPartner_account_number() {
        return partner_account_number;
    }

    public String getComment() {
        return comment;
    }

    public String getArrived_on() {
        return arrived_on;
    }

    public String getValue_date() {
        return value_date;
    }

    public String getStatus() {
        return status;
    }

    public String getLast_fulfilled() {
        return last_fulfilled;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getDays() {
        return days;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public void setId_bank_account_number(int id_bank_account_number) {
        this.id_bank_account_number = id_bank_account_number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setReference_number(int reference_number) {
        this.reference_number = reference_number;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPartner_name(String partner_name) {
        this.partner_name = partner_name;
    }

    public void setPartner_account_number(String partner_account_number) {
        this.partner_account_number = partner_account_number;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setArrived_on(String arrived_on) {
        this.arrived_on = arrived_on;
    }

    public void setValue_date(String value_date) {
        this.value_date = value_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLast_fulfilled(String last_fulfilled) {
        this.last_fulfilled = last_fulfilled;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setDays(String days) {
        this.days = days;
    }
}

