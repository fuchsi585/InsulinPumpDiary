package de.fuchsi.basal_rate_db.activity;

import de.fuchsi.basal_rate_db.R;
import de.fuchsi.basal_rate_db.adapter.EntryListAdapter;
import de.fuchsi.basal_rate_db.database.Entry;
import de.fuchsi.basal_rate_db.database.EntryDataSource;
import de.fuchsi.basal_rate_db.dialog.AboutDialog;
import de.fuchsi.basal_rate_db.dialog.SortListViewDialog;
import de.fuchsi.basal_rate_db.listener.EntryListClickListener.OnEntryListClickListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnEntryListClickListener{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String SORT_ORDER = "SORT_ORDER";

    public EntryDataSource dataSource;
    static int mSortOrder;
    static List<Entry> entries;
    static EntryListAdapter mListAdapter;
    static SharedPreferences preferences;
    static SharedPreferences.Editor prefEditor;


    RecyclerView entryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor  = preferences.edit();
        mSortOrder  = (int) getPreference(SORT_ORDER);

        dataSource = new EntryDataSource(this);
        dataSource.open();
        entries = dataSource.getAllEntries();

        entryList = (RecyclerView) findViewById(R.id.entryList);
        entryList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        entryList.setLayoutManager(mLayoutManager);
        mListAdapter = new EntryListAdapter(getApplicationContext(), entries, this);
        entryList.setAdapter(mListAdapter);

        sortList(mSortOrder);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), AddNewEntryActivity.class),1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Bundle b;
        Entry intentData;

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                b = data.getExtras();
                if (data.hasExtra(Entry.NEW) && (b != null)) {
                    intentData = (Entry) b.getSerializable(Entry.NEW);
                    if (intentData != null) {
                        Log.d(LOG_TAG, "Create Entry!");
                        dataSource.createEntry(intentData.getName(), intentData.getBasalRateArrayString(), intentData.getActive());
                        mListAdapter.add(mListAdapter.getItemCount(),intentData);
                        sortList(mSortOrder);
                    }
                }
            } else if (requestCode == 2) {
                b = data.getExtras();
                if (data.hasExtra(Entry.UPDATE) && (b != null)) {
                    intentData = (Entry) b.getSerializable(Entry.UPDATE);
                    if (intentData != null) {
                        Log.d(LOG_TAG, "Update Entry!");
                        dataSource.updateEntry(intentData);
                    }
                }
                else if (data.hasExtra(Entry.DELETE) && (b != null)){
                    Log.d(LOG_TAG, "Delete Entry!");
                    dataSource.deleteEntry(b.getString(Entry.DELETE));
                }
                refreshList();
            }
        }
    }

    @Override
    public void onEntryListClicked(View aView, int position) {
        mListAdapter.notifyItemChanged(position);
        Entry editEntry = mListAdapter.getItem(position);
        Intent editActivity = new Intent(this, AddNewEntryActivity.class);
        editActivity.putExtra(Entry.UPDATE, editEntry);
        startActivityForResult(editActivity,2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            //startActivity(new Intent(this, SettingsActivity.class));
            SortListViewDialog sortListViewDialog = new SortListViewDialog();
            sortListViewDialog.showDialog(this, mSortOrder);
            return true;
        }
        else if(id == R.id.action_about){
            //startActivity(new Intent(this, AboutActivity.class));
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.showDialog(this," ");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //nur bei app start
    public boolean onPrepareOptionsMenu(Menu menu)    {
        MenuItem actionSortItem= menu.findItem(R.id.action_sort);
        if (entries.size() > 1) actionSortItem.setVisible(true);
        else actionSortItem.setVisible(false);
        return true;
    }
    private void refreshList(){
        entries = dataSource.getAllEntries();
        mListAdapter.updateAdapter(entries);
        sortList(mSortOrder);
    }
    public static void sortList(int order){
        Collections.sort(entries, new Sorter(order));
        mListAdapter.notifyDataSetChanged();
        setPreferences(SORT_ORDER, order);
        mSortOrder = order;
    }
    public static void setPreferences(String pref, Object value){
        if (pref.equals(SORT_ORDER) || pref.equals(AddNewEntryActivity.STEPSIZEVALUE)){
            prefEditor.putInt(pref, (int) value);
        }
        prefEditor.apply();
    }

    public static Object getPreference(String pref){
        if (pref.equals(SORT_ORDER))
            return preferences.getInt(pref,-1);
        else if (pref.equals(AddNewEntryActivity.STEPSIZEVALUE)){
            return preferences.getInt(AddNewEntryActivity.STEPSIZEVALUE,2);
        }
        return null;
    }


    static class Sorter implements Comparator<Entry> {
        int order=-1;
        Sorter(int order){
            this.order = order;
        }

        public int compare(Entry ob1, Entry ob2){
            if(ob1.getName().compareTo(ob2.getName()) == 0) return 0;
            else if(ob1.getName().compareTo(ob2.getName()) < 0)
                return order;
            else
                return(-1*order);
        }
    }
}