package de.fuchsi.basal_rate_db.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EntryDbHelper extends SQLiteOpenHelper{
    private static final String LOG_TAG = EntryDbHelper.class.getSimpleName();

    private static final int DB_VERSION           = 1;
    public  static final int IDX_PUMPBASAL_ID     = 0;
    public  static final int IDX_PUMPBASAL_NAME   = 1;
    public  static final int IDX_PUMPBASAL_RATE   = 2;
    public  static final int IDX_PUMPBASAL_ACTIVE = 3;
    public  static final String DBNAME      = "basalRateDatabase";
    public  static final String TABLE_NAME  = "pump_basal_rate";
    public  static final String COL_ID    = "id";
    public  static final String COL_NAME  = "name";
    public  static final String COL_RATE  = "basalRate";
    public  static final String COL_ISACT = "active";
    public  static final String QUERY_WHERE_ID_IS = "ID=";
    public  static final String QUERY_CREATE_TABLE_PUMPBASAL = "CREATE TABLE IF NOT EXISTS pump_basal_rate("+
                                                                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                                                "name TEXT, " +
                                                                "basalRate TEXT, " +
                                                                "active TEXT)";


    public EntryDbHelper(Context context){
        super(context, DBNAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + " erzeugt.");
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        try {
            db.execSQL(QUERY_CREATE_TABLE_PUMPBASAL);
            Log.d(LOG_TAG,"Table erzeugen!");
        }
        catch (Exception e){
            Log.e(LOG_TAG, "Fehler beim erzeugen des Tables! --> " + e.getMessage() );
        }

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (newVersion > oldVersion) {
            db.setVersion(newVersion);
        }
    }
}
