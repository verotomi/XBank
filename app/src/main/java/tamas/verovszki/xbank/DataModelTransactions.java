package tamas.verovszki.xbank;

import java.io.Serializable;

public class DataModelTransactions implements Serializable {
    int id;
    int id_user;
    int id_bank_account_number;
    String type;
    String direction;
    int reference_number;
    String currency;
    int amount;
    String partner_name;
    String partner_account_number;
    String comment;
    String arrived_on;
    String bank_account_number; // származtatott mező (JOIN)

    public DataModelTransactions(int id, int id_user, int id_bank_account_number, String type, String direction, int reference_number, String currency, int amount, String partner_name, String partner_account_number, String comment, String arrived_on, String bank_account_number) {
        this.id = id;
        this.id_user = id_user;
        this.id_bank_account_number = id_bank_account_number;
        this.type = type;
        this.direction = direction;
        this.reference_number = reference_number;
        this.currency = currency;
        this.amount = amount;
        this.partner_name = partner_name;
        this.partner_account_number = partner_account_number;
        this.comment = comment;
        this.arrived_on = arrived_on;
        this.bank_account_number = bank_account_number;
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

    public int getId_bank_account_number() {
        return id_bank_account_number;
    }

    public void setId_bank_account_number(int id_bank_account_number) {
        this.id_bank_account_number = id_bank_account_number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getReference_number() {
        return reference_number;
    }

    public void setReference_number(int reference_number) {
        this.reference_number = reference_number;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public void setPartner_name(String partner_name) {
        this.partner_name = partner_name;
    }

    public String getPartner_account_number() {
        return partner_account_number;
    }

    public void setPartner_account_number(String partner_account_number) {
        this.partner_account_number = partner_account_number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getArrived_on() {
        return arrived_on;
    }

    public void setArrived_on(String arrived_on) {
        this.arrived_on = arrived_on;
    }

    public String getBank_account_number() {
        return bank_account_number;
    }
}

