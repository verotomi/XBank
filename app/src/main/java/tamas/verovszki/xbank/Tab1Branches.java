package tamas.verovszki.xbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IllegalFormatCodePointException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab1Branches.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab1Branches#newInstance} factory method to
 * create an instance of this fragment.
 */
public class
Tab1Branches extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // listview-hez kellő változók
    ArrayList<DataModelBranches> dataModels;
    ListView listView;
    private static CustomAdapterBranches adapter2;

    // adatbázishoz kell
    private DatabaseHelper db;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Tab1Branches() {
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
    public static Tab1Branches newInstance(String param1, String param2) {
        Tab1Branches fragment = new Tab1Branches();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // ide kellett berakni azt, amit ebben a fragment-ben meg akarok jeleníteni.
        // a szükséges változók kicsit feljebb vannak inicializálva
        View view = inflater.inflate(R.layout.fragment_tab1_branches, container, false);
        listView = (ListView) view.findViewById(R.id.list3);


        dataModels= new ArrayList<>();

        db = new DatabaseHelper(getContext());
        Cursor eredmeny = db.adatLekerdezesBranches();
        //stringbuffer = hosszú string amihez hozzá fűzzük (appendeljük) a változókat
        StringBuffer stringBuffer = new StringBuffer();

        if (eredmeny!=null && eredmeny.getCount()>0)
        {
            while(eredmeny.moveToNext()) {
                dataModels.add(new DataModelBranches(eredmeny.getString(1) + " " + eredmeny.getString(2), eredmeny.getString(3), Double.parseDouble(eredmeny.getString(4)), Double.parseDouble(eredmeny.getString(5)), eredmeny.getString(6), eredmeny.getString(7), eredmeny.getString(8), eredmeny.getString(9), eredmeny.getString(10), eredmeny.getString(11), eredmeny.getString(12)));
            }
        }else
        {
            Toast.makeText(getContext(), R.string.read_error, Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sp = getContext().getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String orderType = sp.getString(Constants.SHAREDPREFERENCES_BRANCHES_AND_ATMS_ORDER_TYPE, "address");
        switch (orderType){
            case "address":
                //Toast.makeText(getContext(), "Fiok cim", Toast.LENGTH_SHORT).show();
                Collections.sort(dataModels, new Comparator<DataModelBranches>() { // lista sorbarendezése cím szerint (utcanév és házszám nincs figyelve, csak az irszám + város)
                    public int compare(DataModelBranches obj1, DataModelBranches o2) {
                        return obj1.getAddress1().compareTo(o2.getAddress1());
                    }
                });
                break;
            case "distance":
                //Toast.makeText(getContext(), "Fiok táv", Toast.LENGTH_SHORT).show();
                Collections.sort(dataModels, new Comparator<DataModelBranches>() { // lista sorbarendezése a jelenlegi pozíciónktól való távolság szerint
                    public int compare(DataModelBranches obj1, DataModelBranches o2) {
                        return obj1.getDistance(getContext()).compareTo(o2.getDistance(getContext()));
                    }
                });
                break;
        }

        adapter2 = new CustomAdapterBranches(dataModels, getActivity().getApplicationContext()); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben

        listView.setAdapter(adapter2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModelBranches dModel= dataModels.get(position);

                /*String isOpen; // ez csak anyit írt ki, hogy nyitva vagy zárva. kibővítettem!
                if (dModel.getOpen()){
                    isOpen = getContext().getResources().getString(R.string.open);
                }
                else{
                    isOpen = getContext().getResources().getString(R.string.closed);
                }*/

                Snackbar.make(view,
                        //getString(R.string.opening_hours) +
                            getString(R.string.opening_hours) + ": \n" +
                                    getString(R.string.monday) + ":\t " +dModel.getOpeningTimeMonday() + "\n" +
                                    getString(R.string.tuesday) + ":\t " +dModel.getOpeningTimeTuesday() + "\n" +
                                    getString(R.string.wednesday) + ":\t " +dModel.getOpeningTimeWednesday() + "\n" +
                                    getString(R.string.thursday) + ":\t " +dModel.getOpeningTimeThursday() + "\n" +
                                    getString(R.string.friday) + ":\t " +dModel.getOpeningTimeFriday() + "\n" +
                                    getString(R.string.saturday) + ":\t " +dModel.getOpeningTimeSaturday() + "\n" +
                                    getString(R.string.sunday) + ":\t " +dModel.getOpeningTimeSunday()
                                // be kellett hozzá állítani a Snackbar maximális sorok számát a values.xml-ben!!
                        , Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        /**
         * (Többek között) ez kell ahhoz, hogy a listview-ekben működjön együtt a felfele görgetés és a lehúzásos frissítés
         *  Lényegében akkor teszi aktivvá a lehúzható frissítést, ha a felfele görgetés során elértük a lista legtetejét
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