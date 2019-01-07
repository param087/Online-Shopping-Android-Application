package com.example.param.green;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.param.green.staticData.ConnectivityReceiver;
import com.example.param.green.staticData.MyApplication;
import com.example.param.green.staticData.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity
        implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener{

    UserAccount obj1,obj2;
    String userId;

    private AlertDialog.Builder alertDialog;

    private  boolean submitted;

    private EditText mEditTextName;
    private EditText mEditTextAddress;
    private EditText mEditTextPhoneno;
    private EditText mEditTextLandmark;
    private EditText mEditTextPincode;
    private EditText mEditTextCity;

    private TextView mTextViewName;
    private TextView mTextViewName2;
    private TextView mTextViewName21;

    private TextView mTextViewAddress;
    private TextView mTextViewAddress2;
    private TextView mTextViewAddress21;

    private TextView mTexrViewPhoneno;
    private TextView mTextViewPhoneno2;
    private TextView mTextViewPhoneno21;

    private TextView mTextViewLandmark;
    private TextView mTextViewLandmark2;
    private TextView mTextViewLandmark21;

    private TextView mTextViewCity;
    private TextView mTextViewCity2;
    private TextView mTextViewCity21;

    private TextView mTextViewPincode;
    private TextView mTextViewPincode2;
    private TextView mTextViewPincode21;

    private LinearLayout mLinearLayout1;
    private LinearLayout mLinearLayout2;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabaseReferenceToUser;
    private FirebaseDatabase mFirebaseDatabase;


    private DatabaseReference mDatabaseReferenceCheckDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);



        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        alertDialog = new AlertDialog.Builder(AccountActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("We can not detect any internet connection please check your internet connection and try again");

        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                showSnack(isNetworkAvailable());
            }
        });


        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                onBackPressed();
               // Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        showSnack(isNetworkAvailable());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userId = mFirebaseUser.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceToUser = mFirebaseDatabase.getReference().child("Customer").child(userId).child("accountDetail");

        mDatabaseReferenceCheckDetail = mFirebaseDatabase.getReference().child("Customer").child(userId).child("DetailPresent");

        mLinearLayout1 = (LinearLayout)findViewById(R.id.ll_editable);
        mLinearLayout1.setVisibility(View.VISIBLE);
        mLinearLayout2 = (LinearLayout)findViewById(R.id.ll_noneditable);
        mLinearLayout2.setVisibility(View.INVISIBLE);

        mEditTextName = (EditText) findViewById(R.id.et_enter_name);
        mEditTextAddress = (EditText) findViewById(R.id.et_enter_address);
        mEditTextPhoneno = (EditText) findViewById(R.id.et_enter_phoneno);
        mEditTextLandmark = (EditText) findViewById(R.id.et_enter_landmark);
        mEditTextCity = (EditText) findViewById(R.id.et_enter_city);
        mEditTextPincode = (EditText) findViewById(R.id.et_enter_pincode);

        mTextViewName = (TextView)findViewById(R.id.tv_enter_name);
        mTextViewName2 = (TextView)findViewById(R.id.tv_enter_name2);
        mTextViewName21 = (TextView)findViewById(R.id.tv_enter_name21);

        mTextViewAddress = (TextView)findViewById(R.id.tv_enter_address);
        mTextViewAddress2 = (TextView) findViewById(R.id.tv_enter_address2);
        mTextViewAddress21 = (TextView) findViewById(R.id.tv_enter_address21);

        mTexrViewPhoneno = (TextView)findViewById(R.id.tv_enter_phoneno);
        mTextViewPhoneno2 = (TextView)findViewById(R.id.tv_enter_phoneno2);
        mTextViewPhoneno21 = (TextView)findViewById(R.id.tv_enter_phoneno21);

        mTextViewLandmark = (TextView) findViewById(R.id.tv_enter_landmark);
        mTextViewLandmark2 = (TextView) findViewById(R.id.tv_enter_landmark2);
        mTextViewLandmark21 = (TextView) findViewById(R.id.tv_enter_landmark21);

        mTextViewCity = (TextView) findViewById(R.id.tv_enter_city);
        mTextViewCity2 = (TextView) findViewById(R.id.tv_enter_city2);
        mTextViewCity21 = (TextView) findViewById(R.id.tv_enter_city21);

        mTextViewPincode = (TextView) findViewById(R.id.tv_enter_pincode);
        mTextViewPincode2 = (TextView) findViewById(R.id.tv_enter_pincode2);
        mTextViewPincode21 = (TextView) findViewById(R.id.tv_enter_pincode21);

        findViewById(R.id.bt_add_userdata).setOnClickListener(this);
        findViewById(R.id.bt_update_userinfo).setOnClickListener(this);

        mDatabaseReferenceCheckDetail.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean bool=false;
                    String key = dataSnapshot.getKey();
                    bool = dataSnapshot.getValue(boolean.class);//might throght error_______****
                    Iterable<DataSnapshot> child = dataSnapshot.getChildren();


                    for (DataSnapshot single : child){
                        bool = single.getValue(Boolean.class);

                    }

                    submitted = bool;
                    if(submitted == true){
                        mLinearLayout1.setVisibility(View.INVISIBLE);
                        mLinearLayout2.setVisibility(View.VISIBLE);

                        mDatabaseReferenceToUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                final List<UserAccount> mList = new ArrayList<UserAccount>();
//                                Iterable<DataSnapshot>children = dataSnapshot.getChildren();
//                                for (DataSnapshot singleDataSnapshot:children) {
//
//                                    UserAccount ob = singleDataSnapshot.getValue(UserAccount.class);
//                                    mList.add(ob);
//                                }
                                obj2 = dataSnapshot.getValue(UserAccount.class);

                                if(obj2!=null) {

                                    String name = obj2.getName();
                                    String add = obj2.getAddress();
                                    String no = obj2.getPhoneno();
                                    String landmk = obj2.getLandmark();
                                    String city = obj2.getCity();
                                    String pincde = obj2.getPincode();

                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name).build();
                                    mFirebaseUser.updateProfile(userProfileChangeRequest);
                                    mTextViewName21.setText(name);
                                    mTextViewAddress21.setText(add);
                                    mTextViewPhoneno21.setText(no);
                                    mTextViewCity21.setText(city);
                                    mTextViewPincode21.setText(pincde);
                                    mTextViewLandmark21.setText(landmk);
                                }
                                Boolean bool=true;
                                mDatabaseReferenceCheckDetail.setValue(bool);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.bt_add_userdata){


            obj1 = new UserAccount();
            obj1.setName(mEditTextName.getText().toString());
            obj1.setAddress(mEditTextAddress.getText().toString());
            obj1.setPhoneno(mEditTextPhoneno.getText().toString());
            obj1.setCity(mEditTextCity.getText().toString());
            obj1.setLandmark(mEditTextLandmark.getText().toString());
            obj1.setPincode(mEditTextPincode.getText().toString());
            obj2 = obj1;
            mDatabaseReferenceToUser.setValue(obj1);

            mLinearLayout1.setVisibility(View.INVISIBLE);
            mLinearLayout2.setVisibility(View.VISIBLE);




            mDatabaseReferenceToUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    final List<UserAccount> mList = new ArrayList<UserAccount>();
//                    Iterable<DataSnapshot>children = dataSnapshot.getChildren();
//                    for (DataSnapshot singleDataSnapshot:children) {
//
//                        UserAccount ob = singleDataSnapshot.getValue(UserAccount.class);
//                        mList.add(ob);
//                    }
//                    obj2 = mList.get(0);
                    obj2 =dataSnapshot.getValue(UserAccount.class);

                    if(obj2!=null) {


                        String name = obj2.getName();
                        String add = obj2.getAddress();
                        String no = obj2.getPhoneno();
                        String landmk = obj2.getLandmark();
                        String city = obj2.getCity();
                        String pincde = obj2.getPincode();

                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        mFirebaseUser.updateProfile(userProfileChangeRequest);

                        mTextViewName21.setText(name);
                        mTextViewAddress21.setText(add);
                        mTextViewPhoneno21.setText(no);
                        mTextViewCity21.setText(city);
                        mTextViewPincode21.setText(pincde);
                        mTextViewLandmark21.setText(landmk);
                    }
                    Boolean bool=true;
                    mDatabaseReferenceCheckDetail.setValue(bool);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        if(id == R.id.bt_update_userinfo){
            mLinearLayout1.setVisibility(View.VISIBLE);
            mLinearLayout2.setVisibility(View.INVISIBLE);

            if(obj2!=null){


                mEditTextAddress.setText(obj2.getAddress());
                mEditTextName.setText(obj2.getName());
                mEditTextLandmark.setText(obj2.getLandmark());
                mEditTextPincode.setText(obj2.getPincode());
                mEditTextCity.setText(obj2.getCity());
                mEditTextPhoneno.setText(obj2.getPhoneno());
            }
        }
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {

        } else {
            message = "Sorry! Not connected to internet";
            alertDialog.show();
            color = Color.RED;
            View parentView = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar
                    .make(parentView, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
