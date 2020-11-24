package com.example.halofarms.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.halofarms.Field;
import com.example.halofarms.MyDialogFragment;
import com.example.halofarms.Point;
import com.example.halofarms.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import java.util.ArrayList;

/**
 * Class displaying a form filling, one for each attribute of the point.
 */
public class InsertDataActivity extends AppCompatActivity implements View.OnClickListener {
    // id of point
    private int id;
    // field name
    private Field field;
    // attributes InputLayout
    private TextInputLayout EcTextInputLayout;
    private TextInputLayout SarInputLayout;
    private TextInputLayout PhTextInputLayout;
    private TextInputLayout CecTextInputLayout;
    // attributes EditText inside InputLayout
    private EditText EcEditText;
    private EditText SarEditText;
    private EditText PhEditText;
    private EditText CecEditText;
    // help image to the right of InputLayout
    private ImageButton CecImageButton;
    private ImageButton EcImageButton;
    private ImageButton SarImageButton;
    private ImageButton PhImageButton;
    // for save the result
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);
        // reference to actionbar, action bar to change title
        ActionBar actionBar = getSupportActionBar();
        // get intent from caller activity
        Intent intent = getIntent();
        id = intent.getIntExtra("ID_ZONE", 0);
        field = new Gson().fromJson(intent.getStringExtra("FIELD"), Field.class);
        // say which zone is ready to be filled
        if (actionBar != null) {
            actionBar.setTitle("Insert data for zone " + id);
        }
        // references to the views
        EcTextInputLayout = findViewById(R.id.text_input_layout_electric_conductivity);
        SarInputLayout = findViewById(R.id.text_input_layout_sar);
        PhTextInputLayout = findViewById(R.id.text_input_layout_ph);
        CecTextInputLayout = findViewById(R.id.text_input_layout_cec);
        EcEditText = findViewById(R.id.edit_text_electric_conductivity);
        SarEditText = findViewById(R.id.edit_text_sar);
        PhEditText = findViewById(R.id.edit_text_ph);
        CecEditText = findViewById(R.id.edit_text_cec);
        CecImageButton = findViewById(R.id.info_cec_image_button);
        SarImageButton = findViewById(R.id.info_sar_image_button);
        PhImageButton = findViewById(R.id.info_ph_image_button);
        EcImageButton = findViewById(R.id.info_ec_image_button);
        saveButton = findViewById(R.id.save_button);
        // listeners to views
        CecImageButton.setOnClickListener(this);
        SarImageButton.setOnClickListener(this);
        EcImageButton.setOnClickListener(this);
        PhImageButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == saveButton) {
            boolean filled = true;
            if (EcEditText.getText().toString().equals("")) {
                filled = false;
                EcTextInputLayout.setError("Missing field");
                Toast.makeText(this, "Some field is empty", Toast.LENGTH_SHORT).show();
            }
            if (SarEditText.getText().toString().equals("")) {
                filled = false;
                SarInputLayout.setError("Missing field");
                Toast.makeText(this, "Some field is empty", Toast.LENGTH_SHORT).show();
            }
            if (PhEditText.getText().toString().equals("")) {
                filled = false;
                PhTextInputLayout.setError("Missing field");
                Toast.makeText(this, "Some field is empty", Toast.LENGTH_SHORT).show();
            }
            if (CecEditText.getText().toString().equals("")) {
                filled = false;
                CecTextInputLayout.setError("Missing field");
                Toast.makeText(this, "Some field is empty", Toast.LENGTH_SHORT).show();
            }
            if (filled) {
                // update the point analyed
                updatePoint();
            }
        }
        // display help image
        if (v == CecImageButton) {
            // id for dialog
            showMyDialog("cec");
        }
        if (v == EcImageButton) {
            // id for dialog
            showMyDialog("ec");
        }
        if (v == SarImageButton) {
            // id for dialog
            showMyDialog("sar");
        }
        if (v == PhImageButton) {
            // id for dialog
            showMyDialog("ph");
        }
    }

    /**
     * Build Dialog showing an help image.
     * @param s identifier for dialog.
     */
    private void showMyDialog(String s) {
        Bundle bundle = new Bundle();
        bundle.putInt("WHAT", 2);
        bundle.putString("WHO", s);
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setArguments(bundle);
        myDialogFragment.show(getSupportFragmentManager(), "");
    }

    /**
     * Update the point just analyzed.
     */
    private void updatePoint() {
        // search the correct point thanks to id
        for (Point p : field.getPoints()) {
            if (p.getZoneId() == id) {
                // update values
                p.setEc(Float.parseFloat(EcEditText.getText().toString()));
                p.setSar(Float.parseFloat(SarEditText.getText().toString()));
                p.setPh(Float.parseFloat(PhEditText.getText().toString()));
                p.setCec(Float.parseFloat(CecEditText.getText().toString()));
                p.setAnalyze(false);
                ArrayList<String> strings = new ArrayList<>();
                strings.add(p.getZoneId() + "");
                strings.add("EC: " + p.getEc() + "\n" +
                        "SAR: " + p.getSar() + "\n" +
                        "pH: " + p.getPh() + "\n" +
                        "CEC: " + p.getCec());
                // bring back resulting point
                Intent intent = new Intent();
                intent.putStringArrayListExtra("ID", strings);
                intent.putExtra("FIELD", new Gson().toJson(field));
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}