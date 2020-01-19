package com.apk.sagitsri.help4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;

import org.oscim.core.GeoPoint;

import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;


/**
 * Created by sagitsri on 24/10/19.
 */

public class DboardHome extends Fragment {

    CardView c1,c2,c3;
    Thread thread ;
    public final static int QRcodeWidth = 350 ;
    Bitmap bitmap;
    private SimpleLocation location;
    SharedPreferences preferences;
    String allmob;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.dboard_home, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        //getActivity().setTitle("Menu 1");

        preferences = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        allmob = preferences.getString("moball",null);
        Toast.makeText(getActivity(),"fragmob: "+allmob.toString(),Toast.LENGTH_LONG).show();

        // construct a new instance of SimpleLocation
        location = new SimpleLocation(getActivity());

        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(getActivity());
        }

        c1 = getActivity().findViewById(R.id.idCardView1);
        c2 = getActivity().findViewById(R.id.idCardView2);
        c3 = getActivity().findViewById(R.id.idCardView3);





        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDoctor();
            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLocation();
            }
        });

//        c3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scanQRCode();
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        location.beginUpdates();
    }

    @Override
    public void onPause() {
        location.endUpdates();
        super.onPause();
    }

    public void callDoctor(){

        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:6374023685"));
        startActivity(intent);
    }

    public void shareLocation(){
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        Toast.makeText(getActivity(),"lat:"+latitude+"&lon:"+longitude,Toast.LENGTH_LONG).show();
        NotifyAdmin(new GeoPoint(latitude,longitude));
    }

    public void scanQRCode(){

        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    // Volley Network method to send values to server
    private void NotifyAdmin(GeoPoint p) {
        final GeoPoint s = p;
        final String S_URL = "http://pyky.000webhostapp.com/Help4U/notify.php?";
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Connecting to Server...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());


        StringRequest postRequest = new StringRequest(Request.Method.POST, S_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();

                        Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_LONG).show();
                        if (Integer.parseInt(response.toString()) == 1) {

                            Toast.makeText(getActivity(), "Shared Successfully!", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", error.getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();


                params.put("lat", Double.toString(s.getLatitude()));
                params.put("lon", Double.toString(s.getLongitude()));
                params.put("mob", allmob);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }



}

