package com.example.halofarms.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.halofarms.Field;
import com.example.halofarms.FieldList;
import com.example.halofarms.MyDialogFragment;
import com.example.halofarms.Point;
import com.example.halofarms.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Class showing Google Map in the UI with field;
 * Map can be empty, only drawn, analysis required, analyzed
 * - empty: if this is the first access with searched address or if user hasn't yet draw the field.
 * - only drawn: if the user has drawn the field but he hasn't requests analysis.
 * - analysis required: map is drawn and user has clicked some point; these are yellow and they
 *   needed to be analyzed.
 * - analyzed: some points have been analyzed, the are now green, field is not more modifiable.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, View.OnClickListener, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter, AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerClickListener,
        MyDialogFragment.DateCallback {

    // field eventually with points drawn (the current one!)
    private Field field;
    // all this fields with analysis
    private FieldList fieldList;
    // date of analysis
    private String dateOfAnalysis;
    // indicate if map is drawn
    private boolean isMapDrawn;
    // map
    private GoogleMap googleMap;
    // array that contains points to draw polygon
    private final List<LatLng> points = new ArrayList<>();
    private final List<Point> toAnalyze = new ArrayList<>();
    // array of ec heatMap
    private final List<TileOverlay> EcTileOverlays = new ArrayList<>();
    // array of sar heatMap
    private final List<TileOverlay> SarTileOverlays = new ArrayList<>();
    // array of Ph heatMap
    private final List<TileOverlay> PhTileOverlays = new ArrayList<>();
    // array of cec heatMap
    private final List<TileOverlay> CecTileOverlays = new ArrayList<>();
    // contains cells of grid: internal polygons
    private final ArrayList<Polygon> polygons = new ArrayList<>();
    // save marker for removing after drawing!
    private final ArrayList<Marker> markers = new ArrayList<>();
    // choose explicitly points
    private Button handFreePointButton;
    // button to draw polygon
    private Button drawPolyButton;
    // delete polygon from map
    private Button deleteButton;
    // mark my position on map
    private Button markButton;
    // shows qr code of point
    private Button qrCodeButton;
    // id of zone of polygon
    private int zone = -1;
    // if positionNeeded is requested
    private boolean positionNeeded;
    // my position on map
    private LatLng position;
    // perimeter poly
    private Polygon perimeterPoly;
    // drawn area dimension
    private double area = 0;
    // text view that shows area dimension
    private TextView areaTextView;
    // indicate what to show (points or heat maps)
    private String toShow = "Points";
    // username (email) of account (to save in the correct entry of Firestore)
    private String username;
    // says if points are to drawn free hand
    private boolean handFree = false;
    // for send and get Field
    private final Gson gson = new Gson();
    // indicate if can be modified (not yet analyzed)
    private boolean isEditable;
    // show field only with red point (new entry in database)
    private boolean newAnalysis;
    // point to show qr code
    private Point qrCodePoint;
    // reference to internal preferences
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // references to view items
        handFreePointButton = findViewById(R.id.points_button);
        drawPolyButton = findViewById(R.id.draw_button);
        deleteButton = findViewById(R.id.delete_button);
        markButton = findViewById(R.id.mark_button);
        areaTextView = findViewById(R.id.area_text_view);
        // spinner shows if heatMap or points
        Spinner spinner = findViewById(R.id.map_spinner);
        qrCodeButton = findViewById(R.id.qr_code_button);
        // Create an ArrayAdapter using the string array and a default spinner layout,
        // this permits to choose what to show in map (only points or heat maps)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.map, R.layout.spinner_text_view);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // set listeners to UI items
        handFreePointButton.setOnClickListener(this);
        drawPolyButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        markButton.setOnClickListener(this);
        qrCodeButton.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);

        // get intent from main activity an get all extras
        Intent intent = getIntent();
        // if the field isn't analyzed can be modified
        isEditable = intent.getBooleanExtra("EDITABLE", true);
        // indicates if the map is already drawn when user starts this activity
        isMapDrawn = intent.getBooleanExtra("IS_MAP_DRAWN", false);
        // if position of user is needed
        positionNeeded = intent.getBooleanExtra("POSITION", false);
        // request new analysis
        newAnalysis = intent.getBooleanExtra("NEW_ANALYSIS", false);
        // field (maybe only name and address)
        field = gson.fromJson(intent.getStringExtra("FIELD"), Field.class);

        // get reference to internal preference for getting some global value
        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        // get username for saving in the correct entry of db
        username = sharedPref.getString("USERNAME", "");
        // not passed as extra because it can be to big
        fieldList = gson
                .fromJson(sharedPref.getString("FIELD_WITH_DATE", null),
                        FieldList.class);
        if (fieldList == null) {
            fieldList = new FieldList();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * <p>
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        // map style
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // check if a polygon (field) is already drawn
        if (isMapDrawn) {
            // build, load, set, the already built map
            buildDrawnMap();
            // map isn't never drawn, user requests to build field using his position
        } else if (positionNeeded) {
            // this button permits to choose the position of points inside field
            handFreePointButton.setVisibility(View.VISIBLE);
            // button which clicked draw perimeter and points inside it
            drawPolyButton.setVisibility(View.VISIBLE);
            // recognize the click on map: every click draws a point of the perimeter
            googleMap.setOnMapClickListener(this);
            // permissions to use GPS
            enableMyLocation();
        } else {
            // the user asks to be taken to a position whose
            // address he has explicitly entered (no GPS)

            // recognize the click on map: every click draws a point of the perimeter
            googleMap.setOnMapClickListener(this);
            // button which clicked draw perimeter and points inside it
            drawPolyButton.setVisibility(View.VISIBLE);
            // button which click permits to free hand draw points inside perimeter
            handFreePointButton.setVisibility(View.VISIBLE);
            // take user into requested address
            moveCameraToAddress();
        }
    }

    /**
     * Load the already drawn field.
     * Set the right listeners according to some parameters.
     * Finally shows the field on map and take the user over it.
     */
    private void buildDrawnMap() {
        // custom layout of windows adapter; in this way the 4 point's
        // attributes of analysis are clearly visible when clicked on it
        googleMap.setInfoWindowAdapter(this);
        // show the above window
        googleMap.setOnMarkerClickListener(this);
        // field can be edited
        if (isEditable) {
            //when user clicked on above window he can
            // insert result of analysis of the yellow points
            googleMap.setOnInfoWindowClickListener(this);
        } else {
            // cannot delete field from this context if it is analyzed,
            // hide delete button
            deleteButton.setVisibility(View.GONE);
        }
        // draw field on map
        loadPolygon();
        // take user into requested address
        moveCameraToAddress();
    }

    /***
     * User will visualize the requested address in map;
     * the address of field is converted in latitude and longitude,
     * and camera is moving over these coordinates.
     */
    private void moveCameraToAddress() {
        try {
            // decode/encode address in latitude and longitude
            Geocoder geocoder = new Geocoder(this);
            // take the address passed from activity
            List<Address> addresses = geocoder.getFromLocationName(field.getAddress()
                    , 1);
            // convert name in GPS coordinates
            LatLng mapPlace = new LatLng(addresses.get(0).getLatitude(),
                    addresses.get(0).getLongitude());
            // move camera over address.
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    mapPlace, 18));

            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPlace, 18f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draw a red point in the GPS position that user clicks on map.
     * This listener is active only when field isn't already drawn:
     * it marks perimeter's vertex or the points inside the perimeter
     * when user explicitly requests them to be hand free drawing.
     * @param latLng clicked on map.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // build a marker on the latLng clicked: vertex of perimeter
        MarkerOptions marker = new MarkerOptions().position(latLng)
                .icon(bitmapDescriptorFromVector(R.drawable.red_round_shape));
        // add marker to map
        markers.add(googleMap.addMarker(marker));
        // add the clicked point to a list used for draw perimeter.
        points.add(latLng);
    }

    /**
     * Build the invisible squares inside perimeter which center contains suggested point.
     * For drawing that cells, firstly calculate minimum rectangle containing perimeter,
     * then build the cells moving a square inside this rectangle until it isn't fill.
     * @param coordinates (points - vertex) of the perimeter.
     */
    private void drawGrid(List<LatLng> coordinates) {
        // include all point of perimeter
        // for drawing the minimum square that contains perimeter
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng latLng : coordinates) {
            builder.include(latLng);
        }
        //------ THIS IS THE MINIMUM RECTANGLE THAT CONTAINS MY PERIMETER ------
        // extract the top-right vertex
        LatLng northeast = builder.build().northeast;
        // extract the bottom left vertex
        LatLng southwest = builder.build().southwest;
        // calculate top-left vertex
        LatLng northwest = new LatLng(northeast.latitude, southwest.longitude);
        // calculate bottom-right vertex
        LatLng southeast = new LatLng(southwest.latitude, northeast.longitude);
        // minimum rectangle that contains perimeterPoly: for drawing suggested point
        Polygon rectanglePoly = googleMap.addPolygon(new PolygonOptions()
                .visible(false)
                .add(northwest)
                .add(northeast)
                .add(southeast)
                .add(southwest));
        // calculate area of internal cells
        float meters = areaOfSquare();
        // number of default iterations for fill the minimum rectangle
        int iterations = (area > 4) ? 50 : 20;
        // build internal cells: the grid
        buildInternalCells(meters, iterations, southeast, rectanglePoly);
    }

    /**
     * Calculate the areas of internal cells according to field's area.
     * Bigger field implies bigger cells area
     * @return cell's area.
     */
    private float areaOfSquare() {
        float meters;
        if (area < 0.1) {
            meters = 5;
        } else if (area > 0.1 && area < 0.2) {
            meters = 6;
        } else if (area > 0.2f && area < 0.3) {
            meters = 7;
        } else if (area > 0.3 && area < 0.5) {
            meters = 8;
        } else if (area > 0.5 && area < 0.7) {
            meters = 9;
        } else if (area > 0.7 && area < 0.9) {
            meters = 9.5f;
        } else if (area > 0.9 && area < 2) {
            meters = 15;
        } else if (area > 2 && area < 3) {
            meters = 20;
        } else if (area > 3 && area < 4) {
            meters = 25;
        } else {
            meters = 30;
        }
        return meters;
    }

    /**
     * Build the grid of the field: every center of internal cells is the suggested point.
     * Build the cells moving a square inside the minimum rectangle until it isn't fill.
     * @param meters area of every cell.
     * @param iterations number of image-moving inside rectangle.
     * @param startPoint point from which image-moving starts.
     * @param rectangle minimum rectangle containing perimeter.
     */
    private void buildInternalCells(float meters, int iterations, LatLng startPoint,
                                    Polygon rectangle) {
        // start to build the internal squares:
        // use an image of square for every cell and drawn on top of it a polygon
        // first square-image: start from bottom-right corner
        LatLng southeast = startPoint, northeast, southwest, northwest;
        GroundOverlayOptions options = new GroundOverlayOptions()
                .visible(false)
                .image(Objects.requireNonNull(bitmapDescriptorFromVector(R.drawable.square)))
                .anchor(0, 0)
                .position(southeast, meters);
        GroundOverlay prev, curr = googleMap.addGroundOverlay(options);
        // move in horizontal
        for (int i = 0; i < iterations; i++) {
            prev = curr;
            // move in vertical
            for (int j = 0; j < iterations; j++) {
                // build new square-image
                curr = googleMap.addGroundOverlay(new GroundOverlayOptions()
                    .visible(false)
                    .image(Objects.requireNonNull(bitmapDescriptorFromVector(R.drawable.square)))
                    .anchor(1, 1)
                    .position(curr.getBounds().northeast, meters));
                // extract the top-right vertex
                northeast = curr.getBounds().northeast;
                // extract the bottom left vertex
                southwest = curr.getBounds().southwest;
                // calculate top-left vertex
                northwest = new LatLng(northeast.latitude, southwest.longitude);
                // calculate bottom-right vertex
                southeast = new LatLng(southwest.latitude, northeast.longitude);
                // check if at least one vertex is inside minimum rectangle
                List<LatLng> rectVertex = rectangle.getPoints();
                if (PolyUtil.containsLocation(northeast, rectVertex, false)
                        || PolyUtil.containsLocation(northwest, rectVertex, false)
                        || PolyUtil.containsLocation(southeast, rectVertex, false)
                        || PolyUtil.containsLocation(southwest, rectVertex, false)) {
                    // build the poly-square on top of square-image
                    Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                            .visible(false)
                            .add(northwest)
                            .add(northeast)
                            .add(southeast)
                            .add(southwest));
                    // add polygon (square) to list that will be used
                    // for drawing suggested points
                    polygons.add(polygon);
                    // remove actual square-image
                    curr.remove();
                }
            }
            // build the new square-image on top of the previous one
            curr = googleMap.addGroundOverlay(new GroundOverlayOptions()
                .visible(false)
                .image(Objects.requireNonNull(bitmapDescriptorFromVector(R.drawable.square)))
                .anchor(1, 1)
                .position(prev.getBounds().southwest, meters));
            // no more needed: remove
            prev.remove();
        }
    }

    /**
     * Build and display a Toast.
     * @param message to show
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Click listener associates to the buttons presents in the UI.
     * @param v the button clicked.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        // user wants to hand free draw the field
        if (v == handFreePointButton) {
            // check if the text in this button is "END" or "POINTS";
            // if the text is "END" save points just drawn to Firestore
            String text = handFreePointButton.getText().toString();
            switch (text) {
                // user chooses to draw hand free points
                case "hand free":
                    handFree = true;
                    // if click again on this button, restore default configuration
                    handFreePointButton.setText("restore");
                    showToast("Draw the perimeter and then click the points you want inside it, " +
                            "or use your position clicking MARK");
                    break;
                // user chooses to draw default suggested points
                case "restore":
                    handFree = false;
                    showToast("Restored default points");
                    handFreePointButton.setText("hand free");
                    break;
                // user wants to save the just drawn hand free field
                case "end":
                    // if he has not marked any points show an error message
                    if (points.isEmpty()) {
                        showToast("Select at least one point");
                    } else {
                        // draw the free hand points
                        drawHandFreePolyAndPoints();
                        // hide all button except delete button
                        handFreePointButton.setVisibility(View.GONE);
                        markButton.setVisibility(View.GONE);
                        showToast("Map saved");
                    }
                    break;
            }
        }
        // user wants to draw field: at least 3 vertex must be present in map
        if (v == drawPolyButton) {
            // check if there are at least three points to connect
            if (points.size() < 3) {
                Toast.makeText(this, "Click at least 3 points", Toast.LENGTH_SHORT).show();
                return;
            }
            // draw the perimeter on map
            perimeterPoly = googleMap.addPolygon(new PolygonOptions().addAll(points));
            // always on top
            perimeterPoly.setZIndex(1);
            // compute area for decides how many points draw and distance
            area = SphericalUtil.computeArea(perimeterPoly.getPoints()) / 10000;
            // show on mmp the dimension of field
            areaTextView.setVisibility(View.VISIBLE);
            areaTextView.setText("Area: " +  new DecimalFormat("#.##").format(area)+ " ha");
            // remove markers that show vertex of perimeter, now the perimeter is drawn
            for (int i = 0; i < markers.size(); i++) {
                markers.get(i).remove();
            }
            markers.clear();
            // user wants default suggested points
            if (!handFree) {
                // draw default poly and points
                drawDefaultPolyAndPoints();
            } else {
                // clean points; so the next click on map is the new point to save
                points.clear();
                // change text on pointButton for restoring default config
                handFreePointButton.setText("end");
            }
            // if user has decided to use his position,
            // he can mark his GPS location as suggested point clicking this button
            if (positionNeeded) {
                markButton.setVisibility(View.VISIBLE);
            }
            // permits to click the suggested point for marking them as to analyze or not
            googleMap.setOnMarkerClickListener(this);
            // hide this button because field is now drawn
            drawPolyButton.setVisibility(View.GONE);
            //drawRandomHeatMap(suggestedPoints);
        }
        // delete the field, delete everything in the map
        if (v == deleteButton) {
            // clean the arrays and remove everything from map
            cleanMap();
            // field isn't more drawn
            isMapDrawn = false;
            // no area presents
            area = 0;
            areaTextView.setVisibility(View.INVISIBLE);
            // restore id: first point to draw is the perimeter and it will have -1 as id
            zone = -1;
            // prepare maps for listening the user click (vertex of perimeter)
            googleMap.setOnMapClickListener(this);
            // remove listener on Marker, now is the moment to draw vertex
            googleMap.setOnMarkerClickListener(null);
            // the buttons must be visible again
            drawPolyButton.setVisibility(View.VISIBLE);
            handFreePointButton.setVisibility(View.VISIBLE);
            handFreePointButton.setText("hand free");
            // if there isn't any field drawn cannot mark the position
            markButton.setVisibility(View.GONE);
        }
        // user marks his position as suggested point
        if (v == markButton) {
            // build  marker on user position position
            MarkerOptions marker = new MarkerOptions().position(position)
                    .icon(bitmapDescriptorFromVector(R.drawable.red_round_shape));
            // hand free case: add the point just clicked point on my GPS position
            if (handFree) {
                // draw the marker (point) on map
                markers.add(googleMap.addMarker(marker));
                // list that will be saved once finishing to mark points
                points.add(position);
                // default configuration: remove default point and mark his position as new one
            } else {
                // remove the suggest marker and save the new one representing user position
                for (int i = 0; i < polygons.size(); i++) {
                    // search which square contains the user position
                    if (PolyUtil.containsLocation(position, polygons.get(i).getPoints(),
                            false)) {
                        // remove default marker
                        markers.get(i).remove();
                        // use the user position as new point
                        markers.set(i, googleMap.addMarker(marker));
                        // update that point
                        updateItemById(i);
                        // save to database
                        saveToFirestore();
                        break;
                    }
                }
            }
        }
        // a Marker (point) is focused, if user want to show QR code associates to that point
        if (v == qrCodeButton) {
            // create and open a dialog that will show QR code
            Bundle bundle = new Bundle();
            // Dialog id
            bundle.putInt("WHAT", 5);
            // string to convert in QR code
            bundle.putString("SHOW_QR_CODE", qrCodePoint.getJsonPoint());
            // create dialog
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            // pass the arguments to it
            myDialogFragment.setArguments(bundle);
            // show it
            myDialogFragment.show(getSupportFragmentManager(), "");
        }
    }

    // remove one item from db

    /**
     * Change the coordinates of a point; user wants
     * to substitute one default point with his position point.
     * @param id of marker that represents the point to move.
     */
    private void updateItemById(int id) {
        // new marker on map
        LatLng newPoint = markers.get(id).getPosition();
        // search previously point ans swap coordinates
        for (Point p : field.getPoints()) {
            // found
            if (p.getZoneId() == id) {
                p.setSuggestedPoint(newPoint.latitude + " " + newPoint.longitude);
                Marker marker = markers.get(id);
                marker.setTitle(p.getZoneId() + "");
                marker.setSnippet(makeSnippet(p));
                break;
            }
        }
    }

    /**
     * Draw and save hand free field.
     * Once user has finished to click the points inside perimeter and he has clicked
     * END button, the field is drawn and saved on Firestore.
     */
    private void drawHandFreePolyAndPoints() {
        // remove marker for drawing the points
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
        markers.clear();
        // array containing points to put inside field
        ArrayList<Point> pts = new ArrayList<>();
        // the first one is the perimeter
        pts.add((new Point(fromPolygonToString(perimeterPoly), zone++, null)));
        // draw the free hand points
        for (LatLng latLng : points) {
            Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                    .visible(false)
                    .add(latLng));
            polygons.add(polygon);
            Point p = new Point(fromPolygonToString(polygon), zone++,
                    latLng.latitude + " " + latLng.longitude);
            p.setJsonPoint(new Gson().toJson(field.getName() + " " + p.getZoneId()));
            pts.add(p);
            // draw on map the point suggested if is inside perimeter!
            markers.add(googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(p.getZoneId() + "").snippet(makeSnippet(p))
                    .icon(bitmapDescriptorFromVector(R.drawable.red_round_shape))));
        }
        configureMapOnceFieldIsDrawn(pts);
    }

    /**
     * Default configuration of field. Standard suggested points inside the field.
     * Once perimeter is drawn, pass its vertexes to a function that will calculate the minimum
     * rectangle containing perimeter and calculate the grid of cells
     * whose centers are the points that this method draws.
     */
    private void drawDefaultPolyAndPoints() {
        // array containing points to put inside field
        ArrayList<Point> pts = new ArrayList<>();
        // the first one is the perimeter
        pts.add((new Point(fromPolygonToString(perimeterPoly), zone++, null)));
        // draw the grid of the perimeter
        drawGrid(perimeterPoly.getPoints());
        // iterate over all cells inside minimum rectangle
        for (int i = 0; i < polygons.size(); i++) {
            Polygon poly = polygons.get(i);
            // save the square of suggested points (center of polygon)
            List<LatLng> coordinates = poly.getPoints();
            // extract center of poly
            LatLng center = LatLngBounds.builder().include(coordinates.get(0)).include(coordinates.get(1))
                    .include(coordinates.get(2)).include(coordinates.get(3))
                    .build().getCenter();
            // build point: square containing the point, zoneId, coordinates of point
            Point p = new Point(fromPolygonToString(poly), zone++,
                    center.latitude + " " + center.longitude);
            // set global unique id
            p.setJsonPoint(new Gson().toJson(field.getName() + " " + p.getZoneId() + " "
                    + field.getDate()));
            pts.add(p);
            // draw on map the point suggested if it is inside perimeter
            markers.add(googleMap.addMarker(new MarkerOptions()
                    .visible(PolyUtil.containsLocation(center, perimeterPoly.getPoints(),
                            false))
                    .position(fromStringToLatLng(p.getSuggestedPoint()).get(0))
                    .title(p.getZoneId() + "").snippet(makeSnippet(p))
                    .icon(bitmapDescriptorFromVector(R.drawable.red_round_shape))));
        }
        configureMapOnceFieldIsDrawn(pts);
    }

    /**
     * Configure map and field once field is ready.
     * @param pts points inside the field.
     */
    private void configureMapOnceFieldIsDrawn(ArrayList<Point> pts) {
        // set these points to field
        field.setPoints(pts);
        // save online
        saveToFirestore();
        // map is now drawn
        isMapDrawn = true;
        // map must be not clickable because the poly is drown
        googleMap.setOnMapClickListener(null);
        // possibility of clicking the markers for inserting analysis
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);
        // hide button for drawing field
        drawPolyButton.setVisibility(View.GONE);
    }

    /**
     * Build the string formatted for the MarkerWindow, showing the attributes of point.
     * @param p point to showing
     * @return formatted string to put as snippet in Marker
     */
    private String makeSnippet(Point p) {
        return "EC: " + p.getEc() + "\n" +
                "SAR: " + p.getSar() + "\n" +
                "pH: " + p.getPh() + "\n" +
                "CEC: " + p.getCec();
    }

    /**
     * Draw the field on Map.
     * variable field is passed from MainActivity, extract its points and draw them on map.
     */
    @SuppressLint("SetTextI18n")
    private void loadPolygon() {
        // get all point of field
        List<Point> points = field.getPoints();

        // for each point get his info
        for (Point p : points) {
            // square containing p
            Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                    .addAll(fromStringToLatLng(p.getSquare())));
            polygon.setVisible(false);
            // as polygon show on map only the perimeter (it has id -1)
            if (p.getZoneId() == -1) {
                perimeterPoly = polygon;
                area = SphericalUtil.computeArea(perimeterPoly.getPoints()) / 10000;
                areaTextView.setVisibility(View.VISIBLE);
                areaTextView.setText("Area: " + new DecimalFormat("#.##")
                        .format(area) + " ha");
                perimeterPoly.setVisible(true);
                // other polygons (cells of the grid)
            } else {
                // show only the center of square (point) if it is inside the perimeter
                LatLng suggestedPoint = fromStringToLatLng(p.getSuggestedPoint()).get(0);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .visible(PolyUtil.containsLocation(suggestedPoint,
                                perimeterPoly.getPoints(), false))
                        .position(suggestedPoint)
                        .title(p.getZoneId() + "").snippet(makeSnippet(p)));
                // set color and attrs properly
                colorPoint(marker, p);
                markers.add(marker);
                polygons.add(polygon);
            }
        }
        // set date and save to database because it is the first time open this activity with
        // this "analysis"
        if (newAnalysis) {
            field.setDate("Not yet analyzed");
            saveToFirestore();
        }
    }

    /**
     * Color the point on map.
     * If new analysis is required, all points will be red,
     * otherwise it depends on state of point:
     * Not to analyze = red;
     * To analyze = yellow;
     * Analayzed = green.
     * @param marker representing point.
     * @param p point to color.
     */
    private void colorPoint(Marker marker, Point p) {
        // new analysis requests, all point will be red and initialized
        if (newAnalysis) {
            marker.setIcon(bitmapDescriptorFromVector(R.drawable.red_round_shape));
            p.setEc(0); p.setSar(0); p.setPh(0); p.setCec(0);
            marker.setSnippet(makeSnippet(p));
            p.setAnalyze(false);
            // default point color
        } else {
            // check if area has been analyzed so color properly the point
            if (p.getEc() != 0) {
                // not yet set the color of p
                if (p.getEcColors().isEmpty()) {
                    setPointColor(p);
                }
                // build heat map for the values of attributes
                buildHeatMap(p);
                // p is analyzed => green
                marker.setIcon(bitmapDescriptorFromVector(R.drawable.green_round_shape));
                // point must be analyzed
            } else if (p.isAnalyze()) {
                // add to a dedicated list
                toAnalyze.add(p);
                // p is to analyze => yellow
                marker.setIcon(bitmapDescriptorFromVector(R.drawable.yellow_round_shape));
                // p isn't to analyze => red
            } else {
                marker.setIcon(bitmapDescriptorFromVector(R.drawable.red_round_shape));
            }
        }
    }

    /**
     * Removes all items from map.
     */
    private void cleanMap() {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
        EcTileOverlays.clear();
        points.clear();
        markers.clear();
        polygons.clear();
        googleMap.clear();
    }

    /**
     * Convert vector into bitmap.
     * Convert a file defined into .xml in a figure that can be used as Marker.
     * @param fig id of drawable figure.
     * @return a BitmapDescriptor from a given Bitmap image.
     */
    private BitmapDescriptor bitmapDescriptorFromVector(int fig) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(), fig);
        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        return null;
    }

    /**
     * Convert a string into a list of LatLng.
     * The string has the format of double.
     * It is saved on Firestore in this way because there is a bug.
     * @param string to convert in a list of LatLng
     * @return the list containing coordinates.
     */
    private ArrayList<LatLng> fromStringToLatLng(String string) {
        StringTokenizer tokenizer = new StringTokenizer(string);
        ArrayList<LatLng> coordinates = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            coordinates.add(new LatLng(Double.parseDouble(tokenizer.nextToken()),
                    Double.parseDouble(tokenizer.nextToken())));
        }
        return coordinates;
    }

    /**
     * Convert a Polygon into a String that will be saved to firestore.
     * @param polygon whose points will be saved as string.
     * @return the string containing coordinates.
     */
    private String fromPolygonToString(Polygon polygon) {
        List<LatLng> latLngs = polygon.getPoints();
        StringBuilder s = new StringBuilder();
        for (LatLng latLng : latLngs) {
            s.append(latLng.latitude).append(" ").append(latLng.longitude).append(" ");
        }
        return s.toString();
    }

    /**
     * Enables GPS if the fine location permission has been granted and take user to his position.
     */
    private void enableMyLocation() {
        // permissions of using gps granted
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // GPS is off, open settings
            if (!Objects.requireNonNull(manager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivityForResult(
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 4);
                // gps is on
            } else {
                showMeOnMap();
            }
            // permission not granted
        } else {
            //  ask for the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    /**
     * Take user to his GPS position on map.
     */
    private void showMeOnMap() {
        if (ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // permission to show position
        googleMap.setMyLocationEnabled(true);
        // retrieving my position
        FusedLocationProviderClient fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            position = new LatLng(location.getLatitude(), location.getLongitude());
                            // geocode store in array current position
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                final List<Address> addresses = geocoder
                                        .getFromLocation(location.getLatitude(),
                                                location.getLongitude(),
                                                1);
                                // start to build the new field
                                field.setAddress(addresses.get(0).getAddressLine(0));
                                fieldList.getFields().add(field);
                                fieldList.setName(field.getName());
                                fieldList.setAddress(field.getAddress());
                                // loadPolygon();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // move camera over address
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    position, 18));
                            /*
                            googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(position, 18f));
                             */
                            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            // error getting position, come back to MainActivity
                        } else {
                            showToast("Error getting position, try again");
                            finish();
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager manager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                // if gps is off open settings
                if (!Objects.requireNonNull(manager)
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
                // show gps user location
                showMeOnMap();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // come back from InsertDataActivity
        if (requestCode == 3 && resultCode == RESULT_OK) {
            if (data != null) {
                // get result
                ArrayList<String> extras = data.getStringArrayListExtra("ID");
                // update field with new analysis
                field = new Gson().fromJson(data
                        .getStringExtra("FIELD"), Field.class);
                // update date of analysis
                field.setDate(dateOfAnalysis);
                if (extras != null) {
                    // extract id of point from intent
                    int id = Integer.parseInt(new StringTokenizer(extras.get(0)).nextToken());
                    // write in the marker all results of analysis
                    Marker marker = markers.get(id);
                    marker.setSnippet(extras.get(1));
                    marker.setIcon(bitmapDescriptorFromVector(R.drawable.green_round_shape));
                    marker.showInfoWindow();
                    // remove the null Date
                    for (Point p : field.getPoints()) {
                        if (p.getZoneId() == id) {
                            setPointColor(p);
                            buildHeatMap(p);
                            break;
                        }
                    }
                    // analyze all points
                    analyzePoints();
                }
            }
            // error loading position
        } else if (requestCode == 4 && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    /**
     * Set the correct color of a point.
     * Point will be colored according to result of analysis.
     * @param p point to color.
     */
    private void setPointColor(Point p) {
        ArrayList<Integer> colorsEc = new ArrayList<>();
        ArrayList<Integer> colorsSar = new ArrayList<>();
        ArrayList<Integer> colorsCec = new ArrayList<>();
        ArrayList<Integer> colorsPh = new ArrayList<>();
        if (p.getEc() <= 2) {
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A100));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A200));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A400));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A700));
        } else if (p.getEc() <= 4) {
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A100));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A200));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A400));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A700));
        } else if (p.getEc() <= 8) {
            colorsEc = p.getEcColors();
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A100));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A200));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A400));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A700));
        }
        else if (p.getEc() <= 16) {
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA100));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA200));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA400));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA700));
        } else {
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA100));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA200));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA400));
            colorsEc.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA700));
        }
        p.setEcColors(colorsEc);
        if (p.getSar() <= 10) {
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A100));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A200));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A400));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A700));
        } else if (p.getSar() <= 18) {
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.yellow_A100));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.yellow_A200));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.yellow_A400));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.yellow_A700));
        } else if (p.getSar() <= 26) {
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA100));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA200));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA400));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA700));
        } else {
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA100));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA200));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA400));
            colorsSar.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA700));
        }
        p.setSarColors(colorsSar);

        if (p.getCec() >= 50 && p.getCec() <= 100) {
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A100));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A200));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A400));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A700));
        } else if (p.getCec() >= 25 && p.getCec() <= 50) {
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A100));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A200));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A400));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.lime_A700));
        } else if (p.getCec() >= 15 && p.getCec() <= 25) {
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A100));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A200));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A400));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.amber_A700));
        }
        else if (p.getCec() >= 10 && p.getCec() <= 15){
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA100));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA200));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA400));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA700));
        } else {
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA100));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA200));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA400));
            colorsCec.add (ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA700));
        }
        p.setCecColors(colorsCec);

        if (p.getPh() <= 5.5) {
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA100));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA200));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA400));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.orangeA700));
        } else if (p.getPh() >= 5.8 && p.getPh() <= 6.5) {
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A100));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A200));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A400));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.light_green_A700));
        } else {
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA100));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA200));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA400));
            colorsPh.add(ContextCompat.getColor(getApplicationContext(),
                    R.color.deep_orangeA700));
        }
        p.setPhColors(colorsPh);
    }

    /**
     * Build heat map of a point.
     * Once set the color of a point, the heat map can be displayed on map.
     * Four different heat map one for every attributes.
     * @param p point to build heat map.
     */
    public void buildHeatMap(Point p) {
        // starting point for each color, given as a percentage of the maximum intensity
        float[] startPoints = {
                0.1f, 0.3f, 0.6f, 1f
        };
        // get ec colors of p
        List<Integer> colors = p.getEcColors();
        // convert to array
        int[] ecColors = {
                colors.get(0), colors.get(1),colors.get(2), colors.get(3)};
        // Create the tile Provider for building heat map
        final HeatmapTileProvider ecProvider = new HeatmapTileProvider.Builder()
                // coordinates of p
                .data(fromStringToLatLng(p.getSuggestedPoint()))
                // color to use
                .gradient(new Gradient(ecColors, startPoints))
                .build();
        ecProvider.setRadius(100);

        colors = p.getCecColors();
        int[] cecColors = {
                colors.get(0), colors.get(1),colors.get(2), colors.get(3)};

        // Create the tile Provider for cec
        final HeatmapTileProvider cecProvider = new HeatmapTileProvider.Builder()
                .data(fromStringToLatLng(p.getSuggestedPoint()))
                .gradient(new Gradient(cecColors, startPoints))
                .build();
        cecProvider.setRadius(100);

        colors = p.getPhColors();
        int[] phColors = {
                colors.get(0), colors.get(1),colors.get(2), colors.get(3)};
        // Create the tile Provider for ph
        final HeatmapTileProvider phProvider = new HeatmapTileProvider.Builder()
                .data(fromStringToLatLng(p.getSuggestedPoint()))
                .gradient(new Gradient(phColors, startPoints))
                .build();
        phProvider.setRadius(100);

        colors = p.getSarColors();
        int[] sarColors = {
                colors.get(0), colors.get(1),colors.get(2), colors.get(3)};
        // Create the tile Provider for sar
        final HeatmapTileProvider sarProvider = new HeatmapTileProvider.Builder()
                .data(fromStringToLatLng(p.getSuggestedPoint()))
                .gradient(new Gradient(sarColors, startPoints))
                .build();
        sarProvider.setRadius(100);

        // display heat map
        EcTileOverlays.add(googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(ecProvider)
                // heat map is visible if spinner is set to Heat Map
                .visible(toShow.equals("ec heat map"))));

        CecTileOverlays.add(googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(cecProvider)
                // heat map is visible if spinner is set to Heat Map
                .visible(toShow.equals("cec heat map"))));

        PhTileOverlays.add(googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(phProvider)
                // heat map is visible if spinner is set to Heat Map
                .visible(toShow.equals("ph heat map"))));

        SarTileOverlays.add(googleMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(sarProvider)
                // heat map is visible if spinner is set to Heat Map
                .visible(toShow.equals("sar heat map"))));
    }


    /**
     * Clicking on window on top of Marker opens a Dialog that indicate the points to analyze.
     * @param marker (point) clicked.
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        // point to analyze
        ArrayList<String> pTa = new ArrayList<>();
        for (int i = 0; i < toAnalyze.size(); i++) {
            // id of zone that will be analyzed
            pTa.add("zone " +  toAnalyze.get(i).getZoneId());
        }
        // create Dialog with arguments
        Bundle bundle = new Bundle();
        bundle.putInt("WHAT", 4);
        bundle.putStringArrayList("POINT_TO_ANALYZE", pTa);
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setArguments(bundle);
        myDialogFragment.show(getSupportFragmentManager(), "");
    }

    /**
     * Open the InsertDataActivity where user inserts the result of analysis of chosen points
     */
    private void analyzePoints() {
        // save to firestore if all points have been analyzed
        if (toAnalyze.isEmpty()) {
            saveToFirestore();
            googleMap.setOnMarkerClickListener(null);
            googleMap.setOnInfoWindowClickListener(null);
            markButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else {
            // insert analyzed value of point
            Intent intent = new Intent(this, InsertDataActivity.class);
            intent.putExtra("ID_ZONE", toAnalyze.get(0).getZoneId());
            intent.putExtra("FIELD", new Gson().toJson(field));
            toAnalyze.remove(0);
            startActivityForResult(intent, 3);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     * custom layout of Marker.
     * Show the attributes of point.
     * @param marker representing a point with attrs.
     * @return the custom layout do show.
     */
    @Override
    public View getInfoContents(Marker marker) {
        Context context = getApplicationContext();
        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(context);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());
        TextView snippet = new TextView(context);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());
        info.addView(title);
        info.addView(snippet);
        return info;
    }

    // element selected in the spinner indicates what to show
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // show only points
        switch (position) {
            case 0 :
                toShow = "Points";
                hideHeatMap(EcTileOverlays);
                hideHeatMap(CecTileOverlays);
                hideHeatMap(PhTileOverlays);
                hideHeatMap(SarTileOverlays);
                break;
            case 1:
                // show points and ec heat map
                toShow = "ec heat map";
                for (TileOverlay tileOverlay : EcTileOverlays) {
                    tileOverlay.setVisible(true);
                }
                hideHeatMap(CecTileOverlays);
                hideHeatMap(PhTileOverlays);
                hideHeatMap(SarTileOverlays);
                break;
            case 2:
                // show points and cec heat map
                toShow = "cec heat map";
                for (TileOverlay tileOverlay : CecTileOverlays) {
                    tileOverlay.setVisible(true);
                }
                hideHeatMap(EcTileOverlays);
                hideHeatMap(PhTileOverlays);
                hideHeatMap(SarTileOverlays);
                break;
            case 3:
                // show points and cec heat map
                toShow = "ph heat map";
                for (TileOverlay tileOverlay : PhTileOverlays) {
                    tileOverlay.setVisible(true);
                }
                hideHeatMap(EcTileOverlays);
                hideHeatMap(CecTileOverlays);
                hideHeatMap(SarTileOverlays);
                break;
            case 4:
                // show points and cec heat map
                toShow = "sar heat map";
                for (TileOverlay tileOverlay : SarTileOverlays) {
                    tileOverlay.setVisible(true);
                }
                hideHeatMap(EcTileOverlays);
                hideHeatMap(CecTileOverlays);
                hideHeatMap(PhTileOverlays);
                break;
        }
    }

    /**
     * Hide heat map.
     * @param tileOverlays to hide.
     */
    private void hideHeatMap(List<TileOverlay> tileOverlays) {
        for (TileOverlay tileOverlay : tileOverlays) {
            tileOverlay.setVisible(false);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Save the edited field to Cloud Firestore.
     */
    private void saveToFirestore() {
        // update name and address of object containing ALL analysis of this field
        fieldList.setName(field.getName());
        fieldList.setAddress(field.getAddress());
        // if field is analyzed, remove the previous one
        for (int i = 0; i < fieldList.getFields().size(); i++) {
            if (fieldList.getFields().get(i).getDate().equals("Not yet analyzed")) {
                fieldList.getFields().remove(i);
                break;
            }
        }
        // add the updated field
        fieldList.getFields().add(field);
        // save to firestore
        FirebaseFirestore.getInstance().collection(username)
                .document(field.getName())
                .set(fieldList);
    }

    // bring back result to caller activity
    @Override
    public void onBackPressed() {
        // map is added only if it is drawn and if is editable
        Log.d("TAG", "back pressed");
        if (isEditable) {
            Log.d("TAG", "edited");
            if (isMapDrawn) {
                Log.d("TAG", "drawn");
                // bring back the new field
                Intent intent = new Intent();
                intent.putExtra("FIELD", gson.toJson(field));
                sharedPref.edit().putString("FIELD_WITH_DATE", new Gson()
                        .toJson(fieldList)).apply();
                setResult(RESULT_OK, intent);
            } else {
                // remove from main recycler view
                setResult(RESULT_FIRST_USER, new Intent()
                        .putExtra("FIELD", gson.toJson(field)));
            }
        }
        super.onBackPressed();
    }

    /**
     * Change the state of a point in  to analyze/!analyze.
     * When click a Marker (point) change its state
     * and color it to yellow if it is to analyze, red otherwise.
     * @param marker clicked.
     * @return false: event consumed.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // permits to show qr code
        qrCodeButton.setVisibility(View.VISIBLE);
        // find point to analyze
        int id = Integer.parseInt(marker.getTitle());
        for (Point p : field.getPoints()) {
            if (p.getZoneId() == id) {
                qrCodePoint = p;
                // if field isn't yet analyzed
                if (isEditable) {
                    // for each tap change state
                    p.setAnalyze(!p.isAnalyze());
                    // if point is marked as !to be analyzed
                    if (!p.isAnalyze()) {
                        toAnalyze.remove(p);
                        marker.setIcon(bitmapDescriptorFromVector(R.drawable.red_round_shape));
                    } else {
                        // if point is marked as to be analyzed color
                        toAnalyze.add(p);
                        marker.setIcon(bitmapDescriptorFromVector(R.drawable.yellow_round_shape));
                    }
                    // save in db the state of point
                    saveToFirestore();
                }
                break;
            }
        }
        return false;
    }

    /**
     * Callback that set date of analysis.
     * It is chosen in proper Dialog.
     * @param string representing the date of analysis
     */
    @Override
    public void dateSelected(String string) {
        dateOfAnalysis = string;
        analyzePoints();
    }
}