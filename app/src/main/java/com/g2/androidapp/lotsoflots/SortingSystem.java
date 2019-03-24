/**
 SortingSystem.java

 Sorts CarParks based on:

 (1) Distance:
    sortCarParkbyDistance(LatLng llc)

 (2) Vacancy:
    sortCarParkbyVacancy(LatLng llc)

 Written by Vincent Wong
 */

package com.g2.androidapp.lotsoflots;

import android.util.Log;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
//import com.mapbox.mapboxsdk.geometry.LatLng;



public class SortingSystem {

    private static String prevTime = null;

    public static ArrayList<CarPark> sortCarparks(LatLng llc) {


        if (Preference.getSort().equals("Distance")) {

            return sortCarParkbyDistance(llc);
        }
        else{

            return sortCarParkbyVacancy(llc);
        }

    }




    /** To sort Car Park List according to Proximity to User */

    public static ArrayList<CarPark> sortCarParkbyDistance(LatLng llc) {

        /** Code first takes into account the user's filter of distance */
        double userDistance = Preference.getDistance();
        //double userDistance = 1000;

        /** Code then takes into account the user's filter of vacancies */
        int userVacancy = Preference.getVacancy();
        //int userVacancy = 10;

        /** Instantiation of CarPark ArrayLists to be used in the sorting algorithm */
        ArrayList<CarPark> temp = CarParkList.getCarParkList();
        ArrayList<CarPark> confirmed1 = new ArrayList<>();
        ArrayList<CarPark> confirmed2 = new ArrayList<>();
        ArrayList<CarPark> confirmed3 = new ArrayList<>();


        /** (1) Filtering based on Distance Filter */
        for (int k = 0; k < temp.size(); k++) {

            if (temp.get(k).calcDistance(llc) <= userDistance) {
                confirmed1.add(temp.get(k));
            }
        }
        System.out.println("the size of temp is: " + temp.size());

        System.out.println("the size of confirmed1 is: " + confirmed1.size());

        /** (2) Filtering results from (1) based on Vacancy Filter */

        for (int n = 0; n < confirmed1.size(); n++) {

            if (confirmed1.get(n).vacancies >= userVacancy) {
                confirmed2.add(confirmed1.get(n));
            }
        }

        System.out.println("the size of confirmed2 is: " + confirmed2.size());

        /** Code then sorts filtered results based on distance in ascending order */
        for (int i = 1; i < confirmed2.size(); i++) {

            for (int j = i; j > 0; j--) {

                if (confirmed2.get(j).calcDistance(llc) < confirmed2.get(j - 1).calcDistance(llc)) {

                    CarPark temp2 = confirmed2.get(j - 1);
                    confirmed2.set(j - 1, confirmed2.get(j));
                    confirmed2.set(j, temp2);
                }
            }
        }

        System.out.println("the size of confirmed2 is: " + confirmed2.size());

        /** Truncating array size to 10 elements or less */
        if (confirmed2.size() > 10) {
            for (int i = 0; i < 10; i++) {
                confirmed3.add(confirmed2.get(i));
            }
        } else {
            for (int i = 0; i < confirmed2.size(); i++) {
                confirmed3.add(confirmed2.get(i));
            }
        }

        System.out.println("the size of confirmed3 is: " + confirmed3.size());


        /** Printing elements in the array in Logcat */
        /*

        for (int m = 0; m < confirmed3.size(); m++) {
            Log.d("Response", "sorted carparks by distance is: " + confirmed3.get(m).calcDistance(llc));
            Log.d("Response", "carpark vacancy is: " + confirmed3.get(m).vacancies);
            Log.d("Response", "sorted carparks by distance is: " + confirmed3.get(m).carpark_address);
        }

        */


        /** Return Value */
        return confirmed3;
    }


    /**
     * To sort Car Park List according to Vacancy Count
     */

    public static ArrayList<CarPark> sortCarParkbyVacancy(LatLng llc) {

        /** Code first takes into account the user's filter of distance */
        double userDistance = Preference.getDistance();
        //double userDistance = 1000;

        /** Code then takes into account the user's filter of vacancies */
        int userVacancy = Preference.getVacancy();
        //int userVacancy = 200;

        /** Instantiation of CarPark ArrayLists to be used in the sorting algorithm */
        ArrayList<CarPark> temp = CarParkList.getCarParkList();
        ArrayList<CarPark> confirmed1 = new ArrayList<>();
        ArrayList<CarPark> confirmed2 = new ArrayList<>();
        ArrayList<CarPark> confirmed3 = new ArrayList<>();


        /** (1) Filtering based on Distance Filter */
        for (int k = 0; k < temp.size(); k++) {

            if (temp.get(k).calcDistance(llc) <= userDistance) {
                confirmed1.add(temp.get(k));
            }
        }


        /** (2) Filtering results from (1) based on Vacancy Filter */
        for (int n = 0; n < confirmed1.size(); n++) {

            if (confirmed1.get(n).getVacancy() >= userVacancy) {
                confirmed2.add(confirmed1.get(n));
            }
        }

        /** Code then sorts filtered results based on vacancy in descending order */

        for (int i = 1; i < confirmed2.size(); i++) {

            for (int j = i; j > 0; j--) {

                if (confirmed2.get(j).getVacancy() > confirmed2.get(j - 1).getVacancy()) {

                    CarPark temp2 = confirmed2.get(j - 1);
                    confirmed2.set(j - 1, confirmed2.get(j));
                    confirmed2.set(j, temp2);
                }
            }
        }

        /** Truncating array size to 10 elements or less */
        if (confirmed2.size() > 10) {
            for (int i = 0; i < 10; i++) {
                confirmed3.add(confirmed2.get(i));
            }
        } else {
            for (int i = 0; i < confirmed2.size(); i++) {
                confirmed3.add(confirmed2.get(i));
            }
        }

        /*

        for (int m = 0; m < confirmed3.size(); m++) {
            Log.d("Response", "sorted carparks by distance is: " + confirmed3.get(m).calcDistance(llc));
            Log.d("Response", "sorted carparks by vacancy is: " + confirmed3.get(m).vacancies);
            Log.d("Response", "sorted carparks by vacancy address is: " + confirmed3.get(m).carpark_address);
        }

        */

        return confirmed3;

    }


    public static ArrayList<CarPark> getSortedList(LatLng location){
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

}
