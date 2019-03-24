package com.g2.androidapp.lotsoflots;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// classes needed to initialize map
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.annotations.Icon;

import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import android.util.Log;


// classes needed to add the location component
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;

// classes needed to add a marker
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;


public class TBTActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button button;
    private boolean searched = false;
//    private ProgressBar pg;
    private Location previouslyLocation;
    private ArrayList<CarPark> listToDisplay = new ArrayList<>(0);
    private BottomSheetBehavior mBottomSheetBehavior;
    LinearLayout clickedItem;
    CarPark lastCarPark;




    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;



    AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
            .setTypeFilter(Place.TYPE_COUNTRY)
            .setCountry("SG")
            .build();

    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Response","stage 1");

        super.onCreate(savedInstanceState);

        requestQueue=Volley.newRequestQueue(this);

        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_tbt);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        APIRetrieveSystem retrieve = new APIRetrieveSystem(requestQueue);
        retrieve.retrieveCarParks();
        Log.d("Response","stage 2");


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
                                new com.google.android.gms.maps.model.LatLng(1.093108, 103.563076),
                                new com.google.android.gms.maps.model.LatLng(1.496751, 104.136911)
                        )).setFilter(autocompleteFilter)
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
                mapboxMap.clear();
                searched = true;
                searchLocation(searchLocation);
                Toast.makeText(TBTActivity.this, place.getName(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void searchLocation(Location location){
//        pg.setVisibility(View.VISIBLE);

        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromBitmap(BitmapFactory.decodeFile("/Users/vincent/StudioProjects/g2lab/app/src/main/res/drawable/pin.png"));
//        Icon icon = BitmapFactory.decodeFile("/sdcard/test2.png")

        previouslyLocation = location;
        //listToDisplay = SortingSystem.sortCarParkbyDistance(new LatLng(location.getLatitude(),location.getLongitude())); TODO: add call to sorting
        listToDisplay = new ArrayList<>(0);
        //listToDisplay.add(new CarPark("E8","ABC",  0, 0, 47.6739881, -122.121512));
        //CarParkList.setCarparksList(listToDisplay);

        listToDisplay = SortingSystem.getSortedList(new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude()));
        if(listToDisplay.size() == 0){
            Log.d("listdisplay", "SIZE 0");
        }
        Log.d("listdisplay", "" + CarParkList.getCarParkList().size());
        mapboxMap.clear();

        for(int i = 0; i < listToDisplay.size(); i++){

            Marker mMarker;
            mMarker = mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(listToDisplay.get(i).getLat(), listToDisplay.get(i).getLng()))
//                    .position(new LatLng(10, 100))
                    .title(listToDisplay.get(i).getCarpark_name()));


        }
        Log.d("Response", "Size of list: " + listToDisplay.size());

        Marker here = mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(),location.getLongitude()))
                .title("Searched Location")
                .icon(icon));

        populateCarParkList(listToDisplay);
//        pg.setVisibility(View.INVISIBLE);
        if(true){
            mapboxMap.moveCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),15));
        }
    }
    private int pxToDP(int px){
        final float scale = findViewById(R.id.main_content).getContext().getResources().getDisplayMetrics().density;
        int dp = (int) (px * scale + 0.5f);
        return dp;
    }
    private void showPin(String carPark){
        CarPark cp = CarParkList.getCarParkList().get(CarParkList.findCarpark(carPark));
        //LatLng pos = new LatLng(cp.lat, cp.lng);
//        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cp.getLocation(),17));
        mapboxMap.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(cp.getLocationMB(), 20));
//        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(cp.getLocation(),(float)0.0,(float)0.0,(float)0.0)), 20);
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
//                launchGoogleMaps(MapsActivity.this, lastCarPark.getLocation().latitude, lastCarPark.getLocation().longitude, lastCarPark.getName());
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
    private void populateCarParkList(ArrayList<CarPark> cpList){
        View bottomSheet = findViewById( R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mBottomSheetBehavior.setPeekHeight(pxToDP(70));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        LinearLayout scrollContents = findViewById(R.id.scrollContents);

        scrollContents.removeAllViewsInLayout();

        if(cpList.size() > 0){
            for(int j = 0; j < cpList.size(); j++){
                LinearLayout itemLayout = new LinearLayout(this);

                // Implement it's on click listener.
                itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Show a toast message.
                        clickedItem = (LinearLayout) view;
                        TextView tv = (TextView) clickedItem.getChildAt(4);
                        Toast.makeText(TBTActivity.this, tv.getText(), Toast.LENGTH_SHORT).show();
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
                TextView address = new TextView(this);
                TextView distance = new TextView(this);
                title.setText("CarPark " + cpList.get(j).getName());
                title.setTypeface(title.getTypeface(), Typeface.BOLD_ITALIC);
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                title.setPadding(5, 5, 5, 5);
                contents.setText("Vacancy: " + cpList.get(j).getVacancy() + "/" + cpList.get(j).getCapacity());
                contents.setPadding(5, 5, 5, 20);
                distance.setText("Distance: " + (int)cpList.get(j).getDistance() + " meters");
                distance.setPadding(5, 5, 5, 20);
                index.setText(cpList.get(j).getName());
                index.setVisibility(View.INVISIBLE);
                address.setText(cpList.get(j).getCarpark_address());
                address.setPadding(5, 5, 5, 20);
                itemLayout.addView(title);
                itemLayout.addView(contents);
                itemLayout.addView(distance);
                itemLayout.addView(address);
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
        }else{
            LinearLayout itemLayout = new LinearLayout(this);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            lp.setMargins(10,10,10,10);
            itemLayout.setLayoutParams(lp);
            itemLayout.setOrientation(LinearLayout.VERTICAL);

            TextView title = new TextView(this);
            title.setText("No Results");
            title.setTypeface(title.getTypeface(), Typeface.BOLD_ITALIC);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            title.setPadding(5, 5, 5, 5);
            itemLayout.addView(title);
            scrollContents.addView(itemLayout);
        }
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Log.d("Response","stage 3");

                ArrayList<CarPark> AllCarparks = CarParkList.getCarParkList();
//
//                for (int i =0; i<5; i++) {
//                    AllCarparks.add(i, (CarParkList.getCarParkList().get(i)));
//                }


//                CarPark cp1 = new CarPark("AB1", "A", 1, 2,  1.354090, 103.687850, 20);
//                CarPark cp2 = new CarPark("AB2", "B", 1, 2,  1.340313, 103.706126, 20);
//                ArrayList<CarPark> AllCarparks = new ArrayList<CarPark>();
//                AllCarparks.add(0,cp1);
//                AllCarparks.add(1,cp2);


                // Add the marker image to map

//
                for (CarPark carpark : AllCarparks) {
                    double lon = carpark.getLng();
                    double lat = carpark.getLat();

                    style.addImage("marker-icon-id"+carpark.carpark_number,
                            BitmapFactory.decodeResource(
                                    TBTActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));

                    GeoJsonSource geoJsonSource = new GeoJsonSource("source-id"+carpark.carpark_number, Feature.fromGeometry(
                            Point.fromLngLat(lon, lat)));
                    style.addSource(geoJsonSource);
                    SymbolLayer symbolLayer = new SymbolLayer("layer-id"+carpark.carpark_number, "source-id"+carpark.carpark_number);
                    symbolLayer.withProperties(
                            PropertyFactory.iconImage("marker-icon-id"+carpark.carpark_number)
                    );
                    style.addLayer(symbolLayer);
//
//
                }


//                style.addImage("marker-icon-id",
//                        BitmapFactory.decodeResource(
//                                MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));
//
//                GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(
//                        Point.fromLngLat(103.687895, 1.353883)));
//                style.addSource(geoJsonSource);
//                SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
//                symbolLayer.withProperties(
//                        PropertyFactory.iconImage("marker-icon-id")
//                );
//                style.addLayer(symbolLayer);



                enableLocationComponent(style);

                addDestinationIconSymbolLayer(style);

                mapboxMap.addOnMapClickListener(TBTActivity.this);
                button = findViewById(R.id.startButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean simulateRoute = true;
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(simulateRoute)
                                .build();
                        // Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(TBTActivity.this, options);
                    }
                });
            }
        });
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
//        Gson gson = new Gson();
//
//        String test = gson.toJson(mapboxMap.getStyle());
//        String test2 = gson.toJson(locationComponent);
//
//        Log.d("Response","style" + test);
//
//        enableLocationComponent(mapboxMap.getStyle());
//
//        Log.d("Response","last known" + test2);

        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
            source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }

        getRoute(originPoint, destinationPoint);
        button.setEnabled(true);
        button.setBackgroundResource(R.color.mapbox_blue);
        return true;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}