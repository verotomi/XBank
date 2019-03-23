package tamas.verovszki.xbank;

public class DataModelStatements {
    private int id;
    int id_user;
    int id_bank_account;
    String number;
    String filename;

    public DataModelStatements(int id, int id_user, int id_bank_account, String number, String filename) {
        this.id = id;
        this.id_user = id_user;
        this.id_bank_account = id_bank_account;
        this.number = number;
        this.filename = filename;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
