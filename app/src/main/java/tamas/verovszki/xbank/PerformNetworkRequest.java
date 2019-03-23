package tamas.verovszki.xbank;

import android.os.AsyncTask;
import android.widget.Toast;

import java.util.HashMap;

public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
    String url;
    HashMap<String, String> params;
    int requestCode;
    String result;

    TaskDelegate delegate;

    // ezt a konstruktort a loginhez használom
    // ide a delegate utólag került be, ezzel értesítem a meghívó activityt, ha lefutott a MySql kommunikáció
    PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode, TaskDelegate delegate) {
        this.url = url;
        this.params = params;
        this.requestCode = requestCode;
        this.delegate = delegate;
    }

    // ugy nez ki, hogy ez nem fog kelleni, a delegate átküldi a result-ot
    public String getResult() {
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        result = s; // átadom a mysql kommunikáció eredményét (az "s" változó értékét)  a resultnak
        delegate.taskCompletionResult(result); // ez jelzi a hívó activitynek, hogy vége az async műveletnek (mysql kommunikáció) + visszaküldi az eredményt
    }

    @Override
    protected String doInBackground(Void... voids) {
        RequestHandlerPhpMySql requestHandlerPhpMySql = new RequestHandlerPhpMySql();
        if (requestCode == Constants.CODE_POST_REQUEST)
            return requestHandlerPhpMySql.sendPostRequest(url, params);
        if (requestCode == Constants.CODE_GET_REQUEST)
            return requestHandlerPhpMySql.sendGetRequest(url);
        return null;
    }

    /**
     * ez az interface szükséges a delegate-hoz
     */
    public interface TaskDelegate {
        public void taskCompletionResult(String result);
    }
}
