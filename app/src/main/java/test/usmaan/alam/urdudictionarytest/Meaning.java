package test.usmaan.alam.urdudictionarytest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Meaning extends ActionBarActivity {
    String result[];
    String word;
    DatabaseHelper dbhelper;
    TextView wordplace;
    TextView meaningplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);
        Intent intent = getIntent();
        word = intent.getExtras().getString("word");
        dbhelper = new DatabaseHelper(this);
        dbhelper.openDataBase();
        result = dbhelper.getMeaning(word);
        wordplace = (TextView) findViewById(R.id.wordf);
        meaningplace = (TextView) findViewById(R.id.meaningf);
        wordplace.setText(result[0]);
        meaningplace.setText(result[1]);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meaning, menu);
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
}
