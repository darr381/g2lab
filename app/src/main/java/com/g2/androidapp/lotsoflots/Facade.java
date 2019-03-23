package com.g2.androidapp.lotsoflots;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Facade {

    private static Facade instance = null;
    private static String prevTime = null;

    private Facade(){
    }

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    public ArrayList<CarPark> getSortedList(LatLng location){
        if(prevTime == null){
            prevTime = Preference.getTime();
            APIRetrieveSystem.retrieveall();
        }else if(!prevTime.equals(Preference.getTime())){
            APIRetrieveSystem.retrieveall();
            prevTime = Preference.getTime();
        }
        Log.d("Response", CarParkList.getCarParkList().size()+"");
        return SortingSystem.sortCarparks(location);
    }

    public void UpdatePreferences(String distance, String vacancy, int hourFinal, int minuteFinal, Date date, String sort){
        PreferenceUpdater.updateAll(distance, vacancy, hourFinal, minuteFinal, date, sort);
    }


    public BookmarkAdapter getAdapter(Context context, int item, ArrayList<BookmarkData>bookmarkdatalist){
        BookmarkAdapter adapter = new BookmarkAdapter(context, item, bookmarkdatalist);
        return adapter;
    }

    public BookmarkAdapter getAdapter(Context context, int item){
        BookmarkAdapter adapter = new BookmarkAdapter(context, item);
        return adapter;
    }

    public SharedPreferences getBookmarkData(Context context){
        return BookmarkAdapter.getBookmarkData(context);
    }


}