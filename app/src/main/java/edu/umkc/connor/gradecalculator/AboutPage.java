package edu.umkc.connor.gradecalculator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutPage extends AppCompatActivity {

    //Basic About Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

    }

    @Override
    protected void onDestroy() {
        //db.close();
        super.onDestroy();
    }

}
