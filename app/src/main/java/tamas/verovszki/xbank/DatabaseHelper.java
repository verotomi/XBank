package tamas.verovszki.xbank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.support.constraint.Constraints.TAG;

/**
 * SQLite adatbázishoz
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDataBase;
    private final Context mContext;

    //konstruktor felvétele
    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
        //---------------------------------------
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
        //  ------------------------------------------
    }

    // ----------------------------

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + Constants.DATABASE_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        InputStream mInput = mContext.getAssets().open(Constants.DATABASE_NAME);
        String outFileName = DB_PATH + Constants.DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public void createDataBase() throws IOException
    {
        //If the database does not exist, copy it from the assets.

        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist)
        {
            this.getReadableDatabase();
            this.close();
            try
            {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + Constants.DATABASE_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }

    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    // ------------------------------------------

    //LÉTREHOZZUK A TÁBLÁT ÉS A BENNE LÉVŐ OSZLOPOKHOZ TÍPUST ADUNK
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //adatlekérdezés

    public Cursor adatLekerdezesBranches() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from " + Constants.TABLE_NAME_BRANCHES, null);
        return result;
    }

    public Cursor adatLekerdezesAtms() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from " + Constants.TABLE_NAME_ATMS, null);
        return result;
    }

    public Cursor adatLekerdezesCurrencies() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from " + Constants.TABLE_NAME_CURRENCIES, null);
        return result;
    }

    public Cursor adatLekerdezesForeignCurrencies() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from " + Constants.TABLE_NAME_FOREIGN_CURRENCIES, null);
        return result;
    }

    //db file beolvasás

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/package_name/databases/";
    // Data Base Version.
    private static final int DATABASE_VERSION = 1;

    /**
     * Copy MySQL to SQLite
     * Elmentjük a MySQL-ből beolvasott adatokat az SQLite adatbázisba
     * Valójában az aktuális állapot kerül mentésre, akkor is, ha , nem volt MySQL beolvasás
     * Ilyen szempontól is jobb ez a verzió, mint a másik, amit nem használok, mert az a PHP-től kapott adatokat menti le. HA nem érkezett adat PHP-ből, akkor nem tudom, mit ment le.
     * ezt használom a Tab-ok (fragmentek) ondestroy() metódusában.
     * Ugyanazt a metódust hazsnálja a valuta-és a deviza lista, át van adva a táblanév
     * Viszont ezzel néha 0-ás id-vel kerül be adat az SQKLite adatbázisba. A másik megoldásnál nem találkoztam ilyen hibával.
     * működik az a megoldás is, hogy a Tab-ok refreshCurrencyList függvényéből hívom meg az updateCurrencies metódust, de az lassabb, mert még a lista készítáésekor csinálja a mentést.
     * @return
     *
    public boolean refreshCurrencies(String tablename, int id, String name, double buy, double sell, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CURRENCIES_ID, id);
        contentValues.put(tablename.equals(COL_CURRENCIES_NAME) ? COL_CURRENCIES_NAME : COL_FOREIGNCURRENCIES_NAME, name); // jelen esetben ugyanaz a két oszlopnév a két táblában, de azért így szebb! :)
        contentValues.put(COL_CURRENCIES_BUY, buy);
        contentValues.put(COL_CURRENCIES_SELL, sell);
        contentValues.put(COL_CURRENCIES_VALIDFROM, date);
        long eredmeny = db.insert(tablename, null, contentValues);

        if (eredmeny == -1)
        {
            return false;           //sikertelen adatfelvétel
        }else
            return true;            //sikeres adatfelvétel

    }*

    /**
     * használaton kívül!
     * @param tablename
     */
    public void deleteSQLiteTable(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + tablename);
    }


    /**
     * Copy MySQL to SQLite
     * // Ez már nem igaz: Ehelyett a refreshCurrencies-t használom a Tab-ok (fragmentek) ondestroy() metódusában, mert gyorsabbnak találom ezt a megoldást.
     * A Tab-ok refreshCurrencyList függvényéből lehet meghívni ezt  az updateCurrencies metódust, de az lassabb, mert még a lista készítésekor csinálja a mentést.
     * Ha eltérő lenne a valuta és a deviza táblák oszlopainak az elnevezése, akkor ezt a metódust kétfelé kéne bontani!
     * @return
     */
    public void updateCurrencies(String tablename, int id, String name, double buy, double sell, String validfrom){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.COL_CURRENCIES_ID, id);
        contentValues.put(Constants.COL_CURRENCIES_NAME, name);
        contentValues.put(Constants.COL_CURRENCIES_BUY, buy);
        contentValues.put(Constants.COL_CURRENCIES_SELL, sell);
        contentValues.put(Constants.COL_CURRENCIES_VALIDFROM, validfrom);
        db.update(tablename, contentValues, Constants.COL_CURRENCIES_ID + " =" + id, null);
    }
    
    public boolean login (int id, int pincode){

        return true;
    } 
}
