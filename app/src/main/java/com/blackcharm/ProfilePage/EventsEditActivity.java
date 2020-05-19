package com.blackcharm.ProfilePage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.blackcharm.PreviewActivity;
import com.blackcharm.R;
import com.blackcharm.events.Events;
import com.blackcharm.events.EventsImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.blackcharm.common.UtilsKt.scaleBitmap;

public class EventsEditActivity extends AppCompatActivity {

    private ImageView imageView;
    private EventsImage eventImage;
    private int currentImageClickIndex=-1;
    private Events events_item;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri mImageUri;
    private String currentPhotoPath;
    TextView m_location;
    TextView m_datetime;
    int AUTOCOMPLETE_REQUEST_CODE = 1000;

    private int mYear, mMonth, mDay, mHour, mMinute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_events_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Intent intent = getIntent();
        events_item = (Events)intent.getSerializableExtra("events_item");
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        m_datetime = (TextView ) findViewById(R.id.events_edit_datetime);

        final Calendar c = Calendar.getInstance();

        m_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    Timestamp timestamp = new Timestamp(year-1900, monthOfYear, dayOfMonth, hourOfDay, minute, 0,0);
                                    events_item.setDatetime(String.valueOf(timestamp.getTime()));
                                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                    cal.setTimeInMillis(timestamp.getTime());
                                    String date = DateFormat.format("EEEE dd LLLL yyyy, hh:mm", cal).toString();
                                    m_datetime.setText(date);
                                }
                            }, mHour, mMinute, false);
                        timePickerDialog.show();

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        Button saveBtn=(Button) findViewById(R.id.toolbar_events_save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEvent();
                events_item.setDraft("false");
                EditText m_title = (EditText) findViewById(R.id.events_edit_title);
                if (m_title.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(v.getContext(), "Input title.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eventImage.getRefurl().equalsIgnoreCase(""))
                {
                    Toast.makeText(v.getContext(), "Select Event image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (events_item.getDatetime().equalsIgnoreCase("")) {
                    Toast.makeText(v.getContext(), "Please Select date and time.", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveToDatabase();
            }
        });


        Button savedraftBtn=(Button) findViewById(R.id.toolbar_events_save_draft);

        savedraftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEvent();
                events_item.setDraft("true");
                saveToDatabase();
            }
        });

        Button previewBtn=(Button) findViewById(R.id.toolbar_events_view);

        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEvent();

                Intent intent = new Intent(getBaseContext(), PreviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            val row = item as LatestMessageRow
                intent.putExtra("event_data",events_item);
                intent.putExtra("type",2);

                startActivity(intent);
            }
        });

        final DialogInterface.OnClickListener deleteDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        StorageReference ref = storageReference.child("events_images/"+ eventImage.getRefurl());
                        ref.delete();
                        mDatabase.child("/events/" + events_item.getEvents_id()).removeValue();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        Button button=(Button)findViewById(R.id.events_remove_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Tem certeza de que excluir este evento?").setPositiveButton("Sim", deleteDialogClickListener)
                        .setNegativeButton("Não", deleteDialogClickListener).show();
            }
        });

        if(events_item.getEvents_id().equalsIgnoreCase("-1"))
            button.setVisibility(View.GONE);

        imageView = (ImageView) findViewById(R.id.events_edit_image_box);

        Button addEventsImages=(Button)findViewById(R.id.addeventsImages);
        addEventsImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Select Images");
                builder.setPositiveButton("Capture image",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!eventImage.getRefurl().equalsIgnoreCase(""))
                        {
                            try {
                                StorageReference ref = storageReference.child("events_images/"+ eventImage.getRefurl());
                                ref.delete();
                            }
                            catch (Exception e)
                            {

                            }
                        }
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photo;
                        try
                        {
                            photo = createTemporaryFile();
                            mImageUri = FileProvider.getUriForFile(getBaseContext(),
                                    "com.myfriendsroomlimited.fileprovider",
                                    photo);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                            startActivityForResult(takePicture, 101);
                        }
                        catch(Exception e)
                        {
                            System.out.println(e.toString());
                        }
                    }
                });
                builder.setNegativeButton("Select From Gallery",   new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 100);
                    }
                });
                builder.setNeutralButton("Cancel",   new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );


        m_location = (TextView) findViewById(R.id.events_edit_location);
        m_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields
                ).build(getBaseContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        setEventsData();
    }

    private File createTemporaryFile() throws Exception
    {
        String timeStamp = "temp";
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void setEventsData(){
        EditText m_title = (EditText) findViewById(R.id.events_edit_title);
        m_title.setText(events_item.getTitle());
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.valueOf(events_item.getDatetime()));
            String date = DateFormat.format("EEEE dd LLLL yyyy, hh:mm", cal).toString();
            m_datetime.setText(date);
        }
        catch (Exception e)
        {
            m_datetime.setText("");
        }
        EditText m_organise = (EditText) findViewById(R.id.events_edit_organise);
        m_organise.setText(events_item.getOrganise());
        TextView m_location = (TextView) findViewById(R.id.events_edit_location);
        m_location.setText(events_item.getLocation());
        EditText m_time = (EditText) findViewById(R.id.events_edit_time);
        m_time.setText(events_item.getTime());
        EditText m_howtofindus = (EditText) findViewById(R.id.events_edit_howtofindus);
        m_howtofindus.setText(events_item.getHowtofindus());
        EditText m_link1 = (EditText) findViewById(R.id.events_edit_link1);
        m_link1.setText(events_item.getLink1());
        EditText m_link2 = (EditText) findViewById(R.id.events_edit_link2);
        m_link2.setText(events_item.getLink2());
        EditText m_description = (EditText) findViewById(R.id.events_edit_description);
        m_description.setText(events_item.getDescription());

        eventImage = events_item.getImage();

        try {
            Glide.with(imageView).load(eventImage.getUri())
                    .placeholder(R.drawable.progress_animation)

                    .error(R.drawable.branco).into(imageView);
        }
        catch (Exception e)
        {

        }

    }

    private void setEvent() {
        EditText m_title = (EditText) findViewById(R.id.events_edit_title);

        events_item.setTitle(m_title.getText().toString());

        EditText m_organise = (EditText) findViewById(R.id.events_edit_organise);
        events_item.setOrganise(m_organise.getText().toString());
        TextView m_location = (TextView) findViewById(R.id.events_edit_location);
        events_item.setLocation(m_location.getText().toString());
        EditText m_time = (EditText) findViewById(R.id.events_edit_time);
        events_item.setTime(m_time.getText().toString());
        EditText m_howtofindus = (EditText) findViewById(R.id.events_edit_howtofindus);
        events_item.setHowtofindus(m_howtofindus.getText().toString());
        EditText m_link1 = (EditText) findViewById(R.id.events_edit_link1);
        events_item.setLink1(m_link1.getText().toString());
        EditText m_link2 = (EditText) findViewById(R.id.events_edit_link2);
        events_item.setLink2(m_link2.getText().toString());
        EditText m_description = (EditText) findViewById(R.id.events_edit_description);
        events_item.setDescription(m_description.getText().toString());

        events_item.setImage(eventImage);

    }

    private void saveToDatabase() {
        if(events_item.getEvents_id().equalsIgnoreCase("-1"))
        {
            events_item.setEvents_id(mDatabase.child("events/").push().getKey());
        }

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" +events_item.getEvents_id(), events_item);

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(this,"Save Successfully", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (events_item.getEvents_id().equalsIgnoreCase("-1"))
        {
            if (!eventImage.getRefurl().equalsIgnoreCase(""))
            {
                try {
                    StorageReference ref = storageReference.child("events_images/"+ eventImage.getRefurl());
                    ref.delete();
                }
                catch (Exception e)
                {

                }
            }
        }
        onBackPressed();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == Activity.RESULT_OK && imageReturnedIntent != null&&requestCode==AUTOCOMPLETE_REQUEST_CODE) {

            Place place = Autocomplete.getPlaceFromIntent(imageReturnedIntent);

            m_location.setText(place.getAddress());
        }
        else {

            Bitmap bitmap = null;
            if (requestCode == 100 && resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 101 && resultCode == RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                Bitmap scaled  = scaleBitmap(bitmap);

                scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading Image " + " ...");
                progressDialog.show();
                final String refUrl = UUID.randomUUID().toString();
                final StorageReference ref = storageReference.child("events_images/" + refUrl);
                ref.putBytes(baos.toByteArray())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.dismiss();

                                        eventImage = new EventsImage(refUrl, uri.toString());
                                        Glide.with(imageView).load(eventImage.getUri())
                                                .placeholder(R.drawable.progress_animation)

                                                .error(R.drawable.branco).into(imageView);
                                        Toast.makeText(EventsEditActivity.this, "Uploaded Image ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploading " + (int) progress + "%");
                            }
                        });
            } catch (Exception e) {
            }
        }
    }
}
