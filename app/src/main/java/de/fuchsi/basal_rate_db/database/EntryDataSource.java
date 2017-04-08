package de.fuchsi.basal_rate_db.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EntryDataSource {
    private static final String LOG_TAG = EntryDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private EntryDbHelper dbHelper;

    private String[] columnNames = {
            EntryDbHelper.COL_ID,
            EntryDbHelper.COL_NAME,
            EntryDbHelper.COL_RATE,
            EntryDbHelper.COL_ISACT
    };

    public EntryDataSource(Context context){
        dbHelper = new EntryDbHelper(context);
        open();
    }
    public void open(){
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "DB " + database.getPath() + " ist geöffnet!");
    }
    public void close(){
        database.close();
        Log.i(LOG_TAG,"Database closed!");
    }

    public void updateEntry(Entry updateData){
        ContentValues values = new ContentValues();
        values.put(EntryDbHelper.COL_NAME, updateData.getName());
        values.put(EntryDbHelper.COL_RATE, updateData.getBasalRateArrayString());
        values.put(EntryDbHelper.COL_ISACT, String.valueOf(updateData.getActive()));

        String whereClause = EntryDbHelper.QUERY_WHERE_ID_IS + String.valueOf(updateData.getId());
        database.update(EntryDbHelper.TABLE_NAME, values, whereClause, null);
    }
    public void deleteEntry(String id){
        String whereClause = EntryDbHelper.QUERY_WHERE_ID_IS + id;
        database.delete(EntryDbHelper.TABLE_NAME,whereClause,null);
    }
    public Entry createEntry(String name, String rateString, boolean isActive){
        ContentValues values = new ContentValues();
        values.put(EntryDbHelper.COL_NAME, name);
        values.put(EntryDbHelper.COL_RATE, rateString);
        values.put(EntryDbHelper.COL_ISACT, String.valueOf(isActive));

        long insertId = database.insert(EntryDbHelper.TABLE_NAME,null,values);
        Cursor cursor = database.query(EntryDbHelper.TABLE_NAME,
                                        columnNames, EntryDbHelper.COL_ID + "=" + insertId,
                                        null, null, null, null);

        cursor.moveToFirst();
        Entry newEntry = cursorToEntry(cursor);
        cursor.close();


        return newEntry;
    }
    private Entry cursorToEntry(Cursor cursor) {
        long id = cursor.getLong(EntryDbHelper.IDX_PUMPBASAL_ID);
        String name = cursor.getString(EntryDbHelper.IDX_PUMPBASAL_NAME);
        String rate = cursor.getString(EntryDbHelper.IDX_PUMPBASAL_RATE);
        boolean act = Boolean.valueOf(cursor.getString(EntryDbHelper.IDX_PUMPBASAL_ACTIVE));

        return new Entry(id, name, rate, act);
    }
    public List<Entry> getAllEntries() {
        List<Entry> entryList = new ArrayList<>();
        Cursor cursor = database.query(EntryDbHelper.TABLE_NAME,
                columnNames, null, null, null, null, null);

        cursor.moveToFirst();
        Entry entry;

        while(!cursor.isAfterLast()) {
            entry = cursorToEntry(cursor);
            entryList.add(entry);
            cursor.moveToNext();
        }
        cursor.close();

        return entryList;
    }
}
