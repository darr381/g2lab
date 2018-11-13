package com.g2.androidapp.lotsoflots;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GeoDataClient mGeoDataClient;
    private ArrayList<CarPark> listToDisplay = new ArrayList<>(0);
    private FusedLocationProviderClient mFusedLocationClient = null; // location provider
    private Location currentLocation = null;
    private Intent receivedIntent;
    private String sTargetLocation = null;
    private boolean searched = false;
    LinearLayout clickedItem;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    final int LOCATION_PERMISSION_REQUEST_CODE = 21;

    CarPark lastCarPark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        try{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                       if(currentLocation != location){
                           currentLocation = location;
                           if(searched == false){
                               searchLocation(location);
                               searched = true;
                           }
                       }

                    }else{
                        Toast.makeText(MapsActivity.this, "Please turn on the GPS", Toast.LENGTH_LONG).show();
                    }
                }

            });
        }catch(SecurityException e){
            e.printStackTrace();
        }

        APIRetrieveSystem.retrieveCarParks(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this, Filter.class));
        }else if(id == R.id.action_bookmarks){
            startActivity(new Intent(this,BookmarkPage.class));
        }else if (id == R.id.action_debug){
            startActivity(new Intent(this,MainActivity.class));
        }else if (id == R.id.action_about){
            startActivity(new Intent(this,AboutActivity.class));
        }else if(id == R.id.action_search){
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setBoundsBias(new LatLngBounds(
                                new LatLng(1.093108, 103.563076),
                                new LatLng(1.496751, 104.136911)
                                ))
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                //Log.i(TAG, "Place: " + place.getName());
                Location searchLocation = new Location("");
                searchLocation.setLongitude(place.getLatLng().longitude);
                searchLocation.setLatitude(place.getLatLng().latitude);
                mMap.clear();
                searchLocation(searchLocation);
                Toast.makeText(MapsActivity.this, place.getName(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receivedIntent = getIntent();
        sTargetLocation = receivedIntent.getStringExtra("com.g2.androidapp.lotsoflots.BMT");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setPadding(0, pxToDP(70), 0, pxToDP(70));

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng singapore = new LatLng(1.3521, 103.8198);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore,10));

        Location targetLocation = new Location("");

        if(sTargetLocation == null){
            if(currentLocation != null){
                targetLocation = currentLocation;
            }else{
                //Toast.makeText(MapsActivity.this, "No location detected", Toast.LENGTH_SHORT).show();
            }

        }else{
            String[] targetLocationBits = sTargetLocation.split(",");
            targetLocation.setLatitude(Double.parseDouble(targetLocationBits[0]));
            targetLocation.setLongitude(Double.parseDouble(targetLocationBits[1]));
            searched = true;
            searchLocation(targetLocation);
        }

    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        if(marker.getTag().getClass() == CarPark.class){
            CarPark cp = (CarPark) marker.getTag();
            showPin(cp.getName());
        }
        // Retrieve the data from the marker.



        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }
        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }
            };


    private int pxToDP(int px){
        final float scale = findViewById(R.id.main_content).getContext().getResources().getDisplayMetrics().density;
        int dp = (int) (px * scale + 0.5f);
        return dp;
    }


    private void searchLocation(Location location){
        //listToDisplay = SortingSystem.sortCarParkbyDistance(new LatLng(location.getLatitude(),location.getLongitude())); TODO: add call to sorting
        listToDisplay = new ArrayList<>(0);
        //listToDisplay.add(new CarPark("E8","ABC",  0, 0, 47.6739881, -122.121512));
        //CarParkList.setCarparksList(listToDisplay);
        listToDisplay = Facade.getSortedList(new LatLng(location.getLatitude(), location.getLongitude()), this);
        if(listToDisplay.size() == 0){
            Log.d("listdisplay", "SIZE 0");
        }
        Log.d("listdisplay", "" + CarParkList.getCarParkList().size());
        if(true){
            Marker here = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .title("Searched Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            here.setTag("HI");
        }
        for(int i = 0; i < listToDisplay.size(); i++){
            if(true){
                Marker mMarker;
                mMarker = mMap.addMarker(new MarkerOptions().position(listToDisplay.get(i).getLocation()).title(listToDisplay.get(i).carpark_number));
                mMarker.setTag(listToDisplay.get(i));
            }

        }
        populateCarParkList(listToDisplay);
        if(true){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),14));
        }
    }

    private void populateCarParkList(ArrayList<CarPark> cpList){
        View bottomSheet = findViewById( R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mBottomSheetBehavior.setPeekHeight(pxToDP(70));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        LinearLayout scrollContents = findViewById(R.id.scrollContents);

        scrollContents.removeAllViewsInLayout();

        for(int j = 0; j < cpList.size(); j++){
            LinearLayout itemLayout = new LinearLayout(this);

            // Implement it's on click listener.
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Show a toast message.
                    clickedItem = (LinearLayout) view;
                    TextView tv = (TextView) clickedItem.getChildAt(2);
                    Toast.makeText(MapsActivity.this, tv.getText(), Toast.LENGTH_SHORT).show();
                    showPin((String)tv.getText());
                }
            });


            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            lp.setMargins(10,10,10,10);
            itemLayout.setLayoutParams(lp);
            itemLayout.setOrientation(LinearLayout.VERTICAL);

            TextView title = new TextView(this);
            TextView contents = new TextView(this);
            TextView index = new TextView(this);
            title.setText(cpList.get(j).getName());
            title.setTypeface(title.getTypeface(), Typeface.BOLD_ITALIC);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            title.setPadding(5, 5, 5, 5);
            contents.setText("Vacancy: " + cpList.get(j).getVacancy());
            contents.setPadding(5, 5, 5, 20);
            index.setText(cpList.get(j).getName());
            index.setVisibility(View.INVISIBLE);
            itemLayout.addView(title);
            itemLayout.addView(contents);
            itemLayout.addView(index);

            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1
            ));
            v.setBackgroundColor(Color.parseColor("#B3B3B3"));
            itemLayout.addView(v);
            scrollContents.addView(itemLayout);
        }
    }

    private void showPin(String carPark){
        CarPark cp = CarParkList.getCarParkList().get(CarParkList.findCarpark(carPark));
        //LatLng pos = new LatLng(cp.lat, cp.lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cp.getLocation(),17));
        openDialog(cp);
    }

    public void openDialog(CarPark cp) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("CarPark " + cp.getName());
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        // Set Message
        TextView msg = new TextView(this);
        // Message Properties
        msg.setText("Vacancies: " + cp.getVacancy() + "/" + cp.getCapacity() + "\n" + "Do you wish to navigate to the car park?");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);

        lastCarPark= cp;

        // Set Button
        // you can more buttons
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"NAVIGATE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
                launchGoogleMaps(MapsActivity.this, lastCarPark.getLocation().latitude, lastCarPark.getLocation().longitude, lastCarPark.getName());
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
            }
        });

        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(Color.BLUE);
        okBT.setLayoutParams(neutralBtnLP);

        final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
    }

    public static void launchGoogleMaps(Context context, double latitude, double longitude, String label) {
        String format = "geo:0,0?q=" + Double.toString(latitude) + "," + Double.toString(longitude) + "(" + label + ")";
        Uri uri = Uri.parse(format);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
