package test.usmaan.alam.urdudictionarytest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends ActionBarActivity {
    private final String TAG = "Main Activity";
    DatabaseHelper dbhelper;
    Random rn;
    int rand;
    TextView word;
    TextView mean;
    String[] random;
    AutoCompleteTextView actv;
    Cursor cursor;
    Button search;
    int flag=0;
    ListView ls;
    ArrayList<String> dataword ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ls = (ListView) findViewById(R.id.mainlist);
        ls.setVisibility(View.INVISIBLE);
        dbhelper = new DatabaseHelper(this);
        try{
            dbhelper.createDataBase();
        }catch (IOException e){
            Log.e(TAG,"can't read/write file ");
            Toast.makeText(this,"error loading data",Toast.LENGTH_SHORT).show();
        }

        dbhelper.openDataBase();
        word = (TextView) findViewById(R.id.word);
        mean = (TextView) findViewById(R.id.meaning);
        rn = new Random();
        rand = rn.nextInt(36789);
        random = dbhelper.getRandom(rand);
        word.setText(random[0]);
        mean.setText(random[1]);

        String[] from = {"english_word"};
        int[] to = {R.id.text};
        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.singalline,null,from,to);
        // This will provide the labels for the choices to be displayed in the AutoCompleteTextView
        adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {

                return cursor.getString(1);
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                cursor=null;
                int count = constraint.length();
                if(count>=3){
                    String constrains = constraint.toString();
                    cursor = dbhelper.query(constrains);
                }
                return cursor;
            }
        });

        actv.setAdapter(adapter);

        search = (Button) findViewById(R.id.button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main Activity","Search button was clicked");
                searchData();
            }
        });

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                flag = 1;
                Log.d("Main Activity","actv list item was ckicked");
            }
        });

        actv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == 0) {
                    if(event.getKeyCode() == 0x42) {
                        searchData();
                        actv.dismissDropDown();
                        hideKeyBoard();
                    }
                }

                return false;
            }
        });

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("My Activity","Item in Lower list is clicked");
                String word = dataword.get(position);
                startMeaning(word);
            }
        });

    }

    public void searchData(){
         String actext = actv.getText().toString().trim();
         if(actext.length()==0) {
             Toast.makeText(getApplicationContext(),"Atleast try to type somethisg....",Toast.LENGTH_SHORT).show();
             return;
         }
        if(cursor.moveToFirst()){
            int temp =cursor.getCount();

            if(check(temp,flag) ){
                Log.d("Main Activity","Only one row is there ");
                String word = null;
                if(flag==1) word = actv.getText().toString().trim();
                if(temp==1) word = cursor.getString(1);

                startMeaning(word);
                flag =0;
            } else {
                dataword = new ArrayList<>();

                Log.d("Main Activity","MANY ROW is there");

                cursor.moveToFirst();
                for(int i=0;i<cursor.getCount();i++){
                    String wordt = cursor.getString(1);

                    dataword.add(wordt);

                    cursor.moveToNext();
                }
                ls.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataword));
                ls.setVisibility(View.VISIBLE);

            }

        }else {
            Toast.makeText(getApplicationContext(),"NO Match found....",Toast.LENGTH_SHORT).show();

        }



    }

    public void startMeaning(String wordf){
        Intent intent = new Intent(this,Meaning.class);
        intent.putExtra("word",wordf);
        dbhelper.close();
        startActivity(intent);
    }

    public boolean check(int a,int b){
        if (a==1) return true;
        if (b==1) return true;
        return false;
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(actv.getWindowToken(), 0x0);
    }

}
