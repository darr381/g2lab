package com.g2.androidapp.lotsoflots;

import android.app.VoiceInteractor;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

public class APIRetrieveSystem {


    public static String teststring;


    APIRetrieveSystem(){ //constructor

    }


    static String converttime(String time){
        //converts current time and date to a week ago

        String strpast = ":(";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        //get the date now in a string
        Date date_0 = new Date();
        Date date = new Date(date_0.getTime() + 60*60*1000*8); //this is the date now
        String date_time = dateFormat.format(date);
        String currentdate = date_time.substring(0, 11);

        //convert the preference time into a string, make it date format
        String inputdatetime = currentdate + time.substring(0, 2) + ":" + time.substring(2, 4) + ":00.838+0000Z";
        Log.d("Response", inputdatetime);
        try {
            Date datepreference = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(inputdatetime);
            Log.d("Response", "the date is: " + date.toString());
            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            Date past = new Date(datepreference.getTime() - (7 * DAY_IN_MS));
            strpast = dateFormat.format(past);
            //Log.d("Response", "a week ago it was: " + strpast);
            strpast = strpast.substring(0, 19);
            Log.d("Response", "a week ago it was: " + strpast);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        //1 week ago
        return strpast;

    }


    static String getTime(){

        //get time now in date format and add 1h
        Date date_0 = new Date();
        Date date = new Date(date_0.getTime() + 60*60*1000*8);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String date_time = dateFormat.format(date);
        Log.d("Response", "date_time is: " + date_time);

        Log.d("Response", "the time right now is: " + date.toString());

        //add 1h to time now
        Date datecompare = new Date(date.getTime() + 60*60*1000);

        Log.d("Response", "the time one hour from now is: " + datecompare.toString());

        //convert preference date to date format
        //String date_time = Instant.now().toString();

        if(Preference.getTime() != null) {
            String currentdate = date_time.substring(0, 11);
            String inputdatetime = currentdate + Preference.getTime().substring(0, 2) + ":" + Preference.getTime().substring(2, 4) + ":00.838+0000Z";
            Date preferencedate = null;
            try {
                preferencedate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(inputdatetime);
                Log.d("Response", "the preference date is: " + preferencedate.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (preferencedate.compareTo(datecompare) > 0) {
                date_time = converttime(Preference.getTime());
            }

        }

        Log.d("Response", "the date_time input is: " + date_time);


        return date_time.substring(0, 19);
    }

    static void retrieveall(Context context){

        //CarParkList.carparksArrayList.clear();

        // get date and time
        String date_time = getTime();

        //first we fill the carpark list array with carpark objects (with no vacancies yet)
        retrieveCarParks(context);
        Log.d("Response","carpark retrieval success");
        // then we fill the vacancies, where we have to do a key match
        retrieveVacancies(date_time, context);
        Log.d("Response","carpark vacancy retrieval success");
    }

    static void retrieveCarParks(Context context){
        int offset = 0;

//        if(CarParkList.getCarParkList().size() != 0){
//            CarParkList.resetCarparksList();
//        }

        //create a request queue
        do {

            String URLpart1 = "https://data.gov.sg/api/action/datastore_search?offset=";
            String URLpart2 = "&resource_id=139a3035-e624-4f56-b63f-89ae28d4ae4c";
            String URL = URLpart1 + offset + URLpart2;

            //Log.d("Response", "URL is " + URL);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            //create json request
            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.d("Response", "response is: " + response.toString());

                            try {
                                JSONObject result = response.getJSONObject("result");
                                //Log.d("Response", "results are: " + result.toString());
                                JSONArray records = result.getJSONArray("records");
                                //Log.d("Response", "records are: " + records.toString());

                                for (int i = 0; i < records.length(); i++) {
                                    JSONObject temp = records.getJSONObject(i);
                                    //Log.d("Response", "temp is " + temp.toString());
                                    LatLonCoordinate latlng = SVY21.computeLatLon(temp.getDouble("y_coord"), temp.getDouble("x_coord"));
                                    CarPark entry = new CarPark(temp.getString("car_park_no"), temp.getString("address"), (float) temp.getDouble("x_coord"), (float) temp.getDouble("y_coord"), latlng.getLatitude(), latlng.getLongitude());
                                    //Log.d("Response", "temp is " + entry.carpark_address);
                                    CarParkList.addCarPark(entry);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Log.e("Response", "exception", e);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );

            requestQueue.add(objectRequest);

            offset = offset + 100;
        }while(offset < 3698);

        Log.d("Response", "end of retrieveCarParks");
    }

    static void retrieveVacancies(Context context){
        String timeStamp = Instant.now().toString();
        retrieveVacancies(timeStamp.substring(0,19), context);

    }

    static void retrieveVacancies(String date_time, Context context){

        String URL = "https://api.data.gov.sg/v1/transport/carpark-availability?date_time=";
        String ReqURL = URL + date_time;


        //create a request queue
        RequestQueue requestQueue=Volley.newRequestQueue(context);

        //create json request
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                ReqURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("Response", response.toString());
                        try{
                            JSONArray items = response.getJSONArray("items");
                            JSONObject itemsobject = items.getJSONObject(0);
                            JSONArray carpark_data = itemsobject.getJSONArray("carpark_data");
                            //Log.d("Response", "carparkdata are: " + carpark_data.toString());
                            //Log.d("Response", "number of carparkdata entries are: "+ carpark_data.length());
                            for(int i = 0; i < carpark_data.length(); i++){
                                JSONObject temp = carpark_data.getJSONObject(i);
                                int index = CarParkList.findCarpark(temp.getString("carpark_number"));
                                //Log.d("Response", "the index is: "+ index);

                                JSONArray carpark_info = temp.getJSONArray("carpark_info");
                                JSONObject carpark_infoobject = carpark_info.getJSONObject(0);
                                //Log.d("Response", "carpark_infoobject is: " + carpark_infoobject.toString());
                                CarParkList.changeVacancy(carpark_infoobject.getInt("lots_available"), index);
                                CarParkList.setCapacity(carpark_infoobject.getInt("total_lots"), index);
                                //Log.d("Response", "vacancies are: " + CarParkList.getCarParkList().get(index).vacancies);
                                //Log.d("Response", "capacity is: " + CarParkList.getCarParkList().get(index).capacity);

                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        requestQueue.add(objectRequest);

    }
}
