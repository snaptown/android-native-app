package com.example.iange_000.snapdupka;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {







    ImageView photoView;

    Context context = this;
    Bitmap photo;

    TextView comment;
    Location currentLocation;
    Button sendButton;
    File file;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = Activity.class.getSimpleName();
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;
    String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button  = (Button) findViewById(R.id.photoButton);
        photoView = (ImageView) findViewById(R.id.photoView);
        photoView.setImageResource(R.drawable.log);
        comment = (TextView) findViewById(R.id.textField);
        sendButton = (Button) findViewById(R.id.sendButton);
        //Check if the user is logged

            SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            name = sharedPref.getString("username", "");


        if(name.isEmpty()) {

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);

            }



        //prompt user to enable gps
        if (((LocationManager) context.getSystemService(Context.LOCATION_SERVICE))
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        } else Utils.displayPromptForEnablingGPS(this);
        //LOCATION TEST CODE

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



    }


    public void launchCamera(View view){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        file = photo;
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView = (ImageView) findViewById(R.id.photoView);
                    ContentResolver cr = getContentResolver();

                    try {
                        photo = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        imageView.setImageBitmap(photo);
                        sendButton.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }






    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            // Blank for a moment...
        }
        else {
            handleNewLocation(location);
        };
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        currentLocation = location;
           }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
   /* Thread thread = new Thread(new Runnable(){
        @Override
        public void run(){

            String lat = Double.toString(currentLocation.getLatitude());
            String lon = Double.toString(currentLocation.getLongitude());
            Log.i(TAG, "Thread started");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://b20db94c.ngrok.io/snaptown/photos");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            try {

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setCharset(MIME.UTF8_CHARSET);

                httppost.addHeader("Accept", "application/json");

                if (comment.getText().toString() != null)
                    builder.addTextBody("comment", comment.getText().toString(), ContentType.create("text/plain", MIME.UTF8_CHARSET));
                if (name != null)
                    builder.addTextBody("username", name, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                if (lat != null)
                    builder.addTextBody("lat", lat, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                if (lon != null)
                    builder.addTextBody("lon", lon, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                if (file != null)
                    builder.addBinaryBody("PHOTO", file, ContentType.MULTIPART_FORM_DATA, file.getName());

                httppost.setEntity(builder.build());
                HttpResponse response = httpclient.execute(httppost);
                Log.i(TAG, "request sent");






            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        }
    });*/


    public void sendData(View view){
        new Thread(new Runnable(){
            @Override
            public void run(){

                String lat = Double.toString(currentLocation.getLatitude());
                String lon = Double.toString(currentLocation.getLongitude());
                Log.i(TAG, "Thread started");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("https://b20db94c.ngrok.io/snaptown/photos");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                try {

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setCharset(MIME.UTF8_CHARSET);

                    httppost.addHeader("Accept", "application/json");

                    if (comment.getText().toString() != null)
                        builder.addTextBody("comment", comment.getText().toString(), ContentType.create("text/plain", MIME.UTF8_CHARSET));
                    if (name != null)
                        builder.addTextBody("username", name, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                    if (lat != null)
                        builder.addTextBody("lat", lat, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                    if (lon != null)
                        builder.addTextBody("lon", lon, ContentType.create("text/plain", MIME.UTF8_CHARSET));
                    if (file != null)
                        builder.addBinaryBody("PHOTO", file, ContentType.MULTIPART_FORM_DATA, file.getName());

                    httppost.setEntity(builder.build());
                    HttpResponse response = httpclient.execute(httppost);
                    Log.i(TAG, "request sent");

                    int statuscode =response.getStatusLine().getStatusCode();
                    if(statuscode == 200) {
                                 MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Picture Uploaded!", Toast.LENGTH_SHORT).show();
                                 comment.setText("");
                                photoView.setImageResource(R.drawable.log);
                            }
                        });
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Error uploading picture!", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
            }
        }).start();
        sendButton.setVisibility(View.INVISIBLE);

        Toast.makeText(this,"Sending Image....", Toast.LENGTH_SHORT)
                .show();

    }
    public void logout(View view){
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("username","");
        editor.putString("password","");
        editor.apply();
         Intent intent = new Intent(this, LoginActivity.class);
         startActivity(intent);
    }
}
