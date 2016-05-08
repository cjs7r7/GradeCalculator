package edu.umkc.connor.gradecalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//TODO Tutorial, simple how-to

public class NewClass extends AppCompatActivity {

    String TAG = "Grade";
    Database db;
    AlertDialog popup;
    AlertDialog.Builder popupbuilder;
    int classID;

    SharedPreferences settings;
    SharedPreferences.Editor editSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        setTitle(R.string.Tutorial);

        settings = getSharedPreferences("file", MODE_PRIVATE);

        //Get Database
        db = Database.getInstance(this);

        //Set up popup to retrieve name of class

        Log.i(TAG, "Before Create popup");

        final EditText editText = new EditText(this);
        popupbuilder = new AlertDialog.Builder(this);

        popupbuilder.setTitle(R.string.nameclass);
        popupbuilder.setMessage(R.string.enterclass);
        popupbuilder.setView(editText);
        popupbuilder.setCancelable(true);

        //On cancel, im using this for errors
        popupbuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Return to last page
                Log.i(TAG, "Bad entry");
                popup.show();
            }
        });
        //On Submit button, im using this for checking, then submitting
        popupbuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                //No Empty Strings
                if (name.equals("")) {
                    //Does nothing for blank
                    popup.cancel();
                } else {
                    //No repeats
                    if (db.getKey(name, 'c', 0) == -1) {
                        Log.i(TAG, "Name: " + name);
                        classID = db.addClass(name);
                        Log.i(TAG, "ID: " + classID);
                    } else {
                        popup.setMessage(getString(R.string.nameentererror));
                        popup.cancel();
                    }
                }

            }
        });
        //On Cancel button, retreat to previous page
        popupbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        popup = popupbuilder.create();
        popup.show();

        //After popup show tutorial?


    }

    public void nextButton(View v) {
        TextView tv = (TextView)findViewById(R.id.newclass_TextView);
        String tempstr = tv.getText().toString();
        if(tempstr.equals("1/3")) {
            tv.setText("2/3");
        } else if (tempstr.equals("2/3")) {
            tv.setText("3/3");
            Button button = (Button)findViewById(R.id.newclass_button);
            button.setText("Finish");
        } else if (tempstr.equals("3/3")) {
            Log.i(TAG, "Class ID Set to " + classID);

            editSettings = settings.edit();
            editSettings.putInt("class_id", classID);
            editSettings.commit();

            Intent i = new Intent(this, GradeScaleSaved.class);
            startActivity(i);
        }
    }


    /*@Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }*/

}
