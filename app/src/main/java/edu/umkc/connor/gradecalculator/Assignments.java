package edu.umkc.connor.gradecalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Assignments extends AppCompatActivity {

    String TAG = "Grade";
    CustLVAdapterAs myAdapter;
    ListView myList;
    TextView total;
    Database db;
    int gs_id, class_id;
    ArrayList<AssignmentItem> arrlist;

    SharedPreferences settings;

    AlertDialog popup, deletepopup;
    AlertDialog.Builder popupbuilder, deletepopupbuilder;

    //Popup to tell the user about unimplemented features
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        settings = getSharedPreferences("file", MODE_PRIVATE);
        class_id = settings.getInt("class_id", -1);
        gs_id = settings.getInt("gs_id", -1);

        //Set up Database
        db = Database.getInstance(this);
        toast = Toast.makeText(getApplicationContext(), "This Feature hasn't been Implemented Yet", Toast.LENGTH_LONG);

        setTitle(db.getGS(gs_id).getName());

        myList = (ListView) findViewById(R.id.listView2);
        myAdapter = new CustLVAdapterAs(getApplicationContext(), R.layout.assn_list);
        myList.setAdapter(myAdapter);

        //Pull info from DB
        arrlist = db.getAllAssn(gs_id);
        for (AssignmentItem x: arrlist) {
            myAdapter.add(x);
        }

        total = (TextView)findViewById(R.id.AssnTextView);
        total.setText(String.format("%.2f", myAdapter.totPerc()));

        //Popup to get information
        popupbuilder = new AlertDialog.Builder(this);
        popupbuilder.setTitle("Add Line");
        popupbuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        popupbuilder.setView(inflater.inflate(R.layout.assn_popup_layout, null));
        popupbuilder.setMessage("Enter information for the new line");

        //I cancel when there is bad input
        popupbuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                popup.show();
            }
        });

        //Cancel button
        popupbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                popup.setMessage("Enter information for the new line");
                popup.dismiss();
            }
        });

        //Submit/Save
        popupbuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Set up EditTexts
                final EditText et1 = (EditText)popup.findViewById(R.id.ApopupEditText1);
                final EditText et2 = (EditText)popup.findViewById(R.id.ApopupEditText2);
                final EditText et3 = (EditText)popup.findViewById(R.id.ApopupEditText3);

                try {
                    String tempstr = "";
                    double tempint1 = 0, tempint2 = 0;

                    tempstr = et1.getText().toString();
                    tempint1 = Integer.parseInt(et2.getText().toString());
                    tempint2 = Integer.parseInt(et3.getText().toString());
                    Log.i(TAG, "String: " + tempstr + " Int: " + tempint1 + " Out of: " + tempint2);

                    if (!tempstr.equals("") && tempint1 >= 0 && tempint2 >= 0) {
                        if (db.getKey(tempstr, 'a', gs_id) == -1) {

                            AssignmentItem temp = new AssignmentItem(tempstr, tempint1, tempint2);

                            arrlist.add(temp);
                            myAdapter.add(temp);

                            db.addAssn(gs_id, new AssignmentItem(tempstr, tempint1, tempint2));
                            total.setText(String.format("%.2f", myAdapter.totPerc()));
                            et1.setText("");
                            et2.setText("");
                            et3.setText("");

                        } else {
                            popup.setMessage("That name already exists");
                            popup.cancel();
                        }
                    } else {
                        popup.setMessage("Invalid input, try again");
                        popup.cancel();
                    }
                } catch (NumberFormatException e) {
                    //nothing/text entered into number area
                    popup.setMessage("Please enter a numbers for the Scores");
                    popup.cancel();
                }
            }
        });

        popup = popupbuilder.create();

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
                    int key = db.getKey(temp, 'a', gs_id);
                    if (key != -1) {
                        Log.i(TAG, "Name found: " + temp);
                        Log.i(TAG, "Key found: " + key);
                        //This doesn't update the screen for some reason
                        //TODO FIX
                        //TODO Delete from MyAdapter doesn't seem to remove it from the screen
                        //TODO Find way to REFRESH or Fix
                        AssignmentItem as = db.getAssn(key);
                        arrlist.remove(as);
                        myAdapter.remove(as);
                        myAdapter.notifyDataSetChanged();
                        db.deleteAssn(temp, gs_id);

                        //Show percent
                        total.setText(String.format("%.2f", myAdapter.totPerc()));
                        editText.setText("");

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
        getMenuInflater().inflate(R.menu.menu_assignments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.features) {
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void addLine(View v) {
        popup.show();
    }

    public void deleteLine(View v) {
        deletepopup.show();
    }

}
