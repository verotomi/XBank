package tamas.verovszki.xbank;

public class DataModelCreditCards {
    int card_id; // MySql adatbázisban az id
    String cardNumber;
    String cardType;
    String validTo;
    String status;
    int limitAtm;
    int limitPos;
    int limitOnline;
    int user_id;

    public DataModelCreditCards(int card_id, String cardNumber, String cardType, String validTo, String status, int limitAtm, int limitPos, int limitOnline, int user_id) {
        this.card_id = card_id;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.validTo = validTo;
        this.status = status;
        this.limitAtm = limitAtm;
        this.limitPos = limitPos;
        this.limitOnline = limitOnline;
        this.user_id = user_id;
    }

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public String getCardType() {
        return cardType;
    }
    public String getValidTo() {
        return validTo;
    }
    public int getLimitAtm() {
        return limitAtm;
    }
    public int getLimitPos() {
        return limitPos;
    }
    public int getLimitOnline() {
        return limitOnline;
    }
    public String getStatus() {
        return status;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLimitAtm(int limitAtm) {
        this.limitAtm = limitAtm;
    }

    public void setLimitPos(int limitPos) {
        this.limitPos = limitPos;
    }

    public void setLimitOnline(int limitOnline) {
        this.limitOnline = limitOnline;
    }
}
