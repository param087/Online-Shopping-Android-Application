package com.example.param.green;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.param.green.staticData.FriendlyMessage;
import com.example.param.green.staticData.MessageAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChartActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH = 1000;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabaseReference;


    private ListView mMessageListView;
    private MessageAdaptor mMessageAdaptor;
    private ProgressBar mProgressBar;
    private EditText mEditText;
    private Button mButton;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String name = mFirebaseUser.getDisplayName();
        mUsername = ANONYMOUS;
        if(name != null){
            mUsername = name;
        }else {
            mUsername = mFirebaseUser.getEmail();
        }
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("ChatForum");

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mEditText = (EditText) findViewById(R.id.messageEidtText);
        mButton = (Button) findViewById(R.id.bt_send_message);

        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdaptor = new MessageAdaptor(this, R.layout.message_item, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdaptor);

        mProgressBar.setVisibility(View.INVISIBLE);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if(charSequence.toString().trim().length() > 0 ){
                        mButton.setEnabled(true);
                    }else{
                        mButton.setEnabled(false);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                mMessageAdaptor.add(friendlyMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH)});

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendlyMessage message = new FriendlyMessage(mUsername, mEditText.getText().toString());
                mDatabaseReference.push().setValue(message);
                mEditText.setText("");
            }
        });

    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener != null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdaptor.add(friendlyMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabseReadListener(){
        if(mChildEventListener!=null){
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


}
