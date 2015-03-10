package test.usmaan.alam.urdudictionarytest;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yesAlam on 10/03/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final String PATH = "/data/data/test.usmaan.alam.urdudictionarytest/databases/";
    private static String DBNAME = "local";
    private SQLiteDatabase myDataBase ;
    private Context context ;
    private ProgressDialog dialog;

    public DatabaseHelper(Context context) {
        super(context,DBNAME,null,1);
        this.context = context ;
    }


    public String[] getRandom(int id){
        Cursor cursor = myDataBase.rawQuery("select * from urdu where _id="+id,null);
        cursor.moveToFirst();
        String[] result = new String[2];
        result[0] = cursor.getString(1);
        result[1] = cursor.getString(2);
        return result;
    }


    public Cursor query(String constrain){
        Cursor cursor = myDataBase.rawQuery("select _id,english_word from urdu where english_word like "+"'%"+constrain+"%'",null);
        return cursor;
    }

    public String[] getMeaning(String id){
        Cursor cursor = myDataBase.rawQuery("select * from urdu where english_word='"+id+"'",null);
        cursor.moveToFirst();
        String[] result = new String[2];
        result[0] = cursor.getString(1);
        result[1] = cursor.getString(2);
        return result;
    }






    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{
            Log.d("Urdu Dictionary","dbExist= false");
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading data ....");
            dialog.setCanceledOnTouchOutside(false);
            Log.e("urdu dictionary","calling a new thread");

                        try {

                            Toast.makeText(context,"Loading data",Toast.LENGTH_LONG).show();
                            dialog.show();
                            copyDataBase();
                        } catch (IOException e){
                            throw new Error("Erro copying database");
                        }



        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = PATH + DBNAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch(Exception e){
            Log.e("Kuch bhi",e.getMessage());
            //database does't exist yet.
            Log.e("DatabaseHelper", "Database is not available");
        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        Log.e("Urdu Dictionary","copyDatabase called");

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DBNAME);

        // Path to the just created empty db
        String outFileName = PATH + DBNAME;
        Log.e("Urdu Dictionary","after ins");
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        dialog.dismiss();
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = PATH + DBNAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
