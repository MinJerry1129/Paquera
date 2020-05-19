package com.blackcharm.ProfilePage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.blackcharm.R;
import com.blackcharm.common.RecyclerItemClickListener;
import com.blackcharm.discovery.Discovery;
import com.blackcharm.discovery.DiscoveryAdapter;

import java.util.ArrayList;

public class DiscoveryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Discovery> myDataset;
    private DatabaseReference mDiscoveryReference;
    DiscoveryAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_discovery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        recyclerView = (RecyclerView) findViewById(R.id.discovery_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        myDataset = new ArrayList<Discovery>();
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DiscoveryAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

        Button addBtn=(Button)findViewById(R.id.toolbar_discovery_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DiscoveryActivity.this, DiscoveryEditActivity.class);
                Discovery discovery=new Discovery();
                discovery.setDiscovery_id("-1");
                discovery.setUserid(FirebaseAuth.getInstance().getUid());
                intent.putExtra("discovery_item",discovery);
                startActivity(intent);
            }
        });

//        mDiscoveryReference = FirebaseDatabase.getInstance().getReference().child("discoveries/" + FirebaseAuth.getInstance().getUid());
        mDiscoveryReference = FirebaseDatabase.getInstance().getReference().child("discoveries/");

        mDiscoveryReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Discovery discovery = dataSnapshot.getValue(Discovery.class);
                    myDataset.add(discovery);
                    recyclerView.setAdapter(mAdapter);
                }
                catch (Exception e)
                {
                    System.err.println(e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    Discovery p0 = dataSnapshot.getValue(Discovery.class);
                    for(int i = 0; i < myDataset.size(); i++) {
                        Discovery discovery_item=myDataset.get(i);
                        String discovery_id=discovery_item.getDiscovery_id();
                        String aa=p0.getDiscovery_id();
                        if(discovery_id.equalsIgnoreCase(aa)) {
                            myDataset.set(i,p0);
                            break;
                        }
                    }
                    recyclerView.setAdapter(mAdapter);
                }
                catch (Exception e)
                {
                    System.err.println(e.toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {

                    Discovery p0 = dataSnapshot.getValue(Discovery.class);
                    int index = -1;
                    for (int i = 0; i < myDataset.size(); i++) {
                        if (myDataset.get(i).getDiscovery_id().equalsIgnoreCase(p0.getDiscovery_id())) {
                            index = i;
                        }
                    }
                    myDataset.remove(index);
                    recyclerView.setAdapter(mAdapter);
                }
                catch (Exception e)
                {
                    System.err.println(e.toString());
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

                    Intent intent=new Intent(DiscoveryActivity.this, DiscoveryEditActivity.class);
                    intent.putExtra("discovery_item",myDataset.get(position));
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
