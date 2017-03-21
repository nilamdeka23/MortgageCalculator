package nilam.project.com.mortgagecalculator.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import nilam.project.com.mortgagecalculator.model.RecordDao;

public class DatabaseHelper extends SQLiteOpenHelper {

    // database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "recordManager";

    // records table name
    private static final String TABLE_RECORDS = "records";

    // records table column names
    private static final String KEY_ID = "id";
    private static final String KEY_STREET_ADDRESS = "street_address";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_ZIP = "zipcode";
    private static final String KEY_TYPE = "type";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DOWN_PAYMENT = "down_payment";
    private static final String KEY_APR = "apr";
    private static final String KEY_TERM = "term";
    private static final String KEY_MONTHLY_PAYMENT = "monthly_payment";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creating table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_STREET_ADDRESS + " TEXT,"
                + KEY_CITY + " TEXT," + KEY_STATE + " TEXT," + KEY_ZIP + " TEXT,"
                + KEY_TYPE + " TEXT," + KEY_AMOUNT + " TEXT," + KEY_DOWN_PAYMENT + " TEXT,"
                + KEY_APR + " TEXT," + KEY_TERM + " TEXT," + KEY_LAT + " FLOAT," + KEY_LNG + " FLOAT,"
                + KEY_MONTHLY_PAYMENT + " TEXT" + ")";
        db.execSQL(CREATE_RECORDS_TABLE);
    }

    // upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // adding new record
    public void addRecord(RecordDao dao) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STREET_ADDRESS, dao.getStreetAddress());
        values.put(KEY_CITY, dao.getCity());
        values.put(KEY_STATE, dao.getState());
        values.put(KEY_TYPE, dao.getType());
        values.put(KEY_ZIP, dao.getZipcode());
        values.put(KEY_AMOUNT, dao.getAmount());
        values.put(KEY_DOWN_PAYMENT, dao.getDownPayment());
        values.put(KEY_APR, dao.getApr());
        values.put(KEY_TERM, dao.getTerm());
        values.put(KEY_LAT, dao.getLatitude());
        values.put(KEY_LNG, dao.getLongitude());
        values.put(KEY_MONTHLY_PAYMENT, dao.getMonthlyPayment());

        // inserting row
        db.insert(TABLE_RECORDS, null, values);
        // closing db connection
        db.close();
    }

    // getting single record
    public RecordDao getRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECORDS, new String[]{KEY_ID, KEY_STREET_ADDRESS, KEY_CITY,
                        KEY_STATE, KEY_ZIP, KEY_TYPE, KEY_AMOUNT, KEY_DOWN_PAYMENT, KEY_APR, KEY_TERM,
                        KEY_LAT, KEY_LNG, KEY_MONTHLY_PAYMENT}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        RecordDao dao = new RecordDao();
        dao.setId(cursor.getInt(0));
        dao.setStreetAddress(cursor.getString(1));
        dao.setCity(cursor.getString(2));
        dao.setState(cursor.getString(3));
        dao.setZipcode(cursor.getString(4));
        dao.setType(cursor.getString(5));
        dao.setAmount(cursor.getString(6));
        dao.setDownPayment(cursor.getString(7));
        dao.setApr(cursor.getString(8));
        dao.setTerm(cursor.getString(9));
        dao.setLatitude(cursor.getDouble(10));
        dao.setLongitude(cursor.getDouble(11));
        dao.setMonthlyPayment(cursor.getString(12));

        cursor.close();
        // return record
        return dao;
    }

    // getting all records
    public List<RecordDao> getAllRecords() {
        List<RecordDao> records = new ArrayList<>();
        // select all query
        String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RecordDao dao = new RecordDao();
                dao.setId(cursor.getInt(0));
                dao.setStreetAddress(cursor.getString(1));
                dao.setCity(cursor.getString(2));
                dao.setState(cursor.getString(3));
                dao.setZipcode(cursor.getString(4));
                dao.setType(cursor.getString(5));
                dao.setAmount(cursor.getString(6));
                dao.setDownPayment(cursor.getString(7));
                dao.setApr(cursor.getString(8));
                dao.setTerm(cursor.getString(9));
                dao.setLatitude(cursor.getDouble(10));
                dao.setLongitude(cursor.getDouble(11));
                dao.setMonthlyPayment(cursor.getString(12));
                // Adding dao to list
                records.add(dao);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return records
        return records;
    }

    // updating single record
    public int updateRecord(RecordDao dao) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STREET_ADDRESS, dao.getStreetAddress());
        values.put(KEY_CITY, dao.getCity());
        values.put(KEY_STATE, dao.getState());
        values.put(KEY_TYPE, dao.getType());
        values.put(KEY_ZIP, dao.getZipcode());
        values.put(KEY_AMOUNT, dao.getAmount());
        values.put(KEY_DOWN_PAYMENT, dao.getDownPayment());
        values.put(KEY_APR, dao.getApr());
        values.put(KEY_TERM, dao.getTerm());
        values.put(KEY_LAT, dao.getLatitude());
        values.put(KEY_LNG, dao.getLongitude());
        values.put(KEY_MONTHLY_PAYMENT, dao.getMonthlyPayment());

        // updating row
        return db.update(TABLE_RECORDS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(dao.getId())});
    }

    // deleting single record
    public void deleteRecord(RecordDao dao) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, KEY_ID + " = ?",
                new String[]{String.valueOf(dao.getId())});
        db.close();
    }

}