package tamas.verovszki.xbank;


public class DataModelCurrencies {



    String currency;
    String buy;
    String sell;
    String date;



    public DataModelCurrencies(String currency, String buy, String sell, String date ) {
        this.currency=currency;
        this.buy=buy;
        this.sell=sell;
        this.date=date;

    }


    public String getBuy() {
        return buy;
    }


    public String getSell() {
        return sell;
    }


    public String getCurrency() {
        return currency;
    }


    public String getDate() {
        return date;
    }

}
