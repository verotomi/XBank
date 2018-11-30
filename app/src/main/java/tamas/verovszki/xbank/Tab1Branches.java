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
 * {@link Tab1Branches.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab1Branches#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab1Branches extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // listview-hez kellő változók
    ArrayList<DataModelBranches> dataModels;
    ListView listView;
    private static CustomAdapterBranches adapter2;

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
        dataModels.add(new DataModelBranches("1011 Budapest", "Deák tér 1.", "1km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1025 Budapest", "Makó tér 6.", "2km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1032 Budapest", "Damjanich utca 27.", "3km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1041 Budapest", "Koller Tivadar utca 61.", "5km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1052 Budapest", "Bokody Dezső tér 2.", "8km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1086 Budapest", "Pallagi utca 7.", "10km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1133 Budapest", "Kossuth Lajos út 111.", "11km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1211 Budapest", "Márvány utca 62.", "12km",getString(R.string.closed)));
        dataModels.add(new DataModelBranches("1072 Budapest", "Sárgarózsa utca 88.", "15km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1165 Budapest", "Semmelweiss utca 5/b.", "17km",getString(R.string.closed)));
        dataModels.add(new DataModelBranches("1182 Budapest", "Rózsa utca 92.", "18km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1217 Budapest", "Tél tér 13.", "19km",getString(R.string.closed)));
        dataModels.add(new DataModelBranches("1144 Budapest", "Koppány utca 47.", "22km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1152 Budapest", "Nemecsek utca 55.", "25km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1044 Budapest", "Barta Béla utca 8.", "25km",getString(R.string.open)));
        dataModels.add(new DataModelBranches("1191 Budapest", "Badacsonyi utca 56.", "28km",getString(R.string.closed)));
        dataModels.add(new DataModelBranches("2120 Dunakeszi", "Fő utca 10.", "32km",getString(R.string.open)));

        /* Adatszerkezet-átalakítás miatt kicsillagoztam a régi sazerkezetet. Hátha valamelyik adat még kelleni fog
        dataModels.add(new DataModelBranches("Központi fiók", "1011 Budapest, Deák tér 1.", "1km","H-P: 8:00-16:00"));
        dataModels.add(new DataModelBranches("12. számú fiók", "1025 Budapest, Makó tér 6.", "2km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("2. számú fiók", "1032 Budapest, Damjanich utca 27.", "3km","H-P: 9:00-18:00"));
        dataModels.add(new DataModelBranches("3. számú fiók", "1041 Budapest, Koller Tivadar utca 61.", "5km","H-P: 8:00-15:00"));
        dataModels.add(new DataModelBranches("14. számú fiók", "1052 Budapest, Bokody Dezső tér 2.", "8km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("5. számú fiók", "1086 Budapest, Pallagi utca 7.", "10km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("18. számú fiók", "1133 Budapest, Kossuth Lajos út 111.", "11km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("11. számú fiók", "1211 Budapest, Márvány utca 62.", "12km","H-P: 8:00-19:00"));
        dataModels.add(new DataModelBranches("1. számú fiók", "1072 Budapest, Sárgarózsa utca 88.", "15km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("4. számú fiók", "1165 Budapest, Semmelweiss utca 5/b.", "17km","H-P: 9:00-20:00"));
        dataModels.add(new DataModelBranches("15. számú fiók", "1182 Budapest, Rózsa utca 92.", "18km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("8. számú fiók", "1217 Budapest, Tél tér 13.", "19km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("22. számú fiók", "1144 Budapest, Koppány utca 47.", "22km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("25. számú fiók", "1152 Budapest, Nemecsek utca 55.", "25km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("26. számú fiók", "1044 Budapest, Barta Béla utca 8.", "25km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("32. számú fiók", "1191 Budapest, Badacsonyi utca 56.", "28km","H-P: 8:00-17:00"));
        dataModels.add(new DataModelBranches("40. számú fiók", "2120 Dunakeszi, Fő utca 10.", "32km","H-P: 8:00-17:00"));*/


        adapter2 = new CustomAdapterBranches(dataModels, getActivity().getApplicationContext()); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben

        listView.setAdapter(adapter2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModelBranches dModel= dataModels.get(position);

                Snackbar.make(view,
                        //getString(R.string.opening_hours) +
                        dModel.getOpen(), Snackbar.LENGTH_LONG)
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