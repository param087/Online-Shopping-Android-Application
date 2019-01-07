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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.param.green.staticData.ConnectivityReceiver;
import com.example.param.green.staticData.CreateOrder;
import com.example.param.green.staticData.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdaptor.ListItemListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceOrders;
    private DatabaseReference getmDatabaseReferenceOrdersChange;

    private AlertDialog.Builder alertDialog;

    private String userId;
    private RecyclerView mRecyclerView;
    private List<CreateOrder> mListOrder;
    private List<String> mListOrderKey;
    private int noOfProduct;
    private CartAdaptor mAdaptor;
    private Toast mToast;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        alertDialog = new AlertDialog.Builder(CartActivity.this);
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
                //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        showSnack(isNetworkAvailable());

        final List<CreateOrder>mList = new ArrayList<CreateOrder>();
        final List<String>mListKey = new ArrayList<String>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userId = mFirebaseUser.getUid();
        mDatabaseReferenceOrders = mFirebaseDatabase.getReference().child("Customer").child(userId).child("Orders");

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_cartdata);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mDatabaseReferenceOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot singleDataSnapshot : children){
                    CreateOrder obj = singleDataSnapshot.getValue(CreateOrder.class);
                    String objs = singleDataSnapshot.getKey();
                    mList.add(obj);
                    mListKey.add(objs);
                    noOfProduct += 1;
                }
                mListOrder =null;
                mListOrder = mList;
                mListOrderKey = null;
                mListOrderKey = mListKey;

                mRecyclerView = null;
                mRecyclerView = (RecyclerView)findViewById(R.id.rv_cartdata);

                mRecyclerView.setHasFixedSize(true);
                mAdaptor = new CartAdaptor(noOfProduct,mListOrder,CartActivity.this);
                mRecyclerView.setAdapter(mAdaptor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onItemClicked(int clickItemIndex) {

        if(mToast!=null){
            mToast=null;
        }

        Intent intent = new Intent(CartActivity.this, TrackActivity.class);
        intent.putExtra("key",mListOrderKey.get(clickItemIndex));
        intent.putExtra("status1",mListOrder.get(clickItemIndex).getStatus());
        intent.putExtra("disdate",mListOrder.get(clickItemIndex).getDispatchdate());
        intent.putExtra("deldate",mListOrder.get(clickItemIndex).getDeliverydate());
        intent.putExtra("odate",mListOrder.get(clickItemIndex).getOrderdate());

        startActivity(intent);
        //mDatabaseReferenceOrders = mFirebaseDatabase.getReference().child("Customer").child(userId).child("Orders").child(mListOrderKey.get(clickItemIndex));
        //mDatabaseReferenceOrders.child("status").setValue("Cancle");

        //mToast = Toast.makeText(CartActivity.this,"Order : "+clickItemIndex+" cancle",Toast.LENGTH_SHORT);
        //mToast.show();

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
