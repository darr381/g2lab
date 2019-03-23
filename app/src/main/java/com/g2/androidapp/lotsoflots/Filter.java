package com.g2.androidapp.lotsoflots;


import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.text.format.DateFormat;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Filter extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {


    //String[] choice =  new String[3];
    String distance = null,  vacancy = null , sort = null, time;
    Spinner distanceSpinner, vacancySpinner, sortSpinner;
    Date date;
    int day,month,year,hour,minute,dayFinal,monthFinal,yearFinal,hourFinal,minuteFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        makeSpinner();
        obtainSelection();

        Button backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PreferenceUpdater.updateAll(distance, vacancy, hourFinal, minuteFinal, date, sort);


                Calendar calendar = Calendar.getInstance();
                calendar.set(yearFinal,monthFinal,dayFinal,hourFinal,minuteFinal,0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.TAIWAN);
                String formattedDate = sdf.format(calendar.getTime());
                try {
                    date = sdf.parse(formattedDate);
                    Preference.setDate(date);
                    Log.i("darrence", Preference.getDate().toString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        Button timeBtn = findViewById(R.id.timeBtn);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog= new DatePickerDialog(Filter.this , Filter.this,
                        year,month,day);
                datePickerDialog.show();

            }
        });

    }
    public void makeSpinner(){

        distanceSpinner = findViewById(R.id.distanceSpinner);
        vacancySpinner =  findViewById(R.id.priceSpinner);
        //timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
        sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.distanceSpinnerNames));
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        distanceSpinner.setAdapter(distanceAdapter);
        distanceSpinner.setSelection(2);

        ArrayAdapter<String> vacancyAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.vacancySpinnerNames));
        vacancyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        vacancySpinner.setAdapter(vacancyAdapter);

        /*ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.timeSpinnerNames));
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        timeSpinner.setAdapter(timeAdapter);*/

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.sortSpinnerNames));
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        sortSpinner.setAdapter(sortAdapter);
    }
    public void obtainSelection(){
        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                distance = distanceSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }

        });
            /*timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    time = timeSpinner.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    return;
                }
            });*/
        vacancySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vacancy = vacancySpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sort = sortSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        //choice[0] = distance;
        //choice[1] = vacancy;
        //choice[2] = sort;

    }

    @Override
    public void onDateSet (DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month;
        dayFinal = dayOfMonth;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(Filter.this ,Filter.this,
                hour, minute , DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
        view.setIs24HourView(true);
        hourFinal = view.getHour();
        minuteFinal = view.getMinute();
    }

}