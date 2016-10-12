package com.example.android.vinter_1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class IMFFragment_v2 extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = IMFFragment_v2.class.getSimpleName();


    private static final int NUM_SPINNERS = 20;

    private Spinner[] spinners = new Spinner[NUM_SPINNERS];
    private TextView summa1TView, summa2TView, summa3TView, summa4TView, summaTotalTView;
    private Button dateButton;
    private AppCompatImageButton helpButton;
    private int year, month, day;

    public IMFFragment_v2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        // Check whether form is IN or OUT, and change background color accordingly
        int fragmentType = getArguments().getInt("type");
        long before = System.currentTimeMillis();
        if (fragmentType == 0) {
            rootView = inflater.inflate(R.layout.fragment_imf_in_v2, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_imf_out_v2, container, false);
        }
        long after = System.currentTimeMillis();
        Log.d(LOG_TAG, "\nV2 - Elapsed time: " + String.valueOf(after - before));

        setupUI(rootView);

        return rootView;
    }

    private void setupUI(View view) {
        /*
        // help button
        helpButton = (AppCompatImageButton) view.findViewById(R.id.imf_help_button);
        // TODO: Only in form has a button so far. That is why we check if null
        if (helpButton != null)
            helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Patientent ska vara...").
                            setTitle("IMF Manual").
                            setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }
            });
            */

        // spinner 1
        spinners[0] = (Spinner) view.findViewById(R.id.spinner1H);
        /*
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_1, R.layout.custom_spinner_item);
        spinners[0].setAdapter(spinnerAdapter);
        */

        spinners[1] = (Spinner) view.findViewById(R.id.spinner1V);
        /*
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_1, R.layout.custom_spinner_item);
        spinners[1].setAdapter(spinnerAdapter);
        */

        // summa 1
        summa1TView = (TextView) view.findViewById(R.id.summa1);

        // spinner 2
        spinners[2] = (Spinner) view.findViewById(R.id.spinner2);
        /*
        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_2, R.layout.custom_spinner_item);
        spinners[2].setAdapter(spinnerAdapter);
        */
        // spinner 3
        spinners[3] = (Spinner) view.findViewById(R.id.spinner3H);
        /*
        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_3, R.layout.custom_spinner_item);
        spinners[3].setAdapter(spinnerAdapter);
        */

        spinners[4] = (Spinner) view.findViewById(R.id.spinner3V);
        /*
        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_3, R.layout.custom_spinner_item);
        spinners[4].setAdapter(spinnerAdapter);
        */

        // spinner 4
        spinners[5] = (Spinner) view.findViewById(R.id.spinner4H);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_4, R.layout.custom_spinner_item);
//        spinners[5].setAdapter(spinnerAdapter);

        spinners[6] = (Spinner) view.findViewById(R.id.spinner4V);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_4, R.layout.custom_spinner_item);
//        spinners[6].setAdapter(spinnerAdapter);

        // spinner 5
        spinners[7] = (Spinner) view.findViewById(R.id.spinner5);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_5, R.layout.custom_spinner_item);
//        spinners[7].setAdapter(spinnerAdapter);

        // spinner 6
        spinners[8] = (Spinner) view.findViewById(R.id.spinner6H);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_6, R.layout.custom_spinner_item);
//        spinners[8].setAdapter(spinnerAdapter);

        // summa 2
        summa2TView = (TextView) view.findViewById(R.id.summa2);

        spinners[9] = (Spinner) view.findViewById(R.id.spinner6V);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_6, R.layout.custom_spinner_item);
//        spinners[9].setAdapter(spinnerAdapter);

        // spinner 7
        spinners[10] = (Spinner) view.findViewById(R.id.spinner7H);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_7, R.layout.custom_spinner_item);
//        spinners[10].setAdapter(spinnerAdapter);

        spinners[11] = (Spinner) view.findViewById(R.id.spinner7V);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_7, R.layout.custom_spinner_item);
//        spinners[11].setAdapter(spinnerAdapter);

        // spinner 8
        spinners[12] = (Spinner) view.findViewById(R.id.spinner8);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_8, R.layout.custom_spinner_item);
//        spinners[12].setAdapter(spinnerAdapter);

        // spinner 9
        spinners[13] = (Spinner) view.findViewById(R.id.spinner9);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_9, R.layout.custom_spinner_item);
//        spinners[13].setAdapter(spinnerAdapter);

        // spinner 10
        spinners[14] = (Spinner) view.findViewById(R.id.spinner10);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_10, R.layout.custom_spinner_item);
//        spinners[14].setAdapter(spinnerAdapter);

        // summa 3
        summa3TView = (TextView) view.findViewById(R.id.summa3);

        // spinner 11
        spinners[15] = (Spinner) view.findViewById(R.id.spinner11);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_11, R.layout.custom_spinner_item);
//        spinners[15].setAdapter(spinnerAdapter);

        // spinner 12
        spinners[16] = (Spinner) view.findViewById(R.id.spinner12H);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_12, R.layout.custom_spinner_item);
//        spinners[16].setAdapter(spinnerAdapter);

        spinners[17] = (Spinner) view.findViewById(R.id.spinner12V);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_12, R.layout.custom_spinner_item);
//        spinners[17].setAdapter(spinnerAdapter);

        // spinner 13
        spinners[18] = (Spinner) view.findViewById(R.id.spinner13H);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_13, R.layout.custom_spinner_item);
//        spinners[18].setAdapter(spinnerAdapter);

        spinners[19] = (Spinner) view.findViewById(R.id.spinner13V);
//        spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_values_13, R.layout.custom_spinner_item);
//        spinners[19].setAdapter(spinnerAdapter);

        // summa 4
        summa4TView = (TextView) view.findViewById(R.id.summa4);

        // summa total
        summaTotalTView = (TextView) view.findViewById(R.id.summa_total);

        /*
        // date button
        dateButton = (Button) view.findViewById(R.id.date_button);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        dateButton.setText(dateStr);
        */

        // register spinners listeners
        for (int i = 0; i < NUM_SPINNERS; i++) {
            spinners[i].setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
