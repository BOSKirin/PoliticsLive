package com.example.bradleycooper.politicslive;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RepublicansList.OnFragmentInteractionListener,
        DemocratsList.OnFragmentInteractionListener,
        HomePage.OnFragmentInteractionListener,
        Settings.OnFragmentInteractionListener,
        UserProfile.OnFragmentInteractionListener {

    private CharSequence mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Ballot.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mTitle = getTitle();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        fragment = new HomePage();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = preferences.getString("Username", "");
        if(userName.equalsIgnoreCase("") || userName==null)
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        /*
        SharedPreferences preferences = getSharedPreferences("com.example.bradleycooper.politicslive", 0);
        String value = preferences.getString("userNameKey", null);
        if (value == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // handle the value
        }*/

        /* Check to see if database is storing any objects.  If not, populate with candidate information */
        CandidateDataSource ds = new CandidateDataSource(MainActivity.this);
        ds.open();
        if(ds.getLastCandidateId() < 1) {
            initializeDB();
        }


    }
    private static boolean doesDataBaseExist(Context context, String dbName){
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
    private Candidate currentCandidate;
    private void initializeDB(){
        Bitmap bitmapSquareBernie = BitmapFactory.decodeResource(getResources(),R.drawable.bernie_square);
        byte[] byteArraySquareBernie = convertBitmapToByteArray(bitmapSquareBernie);
        Bitmap bitmapSquareHilary = BitmapFactory.decodeResource(getResources(), R.drawable.hilary_square);
        byte[] byteArraySquareHilary = convertBitmapToByteArray(bitmapSquareHilary);
        Bitmap bitmapSquareTrump = BitmapFactory.decodeResource(getResources(), R.drawable.trump_square);
        byte[] byteArraySquareTrump = convertBitmapToByteArray(bitmapSquareTrump);
        Bitmap bitmapSquareCruz = BitmapFactory.decodeResource(getResources(), R.drawable.cruz_square);
        byte[] byteArraySquareCruz = convertBitmapToByteArray(bitmapSquareCruz);
        Bitmap bitmapSquareKasich = BitmapFactory.decodeResource(getResources(), R.drawable.kasich_square);
        byte[] byteArraySquareKasich = convertBitmapToByteArray(bitmapSquareKasich);

        Bitmap bitmapWideBernie = BitmapFactory.decodeResource(getResources(),R.drawable.bernie);
        byte[] byteArrayWideBernie = convertBitmapToByteArray(bitmapWideBernie);
        Bitmap bitmapWideHilary = BitmapFactory.decodeResource(getResources(), R.drawable.hilary);
        byte[] byteArrayWideHilary = convertBitmapToByteArray(bitmapWideHilary);
        Bitmap bitmapWideTrump = BitmapFactory.decodeResource(getResources(), R.drawable.trump);
        byte[] byteArrayWideTrump = convertBitmapToByteArray(bitmapWideTrump);
        Bitmap bitmapWideCruz = BitmapFactory.decodeResource(getResources(), R.drawable.cruz);
        byte[] byteArrayWideCruz = convertBitmapToByteArray(bitmapWideCruz);
        Bitmap bitmapWideKasich = BitmapFactory.decodeResource(getResources(), R.drawable.kasich);
        byte[] byteArrayWideKasich = convertBitmapToByteArray(bitmapWideKasich);


        generateCandidates("Bernie Sanders", "Self-proclaimed democratic socialist from Vermont.", byteArraySquareBernie, byteArrayWideBernie, "DNC");
        generateCandidates("Hilary Clinton", "Former Secretary of State, many years in politics.",byteArraySquareHilary, byteArrayWideHilary, "DNC");
        generateCandidates("Donald Trump", "Successful Business man from New York City.", byteArraySquareTrump, byteArrayWideTrump,"GOP");
        generateCandidates("Ted Cruz", "Reported Zodiac killer, wants to turn abolish secularism.",byteArraySquareCruz, byteArrayWideCruz,"GOP");
        generateCandidates("John Kasich", "What am I still doing in this race anyways...?", byteArraySquareKasich, byteArrayWideKasich,"GOP");
    }
    public void generateCandidates(String candidateName, String candidateDescription, byte[] byteArraySquare, byte[] byteArrayWide ,String party){
        currentCandidate = new Candidate();
        currentCandidate.setCandidateName(candidateName);
        currentCandidate.setCandidateDescription(candidateDescription);
        currentCandidate.setSquarePicture(byteArraySquare);
        currentCandidate.setWidePicture(byteArrayWide);
        currentCandidate.setParty(party);
        CandidateDataSource ds = new CandidateDataSource(MainActivity.this);
        ds.open();

        boolean wasSuccessful = false;
        if(currentCandidate.getCandidateID() == -1) {
            wasSuccessful = ds.insertCandidate(currentCandidate);
            int newId = ds.getLastCandidateId();
            currentCandidate.setCandidateID(newId);
        }
        else{
            wasSuccessful = ds.updateCandidate(currentCandidate);
        }
        ds.close();
        if (wasSuccessful) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Success");
            alertDialog.setMessage("Candidate " + currentCandidate.getCandidateName() + " was added as a member of the " + currentCandidate.getParty() + " party. Id is " + currentCandidate.getCandidateID());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",

                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();
        fragment = new HomePage();
        if (id == R.id.nav_home) {
            fragment = new HomePage();
            mTitle = "Politics Live!";
        } else if (id == R.id.nav_GOP) {
            fragment = new RepublicansList();
            mTitle = "Republican Candidates";
        } else if (id == R.id.nav_DNC) {
            fragment = new DemocratsList();
            mTitle = "Democratic Candidates";
        } else if (id == R.id.nav_settings) {
            fragment = new UserProfile();
            mTitle = "User Profiles";
        }
        else if (id == R.id.nav_user) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        restoreActionBar();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void restoreActionBar () {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
