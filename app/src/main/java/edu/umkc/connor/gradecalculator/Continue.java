package edu.umkc.connor.gradecalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Continue extends AppCompatActivity {

    //TODO Add Edit feature

    String TAG = "Grade";
    ArrayAdapter myAdapter;
    Database db;
    EditText editText;

    SharedPreferences settings;
    SharedPreferences.Editor editSettings;

    //Popup to tell the user about unimplemented features
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue);
        setTitle(R.string.load);

        toast = Toast.makeText(getApplicationContext(), "This Feature hasn't been Implemented Yet", Toast.LENGTH_LONG);

        settings = getSharedPreferences("file", MODE_PRIVATE);

        //Get DB and ET
        db = Database.getInstance(this);
        editText = (EditText)findViewById(R.id.cont_nameEdit);

        //Set up a list for the Adapter
        final ListView listView = (ListView)findViewById(R.id.cont_ListView);
        ArrayList<String> list = db.getAllClasses();

        //Set up the Adapter
        myAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText((String)myAdapter.getItem(position));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu
        //TODO EDIT?
        getMenuInflater().inflate(R.menu.menu_continue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.contMenu_edit){
            Log.i(TAG, "Edit Clicked");
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Continue
    public void cont(View v) {
        int tempint = 0;
        Log.i(TAG, "Continue Button Clicked");
        String tempstr = editText.getText().toString();
        Log.i(TAG, "edit text says: " + tempstr);
        tempint = db.getKey(tempstr, 'c', 0);

        //Test if it is in the DB, -1 means that it isn't
        if (tempint > 0) {
            Log.i(TAG, "Class ID Set to " + tempint);
            editSettings = settings.edit();
            editSettings.putInt("class_id", tempint);
            editSettings.commit();

            Intent i = new Intent(this, GradeScaleSaved.class);
            startActivity(i);
        } else { Log.i(TAG, "Can't Find Class Entered"); }
    }

    //Delete
    public void delet(View v) {
        //Delete the line currently typed, does nothing if the line isn't in the table
        String name = editText.getText().toString();
        Log.i(TAG, "Delete Button Pressed: " + name);
        db.deleteClass(name);
        myAdapter.remove(name);
    }

   /* @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }*/

}
