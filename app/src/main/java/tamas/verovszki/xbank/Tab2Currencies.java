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
 * {@link Tab2Currencies.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab2Currencies#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab2Currencies extends Fragment {
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

    public Tab2Currencies() {
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
    public static Tab2Currencies newInstance(String param1, String param2) {
        Tab2Currencies fragment = new Tab2Currencies();
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
        View view = inflater.inflate(R.layout.fragment_tab2_currencies, container2, false);
        listView = (ListView) view.findViewById(R.id.list2);


        dataModels= new ArrayList<>();
        dataModels.add(new DataModelCurrencies("EUR", "318.76", "325.20","2018 November 25"));
        dataModels.add(new DataModelCurrencies("USD", "280.85", "286.52","2018 November 25"));
        dataModels.add(new DataModelCurrencies("GBP", "360.36", "367.64","2018 November 25"));
        dataModels.add(new DataModelCurrencies("AUD","203.00","207.10","2018 November 25"));
        dataModels.add(new DataModelCurrencies("BGN", "162.98", "166.27","2018 November 25"));
        dataModels.add(new DataModelCurrencies("CAD", "212.34", "216.63","2018 November 25"));
        dataModels.add(new DataModelCurrencies("CHF", "281.72", "287.41","2018 November 25"));
        dataModels.add(new DataModelCurrencies("CZK","12.28","12.52","2018 November 25"));
        dataModels.add(new DataModelCurrencies("DKK", "42.72", "43.58","2018 November 25"));
        dataModels.add(new DataModelCurrencies("HRK", "42.90", "43.76","2018 November 25"));
        dataModels.add(new DataModelCurrencies("JPY", "248.96", "253.98","2018 November 25"));
        dataModels.add(new DataModelCurrencies("NOK","32.75","33.41","2018 November 25"));
        dataModels.add(new DataModelCurrencies("PLN","74.20","75.70","2018 November 25"));
        dataModels.add(new DataModelCurrencies("RON", "68.44", "69.82","2018 November 25"));
        dataModels.add(new DataModelCurrencies("RSD", "2.70", "2.76","2018 November 25"));
        dataModels.add(new DataModelCurrencies("RUB", "4.27", "4.35","2018 November 25"));
        dataModels.add(new DataModelCurrencies("SEK", "30.93", "31.55","2018 November 25"));


        adapter = new CustomAdapterCurrencies(dataModels,getActivity().getApplicationContext()); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModelCurrencies dModel= dataModels.get(position);

                Snackbar.make(view,
                        getString(R.string.foreign_currency) + ":" + " " + dModel.getCurrency() + getString(R.string.tab) +
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