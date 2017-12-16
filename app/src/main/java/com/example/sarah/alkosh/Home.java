package com.example.sarah.alkosh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarah.alkosh.supplier.DashboardFragment;
import com.example.sarah.alkosh.supplier.SettingsFragment;
import com.example.sarah.alkosh.supplier.SupplierItemsFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;

/**
 * Created by Sarah on 11/30/2017.
 */

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "Home";

    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Alkosh");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(Home.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(Home.this, LoginActivity.class));
                    finish();
                }
                // ...
            }
        };
//        Set nav details

        View headerView = navigationView.getHeaderView(0);
        ImageView headerImage = (ImageView) headerView.findViewById(R.id.imageView);
        TextView txtUserName = (TextView) headerView.findViewById(R.id.txtUsername);
        TextView txtEmail = (TextView) headerView.findViewById(R.id.txtEmail);

        setNavDetails(headerImage, txtUserName, txtEmail);

        displaySelectedScreen(R.id.nav_home);
    }

    private void setNavDetails(ImageView headerImage, TextView txtUserName, TextView txtEmail) {
        if (FirebaseAuth.getInstance().getCurrentUser().getProviders() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com")) {

                String name = user.getDisplayName();
                String email = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();

                headerImage.setImageURI(photoUrl);
                txtUserName.setText(name);
                txtEmail.setText(email);


            } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {

                String name = user.getDisplayName();
                String email = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();

                headerImage.setImageURI(photoUrl);
                txtUserName.setText(name);
                txtEmail.setText(email);

            } else {
                String name = user.getDisplayName();
                String email = user.getEmail();

                txtUserName.setText(name);
                txtEmail.setText(email);

            }
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
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank

        return true;
    }
    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_suppliers:
                fragment = new SupplierItemsFragment();
                break;
            case R.id.nav_orders:
                fragment = new OrdersFragment();
                break;
            case R.id.nav_cart:
                fragment = new CartFragment();
                break;
            case R.id.nav_about:
                fragment = new About();
                break;
            case R.id.nav_exit:
                logOut();
                break;

            default:
                fragment = new DashboardFragment();
                break;

        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void logOut() {

        Log.d(TAG, "logOut: Is Logging out");
        if (FirebaseAuth.getInstance().getCurrentUser().getProviders() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("facebook.com")) {

                Log.d(TAG, "logOut: Facebook");
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(Home.this, LoginActivity.class));
                finish();


            } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
                Log.d(TAG, "logOut: Google");
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(Home.this, LoginActivity.class));
                finish();

            } else {
                FirebaseAuth.getInstance().signOut();
                Paper.book().destroy();
                startActivity(new Intent(Home.this, LoginActivity.class));
                finish();

            }
        }


    }
}
