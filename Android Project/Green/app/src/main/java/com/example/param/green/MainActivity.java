package com.example.param.green;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.param.green.staticData.ConnectivityReceiver;
import com.example.param.green.staticData.Googleapiuser;
import com.example.param.green.staticData.MyApplication;
import com.example.param.green.staticData.ProductObject;
import com.example.param.green.staticData.ProductSerial;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        ItemAdaptor.ListItemListener,NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    private List<ProductObject> mListOuter;

    private AlertDialog.Builder alertDialog;

    private Toast mToast;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReferenceDetail;
    private DatabaseReference mDatabaseReferenceDetailhome;
    private DatabaseReference mDatabaseReferenceDetailveg;
    private DatabaseReference mDatabaseReferenceDetailfrius;

    private FirebaseAuth mAuth;
    private FirebaseUser muser ;

    private RecyclerView mRecyclerView;


    private ItemAdaptor mItemAdaptor;

    int countOfItem = 0;

    private Button mSignOutBuuton;
    private TextView mUserDetail;

    private int method;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getInstance().setConnectivityListener(this);

        alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setCancelable(false);
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
                dialog.cancel();
            }
        });

        showSnack(isNetworkAvailable());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_mainactivity);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mNavigationView = (NavigationView) findViewById(R.id.nav_menu);


        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView.setNavigationItemSelectedListener(this);







        final ArrayList<ProductObject> mList = new ArrayList<ProductObject>();
        mList.clear();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReferenceDetail = mFirebaseDatabase.getReference().child("Product_deatil");

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_datainfo);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);




        mDatabaseReferenceDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> childern = dataSnapshot.getChildren();

                for (DataSnapshot singleDataSnapshot : childern) {
                    ProductObject obj = singleDataSnapshot.getValue(ProductObject.class);
                    mList.add(obj);
                    countOfItem += 1;
                }

                mListOuter = mList;
                mRecyclerView.setHasFixedSize(true);
                mItemAdaptor = new ItemAdaptor(countOfItem, mList, MainActivity.this);
                mRecyclerView.setAdapter(mItemAdaptor);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Intent mgetIntentActivity = getIntent();
         method =0;

        if(mgetIntentActivity.hasExtra(Intent.EXTRA_TEXT)){
            method = mgetIntentActivity.getIntExtra(Intent.EXTRA_TEXT,0);
        }
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.Search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }
//----------- ---------- ---------- ---------- ----------- --------
    private void search(SearchView searchView){
     searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

         @Override
         public boolean onQueryTextSubmit(String query) {
             return false;
         }

         @Override
         public boolean onQueryTextChange(String newText) {
             mItemAdaptor.getFilter().filter(newText);
             return true;
         }
     });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
           // Toast.makeText(MainActivity.this,"T:"+item,Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        final int id = item.getItemId();

        if(id == R.id.nav_my_account){
            Context context = MainActivity.this;
            Class destinationActivity = AccountActivity.class;
            Intent intentdestinationActivity = new Intent(context, destinationActivity);
            startActivity(intentdestinationActivity);
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerLayout.closeDrawers();
            return true;
        }else if(id == R.id.nav_order){

            Intent instentdestion = new Intent(MainActivity.this, CartActivity.class);
            startActivity(instentdestion);
        }else if(id == R.id.nav_logout){


             final int finalMethod = method;

                mAuth.signOut();

                if(finalMethod ==1){

                    GoogleApiClient mGoogleApiClient = Googleapiuser.getmGoogleApiClient();
                    if(mGoogleApiClient !=null) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(

                        new ResultCallback<Status>() {
                              @Override
                              public void onResult(@NonNull Status status) {

                              }
                           }
                    );
                    }
                }
            Toast.makeText(MainActivity.this,"Sign Out",Toast.LENGTH_SHORT).show();
            Context context = MainActivity.this;
            Class destinationActivity = LoginActivity.class;
            Intent intentdestinationActivity = new Intent(context, destinationActivity);
            startActivity(intentdestinationActivity);
            mDrawerLayout.closeDrawers();
            return true;

        } else if(id == R.id.nav_catergoryoption){

            updateViewMenuToCategory();

        } else if(id == R.id.nav_feedback){

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setData(Uri.parse("email"));
            String [] s = {"sdlproject087@gmail.com"};
            i.putExtra(Intent.EXTRA_EMAIL, s);
            i.putExtra(Intent.EXTRA_SUBJECT, "feedback");
            i.setType("message/rfc822");
            Intent chooser = Intent.createChooser(i,"Launch Email");
            startActivity(chooser);

            mDrawerLayout.closeDrawers();
        }else  if(id == R.id.nav_backmenu){
            updateCategorybacktoMenu();
        } else if(id == R.id.nav_nonveg){

            mDrawerLayout.closeDrawers();
        } else if(id == R.id.nav_glocery){

            mDrawerLayout.closeDrawers();
        } else if(id == R.id.nav_fruits){
            countOfItem =0;
            mListOuter.clear();
            mDatabaseReferenceDetailfrius = mFirebaseDatabase.getReference().child("category").child("FRUITS");
            final ArrayList<ProductObject> mList = new ArrayList<ProductObject>();
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_datainfo);





            mDatabaseReferenceDetailfrius.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> childern = dataSnapshot.getChildren();

                    for (DataSnapshot singleDataSnapshot : childern) {
                        ProductObject obj = singleDataSnapshot.getValue(ProductObject.class);
                        mList.add(obj);
                        countOfItem += 1;
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    mRecyclerView.setLayoutManager(linearLayoutManager);

                    mListOuter = mList;
                    mRecyclerView.setHasFixedSize(true);
                    mItemAdaptor = new ItemAdaptor(countOfItem, mList, MainActivity.this);
                    mRecyclerView.setAdapter(mItemAdaptor);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDrawerLayout.closeDrawers();
        } else if(id == R.id.nav_vegitables){

            countOfItem =0;
            mListOuter.clear();
            mDatabaseReferenceDetailveg = mFirebaseDatabase.getReference().child("category").child("vegetables");
            final ArrayList<ProductObject> mList = new ArrayList<ProductObject>();
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_datainfo);





            mDatabaseReferenceDetailveg.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> childern = dataSnapshot.getChildren();

                    for (DataSnapshot singleDataSnapshot : childern) {
                        ProductObject obj = singleDataSnapshot.getValue(ProductObject.class);
                        mList.add(obj);
                        countOfItem += 1;
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    mRecyclerView.setLayoutManager(linearLayoutManager);

                    mListOuter = mList;
                    mRecyclerView.setHasFixedSize(true);
                    mItemAdaptor = new ItemAdaptor(countOfItem, mList, MainActivity.this);
                    mRecyclerView.setAdapter(mItemAdaptor);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDrawerLayout.closeDrawers();
        } else if(id == R.id.nav_home){

           // Toast.makeText(MainActivity.this,"hi",Toast.LENGTH_SHORT).show();
            countOfItem =0;
            mListOuter.clear();
            mDatabaseReferenceDetailhome = mFirebaseDatabase.getReference().child("Product_deatil");
            final ArrayList<ProductObject> mList = new ArrayList<ProductObject>();
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_datainfo);





            mDatabaseReferenceDetailhome.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> childern = dataSnapshot.getChildren();

                    for (DataSnapshot singleDataSnapshot : childern) {
                        ProductObject obj = singleDataSnapshot.getValue(ProductObject.class);
                        mList.add(obj);
                        countOfItem += 1;
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    mRecyclerView.setLayoutManager(linearLayoutManager);

                    mListOuter = mList;
                    mRecyclerView.setHasFixedSize(true);
                    mItemAdaptor = new ItemAdaptor(countOfItem, mList, MainActivity.this);
                    mRecyclerView.setAdapter(mItemAdaptor);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            mDrawerLayout.closeDrawers();
        }else if(id == R.id.nav_forum){
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
            mDrawerLayout.closeDrawers();
        }

        return false;
    }

    private void updateViewMenuToCategory(){
        mNavigationView.getMenu().findItem(R.id.nav_backmenu).setVisible(true);

        mNavigationView.getMenu().findItem(R.id.nav_catergoryoption).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_my_account).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_order).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_feedback).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_home).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_forum).setVisible(false);

        mNavigationView.getMenu().findItem(R.id.nav_fruits).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_vegitables).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_glocery).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_nonveg).setVisible(false);

    }
    private void updateCategorybacktoMenu(){
        mNavigationView.getMenu().findItem(R.id.nav_backmenu).setVisible(false);

        mNavigationView.getMenu().findItem(R.id.nav_catergoryoption).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_my_account).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_order).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_feedback).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
        mNavigationView.getMenu().findItem(R.id.nav_forum).setVisible(true);

        mNavigationView.getMenu().findItem(R.id.nav_fruits).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_vegitables).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_glocery).setVisible(false);
        mNavigationView.getMenu().findItem(R.id.nav_nonveg).setVisible(false);
    }
    @Override
    public void onItemClicked(int clikedItemIndex,ArrayList<ProductObject> filerList) {

        clikedItemIndex += 1;
        if(mToast != null){
            mToast=null;
        }
        //  mToast = Toast.makeText(MainActivity.this,"Item = "+clikedItemIndex,Toast.LENGTH_SHORT);
        // mToast.show();

        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent intentdestinationActivity = new Intent(context,destinationActivity);
        ProductObject product = filerList.get(clikedItemIndex-1);

        ProductSerial productSerial = new ProductSerial();
        productSerial.setUrl(product.getUrl());
        productSerial.setDescription(product.getDescription());
        productSerial.setDiscountPrecentage(product.getDiscountPrecentage());
        productSerial.setExpiryDate(product.getExpiryDate());
        productSerial.setDiscountPrice(product.getDiscountPrice());
        productSerial.setMrpPrice(product.getMrpPrice());
        productSerial.setProductName(product.getProductName());

        intentdestinationActivity.putExtra("ProductDetail",productSerial);

        startActivity(intentdestinationActivity);
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
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            alertDialog.show();
            color = Color.RED;
        }

        View parentView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(parentView, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
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



    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
