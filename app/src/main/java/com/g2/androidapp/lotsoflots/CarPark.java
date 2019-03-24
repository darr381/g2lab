package com.g2.androidapp.lotsoflots;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class CarPark {

    /**  Attributes for APIRetrieveSystem   */
    protected String carpark_number;
    protected String carpark_address;
    protected String carpark_name;
    protected float x_coord;
    protected float y_coord;
    protected double lat;
    protected double lng;
    protected int vacancies;
    protected int capacity;
    protected float distance = 0;


    private String location;

    CarPark(){ int vacancies = 0; }

/*
    CarPark(double lat, double lng){}

*/
    CarPark(double latitude, double longitude, int vac){
        lat = latitude;
        lng = longitude;
        vacancies = vac;
    }


    CarPark(String number, String address, float x, float y, double latitude, double longitude){
        carpark_number = number;
        carpark_address = address;
        carpark_name = address;
        x_coord = x;
        y_coord = y;
        lat = latitude;
        lng = longitude;
        vacancies = 0;

    }
    CarPark(String number, String address, float x, float y,  double latitude, double longitude, int vac){
        carpark_number = number;
        carpark_address = address;
        carpark_name = address;
        x_coord = x;
        y_coord = y;
        lat = latitude;
        lng = longitude;
        vacancies = vac;
    }



    /** Method to get location in the CarPark class */
    public LatLng getLocation(){
        LatLng latlonobject = new LatLng(lat, lng);
        return latlonobject;
    }

    public String getName(){
        return carpark_name;
    }

    public int getCapacity(){
        return capacity;
    }

    public void setCapacity(int cap){
        capacity = cap;
    }


    public float getDistance() {
        return distance;
    }

    /** Method to set location in the CarPark class */

 /*   public void setLocation(String Location){
        this.location = Location;
    }
*/


    public String getCarpark_address() {
        return carpark_address;
    }

    /** Method to get number of Vacancies within CarPark*/
    public int getVacancy(){
        return vacancies;

    }

    /** Method to set number of vacancies within CarPark */

    public void setVacancy(int vacancy){
        vacancies = vacancy;
    }

    public String getNumber(){
        return carpark_number;
    }



public  float calcDistance(LatLng currentloc){
    Location currentloc2 = new Location("");
    currentloc2.setLatitude(currentloc.latitude);
    currentloc2.setLongitude(currentloc.longitude);

    Location carparkloc = new Location("");
    carparkloc.setLatitude(lat);
    carparkloc.setLongitude(lng);
    distance = carparkloc.distanceTo(currentloc2);
    return carparkloc.distanceTo(currentloc2);


}


    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}

