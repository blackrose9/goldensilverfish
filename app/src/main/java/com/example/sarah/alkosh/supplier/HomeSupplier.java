package com.example.sarah.alkosh.supplier;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarah.alkosh.About;
import com.example.sarah.alkosh.LoginActivity;
import com.example.sarah.alkosh.R;
import com.example.sarah.alkosh.model.Item;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

/**
 * Created by Sarah on 11/30/2017.
 */

public class HomeSupplier extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "HomeSupplier";

    FloatingActionButton fab;

    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;

    private final int PICK_IMAGE_REQUEST = 11;
    private Uri saveUri;
    private List<String> categories = new ArrayList<>();


    Button btnUpload, btnSelect;
    ProgressDialog mProgress;

    Item newItem;
    MaterialEditText edtName, edtDiscount, edtPrice, edtDescription;
    MaterialSpinner spinnerCategory;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_supplier);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);

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
                        Toast.makeText(HomeSupplier.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(HomeSupplier.this, LoginActivity.class));
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


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeSupplier.this);
        alertDialog.setTitle("Add New Item");


        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_item_layout, null);

        edtName = (MaterialEditText) add_menu_layout.findViewById(R.id.edtName);
        edtDiscount = (MaterialEditText) add_menu_layout.findViewById(R.id.edtDiscount);
        edtPrice = (MaterialEditText) add_menu_layout.findViewById(R.id.edtPrice);
        edtDescription = (MaterialEditText) add_menu_layout.findViewById(R.id.edtDescription);

        spinnerCategory = (MaterialSpinner) add_menu_layout.findViewById(R.id.spinnerCategory);
        spinnerCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                category = item.toString();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String cat = postSnapshot.child("categoryName").getValue().toString();
                    if (!categories.contains(cat)) {
                        categories.add(cat);
                    }
                }
                spinnerCategory.setItems(categories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSelect = (Button) add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = (Button) add_menu_layout.findViewById(R.id.btnUpload);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

//        Set Button
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mProgress.setMessage("Adding Item");
                mProgress.setCancelable(false);
                mProgress.show();
                //Here Create New Category
                if (newItem != null) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");
                    ref.push().setValue(newItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mProgress.dismiss();
                            Toast.makeText(HomeSupplier.this, "Item Added!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeSupplier.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading.....");
            dialog.setCancelable(false);
            dialog.show();
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = FirebaseStorage.getInstance().getReference().child("ItemImages/" + imageName);
            imageFolder.putFile(saveUri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(HomeSupplier.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newItem = new Item();
                                    newItem.setItemName(edtName.getText().toString());
                                    newItem.setItemDescription(edtDescription.getText().toString());
                                    newItem.setItemDiscount(edtDiscount.getText().toString());
                                    newItem.setItemPhotoUrl(uri.toString());
                                    newItem.setItemCategory(category);
                                    newItem.setItemPrice(edtPrice.getText().toString());
                                    newItem.setItemSupplier(user.getDisplayName());


                                }
                            });
                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(HomeSupplier.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("Selected!");
        }
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
        getMenuInflater().inflate(R.menu.home_supplier, menu);
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
                fragment = new DashboardFragment();
                break;
            case R.id.nav_items:
                fragment = new SupplierItemsFragment();
                break;
            case R.id.nav_orders:
                fragment = new OrdersSupplierFragment();
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
                startActivity(new Intent(HomeSupplier.this, LoginActivity.class));
                finish();


            } else if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
                Log.d(TAG, "logOut: Google");
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(HomeSupplier.this, LoginActivity.class));
                finish();

            } else {
                FirebaseAuth.getInstance().signOut();
                Paper.book().destroy();
                startActivity(new Intent(HomeSupplier.this, LoginActivity.class));
                finish();

            }
        }


    }
}
