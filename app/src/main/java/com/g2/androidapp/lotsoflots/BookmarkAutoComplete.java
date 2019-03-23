package com.g2.androidapp.lotsoflots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class BookmarkAutoComplete extends AppCompatActivity implements BookmarkDataManager {
    private static final String TAG = "MainActivity";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE =1;
    Place finalPlace;
    static int i = 0;
    private static Gson gson = new Gson();
    ArrayList<BookmarkData> bookmarkData;

    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
            .setTypeFilter(Place.TYPE_COUNTRY)
            .setCountry("SG")
            .build();

    @Override
    protected void onActivityResult(int requestCode , int resultCode, Intent data){
        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Ds" + place.getName());
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR){
                Status status = PlaceAutocomplete.getStatus(this, data);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_auto_complete);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(autocompleteFilter);
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(1.093108, 103.563076),
                new LatLng(1.496751, 104.136911)
        ));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "TACLocation: " + place.getName());
                Log.i(TAG, "TACPlace: " + place.getLatLng());
                finalPlace = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        updateBookmarkData();

    }

    public void printAddedBookmark(View view) {
        updateBookmarkData();

        BookmarkData newBookmark = new BookmarkData(finalPlace.getLatLng().latitude + "," + finalPlace.getLatLng().longitude,finalPlace.getName().toString());
        addBookmark(newBookmark);

        finish();
    }

    private void updateBookmarkData(){
        String preferenceData = getSharedPreferences(BOOKMARK_KEY, MODE_PRIVATE).getString("bookmarkData","Not available");
        if (!preferenceData.equals("Not available") && !preferenceData.equals("")) {
            bookmarkData = gson.fromJson(preferenceData,new TypeToken<ArrayList<BookmarkData>>(){}.getType());
        }
        else {
            bookmarkData = new ArrayList<>();
        }
    }

    @Override
    public void deleteBookmark(String key) {

    }

    @Override
    public void addBookmark(BookmarkData bookmark) {
        SharedPreferences.Editor editor = getSharedPreferences(BOOKMARK_KEY, MODE_PRIVATE).edit();
        bookmarkData.add(bookmark);
        editor.putString("bookmarkData",gson.toJson(bookmarkData));
        editor.commit();
    }
}
