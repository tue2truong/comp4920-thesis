package com.example.tue2t.iotdevicelist.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tue2t.iotdevicelist.Models.Device;
import com.example.tue2t.iotdevicelist.MySingleton;
import com.example.tue2t.iotdevicelist.R;

import java.io.InputStream;
import java.net.URL;


/*
    Camera Activity
 */
public class    ThirdActivity extends AppCompatActivity {

    // UI
    Toolbar tb;
    Button get_btn, stream_btn, gallery_btn;
    ImageView img;

    // device info
    private String deviceID;

    // gallery
    int counter = 0;
    final static int RESULT_LOAD_IMG = 1;

    // network variable
    String ipAdr;
    String port;
    final static int MY_SOCKET_TIME_OUT = 10000;


    class GetImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            }
            catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            img.setImageBitmap(bitmap);
            img.setVisibility(View.VISIBLE);

            // save image to gallery
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "photo_" + String.valueOf(counter), "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // init
        tb = (Toolbar) findViewById(R.id.toolbar);
        img = (ImageView) findViewById(R.id.imageView);
        get_btn = (Button) findViewById(R.id.button_get);
        stream_btn = (Button) findViewById(R.id.button_stream);
        gallery_btn = (Button) findViewById(R.id.button_select);

        // get info from previous activity
        Device device = (Device) getIntent().getSerializableExtra("DeviceObject");
        if(device != null) {
            tb.setTitle(device.getDeviceName());
            deviceID = device.getDeviceID();
            ipAdr = device.getDeviceIP();
            port = device.getDevicePort();
        }

        getImageEvent();
        getStreamEvent();
        openGalleryEvent();

    }

    // streaming event
    private void getStreamEvent() {
        stream_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://"+ ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?stream=120";
                        Log.d("Test", url);
                        StringRequest imgReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    response = response.replace("\"", "");

                                    Log.d("Test", response);

                                    // open stream in browser
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(response));
                                    startActivity(i);
                                }
                                catch (Exception e) {
                                    Log.d("Error", "Failed to retrieve video");
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ThirdActivity.this, "Failed to get video URL", Toast.LENGTH_SHORT).show();
                                Log.d("Error", "Failed to get image URL");
                                error.printStackTrace();
                            }
                        });
                        imgReq.setRetryPolicy(new DefaultRetryPolicy(
                                MY_SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        ));
                        MySingleton.getInstance(ThirdActivity.this).addToRequestQueue(imgReq);

                    }
                }).start();

            }
        });
    }

    // take photo event
    private void getImageEvent() {
        get_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://"+ ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?photo=true";
                        Log.d("Test", url);
                        StringRequest imgReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    response = response.replace("\"", "");

                                    // perform download image task in background
                                    new GetImage().execute(response);
                                }
                                catch (Exception e) {
                                    Log.d("Error", "Failed to retrieve image");
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ThirdActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                Log.d("Error", "Failed to get image URL");
                                error.printStackTrace();
                            }
                        });
                        imgReq.setRetryPolicy(new DefaultRetryPolicy(
                            MY_SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        ));
                        MySingleton.getInstance(ThirdActivity.this).addToRequestQueue(imgReq);

                    }
                }).start();
            }
        });
    }

    // open gallery event
    private void openGalleryEvent() {
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPhotoIntent = new Intent(Intent.ACTION_PICK);
                selectPhotoIntent.setType("image/*");
                startActivityForResult(selectPhotoIntent, RESULT_LOAD_IMG);
            }
        });

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img.setImageBitmap(selectedImage);
                img.setVisibility(View.VISIBLE);
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ThirdActivity.this, "File not Found Exception", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(ThirdActivity.this, "You have not pick an image", Toast.LENGTH_LONG).show();
        }
    }
}
