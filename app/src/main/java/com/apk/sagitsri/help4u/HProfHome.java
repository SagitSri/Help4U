package com.apk.sagitsri.help4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_item;


/**
 * Created by sagitsri on 2/11/19.
 */

public class HProfHome extends Fragment implements View.OnClickListener{

    public EditText grp,gluc,chol,ht,wt,dis;
    public Button btnedit,btnsave;
    String sgrp,sgluc,schol,sht,swt,sdis;
    SharedPreferences preferences;
    String allmob;

    public static String AssetJSONFile (String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.hprof_home, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        //getActivity().setTitle("Menu 1");

        grp = view.findViewById(R.id.hprobldgrp);
        gluc = view.findViewById(R.id.hprobldglu);
        chol = view.findViewById(R.id.hprochol);
        ht = view.findViewById(R.id.hproheight);
        wt = view.findViewById(R.id.hproweight);
        dis = view.findViewById(R.id.hprodis);

        btnedit = view.findViewById(R.id.btnedit);
        btnsave = view.findViewById(R.id.btnsave);

        btnedit.setOnClickListener(this);
        btnsave.setOnClickListener(this);

        preferences = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        allmob = preferences.getString("moball",null);
        Toast.makeText(getActivity(),"fragmob: "+allmob.toString(),Toast.LENGTH_LONG).show();

    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnedit){
            editit();
        }
        else if (v.getId() == R.id.btnsave) {

            if (grp.getText().length() > 0 && gluc.getText().length() > 0 && chol.getText().length() > 0 && ht.getText().length() > 0 && wt.getText().length() > 0 && dis.getText().length() > 0) {
                Toast.makeText(v.getContext(), "Action Save", Toast.LENGTH_SHORT).show();
                saveit();

            } else {
                Toast.makeText(v.getContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void editit() {
        final String S_URL = "http://pyky.000webhostapp.com/Help4U/hprof.php?";
        final ProgressDialog progressDialog;
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Connecting to Server...");
//        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());


        StringRequest postRequest = new StringRequest(Request.Method.POST, S_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressDialog.hide();

//                        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(response.toString());

                        try {

                            JSONObject obj = new JSONObject(response.toString());



                            JSONArray dataArray = obj.getJSONArray("res");

                            for (int i = 0; i < dataArray.length(); i++) {


                                JSONObject dataobj = dataArray.getJSONObject(i);

                                grp.setText(dataobj.getString("grp"));
                                gluc.setText(dataobj.getString("gluc"));
                                chol.setText(dataobj.getString("chol"));
                                ht.setText(dataobj.getString("ht"));
                                wt.setText(dataobj.getString("wt"));
                                dis.setText(dataobj.getString("dis"));



                            }






                        } catch (JSONException e) {
                            e.printStackTrace();
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



                params.put("mob", allmob);
                params.put("eRs","edit");

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

    // Volley Network method to send values to server
    private void saveit() {
        final String S_URL = "http://pyky.000webhostapp.com/Help4U/hprof.php?";
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

                        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(response.toString());

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


                params.put("grp", grp.getText().toString());
                params.put("gluc", gluc.getText().toString());
                params.put("chol", chol.getText().toString());
                params.put("ht", ht.getText().toString());
                params.put("wt", wt.getText().toString());
                params.put("dis", dis.getText().toString());
                params.put("mob", allmob);
                params.put("eRs","save");

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }
}
