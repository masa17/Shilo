package com.aldarjose.shilo;


import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.List;

/////////ADD BACK TO CAMERA
/////////AUTO CLOSE THE WINDOW AFTER THE UPLOAD COMPLETED
/////////AUTO FOCUS ON THIS WINDOW

public class Post extends AppCompatActivity {

    //Declaring global variables
    private ProgressBar mProgressBar;
    private String problemType;
    private String imageUploadID;
    private Uri fileUri;
    private String fileUrl;   //Gotta be in "https://firebasestorage.google..." format. Otherwise, won't work

    //Declaring Firebase stuff
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask<UploadTask.TaskSnapshot> mUploadTask;

    LocationManager mLocationManager;
    LocationListener mLocationListener;
    double longitude, latitude;
    TextView mTextView;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the pop-up app screen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post);

        //Initialization
        Button uploadButton = findViewById(R.id.postButton);
        ImageView captureImageView = findViewById(R.id.postImageView);
        mProgressBar = findViewById(R.id.progressBar);
        mTextView = findViewById(R.id.postTextView);
        mContext = this;

        //Firebase initialization
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = firebaseStorage.getReference();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference();

        //There will be some local folder that the app will use to save cache
        final String imageLocalPathname = "/storage/emulated/0/photo.jpg";

        //Display captured image in the ImageView of the pop-up window
        File file = new File(imageLocalPathname);
        if(file.exists()) {
            Bitmap uploadBitmap = BitmapFactory.decodeFile(imageLocalPathname);
            captureImageView.setImageBitmap(uploadBitmap);
            fileUri = Uri.fromFile(file); /////////This is almost the same as pathname. Optimize!
            Log.d("Shilo", "Uri: " + Uri.fromFile(file) + "; Pathname: " + imageLocalPathname);
        } else {
            Log.e("Shilo", "Something's wrong, can't find the file path/name");
        }

        //Setting up SPINNER menu
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.problem_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner problemSpinnerMenu = findViewById(R.id.spinnerMenu);
        problemSpinnerMenu.setAdapter(adapter);
        problemSpinnerMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                problemType = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Shilo", "Nothing selected");
            }
        });

        ///1

        if (ContextCompat.checkSelfPermission(Post.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("aj", "Permission is not granted");
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    11 );
        }

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                500,
                10, locationListenerGPS);


        //Tie action to a button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadTask == null || !mUploadTask.isInProgress())
                startImageUpload();
                Log.d("Shilo", "Upload button pressed yo ");
            }
        });
    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            loc_func(location);

            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //////////This thing didn't work.. It returned NULL. Figure out whether you need it or not, cuz app works ok without it.
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void startImageUpload(){
        if(fileUri != null){
            ////////imageUploadID = System.currentTimeMillis() + "." + getFileExtension(fileUri);
            imageUploadID = "images/"+ System.currentTimeMillis() + ".jpg"; /////////Do you need "image/" and ".jpg" in the name??
            mUploadTask = mStorageReference.child(imageUploadID).putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Post.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            //This is to delay the progress bar reset, so the user can see that the bar reached FULL in onProgress
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            mStorageReference.child(imageUploadID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    fileUrl = uri.toString();
                                    UploadRecord upload = new UploadRecord(problemType, imageUploadID, fileUrl);
                                    mDatabaseReference.child("Posts" + mDatabaseReference.push().getKey()).setValue(upload);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Log.d("Shilo", "error");
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Post.this, "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress( (int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

   ///2

    private void loc_func(Location location){
        try{
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(latitude, longitude,1);
            String adminArea = addresses.get(0).getSubAdminArea();
            mTextView.setText("Location: " + adminArea);

        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error"+e,Toast.LENGTH_SHORT).show();
        }
    }

}
