package edu.umkc.connor.gradecalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GradeScaleSaved extends AppCompatActivity {

    String TAG = "Grade";
    CustLVAdapterGS myAdapter;
    TextView total, totalScore;
    AlertDialog popupdialog, deletepopup;
    AlertDialog.Builder popupbuilder, deletepopupbuilder;
    int classId;
    ArrayList<GradeScaleItem> arrlist;

    Database db;

    SharedPreferences settings;
    SharedPreferences.Editor editSettings;

    //Popup to tell the user about unimplemented features
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_scale_saved);
        setTitle(R.string.GradeScaletext1);

        settings = getSharedPreferences("file", MODE_PRIVATE);
        classId = settings.getInt("class_id", -1);
        Log.i(TAG, "GS Class Id is " + classId);

        //Get DB and Intent stuff
        db = Database.getInstance(this);
        toast = Toast.makeText(getApplicationContext(), "This Feature hasn't been Implemented Yet", Toast.LENGTH_LONG);

        //Set up TextView for Final Score
        totalScore = (TextView)findViewById(R.id.GSTextViewScore);
        totalScore.setText(String.format("%.2f",db.getGrade(classId)));
        //TODO Edit in DB

        //Set up ListView
        final ListView myList = (ListView) findViewById(R.id.listView3);
        myAdapter = new CustLVAdapterGS(getApplicationContext(), R.layout.list_items_saved);
        myList.setAdapter(myAdapter);

        //Pull info from DB
        arrlist = db.getAllGS(classId);
        for (GradeScaleItem x: arrlist) {
            myAdapter.add(x);
        }
        //Get Total Percentage
        total = (TextView)this.findViewById(R.id.GSSTextView3);
        total.setText(String.valueOf(myAdapter.totPerc()));

        //Set up Listener on each line to move to Assignments page
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int gsID = db.getKey(arrlist.get(position).getName(), 'g', classId);
                Log.i(TAG, "GS id is " + gsID);

                editSettings = settings.edit();
                editSettings.putInt("gs_id", gsID);
                editSettings.commit();

                Intent i = new Intent(view.getContext(), Assignments.class);
                startActivity(i);
            }
        });


        //Popup to get information
        popupbuilder = new AlertDialog.Builder(this);
        popupbuilder.setTitle("Add Line");
        popupbuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        popupbuilder.setView(inflater.inflate(R.layout.gradescale_popup_layout, null));
        popupbuilder.setMessage("Enter information for the new line");

        //I cancel when there is bad input
        popupbuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                popupdialog.show();
            }
        });

        //Cancel button
        popupbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                popupdialog.dismiss();
            }
        });

        //Submit/Save
        popupbuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Set up EditTexts
                final EditText et1 = (EditText)popupdialog.findViewById(R.id.GSPeditText);
                final EditText et2 = (EditText)popupdialog.findViewById(R.id.GSPeditText2);

                try {
                String tempstr = "";
                double tempint = 0;

                tempstr = et1.getText().toString();
                tempint = Integer.parseInt(et2.getText().toString());
                Log.i(TAG, "String: " + tempstr + " Int: " + tempint);

                if (!tempstr.equals("") && tempint >= 0) {
                    if (db.getKey(tempstr, 'g', classId) == -1) {

                        GradeScaleItem temp = new GradeScaleItem(tempstr, tempint);
                        arrlist.add(temp);
                        myAdapter.add(temp);

                        db.addGS(classId, new GradeScaleItem(tempstr, tempint));
                        total.setText(String.valueOf(myAdapter.totPerc()));

                        et1.setText("");
                        et2.setText("");
                    } else {
                        popupdialog.setMessage("That name already exists");
                        popupdialog.cancel();
                    }
                } else {
                    popupdialog.cancel();
                }
                } catch (NumberFormatException e) {
                    //nothing/text entered into number area
                    popupdialog.setMessage("Please enter a number for the percentage");
                    popupdialog.cancel();
                }
            }
        });

        popupdialog = popupbuilder.create();

        //Set up a Delete Popup
        //Enter the name and Delete
        final EditText editText = new EditText(this);
        deletepopupbuilder = new AlertDialog.Builder(this);
        deletepopupbuilder.setTitle("Delete");
        deletepopupbuilder.setMessage("Enter the name of the item to delete");
        deletepopupbuilder.setView(editText);
        deletepopupbuilder.setCancelable(true);

        //Restart with changed Message
        deletepopupbuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                deletepopup.show();
            }
        });

        //Cancel
        deletepopupbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletepopup.setMessage("Enter the name of the item to delete");
                deletepopup.dismiss();
            }
        });

        //Check for correct values
        deletepopupbuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Get and test the value entered
                String temp = editText.getText().toString();

                if (temp.equals("")) {
                    deletepopup.setMessage("Please enter Something");
                    deletepopup.cancel();
                } else {
                    int key = db.getKey(temp, 'g', classId);
                    if (key != -1) {
                        Log.i(TAG, "Name found: " + temp);
                        Log.i(TAG, "Key found: " + key);
                        //This doesn't update the screen for some reason
                        //TODO FIX
                        //TODO Delete from MyAdapter doesn't seem to remove it from the screen
                        //TODO Find way to REFRESH or Fix
                        arrlist.remove(db.getGS(key));
                        myAdapter.remove(db.getGS(key));
                        myAdapter.notifyDataSetChanged();
                        db.deleteGS(temp, classId);

                    } else {
                        deletepopup.setMessage("Name not found");
                        deletepopup.cancel();
                    }
                }
            }
        });

        deletepopup = deletepopupbuilder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gradescalesaved, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //Go back to GradeScaleEdit page
        //TODO FIX?, REMOVE?, CHANGE?
        if(id == R.id.edit){
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void addLineButton (View view) {
        popupdialog.show();
    }

    public void deleteLineButton(View view) { deletepopup.show(); }
}
