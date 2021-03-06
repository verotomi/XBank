package tamas.verovszki.xbank;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab1Currencies.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab1Currencies#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab1Currencies extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;

    // listview-hez kellő változók
    ArrayList<DataModelCurrencies> dataModels;
    ListView listView;
    private static CustomAdapterCurrencies adapter;

    // SQLite adatbázishoz kell
    private DatabaseHelper db;

   // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Tab1Currencies() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab1Currencies.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab1Currencies newInstance(String param1, String param2) {
        Tab1Currencies fragment = new Tab1Currencies();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Inflate the layout for this fragment
     * A fragmentben található Listview felépítése
     * ide kellett berakni azt, amit ebben a fragment-ben meg akarok jeleníteni.
     * a szükséges változók kicsit feljebb vannak inicializálva
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1_currencies, container, false);
        listView = (ListView) view.findViewById(R.id.list1);

        db = new DatabaseHelper(getContext()); // ez nem kell, mert amihez kellett, azt átraktam más activitybe
        dataModels= new ArrayList<>(); // új üres lista példányosítása

        readCurrencyDataFromSqlite();

        // Adatok beolvasása MySQL online adatbázisból átraktam splashscreenre
        // readCurrencies(); // Átraktam a frissitést a splashscreenbe ! ez meg fogja hívni a PerformNetworkRequest-et a megfelelő paraméterekkel. A PerformNetworkRequest meghívja refreshCurrencyList-et, ami frissíti a datamodels listát még a Listview felépítése előtt

        adapter = new CustomAdapterCurrencies(dataModels,getActivity().getApplicationContext()); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String currencyName;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Snackbar
                DataModelCurrencies dModel= dataModels.get(position);

                switch (dModel.getCurrency()){
                    case "EUR": currencyName = getString(R.string.EUR); break;
                    case "USD": currencyName = getString(R.string.USD); break;
                    case "GBP": currencyName = getString(R.string.GBP); break;
                    case "AUD": currencyName = getString(R.string.AUD); break;
                    case "BGN": currencyName = getString(R.string.BGN); break;
                    case "CAD": currencyName = getString(R.string.CAD); break;
                    case "CHF": currencyName = getString(R.string.CHF); break;
                    case "CZK": currencyName = getString(R.string.CZK); break;
                    case "DKK": currencyName = getString(R.string.DKK); break;
                    case "HRK": currencyName = getString(R.string.HRK); break;
                    case "JPY": currencyName = getString(R.string.JPY); break;
                    case "NOK": currencyName = getString(R.string.NOK); break;
                    case "PLN": currencyName = getString(R.string.PLN); break;
                    case "RON": currencyName = getString(R.string.RON); break;
                    case "RSD": currencyName = getString(R.string.RSD); break;
                    case "RUB": currencyName = getString(R.string.RUB); break;
                    case "SEK": currencyName = getString(R.string.SEK); break;
                }

                Snackbar.make(view,
                        currencyName + ":\n" + getString(R.string.tab) +
                        getString(R.string.text_buy) + ": " + dModel.getBuy() + "\n" + getString(R.string.tab) +
                        getString(R.string.text_sell) + ": " + dModel.getSell(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        /**
         * (Többek között) ez kell ahhoz, hogy a listview-ekben működjön együtt a felfele görgetés és a lehúzásos frissítés
         *  Lényegében akkor teszi aktyvvá a lehúzható frissítést, ha a felfele görgetés során elértük a lista legtetejét
         */
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                SwipeRefreshLayout swipeRefreshLayout = ((ExchangeRatesActivity) getContext()).findViewById(R.id.SwipeRefreshLayout);
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
        // fragment-ben megjelenítendő rész vége
    }

    private void readCurrencyDataFromSqlite() {
        //StringBuffer stringBuffer = new StringBuffer(); //stringbuffer = hosszú string amihez hozzá fűzzük (appendeljük) a változókat Akkor használtam, amikor még "beégetve" voltak az adatok, nem adatbázisban

        Cursor eredmeny = db.adatLekerdezesCurrencies();// adatok kiolvasása SQLite adatbázisból
        if (eredmeny!=null && eredmeny.getCount()>0) // az SQLite adatbázisból kiolvasott adatok betöltése a dataModels-be, amiből majd a ListView fog felépülni
        {
            while(eredmeny.moveToNext()) {
                dataModels.add(new DataModelCurrencies(eredmeny.getInt(0), eredmeny.getString(1), eredmeny.getDouble(2), eredmeny.getDouble(3), eredmeny.getString(4)));
            }
        }else
        {
            Toast.makeText(getContext(), getString(R.string.read_error), Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCFragmentInteraction(Uri uri);
    }

    /* Átrakktam SplashScreen-re

    /**
     * Hálózati kommunikációt végzi
     * A MySQL adatbázis eléréséhez kell
     *
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //Toast.makeText(getContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getContext(), "Adatok frissítve", Toast.LENGTH_SHORT).show(); // végül ez sem kell ide
                    refreshCurrencyList(object.getJSONArray("currencies")); // kommunikáció után (legyen az akár új felvitel,törlés, módosítás, vagy lekérdezés)
                    adapter = new CustomAdapterCurrencies(dataModels,getActivity().getApplicationContext()); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
                    listView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandlerPhpMySql requestHandlerPhpMySql = new RequestHandlerPhpMySql();
            if (requestCode == CODE_POST_REQUEST)
                return requestHandlerPhpMySql.sendPostRequest(url, params);
            if (requestCode == CODE_GET_REQUEST)
                return requestHandlerPhpMySql.sendGetRequest(url);
            return null;
        }
    }

    private void refreshCurrencyList(JSONArray currencies) throws JSONException {
        dataModels.clear();
        for (int i = 0; i < currencies.length(); i++) {
            JSONObject obj = currencies.getJSONObject(i);
            dataModels.add(new DataModelCurrencies(
                    obj.getInt("id"),
                    obj.getString("name"),
                    obj.getDouble("buy"),
                    obj.getDouble("sell"),
                    obj.getString("date")
            ));
            // ez is működik, de
            // db.updateCurrencies(obj.getInt("id"),obj.getString("name"), obj.getDouble("buy"), obj.getDouble("sell"),obj.getString("date"));
            // db.refreshCurrencies(DatabaseHelper.TABLE_NAME_CURRENCIES, obj.getInt("id"),obj.getString("name"), obj.getDouble("buy"), obj.getDouble("sell"),obj.getString("date")); // erre már nem emlékszem, hogy jó-e ide: De máshova átraktam és ott működik jól
        }
    }

    /**
     * Az összes valuta beolvasása MySQL adatbázisból
     *
    private void readCurrencies() {
        PerformNetworkRequest request = new PerformNetworkRequest(Constants.URL_READ_CURRENCIES, null, CODE_GET_REQUEST);
        request.execute();
    }

    /**
     * Elmentjük a MySQL-ből beolvasott adatokat az SQLite adatbázisba
     * Valójában az aktuális állapot kerül mentésre, akkor is, ha , nem volt MySQL beolvasás
     *
    @Override
    public void onDestroy() {
        for (DataModelCurrencies d :dataModels) {
            // Toast.makeText(getContext(), "" + d.getId(), Toast.LENGTH_SHORT).show();
            db.refreshCurrencies(DatabaseHelper.TABLE_NAME_CURRENCIES, d.getId(), d.getCurrency(),d.getBuy(),d.getSell(),d.getValidfrom());
        }
        super.onDestroy();
    }*/

}