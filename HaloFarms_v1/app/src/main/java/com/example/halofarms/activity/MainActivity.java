package com.example.halofarms.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.halofarms.Field;
import com.example.halofarms.FieldList;
import com.example.halofarms.MyDialogFragment;
import com.example.halofarms.adapter.MainActivityAdapter;
import com.example.halofarms.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Class launched when user open the App.
 *  It connect the user to database with his Google Account,
 *  get data from Cloud Firestore and show it in a RecyclerView.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        MyDialogFragment.FieldCallback {
    // field shown in UI
    private MainActivityAdapter mAdapter;
    // bottom-right bottom (show a dialog)
    private FloatingActionButton addMapFab;
    // above of addMapFab
    private FloatingActionButton myPositionMapFab;
    // above of myPositionMapFab
    private FloatingActionButton reloadFab;
    // shown while getting from Firestore
    private ProgressBar progressBar;
    // username (email) of client
    private String username;
    // contains all field of user; this lists is shown in UI
    private final List<Field> fields = new ArrayList<>();
    // contains all fields with all analysis ("field1, [analysis1, analysis2...]").
    // It is shown in a dialog when a field is tapped
    private final List<FieldList> analyzedFields = new ArrayList<>();
    // file in which will be saved the username(account google) of logged user
    private SharedPreferences sharedPref;
    // send and get JSON object
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout containing UI items
        setContentView(R.layout.activity_main);
        // reference to preferences
        sharedPref = getApplicationContext()
                .getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        // references to UI items
        addMapFab = findViewById(R.id.add_map_fab);
        progressBar = findViewById(R.id.progress_bar);
        reloadFab = findViewById(R.id.reload_fab);
        myPositionMapFab = findViewById(R.id.my_position_map_fab);
        // build the recyclerView (list) on UI
        RecyclerView recyclerView = findViewById(R.id.map_name_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MainActivityAdapter(fields);
        recyclerView.setAdapter(mAdapter);
        // set listeners
        addMapFab.setOnClickListener(this);
        myPositionMapFab.setOnClickListener(this);
        reloadFab.setOnClickListener(this);
        mAdapter.setOnItemClickListener(onItemClickListener);
        // user's login
        auth();
    }

    /***
     * Connect the user with his google account to Cloud Firestore.
     */
    private void auth() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                0);
    }

    /**
     * Waiting for result from another Activity.
     * @param requestCode indicates the operation required.
     * @param resultCode of operation.
     * @param data eventually extras get back.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get the result of google login (auth method)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in => get instance of user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // save the email that will be used as unique key for accessing data in db
                if (user != null) {
                    sharedPref.edit().putString("USERNAME", username = user.getEmail()).apply();
                    // get his maps from database
                    readMapsFromFirestore();
                }
            } else {
                Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            }
        }
        // update list shows items in page => coming back from MapsActivity
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // get result from MapsActivity back ALL FIELD! AREA NAME + ADDRESS + POINTS
                Field field = gson.fromJson(data.getStringExtra("FIELD"), Field.class);
                Log.d("TAG", field.getName());

                // this Object could be to big for being passed as Extra, so it is shared between
                // activities with preferences
                FieldList fieldList =  gson
                        .fromJson(sharedPref.getString("FIELD_WITH_DATE", null),
                                FieldList.class);
                // once got, remove it from preferences
                sharedPref.edit().remove("FIELD_WITH_DATE").apply();
                // update the field (maybe only a point is changed)
                for (int i = 0; i < fields.size(); i++) {
                    if (field.getName().equals(fields.get(i).getName())) {
                        fields.set(i, field);
                        break;
                    }
                }
                // no previous found
                if (!fields.contains(field)) {
                    Log.d("TAG", field.getName());
                    fields.add(field);
                }
                // update above field with analysis
                for (int i = 0; i < analyzedFields.size(); i++) {
                    if (analyzedFields.get(i).getName().equals(fieldList.getName())) {
                        analyzedFields.set(i, fieldList);
                        break;
                    }
                }
                if (!analyzedFields.contains(fieldList)) {
                    analyzedFields.add(fieldList);
                }
            } else if (resultCode == RESULT_FIRST_USER) {
                // if the map was opened but not draw the field
                Field field = gson.fromJson(data.getStringExtra("FIELD"), Field.class);
                fields.remove(field);
            }
            // update UI
            mAdapter.notifyDataSetChanged();
        }
    }


    /***
     *  Get user's maps maps from Firestore, access to his entry in db:
     *  username => fields => analysis.
     *  Every time this method is called, the lists containing fields shown in UI and
     *  the one containing all field with all analysis will be cleaned
     *  because there is a new get from db. So the UI will be refreshed.
     */
    private void readMapsFromFirestore() {
        // clean list
        fields.clear();
        analyzedFields.clear();
        // reference to user's entry in database
        FirebaseFirestore.getInstance()
                .collection(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // iterate over user's documents
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    // get all fields with the date of analysis of this document
                                    FieldList fieldList = document
                                            .toObject(FieldList.class);
                                    // add field to the list shown in UI
                                    fields.add(fieldList.getFields().get(0));
                                    // add all field to the list shown in dialog
                                    // when tap on an element of above list
                                    analyzedFields.add(fieldList);
                                    // update the UI
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                            // hide progress bar (end of get)
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


    /**
     * This method deletes a document from database.
     * Take reference to user entry and delete an entire document
     * @param areaName: field to delete.
     */
    private void deleteFromFirebase(final String areaName) {
        FirebaseFirestore.getInstance()
                .collection(username)
                .document(areaName)
                .delete();
    }


    /**
     * Method associated to the recycler view; it is called when an item on list is clicked.
     * It is linked to the list thanks to MainAdapter.
     * If the red ImageButton is clicked, the item will be deleted from list and database.
     * If the entire item is clicked, show a dialog that ask which analysis open in map.
     */
    public View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // reference to Object tapped
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            // index of item clicked
            int i = viewHolder.getAdapterPosition();
            // click on remove button => delete elements from db and ui list
            if (view.getId() == R.id.remove_image_button) {
                // get the name of field shown in the list
                String areaName = fields.get(i).getName();
                // remove from list
                fields.remove(i);
                // update UI (recycler view)
                mAdapter.notifyDataSetChanged();
                // delete from db
                deleteFromFirebase(areaName);
            } else {
                // if click on item list show a dialog that ask which day of analysis show in map
                // reference to field tapped
                Field field = fields.get(i);
                // search the position of field tapped in the total list;
                // then open a dialog showing date of analysis of field
                for (FieldList fieldList : analyzedFields) {
                    if (field.getName().equals(fieldList.getName())) {
                        // container of message to bring to dialog
                        Bundle bundle = new Bundle();
                        // used as ID: for identify what show on dialog
                        bundle.putInt("WHAT", 3);
                        // convert field and hitsis analysis in string for efficiency
                        bundle.putString("FIELD", gson.toJson(field));
                        bundle.putString("FIELD_WITH_DATE", gson.toJson(fieldList));
                        // create instance of custom dialog
                        MyDialogFragment myDialogFragment = new MyDialogFragment();
                        // pass the arguments
                        myDialogFragment.setArguments(bundle);
                        // show dialog
                        myDialogFragment.show(getSupportFragmentManager(), "");
                        break;
                    }
                }
            }
        }
    };

    /**
     * Listener associated to some View when clicked.
     * @param v the view clicked
     */
    @Override
    public void onClick(View v) {
        // tapped on this fab
        if (v == addMapFab) {
            // container of message to bring to dialog
            Bundle bundle = new Bundle();
            // used as ID: for identify what show on dialog
            bundle.putInt("WHAT", 0);
            // create instance of custom dialog
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            // pass the arguments
            myDialogFragment.setArguments(bundle);
            // show dialog
            myDialogFragment.show(getSupportFragmentManager(), "");
        }
        // tapped on this fab
        if (v == myPositionMapFab) {
            // container of message to bring to dialog
            Bundle bundle = new Bundle();
            // used as ID: for identify what show on dialog
            bundle.putInt("WHAT", 1);
            // create instance of custom dialog
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            // pass the arguments
            myDialogFragment.setArguments(bundle);
            // show dialog
            myDialogFragment.show(getSupportFragmentManager(), "");
        }
        // tapped on this fab
        if (v == reloadFab) {
            // show loading bar until getting from db isn't ended
            progressBar.setVisibility(View.VISIBLE);
            // reload from database
            readMapsFromFirestore();
        }
    }

    /**
     * Update field with his analysis when tap on an item in list shown in dialog.
     * This callback is called by MyDialogFragment.class.
     * This is the correct way to get result from a Dialog.
     */
    @Override
    public void updateFieldWith(FieldList fieldList) {
        // search in which position is the field clicked, and update with the new onw
        for (int i = 0; i < analyzedFields.size(); i++) {
            if (analyzedFields.get(i).getName().equals(fieldList.getName())) {
                analyzedFields.set(i, fieldList);
                break;
            }
        }

    }
}