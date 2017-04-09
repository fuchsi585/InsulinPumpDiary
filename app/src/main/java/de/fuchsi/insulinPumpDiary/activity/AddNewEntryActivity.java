package de.fuchsi.insulinPumpDiary.activity;

import de.fuchsi.insulinPumpDiary.R;
import de.fuchsi.insulinPumpDiary.database.Entry;
import de.fuchsi.insulinPumpDiary.views.GraphView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Arrays;


public class AddNewEntryActivity extends AppCompatActivity implements OnItemSelectedListener{
    private static final String LOG_TAG = AddNewEntryActivity.class.getSimpleName();
    public static final String STEPSIZEVALUE = "STEPSIZEVALUE";

    private double[] data = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] stepSizes = {0.01,0.05,0.10};

    private Spinner stepSizeSpinner;
    private EditText nameEditText;
    private GraphView plotView;
    private Switch activeSwitch;
    private boolean mUpdateEntry = false;
    private Entry mEntryUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_entry);

        plotView = (GraphView) findViewById(R.id.graphView);
        stepSizeSpinner = (Spinner) findViewById(R.id.spinner);
        nameEditText = (EditText) findViewById(R.id.add_name_txt);
        activeSwitch = (Switch) findViewById(R.id.switchActive);

        stepSizeSpinner.setOnItemSelectedListener(this);
        stepSizeSpinner.setSelection((int) MainActivity.getPreference(STEPSIZEVALUE));

        Log.d(LOG_TAG, "Has update intent: " + getIntent().hasExtra(Entry.UPDATE));
        if (getIntent().hasExtra(Entry.UPDATE)){
            mUpdateEntry = true;
            mEntryUpdate = (Entry) getIntent().getExtras().getSerializable(Entry.UPDATE);
            nameEditText.setText(mEntryUpdate.getName());
            activeSwitch.setChecked(mEntryUpdate.getActive());
            plotView.initializeData(mEntryUpdate.getBasalRateArray());
        }
        else plotView.initializeData(data);
    }

    public void onDecreaseCursor(View v){
        plotView.decreaseCursor();
    }
    public void onIncreaseValue(View v){
        plotView.increaseValue(stepSizes[stepSizeSpinner.getSelectedItemPosition()]);
    }
    public void onDecreaseValue(View v){
        plotView.decreaseValue(stepSizes[stepSizeSpinner.getSelectedItemPosition()]);
    }
    public void onIncreaseCursor(View v){
        plotView.increaseCursor();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_entry, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem deleteActionBarItem = menu.findItem(R.id.action_delete);
        MenuItem copyActionBarItem   = menu.findItem(R.id.action_copy);
        MenuItem shiftActionBarItem  = menu.findItem(R.id.action_shift);
        if (mUpdateEntry) {
            deleteActionBarItem.setVisible(true);
            copyActionBarItem.setVisible(true);
            shiftActionBarItem.setVisible(true);
        }
        else {
            deleteActionBarItem.setVisible(false);
            copyActionBarItem.setVisible(false);
            shiftActionBarItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (nameEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Bitte Namen eintragen!", Toast.LENGTH_LONG).show();
        }
        else {
            if (id == R.id.action_done) {
                saveEntryToDatabase(mUpdateEntry);
                return true;
            } else if (id == R.id.action_delete) {
                deleteEntry();
                return true;
            } else if (id == R.id.action_copy) {
                saveEntryToDatabase(false);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void deleteEntry(){
        Intent intent = new Intent();
        intent.putExtra(Entry.DELETE, String.valueOf(mEntryUpdate.getId()));
        setResult(RESULT_OK, intent);
        finish();
    }
    private void saveEntryToDatabase(boolean bEdit){
        Log.d(LOG_TAG,"Eintrag editieren: " + String.valueOf(bEdit));
        if (bEdit){
            mEntryUpdate.setName(nameEditText.getText().toString());
            mEntryUpdate.setBasalRateArray(plotView.getData());
            mEntryUpdate.setActive(activeSwitch.isChecked());
            Intent updateIntent = new Intent();
            updateIntent.putExtra(Entry.UPDATE, mEntryUpdate);
            setResult(RESULT_OK, updateIntent);
            finish();
        }else{
            Intent intent = new Intent();
            Entry newEntry = new Entry(-1,nameEditText.getText().toString(), Arrays.toString(plotView.getData()),
                                        activeSwitch.isChecked());
            intent.putExtra(Entry.NEW, newEntry);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    @Override
    public void onStart() {
        if (mUpdateEntry) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        super.onStart();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        MainActivity.setPreferences(STEPSIZEVALUE, position);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}
