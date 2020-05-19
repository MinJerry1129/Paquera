package com.blackcharm.ProfilePage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.blackcharm.R;
import com.blackcharm.common.RecyclerItemClickListener;
import com.blackcharm.events.Events;
import com.blackcharm.events.EventsAdapter;

import java.util.ArrayList;

public class EventsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Events> myDataset;
    private DatabaseReference mEventsReference;
    EventsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_events_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        recyclerView = (RecyclerView) findViewById(R.id.events_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        myDataset = new ArrayList<Events>();
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new EventsAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

        Button addBtn=(Button)findViewById(R.id.toolbar_events_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventsActivity.this, EventsEditActivity.class);
                Events events=new Events();
                events.setEvents_id("-1");
                events.setUserid(FirebaseAuth.getInstance().getUid());
                intent.putExtra("events_item",events);
                startActivity(intent);
            }
        });

//        mEventsReference = FirebaseDatabase.getInstance().getReference().child("events/" + FirebaseAuth.getInstance().getUid());
        mEventsReference = FirebaseDatabase.getInstance().getReference().child("events/");

        mEventsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Events events = dataSnapshot.getValue(Events.class);
                    myDataset.add(events);
                    recyclerView.setAdapter(mAdapter);
                }catch (Exception e)
                {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    Events p0 = dataSnapshot.getValue(Events.class);
                    for(int i = 0; i < myDataset.size(); i++) {
                        Events events_item=myDataset.get(i);
                        String events_id=events_item.getEvents_id();
                        String aa=p0.getEvents_id();
                        if(events_id.equalsIgnoreCase(aa)) {
                            myDataset.set(i,p0);
                            break;
                        }
                    }
                    recyclerView.setAdapter(mAdapter);
                }catch (Exception e)
                {

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {
                    Events p0 = dataSnapshot.getValue(Events.class);
                    int index=-1;
                    for(int i = 0; i < myDataset.size(); i++) {
                        if(myDataset.get(i).getEvents_id().equalsIgnoreCase(p0.getEvents_id())) {
                            index=i;
                        }
                    }
                    myDataset.remove(index);
                    recyclerView.setAdapter(mAdapter);
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.ClickListener() {

                @Override public void onItemClick(View view, int position) {

                    Intent intent=new Intent(EventsActivity.this, EventsEditActivity.class);
                    intent.putExtra("events_item",myDataset.get(position));
                    startActivity(intent);
                }

                @Override public void onLongItemClick(View view, int position) {
                }
            })
        );

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
