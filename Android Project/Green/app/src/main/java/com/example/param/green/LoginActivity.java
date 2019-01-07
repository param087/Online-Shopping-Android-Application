package com.example.param.green;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.param.green.staticData.Googleapiuser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    static private String TAG = "EmailAuthentication";
    static private String TAG1 = "GoogleAPI";

    static private String saveInstacees = "saveInstances";

    private static final int RC_SING_IN =9001;

    private TextView mEmailTextView;
    private TextView mPassTextView;
    private EditText mEmailEditText;
    private EditText mPassEditText;

    private int method;

    private FirebaseAuth mFirebaseAuth;

    private GoogleApiClient mGoogleApiClient;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceorder;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(savedInstanceState != null){
            method = savedInstanceState.getInt(saveInstacees);
        } else {
            method=0;
        }

        mEmailTextView = (TextView) findViewById(R.id.tv_enter_email);
        mPassTextView = (TextView) findViewById(R.id.tv_enter_pass);
        mEmailEditText = (EditText) findViewById(R.id.et_enter_email);
        mPassEditText = (EditText) findViewById(R.id.et_enter_pass);

        findViewById(R.id.bt_sign_in).setOnClickListener(this);
        findViewById(R.id.bt_sign_up).setOnClickListener(this);
        findViewById(R.id.gbt_sign_in).setOnClickListener(this);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        Googleapiuser.setmGoogleApiClient(mGoogleApiClient);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mCurrentUser = mFirebaseAuth.getCurrentUser();
        if(mCurrentUser != null){

            Context context = LoginActivity.this;
            Class  destinationActivity = MainActivity.class;
            Intent intentdestinationActivity = new Intent(context, destinationActivity);
            intentdestinationActivity.putExtra(Intent.EXTRA_TEXT,method);
            startActivity(intentdestinationActivity);
        }

    }

    private void createAccount(String email,String password){
        Log.d(TAG," createAccount "+email);

        if(! validForm()){
            return;
        }

        mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmailID: scusses");
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification();
                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Customer")
                                    .child(firebaseUser.getUid()).child("DetailPresent");
                            mDatabaseReferenceorder = mFirebaseDatabase.getReference().child("Company")
                                    .child("Orders").child(firebaseUser.getUid());
                            Boolean bool = false;
                            mDatabaseReference.setValue(bool);
                            int order = -1;
                            mDatabaseReferenceorder.setValue(order);

                            dialog.dismiss();

                            Context context = LoginActivity.this;
                            Class destinationActivity = MainActivity.class;
                            method=0;
                            Toast.makeText(LoginActivity.this,"SignUp successful",Toast.LENGTH_SHORT).show();
                            Intent intentdestinationActivity = new Intent(context, destinationActivity);
                            intentdestinationActivity.putExtra(Intent.EXTRA_TEXT,method);
                            startActivity(intentdestinationActivity);


                        } else {

                            dialog.dismiss();

                            Log.w(TAG," createUserWithEmailId : fail",task.getException());
                            Toast.makeText(LoginActivity.this,"Invalid EmailId or Password",
                                    Toast.LENGTH_SHORT).show();


                        }

                    }
                });
    }


    private void signIn(String email, String password){

        Log.d(TAG," Sign In "+email);

        if(! validForm()){
            return;
        }

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            Log.d(TAG, "SingIn wiht Email: Sucessful");
                            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

                            dialog.dismiss();

                            Context context = LoginActivity.this;
                            Class destinationActivity = MainActivity.class;
                            Intent intentdestinationActivity = new Intent(context, destinationActivity);
                            method=0;
                            Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                            intentdestinationActivity.putExtra(Intent.EXTRA_TEXT,method);
                            startActivity(intentdestinationActivity);
                        } else {

                            dialog.dismiss();

                            Log.w(TAG,"Sing In with Email: fail",task.getException());
                            Toast.makeText(LoginActivity.this,"Invalid EmailId or Password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean validForm(){
        boolean valid = true;

        String email = mEmailEditText.getText().toString();
        if(TextUtils.isEmpty(email)){
            mEmailTextView.setError("Required");
            valid = false;
        }else {
            mEmailTextView.setError(null);
        }

        String password = mPassEditText.getText().toString();
        if(TextUtils.isEmpty(password)){
            mPassEditText.setError("Required");
            valid=false;
        } else {
            mPassEditText.setError(null);
        }


        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SING_IN){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

           // Toast.makeText(LoginActivity.this,"Google: "+result.getStatus(),Toast.LENGTH_LONG).show();
            if(result.isSuccess()){
                GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
                firebaseAuthWithGoogle(googleSignInAccount);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d(TAG1," FirebaseAuthWithGoogle");

        dialog.show();

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG1," SignInWithCredential : Successful");
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Customer").
                                    child(firebaseUser.getUid()).child("DetailPresent");

                            mDatabaseReferenceorder = mFirebaseDatabase.getReference().child("Company")
                                    .child("Orders").child(firebaseUser.getUid());
                            final int [] ordercount = {-1};

                            try {

                                mDatabaseReferenceorder.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        int count = -1;
                                        String key = dataSnapshot.getKey();
                                        try {
                                            count = dataSnapshot.getValue(Integer.class);

                                        }catch (NullPointerException e){

                                            mDatabaseReferenceorder.setValue(-1);

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }catch (DatabaseException e){
                                    mDatabaseReferenceorder.setValue(-1);

                            }



                            final boolean[] submitted = {false};


                            try {
                                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean bool = false;
                                        String key = dataSnapshot.getKey();
                                        try {
                                            bool = dataSnapshot.getValue(boolean.class);
                                        }catch (NullPointerException e){
                                            if (submitted[0] != true) {
                                                mDatabaseReference.setValue(false);
                                            }



                                            dialog.dismiss();
                                            Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                                            Context context = LoginActivity.this;
                                            Class destinationActivity = MainActivity.class;
                                            Intent intentDestinationActivity = new Intent(context, destinationActivity);
                                            method = 1;
                                            intentDestinationActivity.putExtra(Intent.EXTRA_TEXT, method);
                                            startActivity(intentDestinationActivity);

                                        }
                                        Iterable<DataSnapshot> child = dataSnapshot.getChildren();


                                        for (DataSnapshot single : child) {
                                            bool = single.getValue(Boolean.class);

                                        }
                                        submitted[0] = bool;
                                        if (submitted[0] != true) {
                                            mDatabaseReference.setValue(false);
                                        }
                                        Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                                        Context context = LoginActivity.this;
                                        Class destinationActivity = MainActivity.class;
                                        Intent intentDestinationActivity = new Intent(context, destinationActivity);
                                        method = 1;
                                        intentDestinationActivity.putExtra(Intent.EXTRA_TEXT, method);
                                        startActivity(intentDestinationActivity);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }catch (DatabaseException e) {
                                if (submitted[0] != true) {
                                    mDatabaseReference.setValue(false);
                                }

                                dialog.dismiss();

                                Context context = LoginActivity.this;
                                Class destinationActivity = MainActivity.class;
                                Intent intentDestinationActivity = new Intent(context, destinationActivity);
                                method = 1;
                                intentDestinationActivity.putExtra(Intent.EXTRA_TEXT, method);
                                startActivity(intentDestinationActivity);
                            }

                        } else {

                            dialog.dismiss();

                            Log.w(TAG1, "SingInWithCredential : fail");
                            Toast.makeText(LoginActivity.this, "Authentication fail",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void gSignIn(){

        Intent singalInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(singalInIntent, RC_SING_IN);

    }


    @Override
    public void onClick(View v) {

        int i = v.getId();

        if(i == R.id.bt_sign_in){

            dialog.show();

            signIn(mEmailEditText.getText().toString(),mPassEditText.getText().toString());
        }
        else if(i == R.id.bt_sign_up){

            dialog.show();

            createAccount(mEmailEditText.getText().toString(),mPassEditText.getText().toString());

        }
        else if(i == R.id.gbt_sign_in){

            gSignIn();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG1," onConnectionFail"+connectionResult);
        Toast.makeText(LoginActivity.this,"Google play services fail",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }
}
