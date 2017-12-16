package com.example.sarah.alkosh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.sarah.alkosh.common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

/**
 * Created by Sarah on 11/30/2017.
 */

public class RegisterActivity extends AppCompatActivity{
    private static final String TAG = "RegisterActivity";

    Button btnRegister;
    EditText txtEmail,txtPass,txtConfirmPass,txtUserName,txtPhoneNo;
    Switch switchUserRole;

    FirebaseAuth mAuth;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        mAuth=FirebaseAuth.getInstance();

        mProgress=new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Register User
                mProgress.setMessage("Signing up....");
                mProgress.setCancelable(false);
                mProgress.show();
                signUp();


            }
        });
    }
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        View decorView = getWindow().getDecorView();
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//
//    }

    private void signUp() {

        final String email=txtEmail.getText().toString().trim();
        final String password=txtPass.getText().toString().trim();
        final String userName=txtUserName.getText().toString().trim();
        String confirmPass=txtConfirmPass.getText().toString().trim();
        final String phoneNo=txtPhoneNo.getText().toString().trim();


        String userRole="";
        if(switchUserRole.isChecked())
        {
            userRole="Supplier";
        }
        else
        {
            userRole="Customer";
        }


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(confirmPass))
        {
            if(!confirmPass.equals(password))
            {
                Toast.makeText(this, "Please Confirm your Correct Password", Toast.LENGTH_SHORT).show();
            }
            else
            {
            final String finalUserRole = userRole;
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            String error=task.getException().getLocalizedMessage();
                            Toast.makeText(RegisterActivity.this, error,
                                    Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").child(id);
                            ref.child("userEmail").setValue(email);
                            ref.child("userName").setValue(userName);
                            ref.child("userPhoneNo").setValue(phoneNo);
                            ref.child("userRole").setValue(finalUserRole);


                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName).build();


                            Common.userName=userName;
                            Common.userPhoneNo=phoneNo;


                            Paper.book().write("userEmail",email);
                            Paper.book().write("userPass",password);


                            user.updateProfile(profileUpdates);
                            mProgress.dismiss();
                            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }
                        // ...
                    }
                });
            }
        }
        else
        {
            Toast.makeText(this, "Please fill out all the Fields!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initViews() {
        btnRegister=(Button)findViewById(R.id.btnRegister);
        txtEmail=(EditText) findViewById(R.id.txt_email);
        txtUserName=(EditText) findViewById(R.id.txt_userName);
        txtPass=(EditText) findViewById(R.id.txt_pass);
        txtConfirmPass=(EditText) findViewById(R.id.txt_confirm_pass);
        txtPhoneNo=(EditText)findViewById(R.id.txt_phoneNo);
        switchUserRole=(Switch)findViewById(R.id.userRole);
    }
}
