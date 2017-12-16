package com.example.sarah.alkosh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarah.alkosh.EditImage.EditActivity;
import com.example.sarah.alkosh.common.Common;
import com.example.sarah.alkosh.supplier.HomeSupplier;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;

/**
 * Created by Sarah on 11/30/2017.
 */

public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";

    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    GoogleApiClient mGoogleApiClient;

    CallbackManager mCallbackManager;

    LoginButton btnFB;
    SignInButton btnGG;
    Button btnLogin;
    TextView txtRegister;

    EditText txtEmail, txtPassword;
    private static final int RC_SIGN_IN = 2;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        Paper.init(this);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.sarah.alkosh",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }

        initViews();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    String id = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("userRole").equals("Supplier")) {
                                startActivity(new Intent(LoginActivity.this, HomeSupplier.class));
                            } else if (dataSnapshot.child("userRole").equals("Customer")) {
                                startActivity(new Intent(LoginActivity.this, Home.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        };


        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        Check Remember
        String user = Paper.book().read("userEmail");
        String pwd = Paper.book().read("userPass");
        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty()) {
                Log.d(TAG, "onCreate: Yes");
                mProgress.setMessage("One moment please....");
                mProgress.setCancelable(false);
                mProgress.show();

                mAuth.signInWithEmailAndPassword(user, pwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                if (!task.isSuccessful()) {

                                    String error = task.getException().getLocalizedMessage();
                                    Toast.makeText(LoginActivity.this, error,
                                            Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                } else {
                                    Log.d(TAG, "onComplete: Sign Up Success");

                                    normalSignIn();
                                }

                                // ...
                            }
                        });
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String email = txtEmail.getText().toString().trim();
                final String password = txtPassword.getText().toString().trim();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {

                    mProgress.setMessage("Signing you in");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {

                                        String error = task.getException().getLocalizedMessage();
                                        Toast.makeText(LoginActivity.this, error,
                                                Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                    } else {
                                        Log.d(TAG, "onComplete: Sign Up Success");

                                        Paper.book().write("userEmail",email);
                                        Paper.book().write("userPass",password);

                                        normalSignIn();
                                    }

                                    // ...
                                }
                            });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Please fill out all the fields!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


    private void normalSignIn() {

        Log.d(TAG, "onComplete: Sign In Success!");

        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "ID" + id);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgress.dismiss();
                if (dataSnapshot.child("userRole").getValue().equals("Supplier")) {

                    Log.d(TAG, "onDataChange: Yes");
                    String name=dataSnapshot.child("userName").getValue().toString();
                    String phone=dataSnapshot.child("userPhoneNo").getValue().toString();
                    Common.userName=name;
                    Common.userPhoneNo=phone;

                    startActivity(new Intent(LoginActivity.this, HomeSupplier.class));
                    finish();
                }
                if (dataSnapshot.child("userRole").getValue().equals("Customer")) {

                    String name=dataSnapshot.child("userName").getValue().toString();
                    String phone=dataSnapshot.child("userPhoneNo").getValue().toString();
                    Common.userName=name;
                    Common.userPhoneNo=phone;


                    Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, Home.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void firebase_sign(FirebaseUser user) {
        final String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        String user_id = user.getUid();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        ref.child("userName").setValue(name);
        ref.child("userEmail").setValue(email);
        String userRole = "";


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                startActivity(new Intent(LoginActivity.this, Home.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
//        btnFB=(LoginButton)findViewById(R.id.btnFb);
//        btnGG=(SignInButton) findViewById(R.id.btnGG);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtEmail = (EditText) findViewById(R.id.txt_email);
        txtPassword = (EditText) findViewById(R.id.txt_pass);
        txtRegister = (TextView) findViewById(R.id.txt_signUp);
        mProgress = new ProgressDialog(this);
    }

}
