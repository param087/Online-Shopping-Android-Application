package com.example.param.green;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.param.green.staticData.ConnectivityReceiver;
import com.example.param.green.staticData.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceOrders;
    private DatabaseReference mDatabaseReferenceOrdersServer;

    private AlertDialog.Builder alertDialog;

    private String userId;
    private String status;
    private String desdate;
    private String deldate;
    private String orderDate;
    private String keyUpdate;

    private TextView mTextView1;
    private TextView mTextView2;
    private SeekBar mSeekBar;
    private Button mButton;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mButton = (Button) findViewById(R.id.bt_tarcker_cancle);
        mTextView1 = (TextView) findViewById(R.id.tv_tracker_detail);
        mTextView2 = (TextView) findViewById(R.id.tv_tracker_detail_date);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        alertDialog = new AlertDialog.Builder(TrackActivity.this);
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
                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        showSnack(isNetworkAvailable());


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userId = mFirebaseUser.getUid();
        mDatabaseReferenceOrders = mFirebaseDatabase.getReference().child("Customer").child(userId).child("Orders");

        final Intent intent = getIntent();
        keyUpdate = intent.getStringExtra("key");
        status = intent.getStringExtra("status1");
        orderDate = intent.getStringExtra("odate");
        desdate = intent.getStringExtra("disdate");
        deldate = intent.getStringExtra("deldate");





        if(status.equals("Cancle")){
            mSeekBar.setProgress(0);
            mTextView1.setText("Order Cancled");
            mButton.setVisibility(View.INVISIBLE);
        }else if (status.equals("NotDeliver")){

           mSeekBar.setProgress(33);

            mTextView1.setText("OrderPlaced");
            mTextView2.setText(orderDate);
            mButton.setVisibility(View.VISIBLE);
        } else if (status.equals("Dispatch")){

            mSeekBar.setProgress(66);

            mTextView1.setText(status);
            mTextView2.setText(desdate);
            mButton.setVisibility(View.VISIBLE);
        }else if (status.equals("Delivered")){

            mSeekBar.setProgress(99);
            mTextView1.setText(status);
            mTextView2.setText(deldate);
            mButton.setVisibility(View.INVISIBLE);
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReferenceOrders = mFirebaseDatabase.getReference().child("Customer").child(userId).child("Orders").child(keyUpdate);
                mDatabaseReferenceOrders.setValue(null);
                mDatabaseReferenceOrdersServer = mFirebaseDatabase.getReference().child("CompanyOrder").child(userId).child(keyUpdate);
                mDatabaseReferenceOrdersServer.child("status").setValue("Cancle");
                mToast = Toast.makeText(TrackActivity.this,"Order cancle",Toast.LENGTH_SHORT);
                mToast.show();
                Intent intent1 = new Intent(TrackActivity.this, CartActivity.class);
                startActivity(intent1);
            }
        });
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
