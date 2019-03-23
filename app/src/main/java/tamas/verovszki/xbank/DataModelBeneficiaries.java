package tamas.verovszki.xbank;

import java.io.Serializable;

public class DataModelBeneficiaries implements Serializable {
    private int id;
    private int id_user;
    private String name;
    private String partner_name;
    private String partner_account_number;
    private String status;
    private String created_on;

    public DataModelBeneficiaries(int id, int id_user, String name, String partner_name, String partner_account_number, String status, String created_on) {
        this.id = id;
        this.id_user = id_user;
        this.name = name;
        this.partner_name = partner_name;
        this.partner_account_number = partner_account_number;
        this.status = status;
        this.created_on = created_on;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
