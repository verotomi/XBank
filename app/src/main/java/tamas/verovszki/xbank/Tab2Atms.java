package tamas.verovszki.xbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab2Atms.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab2Atms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab2Atms extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // listview-hez kellő változók
    ArrayList<DataModelAtms> dataModels;
    ListView listView;
    private static CustomAdapterAtms adapter2;

    // adatbázishoz kell
    private DatabaseHelper db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Tab2Atms() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab2ForeignCurrencies.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab2Atms newInstance(String param1, String param2) {
        Tab2Atms fragment = new Tab2Atms();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container2,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // ide kellett berakni azt, amit ebben a fragment-ben meg akarok jeleníteni.
        // a szükséges változók kicsit feljebb vannak inicializálva
        View view = inflater.inflate(R.layout.fragment_tab2_atms, container2, false);
        listView = (ListView) view.findViewById(R.id.list4);


        dataModels= new ArrayList<>();

        db = new DatabaseHelper(getContext());
        Cursor eredmeny = db.adatLekerdezesAtms();
        //stringbuffer = hosszú string amihez hozzá fűzzük (appendeljük) a változókat
        StringBuffer stringBuffer = new StringBuffer();

        if (eredmeny!=null && eredmeny.getCount()>0)
        {
            while(eredmeny.moveToNext()) {
                dataModels.add(new DataModelAtms(eredmeny.getString(1) + " " + eredmeny.getString(2), eredmeny.getString(3), Double.parseDouble(eredmeny.getString(4)), Double.parseDouble(eredmeny.getString(5)), eredmeny.getString(6)));
            }
        }else
        {
            Toast.makeText(getContext(), getString(R.string.read_error), Toast.LENGTH_SHORT).show();
        }

        /*
        dataModels.add(new DataModelAtms("1052 Budapest", "Deák tér 1.", 47.4969004, 19.0521338, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1065 Budapest", "Andrássy út 29.", 47.5026159, 19.0577201, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1137 Budapest", "Szent István krt. 10.", 47.5363719, 19.047132, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1044 Budapest", "Fóti út 4.", 47.5820813, 19.0820029, getString(R.string.euro)));
        dataModels.add(new DataModelAtms("1035 Budapest", "Vörösvári út 22.", 47.478151, 19.086685, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1025 Budapest", "Pusztaszeri út 67.", 47.5243734, 19.020056, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1086 Budapest", "Baross utca 124.", 47.4895625, 19.0863731, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1089 Budapest", "Nagyvárad tér 3.", 47.4744249, 19.0947822, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1011 Budapest", "Batthyány tér 3.", 47.5067068, 19.0358404, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1013 Budapest", "Lánchíd utca 8.", 47.4973662, 19.0387893, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1212 Budapest", "Kossuth Lajos utca 50.", 47.432696, 19.0687424, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1077 Budapest", "Wesselényi utca 17.", 47.4976228, 19.0615186, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1098 Budapest", "Epreserdő u. 20.", 47.4648092, 19.1076891, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1165 Budapest", "Veres Péter út 145.", 47.512915, 19.1967863, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1182 Budapest", "Péterhalmi út 8.", 47.4232156, 19.187561, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1238 Budapest", "Grassalkovich út 70.", 47.407117, 19.1097393, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1144 Budapest", "Füredi utca 19", 47.5097894, 19.1442344, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1154 Budapest", "Szentmihályi út 152.", 47.5426809, 19.1482363, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1221 Budapest", "Leányka utca 118.", 47.4349066, 19.0346482, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1173 Budapest", "Pesti út 222.", 47.4772673, 19.261696, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1195 Budapest", "Vas Gereben utca 11.", 47.451745, 19.1530642, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1111 Budapest", "Bertalan Lajos utca 23.", 47.4798003, 19.0501078, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("2120 Dunakeszi", "Fő út 10.", 47.630479, 19.1262055, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("2000 Szentendre", "Sztaravodai út 74.", 47.6783282, 19.0640866, getString(R.string.forint)));
        dataModels.add(new DataModelAtms("2142 Kistarcsa", "Szabadság út 42.", 47.54481, 19.2567063, getString(R.string.forint)));*/

        SharedPreferences sp = getContext().getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String orderType = sp.getString(Constants.SHAREDPREFERENCES_BRANCHES_AND_ATMS_ORDER_TYPE, "address");
        //Toast.makeText(getContext(), "" + orderType, Toast.LENGTH_SHORT).show();
        switch (orderType){
            case "address":
                //Toast.makeText(getContext(), "ATM cim", Toast.LENGTH_SHORT).show();
                Collections.sort(dataModels, new Comparator<DataModelAtms>() { // lista sorbarendezése cím szerint (utcanév és házszám nincs figyelve, csak az irszám + város)
                    public int compare(DataModelAtms obj1, DataModelAtms obj2) {
                        return obj1.getAddress1().compareTo(obj2.getAddress1());
                    }
                });
                break;
            case "distance":
                //Toast.makeText(getContext(), "ATM táv", Toast.LENGTH_SHORT).show();
                Collections.sort(dataModels, new Comparator<DataModelAtms>() { // lista sorbarendezése a jelenlegi pozíciónktól való távolság szerint
                    public int compare(DataModelAtms obj1, DataModelAtms obj2) {
                        return obj1.getDistance(getContext()).compareTo(obj2.getDistance(getContext()));
                    }
                });
                break;
        }

        /* Adatszerkezet-átalakítás miatt kicsillagoztam a régi sazerkezetet. Hátha valamelyik adat még kelleni fog
        dataModels.add(new DataModelAtms("Központi ATM", "1011 Budapest, Deák tér 1.", "1km","Forint"));
        dataModels.add(new DataModelAtms("12. számú ATM", "1025 Budapest, Makó tér 6.", "2km","Forint"));
        dataModels.add(new DataModelAtms("2. számú ATM", "1032 Budapest, Damjanich utca 27.", "3km","EUR"));
        dataModels.add(new DataModelAtms("3. számú ATM", "1041 Budapest, Koller Tivadar utca 61.", "5km","Forint"));
        dataModels.add(new DataModelAtms("14. számú ATM", "1052 Budapest, Bokody Dezső tér 2.", "8km","Forint"));
        dataModels.add(new DataModelAtms("5. számú ATM", "1086 Budapest, Pallagi utca 7.", "10km","Forint"));
        dataModels.add(new DataModelAtms("18. számú ATM", "1133 Budapest, Kossuth Lajos út 111.", "11km","Forint"));
        dataModels.add(new DataModelAtms("11. számú ATM", "1211 Budapest, Márvány utca 62.", "12km","EUR"));
        dataModels.add(new DataModelAtms("1. számú ATM", "1072 Budapest, Sárgarózsa utca 88.", "15km","Forint"));
        dataModels.add(new DataModelAtms("4. számú ATM", "1165 Budapest, Semmelweiss utca 5/b.", "17km","Forint"));
        dataModels.add(new DataModelAtms("15. számú ATM", "1182 Budapest, Rózsa utca 92.", "18km","Forint"));
        dataModels.add(new DataModelAtms("8. számú ATM", "1217 Budapest, Tél tér 13.", "19km","Forint"));
        dataModels.add(new DataModelAtms("22. számú ATM", "1144 Budapest, Koppány utca 47.", "22km","Forint"));
        dataModels.add(new DataModelAtms("25. számú ATM", "1152 Budapest, Nemecsek utca 55.", "25km","Forint"));
        dataModels.add(new DataModelAtms("26. számú ATM", "1044 Budapest, Barta Béla utca 8.", "25km","Forint"));
        dataModels.add(new DataModelAtms("32. számú ATM", "1191 Budapest, Badacsonyi utca 56.", "28km","Forint"));
        dataModels.add(new DataModelAtms("40. számú ATM", "2120 Dunakeszi, Fő utca 10.", "32km","Forint"));
        */


        adapter2 = new CustomAdapterAtms(dataModels,getActivity().getApplicationContext()); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben

        listView.setAdapter(adapter2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModelAtms dModel= dataModels.get(position);

                Snackbar.make(view,
                        getString(R.string.atm_type) + ":" + " " + dModel.getType(), Snackbar.LENGTH_LONG)
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
                SwipeRefreshLayout swipeRefreshLayout = ((BranchAndAtmListActivity) getContext()).findViewById(R.id.SwipeRefreshLayout);
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        return view;
        // fragment-ben megjelenítendő rész vége
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onBFragmentInteraction(uri);
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
        void onBFragmentInteraction(Uri uri);
    }
}