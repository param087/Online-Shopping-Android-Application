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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.param.green.staticData.ConnectivityReceiver;
import com.example.param.green.staticData.CreateOrder;
import com.example.param.green.staticData.MyApplication;
import com.example.param.green.staticData.ProductObject;
import com.example.param.green.staticData.ProductSerial;
import com.example.param.green.staticData.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class DetailActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceUserOrder;
    private DatabaseReference mDatabaseReferenceUserdetail;
    private DatabaseReference mDatabaseReferenceStatus;
    private DatabaseReference mDatabaseReferenceServerOrder;
    private DatabaseReference mDatabaseReferenceServerOrder2;

    String userId;

    private AlertDialog.Builder alertDialog;

    private ProductSerial productSerial;
    private ProductObject product;

    private ImageView mImageView;
    private TextView mProductName;
    private TextView mProductPrice;
    private TextView mProductDiscount;
    private TextView mExpiryDate;
    private TextView mDiscription;
    private Button mOrder;


    private UserAccount account;
    private Boolean status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        alertDialog = new AlertDialog.Builder(DetailActivity.this);
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
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userId = mFirebaseUser.getUid();

        mDatabaseReferenceStatus = mFirebaseDatabase.getReference().child("Customer").child(userId).child("DetailPresent");
        mDatabaseReferenceUserdetail = mFirebaseDatabase.getReference().child("Customer").child(userId).child("accountDetail");

        mImageView = (ImageView) findViewById(R.id.im_detail);
        mProductName = (TextView) findViewById(R.id.tv_product_name_detail);
        mProductPrice = (TextView) findViewById(R.id.tv_product_price_detail);
        mProductDiscount =(TextView) findViewById(R.id.tv_product_discount_detail);
        mExpiryDate = (TextView) findViewById(R.id.tv_product_expirydate_detail);
        mDiscription = (TextView) findViewById(R.id.tv_product_desciption_detail);
        mOrder =(Button) findViewById(R.id.bt_submit_detail);

        final Intent intent = getIntent();
        if(intent.hasExtra("ProductDetail")){
            productSerial = (ProductSerial) intent.getSerializableExtra("ProductDetail");
        }

        product = new ProductObject();
        if(productSerial!=null){

            product.setDiscountPrecentage(productSerial.getDiscountPrecentage());
            product.setExpiryDate(productSerial.getExpiryDate());
            product.setProductName(productSerial.getProductName());
            product.setMrpPrice(productSerial.getMrpPrice());
            product.setDiscountPrice(productSerial.getDiscountPrice());
            product.setDescription(productSerial.getDescription());
            product.setUrl(productSerial.getUrl());

            Glide.with(mImageView.getContext())
                    .load(productSerial.getUrl())
                    .into(mImageView);
            mProductName.setText(product.getProductName());
            mProductPrice.setText(String.valueOf(product.getDiscountPrice()));
            mProductDiscount.setText(product.getDiscountPrecentage());
            mExpiryDate.setText(product.getExpiryDate());
            mDiscription.setText(product.getDescription());

            mDatabaseReferenceStatus.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    boolean bool =false;
                    String key = dataSnapshot.getKey();
                    bool = dataSnapshot.getValue(Boolean.class);
                    status = bool;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(productSerial != null) {

                        if (status == false) {
                            Intent intent = new Intent(DetailActivity.this, AccountActivity.class);
                            startActivity(intent);
                        } else {

                            mDatabaseReferenceUserdetail.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    UserAccount obj = null;
                                    obj = dataSnapshot.getValue(UserAccount.class);

                                    account = obj;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.dialog_to_payment, null);

                            final EditText mEditText = (EditText) mView.findViewById(R.id.et_amount_detail);
                            final RadioGroup mRadioGroup = (RadioGroup) mView.findViewById(R.id.rg_payment_detail);
                            Button mSubmit = (Button) mView.findViewById(R.id.bt_submit_payment);
                            Button mCancle = (Button) mView.findViewById(R.id.bt_cancle_payment);
                            final RadioButton[] mRadioButton = new RadioButton[1];

                            mBuilder.setView(mView);

                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();

                            mSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    CreateOrder createOrder = new CreateOrder();
                                    int count = 0;
                                    String amount;
                                    String methodOfPayment = null;
                                    int totalamount;
                                    int selectId = R.id.rb_cod_detail;
                                    selectId = mRadioGroup.getCheckedRadioButtonId();

                                    amount = mEditText.getText().toString();
                                    if(amount.equals("")) {

                                    }else {
                                        count = Integer.parseInt(amount);
                                    }
                                    if(count > 0) {


                                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                                        createOrder.setPincode(account.getPincode());
                                        createOrder.setLandSmark(account.getLandmark());
                                        createOrder.setStatus("NotDeliver");
                                        createOrder.setProductName(product.getProductName());
                                        createOrder.setAmount(count);
                                        createOrder.setCustomername(account.getName());
                                        createOrder.setCustomerNo(account.getPhoneno());
                                        createOrder.setPrice(product.getDiscountPrice());
                                        createOrder.setTotalCost(count * product.getDiscountPrice());
                                        createOrder.setDeliverydate("N");
                                        createOrder.setDispatchdate("N");
                                        createOrder.setOrderdate(currentDateTimeString);
                                        createOrder.setUserid(userId);
                                        createOrder.setAddress(account.getAddress());
                                        createOrder.setUrl(product.getUrl());
                                        if (selectId == R.id.rb_cod_detail) {
                                            methodOfPayment = "cod";

                                            createOrder.setPaymentmethod(methodOfPayment);
                                            createOrder.setPaymentstatus("N");
                                        }
                                        if (selectId == R.id.rb_online_detail) {
                                            methodOfPayment = "online";
                                            createOrder.setPaymentmethod(methodOfPayment);
                                            createOrder.setPaymentstatus("Y");
                                        }

                                        Random ran = new Random();
                                        int r = ran.nextInt(99999999);
                                        String ranString = String.valueOf(r);
                                        mDatabaseReferenceUserOrder = mFirebaseDatabase.getReference().child("Customer").child(userId).child("Orders");
                                        mDatabaseReferenceUserOrder.child(ranString).setValue(createOrder);
                                        mDatabaseReferenceServerOrder2 = mFirebaseDatabase.getReference().child("CompanyOrder").child(userId);
                                        mDatabaseReferenceServerOrder2.child(ranString).setValue(createOrder);

                                        final int[] flag = {0};
                                        final int[] couterorder = new int[1];
                                        mDatabaseReferenceServerOrder = mFirebaseDatabase.getReference().child("Company").child("Orders").child(userId);

                                        mDatabaseReferenceServerOrder.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int counter = 0;

                                                if (flag[0] == 0) {
                                                    counter = dataSnapshot.getValue(Integer.class);
                                                    couterorder[0] = counter;
                                                    if (couterorder[0] == -1) {
                                                        mDatabaseReferenceServerOrder.setValue(1);
                                                    } else {
                                                        mDatabaseReferenceServerOrder.setValue(couterorder[0] + 1);
                                                    }
                                                    flag[0] = 1;

                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        Toast.makeText(getApplicationContext(), "Order successfully Placed", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Order Not Placed",Toast.LENGTH_LONG).show();
                                    }
                                    dialog.cancel();



                                }
                            });

                            mCancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });

                        }
                    }

                    }
            });
        }


    }


    private  int getRandom(){
        Random random = new Random();
        int r  = random.nextInt(99999);
        return r;
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
