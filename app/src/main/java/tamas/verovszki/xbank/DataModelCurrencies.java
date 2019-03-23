package tamas.verovszki.xbank;


public class DataModelCurrencies {

    private int id;
    private String currency;
    private double buy;
    private double sell;
    private String validfrom;

    public DataModelCurrencies( int id, String currency, Double buy, Double sell, String validfrom) {
        this.currency=currency;
        this.buy=buy;
        this.sell=sell;
        this.validfrom = validfrom;
    }

    public int getId() {
        return id;
    }
    public Double getBuy() {
        return buy;
    }
    public Double getSell() {
        return sell;
    }
    public String getCurrency() {
        return currency;
    }
    public String getValidfrom() {
        return validfrom;
    }
}
