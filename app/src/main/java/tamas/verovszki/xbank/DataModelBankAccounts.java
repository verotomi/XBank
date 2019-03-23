package tamas.verovszki.xbank;

import java.io.Serializable;

public class DataModelBankAccounts implements Serializable {
    int id;
    int id_user;
    String number;
    String type;
    String currency;
    double balance;
    String status;
    String created_on;
    String updated_on;
    String userFirstname; // Származtatott mező
    String userLastname; // Származtatott mező

    public DataModelBankAccounts(int id, int id_user, String number, String type, String currency, double balance, String status, String created_on, String updated_on, String userFirstname, String userLastname) {
        this.id = id;
        this.id_user = id_user;
        this.number = number;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.created_on = created_on;
        this.updated_on = updated_on;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public String getUserLastname() {
        return userLastname;
    }

    /*@Override
    public String toString() {
        return "DataModelBankAccounts{" +
                "id=" + id +
                ", id_user=" + id_user +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", created_on='" + created_on + '\'' +
                ", updated_on='" + updated_on + '\'' +
                ", userFirstname='" + userFirstname + '\'' +
                ", userLastname='" + userLastname + '\'' +
                '}';
    }*/
}
