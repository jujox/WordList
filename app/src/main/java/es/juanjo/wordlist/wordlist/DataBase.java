package es.juanjo.wordlist.wordlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase {
    static final String WORD = "word";
    static final String MEANING = "meaning";
    static final String DICTIONARY = "dictionary";
    static final String DATABASE_NAME = "WordListDB";
    static final String DATABASE_TABLE = "WordList";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + "(id integer primary key autoincrement, " +
            WORD + " text not null, " + MEANING + " text not null, " + DICTIONARY + " text not null);";

    SQLiteDatabase db;
    DatabaseHelper DBHelper;
    final Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
                System.out.println(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w("DataBase", "Upgrading database from version...");
            db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TABLE);
            onCreate(db);
        }

    }

    public DataBase(Context cntx) {
        this.context = cntx;
        this.DBHelper = new DatabaseHelper(cntx);
    }

    //---opens the database---
    public DataBase open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    public long insertWord(String word, String meaning, String dictionary)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(WORD, word);
        initialValues.put(MEANING, meaning);
        initialValues.put(DICTIONARY, dictionary);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteWord(long rowId)
    {
        return db.delete(DATABASE_TABLE, "id = " + rowId, null) > 0;
    }

    public Cursor getWordFromDictionary(String dictionary) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {WORD,
                        MEANING, DICTIONARY}, DICTIONARY + " = '" + dictionary + "'", null,
            null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getDictionaries() throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {DICTIONARY}, null, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateWord(long rowId, String word, String meaning, String dictionary)
    {
        ContentValues args = new ContentValues();
        args.put(WORD, word);
        args.put(MEANING, meaning);
        args.put(DICTIONARY, dictionary);
        return db.update(DATABASE_TABLE, args, "ID" + " = " + rowId, null) > 0;
    }
}