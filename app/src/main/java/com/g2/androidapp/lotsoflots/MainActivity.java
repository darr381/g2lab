package com.g2.androidapp.lotsoflots;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* APIRetrieveSystem.retrieveVacancies(MainActivity.this);
        Log.d(TAG, APIRetrieveSystem.teststring);*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button mapsButton = (Button) findViewById(R.id.maps_button);



        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        //testing of API retrieval starts here

        Button testapi_button = (Button) findViewById(R.id.testapi_button);
        final TextView testapi_output = (TextView) findViewById(R.id.testapi_output);

        testapi_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //APIRetrieveSystem.retrieveCarParks(MainActivity.this);

                //APIRetrieveSystem.retrieveCarParks(MainActivity.this);
                //testapi_output.setText(CarParkList.getCarParkList().get(1).vacancies);

               //testapi_output.setText(Instant.now().toString());


                APIRetrieveSystem.retrieveall(MainActivity.this);
               testapi_output.setText("ran!");
            }
        });

        //testing of API retrieval ends here

        //testing of sorting system starts here

        Button testsort_button = (Button) findViewById(R.id.testsort_button);

        testsort_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LatLng random = new LatLng(1.374363, 103.746769);
                SortingSystem.sortCarParkbyVacancy(random);
                testapi_output.setText("sorted!");
            }
        });

        //testing of sorting system ends here

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
