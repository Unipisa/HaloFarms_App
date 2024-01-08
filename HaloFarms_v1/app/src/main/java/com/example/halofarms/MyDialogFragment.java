package com.example.halofarms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.halofarms.activity.MapsActivity;
import com.example.halofarms.adapter.AnalyzedFieldAdapter;
import com.example.halofarms.adapter.DateAdapter;
import com.example.halofarms.adapter.StringAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for every Dialog shown on app.
 */
public class MyDialogFragment extends DialogFragment {
    // this interfaces permits to "bring" items in Activities

    // update the date of analysis (MapsActivity)
    public interface DateCallback {
        // set the date of analysis
        void dateSelected(String string);
    }

    // update item in MainActivity
    public interface FieldCallback {
        void updateFieldWith(FieldList fieldList);
    }

    // link with activities
    private DateCallback dateCallback;
    private FieldCallback fieldCallback;

    // list containing places searched
    private final ArrayList<String> places = new ArrayList<>();
    // get the name inserted as field (needed for recycler view)
    private EditText nameEditText;
    // dates of one field, if user delete a date in dialog
    // this reference update lists in MainActivity
    private FieldList fieldList;
    // adapter showing dates of analysis of field tapped
    private AnalyzedFieldAdapter mAdapter;

    // empty constructor
    public MyDialogFragment(){}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        // associate callback to activities
        if (activity != null) {
            if (activity.getClass().getName()
                    .equals("com.example.halofarms.activity.MainActivity")) {
                // for updating field
                fieldCallback = (FieldCallback) context;
            } else if (getActivity().getClass().getName()
                    .equals("com.example.halofarms.activity.MapsActivity")) {
                // for setting date of analysis
                dateCallback = (DateCallback) context;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // an activity open dialog with argument put in a Bundle
        Bundle bundle = getArguments();
        int id = -1;
        if (bundle != null) {
            id = bundle.getInt("WHAT");
        }
        switch (id) {
            // users insert name of field and its address, no GPS required
            case 0: {
                // build a customized UI for dialog
                View v = LayoutInflater.from(getContext())
                        .inflate(R.layout.add_map_fragment_layout, null);
                // list containing places searched in address EditText
                RecyclerView recyclerView = v.findViewById(R.id.found_map_names_recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                final StringAdapter mAdapter = new StringAdapter(places);
                recyclerView.setAdapter(mAdapter);
                // click listener: when tapped on the address start MapsActivity
                mAdapter.setOnItemClickListener(onItemClickListener);
                EditText addressEditText = v.findViewById(R.id.edit_text_add_address);
                nameEditText = v.findViewById(R.id.edit_text_name_address);
                // update place list while typing
                addressEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(final Editable s) {
                        // clear list
                        places.clear();
                        // convert string address in latitude and longitude
                        final Geocoder geocoder = new Geocoder(getContext());
                        // geocoding can be a long task, so run in a separate thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // take the address typed in addressEditText and search on maps
                                    List<Address> addresses = geocoder
                                            .getFromLocationName(s.toString(), 10);
                                    // show the addresses found
                                    for (Address address : addresses) {
                                        String addressToString = address.getAddressLine(0);
                                        // add to the list displayed if not already in
                                        if (!places.contains(addressToString)) {
                                            places.add(addressToString);
                                            // only UI thread can modify UI (recycler view)
                                            // so call it to do the task
                                            FragmentActivity fragmentActivity = getActivity();
                                            if (fragmentActivity != null) {
                                                fragmentActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // update ui (item in list)
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                // build the dialog
                return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        .setTitle("Add a field")
                        .setView(v)
                        .create();
            }
            // user want to show his location, gps needed
            case 1: {
                // build custom layout
                View v = LayoutInflater.from(getContext())
                        .inflate(R.layout.single_edit_text, null);
                final EditText fieldNameEditText = v.findViewById(R.id.edit_text_name_address);
                return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        // set title of dialog
                        .setTitle("Add field name")
                        // set custom layout
                        .setView(v)
                        // set positive button with a listener
                        .setPositiveButton("save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // get the field name in edit text; initialize field
                                Field field = new Field(fieldNameEditText.getText().toString(),
                                        null, null);
                                // intent to start MapsActivity with extras data
                                Intent intent = new Intent(getContext(), MapsActivity.class);
                                // indicate if map isn't drawn: new field
                                intent.putExtra("IS_MAP_DRAWN", false);
                                // use GPS
                                intent.putExtra("POSITION", true);
                                // pass field object as string (JSON)
                                intent.putExtra("FIELD", new Gson().toJson(field));
                                Objects.requireNonNull(getActivity())
                                .startActivityForResult(intent,1);
                            }
                        })
                        .create();
            }
            // when an user inserts result of analysis and
            // tap on info image button in InsertDataActivity
            case 2: {
                // which image button clicked
                String s = bundle.getString("WHO");
                // get correct image to show
                ImageView v = (ImageView) LayoutInflater
                        .from(getContext()).inflate(R.layout.single_image_view, null);
                // set image in dialog to show
                switch (Objects.requireNonNull(s)) {
                    case "ec" :
                        v.setImageResource(R.drawable.ec);
                        break;
                    case "ph":
                        v.setImageResource(R.drawable.ph);
                        break;
                    case "sar":
                        v.setImageResource(R.drawable.sar);
                        break;
                    case "cec":
                        v.setImageResource(R.drawable.cec);
                }
                return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        .setView(v)
                        .create();
            }
            // user tap a field in MainActivity, show which date show in map
            case 3:
                // listing of date of one field
                final String jsonFieldWithDate = bundle.getString("FIELD_WITH_DATE");
                fieldList = new Gson()
                        .fromJson(jsonFieldWithDate, FieldList.class);
                RecyclerView recyclerView = new RecyclerView(Objects.requireNonNull(getContext()));
                // show all analysis
                mAdapter = new AnalyzedFieldAdapter(fieldList.getFields());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(mAdapter);
                // when user tap on a date, start MapsActivity that will show chosen analysis
                mAdapter.setOnItemClickListener(onDateClickListener);
                return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    // title
                    .setTitle("Select an analysis")
                    // set views as a list
                    .setView(recyclerView)
                    // listener
                    .setPositiveButton("new", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // new field is permitted only if all field in list
                            // are already analyzed
                            boolean found = false;
                            for (Field field : fieldList.getFields()) {
                                if (field.getDate().equals("Not yet analyzed")) {
                                    found = true;
                                    break;
                                }
                            }
                            // new analysis is permitted
                            if (!found) {
                                // intent with extras
                                Intent intent = new Intent(getContext(), MapsActivity.class);
                                intent.putExtra("IS_MAP_DRAWN", true);
                                intent.putExtra("NEW_ANALYSIS", true);
                                intent.putExtra("FIELD", new Gson()
                                        .toJson(fieldList.getFields().get(0)));
                                // fieldWithDate is to big for to be passed as a extra
                                // put it in reference files
                                // reference to preferences
                                SharedPreferences sharedPref = Objects.requireNonNull(getContext())
                                        .getSharedPreferences(
                                                getString(R.string.preference_file_key),
                                                Context.MODE_PRIVATE);
                                sharedPref.edit().remove("FIELD_WITH_DATE").apply();
                                // put element
                                sharedPref.edit()
                                        .putString("FIELD_WITH_DATE", jsonFieldWithDate)
                                        .apply();
                                FragmentActivity fragmentActivity = getActivity();
                                if (fragmentActivity != null) {
                                    dialog.cancel();
                                    fragmentActivity
                                            .startActivityForResult(intent, 1);
                                }
                            }
                        }
                    }).create();
            // indicates points to analyze in map (yellow point)
            case 4:
                // custom layout
                View v = LayoutInflater.from(getContext()).inflate(R.layout.set_date_view, null);
                RecyclerView recyclerViewDate = v.findViewById(R.id.date_recycler_view);
                recyclerViewDate.setHasFixedSize(true);
                recyclerViewDate.setLayoutManager(new LinearLayoutManager(v.getContext()));
                // get id of points to analyze
                final DateAdapter mAdapter = new DateAdapter(
                        bundle.getStringArrayList("POINT_TO_ANALYZE"));
                recyclerViewDate.setAdapter(mAdapter);
                // set a default date (today)
                final EditText dateEditText = v.findViewById(R.id.edit_text_date);
                // Display a date in day, month, year format
                Calendar c = Calendar.getInstance();
                dateEditText.setText(c.get(Calendar.DATE) + "/" +
                        (c.get(Calendar.MONTH) + 1)+ "/" + c.get(Calendar.YEAR));
                return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    .setView(v)
                    .setTitle("Point to analyze")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // set date of analysis in MapsActivity
                            dateCallback.dateSelected(dateEditText.getText().toString());
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
            // button QR CODE clicked; show point's QR CODE
            case 5:
                String pId = bundle.getString("SHOW_QR_CODE");
                ImageView imageView = new ImageView(getContext());
                Bitmap bitmap = null;
                try {
                    bitmap = textToImage(pId);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
                    return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        .setView(imageView).create();

        }
        return new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle("Something go wrong, restart App")
                .create();
    }

    /**
     * Take a string and convert it to a QR code
     * @param text JSON identifier of point
     * @return bitmap representing QR code
     */
    private Bitmap textToImage(String text) throws WriterException, NullPointerException,
            IllegalArgumentException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,
                500, 500, null);
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        int colorWhite = 0xFFFFFFFF;
        int colorBlack = 0xFF000000;
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? colorBlack : colorWhite;
            }
        }
        Bitmap bitmap = Bitmap
                .createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    /**
     * If user tap on red image button near date,
     * that day analysis will be removed from list and from database.
     * If user tap on a date, MapsActivity will be shown with the analysis chosen
     */
    private final View.OnClickListener onDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            // position clicked
            int position = viewHolder.getAdapterPosition();
            // get analysis associated to date clicked
            Field field = fieldList.getFields().get(position);
            // if red icon button is clicked
            if (v.getId() == R.id.remove_image_button) {
                // remove from list and from cloud Firestore
                if (!field.getDate().equals("Not yet analyzed")) {
                    // if is the unique analysis, reset the point
                    if (fieldList.getFields().size() == 1) {
                        field.setDate("Not yet analyzed");
                        for (Point p : field.getPoints()) {
                            p.setAnalyze(false);
                            p.setCec(0);
                            p.setEc(0);
                            p.setPh(0);
                            p.setSar(0);
                        }
                    } else {
                        // remove from list
                        fieldList.getFields().remove(position);
                    }
                    // save to database, get username from preferences
                    FirebaseFirestore.getInstance().collection(Objects
                            .requireNonNull(getActivity()).
                            getApplicationContext().getSharedPreferences(
                            getString(R.string.preference_file_key),
                            Context.MODE_PRIVATE).getString("USERNAME", ""))
                            .document(field.getName())
                            .set(fieldList);
                    mAdapter.notifyDataSetChanged();
                    // update the list in main activity
                    fieldCallback.updateFieldWith(fieldList);

                }
            // click on date
            } else {
                // intent with extra
                Intent intent = new Intent(getContext(), MapsActivity.class);
                // map is surely drawn
                intent.putExtra("IS_MAP_DRAWN", true);
                // put field
                intent.putExtra("FIELD", new Gson().toJson(field));
                // check if field is to analyze or no
                if (field.getDate().equals("Not yet analyzed")) {
                    // fieldWithDate is to big for to be passed as a extra
                    // put it in reference files
                    // reference to preferences
                    SharedPreferences sharedPref = Objects.requireNonNull(getActivity())
                            .getSharedPreferences(
                                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    sharedPref.edit().remove("FIELD_WITH_DATE").apply();
                    sharedPref.edit().putString("FIELD_WITH_DATE", new Gson()
                            .toJson(fieldList)).apply();
                    getActivity().startActivityForResult(intent, 1);
                } else {
                    intent.putExtra("EDITABLE", false);
                    startActivity(intent);
                }
                Objects.requireNonNull(getDialog()).cancel();
            }
        }
    };

    /**
     * Listener associated to list shown when user search an address.
     * When user tap on a shown address, launch map in this location.
     */
    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            // create field with name and address in edit text
            Field field = new Field(nameEditText.getText().toString(), places.get(position),
                    null);
            Intent intent = new Intent(getContext(), MapsActivity.class);
            // tell map isn't yet drawn
            intent.putExtra("IS_MAP_DRAWN", false);
            // put field as extra
            intent.putExtra("FIELD", new Gson().toJson(field));
            Objects.requireNonNull(getDialog()).cancel();
            Objects.requireNonNull(getActivity()).startActivityForResult(intent,1);
        }
    };
}