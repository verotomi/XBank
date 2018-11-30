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

    // listview-hez kellő változók
    ArrayList<DataModelCurrencies> dataModels;
    ListView listView;
    private static CustomAdapterCurrencies adapter;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // ide kellett berakni azt, amit ebben a fragment-ben meg akarok jeleníteni.
        // a szükséges változók kicsit feljebb vannak inicializálva
        View view = inflater.inflate(R.layout.fragment_tab1_currencies, container, false);
        listView = (ListView) view.findViewById(R.id.list1);


        dataModels= new ArrayList<>();
        dataModels.add(new DataModelCurrencies("EUR", "312.96", "331.00","2018 November 25"));
        dataModels.add(new DataModelCurrencies("USD", "275.74", "291.63","2018 November 25"));
        dataModels.add(new DataModelCurrencies("GBP", "353.80", "374.19","2018 November 25"));
        dataModels.add(new DataModelCurrencies("AUD","198.28","211.81","2018 November 25"));
        dataModels.add(new DataModelCurrencies("BGN", "155.90", "173.35","2018 November 25"));
        dataModels.add(new DataModelCurrencies("CAD", "207.40", "221.56","2018 November 25"));
        dataModels.add(new DataModelCurrencies("CHF", "275.17", "293.95","2018 November 25"));
        dataModels.add(new DataModelCurrencies("CZK","11.74","13.06","2018 November 25"));
        dataModels.add(new DataModelCurrencies("DKK", "41.73", "44.57","2018 November 25"));
        dataModels.add(new DataModelCurrencies("HRK", "41.03", "45.63","2018 November 25"));
        dataModels.add(new DataModelCurrencies("JPY", "243.17", "259.77","2018 November 25"));
        dataModels.add(new DataModelCurrencies("NOK","31.99","34.17","2018 November 25"));
        dataModels.add(new DataModelCurrencies("PLN","70.97","78.92","2018 November 25"));
        dataModels.add(new DataModelCurrencies("RON", "65.46", "72.79","2018 November 25"));
        dataModels.add(new DataModelCurrencies("RSD", "2.58", "2.87","2018 November 25"));
        dataModels.add(new DataModelCurrencies("RUB", "4.08", "4.54","2018 November 25"));
        dataModels.add(new DataModelCurrencies("SEK", "30.21", "32.27","2018 November 25"));


        adapter = new CustomAdapterCurrencies(dataModels,getActivity().getApplicationContext()); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModelCurrencies dModel= dataModels.get(position);

                Snackbar.make(view,
                        getString(R.string.currency) + ":" + " " + dModel.getCurrency() + getString(R.string.tab) +
                                getString(R.string.text_buy) + " " + dModel.getBuy() + getString(R.string.tab) +
                                getString(R.string.text_sell) + " " + dModel.getSell(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
        return view;
        // fragment-ben megjelenítendő rész vége
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
}