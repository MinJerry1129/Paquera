package com.blackcharm.ProfilePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.blackcharm.PreviewActivity;
import com.blackcharm.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcharm.bottom_nav_pages.FullScreenImageActivity;
import com.blackcharm.common.RecyclerItemClickListener;
import com.blackcharm.discovery.Discovery;
import com.blackcharm.discovery.DiscoveryImage;
import com.blackcharm.discovery.DiscoveryImageAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import static com.blackcharm.common.UtilsKt.scaleBitmap;


public class DiscoveryEditActivity extends AppCompatActivity {

    private String discoverId="";
    private RecyclerView recyclerView;
    private ArrayList<DiscoveryImage> mImages;
    private DiscoveryImageAdapter mAdapter;
    private int currentImageClickIndex=-1;
    private Discovery discovery_item;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    int AUTOCOMPLETE_REQUEST_CODE = 1000;
    private Uri mImageUri;
    private String currentPhotoPath;
    TextView m_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_discovery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Intent intent = getIntent();
        discovery_item = (Discovery)intent.getSerializableExtra("discovery_item");
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyChxF3wrDrHLCRsem-L1okrXSmMmgVLUMg");
        }

        Button saveBtn=(Button) findViewById(R.id.toolbar_discovery_save);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDiscovery();
                discovery_item.setDraft("false");
                EditText m_title = (EditText) findViewById(R.id.discovery_edit_title);
                if (m_title.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(v.getContext(), "Título da entrada", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mImages.size()==0)
                {
                    Toast.makeText(v.getContext(), "Adicione pelo menos uma imagem", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveToDatabase();
            }
        });


        Button savedraftBtn=(Button) findViewById(R.id.toolbar_discovery_save_draft);

        savedraftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDiscovery();
                discovery_item.setDraft("true");
                saveToDatabase();
            }
        });

        final DialogInterface.OnClickListener deleteDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        for (int i=0;i<mImages.size();i++)
                        {
                            StorageReference ref = storageReference.child("discovery_images/"+ mImages.get(i).getRefurl());
                            ref.delete();
                        }
                        mDatabase.child("/discoveries/"+discovery_item.getDiscovery_id()).removeValue();
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };

        Button button=(Button)findViewById(R.id.discovery_remove_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Tem certeza de que excluiu esta descoberta?").setPositiveButton("Sim", deleteDialogClickListener)
                        .setNegativeButton("Não", deleteDialogClickListener).show();
            }
        });

        if(discovery_item.getDiscovery_id().equalsIgnoreCase("-1"))
            button.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.discovery_edit_image_box);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));


        final DialogInterface.OnClickListener removeImageDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        StorageReference ref = storageReference.child("discovery_images/"+ mImages.get(currentImageClickIndex).getRefurl());
                        ref.delete();
                        mImages.remove(currentImageClickIndex);
                        recyclerView.setAdapter(mAdapter);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };


        recyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.ClickListener() {

                @Override public void onItemClick(View view, int position) {

                    Intent fullScreenIntent = new Intent(view.getContext(), FullScreenImageActivity.class);
                    fullScreenIntent.setData(Uri.parse(mImages.get(position).getUri()));
                    startActivity(fullScreenIntent);

                }

                @Override public void onLongItemClick(View view, int position) {
                    currentImageClickIndex=position;
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Você tem certeza de remover esta imagem?").setPositiveButton("Sim", removeImageDialogClickListener)
                            .setNegativeButton("Não", removeImageDialogClickListener).show();
                }
            })
        );

        Button addDiscoveryImages=(Button)findViewById(R.id.addDiscoveryImages);
        addDiscoveryImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Select Images");
                builder.setPositiveButton("Capture image",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

        Button discovery_view_btn= (Button) this.findViewById(R.id.toolbar_discovery_view);
        discovery_view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDiscovery();
                Intent intent = new Intent(getBaseContext(), PreviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            val row = item as LatestMessageRow
                intent.putExtra("discovery_data",discovery_item);
                intent.putExtra("type",1);

                startActivity(intent);
            }
        });

        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );


        m_location = (TextView) findViewById(R.id.discovery_edit_location);
        m_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields
                ).build(getBaseContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        setDiscoveryData();
    }

    private void setDiscoveryData(){
        EditText m_title = (EditText) findViewById(R.id.discovery_edit_title);
        m_title.setText(discovery_item.getTitle());

        EditText m_subtitle = (EditText) findViewById(R.id.discovery_edit_subtitle);
        m_subtitle.setText(discovery_item.getSubtitle());

        TextView m_location = (TextView) findViewById(R.id.discovery_edit_location);
        m_location.setText(discovery_item.getLocation());

        EditText m_price = (EditText) findViewById(R.id.discovery_edit_price);
        m_price.setText(discovery_item.getPrice());

        EditText m_includes = (EditText) findViewById(R.id.discovery_edit_includes);
        m_includes.setText(discovery_item.getIncludes());

        EditText m_duration = (EditText) findViewById(R.id.discovery_edit_duration);
        m_duration.setText(discovery_item.getDurations());

        EditText m_minage = (EditText) findViewById(R.id.discovery_edit_minage);
        m_minage.setText(discovery_item.getMinage());

        EditText m_groupsize = (EditText) findViewById(R.id.discovery_edit_groupsize);
        m_groupsize.setText(discovery_item.getGroupsize());

        EditText m_email = (EditText) findViewById(R.id.discovery_edit_email);
        m_email.setText(discovery_item.getEmail());

        EditText m_website = (EditText) findViewById(R.id.discovery_edit_website);
        m_website.setText(discovery_item.getWebsite());

        EditText m_phone = (EditText) findViewById(R.id.discovery_edit_phone);
        m_phone.setText(discovery_item.getPhone());

        EditText m_instagram = (EditText) findViewById(R.id.discovery_edit_instagram);
        m_instagram.setText(discovery_item.getInstagram());

        EditText m_moreinfo = (EditText) findViewById(R.id.discovery_edit_description);
        m_moreinfo.setText(discovery_item.getDescription());

        mImages=discovery_item.getImages();

        mAdapter = new DiscoveryImageAdapter(mImages);

        recyclerView.setAdapter(mAdapter);
    }

    private void setDiscovery() {
        EditText m_title = (EditText) findViewById(R.id.discovery_edit_title);
        discovery_item.setTitle(m_title.getText().toString());

        EditText m_subtitle = (EditText) findViewById(R.id.discovery_edit_subtitle);
        discovery_item.setSubtitle(m_subtitle.getText().toString());

        TextView m_location = (TextView) findViewById(R.id.discovery_edit_location);
        discovery_item.setLocation(m_location.getText().toString());

        EditText m_price = (EditText) findViewById(R.id.discovery_edit_price);
        discovery_item.setPrice(m_price.getText().toString());

        EditText m_includes = (EditText) findViewById(R.id.discovery_edit_includes);
        discovery_item.setIncludes(m_includes.getText().toString());

        EditText m_duration = (EditText) findViewById(R.id.discovery_edit_duration);
        discovery_item.setDurations(m_duration.getText().toString());

        EditText m_minage = (EditText) findViewById(R.id.discovery_edit_minage);
        discovery_item.setMinage(m_minage.getText().toString());

        EditText m_groupsize = (EditText) findViewById(R.id.discovery_edit_groupsize);
        discovery_item.setGroupsize(m_groupsize.getText().toString());

        EditText m_email = (EditText) findViewById(R.id.discovery_edit_email);
        discovery_item.setEmail(m_email.getText().toString());

        EditText m_website = (EditText) findViewById(R.id.discovery_edit_website);
        discovery_item.setWebsite(m_website.getText().toString());

        EditText m_phone = (EditText) findViewById(R.id.discovery_edit_phone);
        discovery_item.setPhone(m_phone.getText().toString());

        EditText m_instagram = (EditText) findViewById(R.id.discovery_edit_instagram);
        discovery_item.setInstagram(m_instagram.getText().toString());

        EditText m_moreinfo = (EditText) findViewById(R.id.discovery_edit_description);
        discovery_item.setDescription(m_moreinfo.getText().toString());

        discovery_item.setImages(mImages);

    }

    private void saveToDatabase() {

        if(discovery_item.getDiscovery_id().equalsIgnoreCase("-1"))
        {
            discovery_item.setDiscovery_id(mDatabase.child("discoveries/").push().getKey());
        }

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/discoveries/"+discovery_item.getDiscovery_id(), discovery_item);

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(this,"Salvo com sucesso", Toast.LENGTH_LONG).show();
        this.finish();
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

    @Override
    public boolean onSupportNavigateUp() {
        if(!discovery_item.getDiscovery_id().equalsIgnoreCase("-1"))
        {
            if(mImages.size()==0)
                Toast.makeText(this,"Você precisa salvar a descoberta", Toast.LENGTH_LONG).show();
            else
                onBackPressed();
        }else
            onBackPressed();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == Activity.RESULT_OK && imageReturnedIntent != null&&requestCode==AUTOCOMPLETE_REQUEST_CODE) {

            Place place = Autocomplete.getPlaceFromIntent(imageReturnedIntent);

            m_location.setText(place.getAddress());
        }
        else
        {

            Bitmap bitmap = null;
            if(requestCode==100&&resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode==101&&resultCode == RESULT_OK) {
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
                final StorageReference ref = storageReference.child("discovery_images/" + refUrl);
                ref.putBytes(baos.toByteArray())
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.dismiss();

                                        mImages.add(new DiscoveryImage(refUrl, uri.toString()));
                                        recyclerView.setAdapter(mAdapter);
                                        Toast.makeText(DiscoveryEditActivity.this, "Imagem carregada ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(DiscoveryEditActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Enviando " + (int) progress + "%");
                            }
                        });
            }
            catch (Exception e)
            {
                //Toast.makeText(DiscoveryEditActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
