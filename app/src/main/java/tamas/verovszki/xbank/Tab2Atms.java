package tamas.verovszki.xbank;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


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
     * @return A new instance of fragment Tab2Currencies.
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
        dataModels.add(new DataModelAtms("1011 Budapest", "Deák tér 1.", "1km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1025 Budapest", "Makó tér 6.", "2km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1032 Budapest", "Damjanich utca 27.", "3km",getString(R.string.euro)));
        dataModels.add(new DataModelAtms("1041 Budapest", "Koller Tivadar utca 61.", "5km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1052 Budapest", "Bokody Dezső tér 2.", "8km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1086 Budapest", "Pallagi utca 7.", "10km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1133 Budapest", "Kossuth Lajos út 111.", "11km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1211 Budapest", "Márvány utca 62.", "12km",getString(R.string.euro)));
        dataModels.add(new DataModelAtms("1072 Budapest", "Sárgarózsa utca 88.", "15km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1165 Budapest", "Semmelweiss utca 5/b.", "17km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1182 Budapest", "Rózsa utca 92.", "18km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1217 Budapest", "Tél tér 13.", "19km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1144 Budapest", "Koppány utca 47.", "22km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1152 Budapest", "Nemecsek utca 55.", "25km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1191 Budapest", "Barta Béla utca 8.", "25km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("1044 Budapest", "Badacsonyi utca 56.", "28km",getString(R.string.forint)));
        dataModels.add(new DataModelAtms("2120 Dunakeszi", "Fő utca 10.", "32km",getString(R.string.forint)));

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