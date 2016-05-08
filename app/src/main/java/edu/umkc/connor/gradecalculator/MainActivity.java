package edu.umkc.connor.gradecalculator;

//TODO Add in popup to GradeScaleSaved and Assignment pages
//TODO Get Data From popup and Add to Listview
//TODO Implement SQLite Databases
//TODO Save and pull data
//TODO MATH
//TODO Clean it up


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Go to About Page
        if (id == R.id.about) {
            Intent i = new Intent(this, AboutPage.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    //Button New Grade, Goes to GradeScaleSaved page
    public void newGrade (View v) {
        Intent i = new Intent(this, NewClass.class);
        startActivity(i);
    }

    public void continueButton(View v) {
        Intent i = new Intent(this, Continue.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        //db.close();
        super.onDestroy();
    }
}
