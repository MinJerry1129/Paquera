package com.blackcharm.ProfilePage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.blackcharm.Common;
import com.blackcharm.R;
import com.blackcharm.SplashActivity;
import com.blackcharm.messages.LatestMessagesActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rd.PageIndicatorView;

import org.json.JSONException;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MembershipActivity extends AppCompatActivity {
    private static final String TAG = "paymentExample";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "Adyx0K5qLbTA8_N2afzTwBanzT2Wr8Y57cy4FLdMK4GqjBuVtKFVc6HKoE9lkAxO2l7ESz3nueIeYuOy";
    String page_status = "no";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(true)
            .rememberUser(false);
    Button _member6;
    Button _member1;
    Button _memberweek;
    String membership_status;
    LoopingViewPager viewPager;
    PageIndicatorView indicatorView;
    MembershipPagerAdapter adapter;
    private DatabaseReference mEventsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        viewPager = findViewById(R.id.viewpager);
        indicatorView = findViewById(R.id.indicator);
        adapter = new MembershipPagerAdapter(this, createSecondDummyItems(), true);
        viewPager.setAdapter(adapter);
        _member6 = (Button)findViewById(R.id.btn_membership_6month);
        _member1 = (Button)findViewById(R.id.btn_membership_month);
        _memberweek = (Button)findViewById(R.id.btn_membership_week);
        mEventsReference = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().getUid());
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        indicatorView.setCount(viewPager.getIndicatorCount());
        viewPager.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
            @Override
            public void onIndicatorProgress(int selectingPosition, float progress) {
                indicatorView.setProgress(selectingPosition, progress);
            }

            @Override
            public void onIndicatorPageChange(int newIndicatorPosition) {
//                indicatorView.setSelection(newIndicatorPosition);
            }
        });
        getReady();
    }
    private ArrayList<Integer> createSecondDummyItems() {
        ArrayList<Integer> items = new ArrayList<>();
        items.add(0, 1);
        items.add(1, 2);
        items.add(2, 3);
        items.add(3, 4);
        return items;
    }
    private void getReady(){


        mEventsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String membershipstatus = dataSnapshot.child("membershipstatus").getValue().toString();
                Log.d("membershipstatus", membershipstatus);
                if(membershipstatus.equals("months")){
                    _member6.setBackgroundResource(R.drawable.back_member1_button);
                }else if(membershipstatus.equals("month")){
                    _member1.setBackgroundResource(R.drawable.back_member1_button);
                }else{
                    _memberweek.setBackgroundResource(R.drawable.back_member1_button);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        _member6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membership6month();
            }
        });
        _member1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membershipmonth();
            }
        });
        _memberweek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membershipweek();
            }
        });
    }
    private void membership6month(){
        double total = 5.99*6;

        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(String.valueOf(total)), "BRL", "Contact information fee",PayPalPayment.PAYMENT_INTENT_SALE);
//        thingToBuy.payeeEmail("testshop1@test.com");
        Intent intent = new Intent(this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        membership_status= "months";

    }
    private void membershipmonth(){
        double total = 15;

        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(String.valueOf(total)), "BRL", "Contact information fee",PayPalPayment.PAYMENT_INTENT_SALE);
//        thingToBuy.payeeEmail("testshop1@test.com");
        Intent intent = new Intent(this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        membership_status= "month";
    }
    private void membershipweek(){
        double total = 9.99;

        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(String.valueOf(total)), "BRL", "Contact information fee",PayPalPayment.PAYMENT_INTENT_SALE);
//        thingToBuy.payeeEmail("testshop1@test.com");
        Intent intent = new Intent(this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        membership_status= "week";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        setResult(102);
                        setMembership();
                    } catch (JSONException e) {
                        Toast.makeText(getBaseContext(), "an extremely unlikely failure occurred:"+e, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Log.i(TAG, "The user canceled.");
//                setMembership();
                Toast.makeText(getBaseContext(), "The user canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                //Log.i(TAG,"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(getBaseContext(), "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void setMembership(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MONTH,6);
//        cal.add(Calendar.MONTH,1);
//        cal.add(Calendar.DAY_OF_YEAR,7);
        Common.getInstance().setMembership_status("yes");
        page_status = "yes";
        if(membership_status.equals("months")){
            cal.add(Calendar.MONTH,6);
            Date Result = cal.getTime();
            String today = sdf.format(Result);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("membershipdate",today);
            childUpdates.put("membershipstatus","months");

            mEventsReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    _member6.setBackgroundResource(R.drawable.back_member1_button);
                    _member1.setBackgroundResource(R.drawable.back_member2_button);
                    _memberweek.setBackgroundResource(R.drawable.back_member2_button);
                }
            });

        }else if (membership_status.equals("month")){
            cal.add(Calendar.MONTH,1);
            Date Result = cal.getTime();
            String today = sdf.format(Result);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("membershipdate",today);
            childUpdates.put("membershipstatus","month");
            mEventsReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    _member6.setBackgroundResource(R.drawable.back_member2_button);
                    _member1.setBackgroundResource(R.drawable.back_member1_button);
                    _memberweek.setBackgroundResource(R.drawable.back_member2_button);
                }
            });
        }else{
            cal.add(Calendar.DAY_OF_YEAR,7);
            Date Result = cal.getTime();
            String today = sdf.format(Result);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("membershipdate",today);
            childUpdates.put("membershipstatus","week");
            mEventsReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    _member6.setBackgroundResource(R.drawable.back_member2_button);
                    _member1.setBackgroundResource(R.drawable.back_member2_button);
                    _memberweek.setBackgroundResource(R.drawable.back_member1_button);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(page_status.equals("yes")){
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(getApplicationContext(), LatestMessagesActivity.class);
            intent.putExtra("isFirstVisit", false);
            startActivity(intent);
            finish();
        }
        Log.d("yes","adfadfad");
    }
}
