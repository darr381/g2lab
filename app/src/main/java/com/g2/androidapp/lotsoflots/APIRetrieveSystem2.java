package com.g2.androidapp.lotsoflots;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import com.google.gson.Gson;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

public class APIRetrieveSystem2 {


    public static String teststring;
    public RequestQueue requestQueue;


    APIRetrieveSystem2(RequestQueue queue) { //constructor
        requestQueue = queue;

    }

    static void retrieveCarParks2(Context context){
        Log.d("Response", "retrieve carparks 2 method entered");

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        Map<String, String> params = new HashMap<>();
        params.put("AccountKey", "UdO0OyIPRCugajEoUNE1UA==");//put your parameters here
//        params.put("accept", "application/json");
        Log.d("Response","params are: " + params);
        CustomVolleyRequest jsObjRequest = new CustomVolleyRequest(
                Request.Method.GET, "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2", params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject result = null;
                        try {
                            result = response.getJSONObject("result");
                            Log.d("Response", result.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError response)
                    {
                        if(response.networkResponse != null && response.networkResponse.data != null){
                            response = new VolleyError(new String(response.networkResponse.data));
                        }
                        Gson gson = new Gson();
                        Log.d("Response", gson.toJson(response.networkResponse));
                        Log.d("Response", gson.toJson(response.networkResponse.headers));

                    }


                }
        );

        requestQueue.add(jsObjRequest);


    }
}