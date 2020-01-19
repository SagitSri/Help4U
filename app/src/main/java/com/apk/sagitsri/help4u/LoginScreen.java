package com.apk.sagitsri.help4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.R.layout.simple_spinner_item;

public class LoginScreen extends AppCompatActivity {
    Button login;

    TextInputEditText emailTxt,passwordTxt;
    TextView login_skiptxt;

    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    Context context;
    private String result;
    private static String URL  ="http://pyky.000webhostapp.com/Help4U/login.php";
    SharedPreferences.Editor editor,remedit;
    SharedPreferences preferences,remme;
    Boolean checkrem;
    public CheckBox rem;
    private String username,password;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        context = this;

        setTitle("Login");

        requestPermission();

        emailTxt = findViewById(R.id.emailEditText);
        passwordTxt = findViewById(R.id.passwordEditText);
        login_skiptxt = findViewById(R.id.skipTextView);
        rem = findViewById(R.id.remme);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        progressDialog = new ProgressDialog(getApplicationContext());

        preferences = getApplicationContext().getSharedPreferences("MyPref",0);
        editor = preferences.edit();

        remme = getApplicationContext().getSharedPreferences("loginpref",0);
        remedit = remme.edit();

        checkrem = remme.getBoolean("saveLogin", false);

        if (checkrem == true) {
            emailTxt.setText(remme.getString("username", ""));
            passwordTxt.setText(remme.getString("password", ""));
            rem.setChecked(true);
        }




        login_skiptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerintent = new Intent(LoginScreen.this,Activity_Registration.class);
                startActivity(registerintent);
            }
        });
    }

    public void toDashboard(View view) {
        login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailTxt.getText().toString().trim().length() == 0 && passwordTxt.getText().toString().trim().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "UserName and Password is empty", Toast.LENGTH_SHORT).show();
                }
//                if(passwordTxt.getText().toString().trim().length() == 0)
//                {
//                    Toast.makeText(getApplicationContext(), "password is empty", Toast.LENGTH_SHORT).show();
//                }

                if(emailTxt.getText().length()>0 && passwordTxt.getText().length()>0) {
                    username = emailTxt.getText().toString();
                    password = passwordTxt.getText().toString();
                    if (rem.isChecked()) {
                        remedit.putBoolean("saveLogin", true);
                        remedit.putString("username", username);
                        remedit.putString("password", password);
                        remedit.commit();
                    } else {
                        remedit.clear();
                        remedit.commit();
                    }
                    loginRequest();

                }
            }
        });
    }


    // Method to Call Volley Network Library to Check user and password in Server
    private void loginRequest()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in you...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {

                            JSONObject obj = new JSONObject(response);



                            JSONArray dataArray = obj.getJSONArray("result");

                            for (int i = 0; i < dataArray.length(); i++) {


                                JSONObject dataobj = dataArray.getJSONObject(i);

                                Integer res = Integer.parseInt(dataobj.getString("res"));
                                if(res == 1){
                                    editor.putString("moball",dataobj.getString("mob"));
                                    editor.commit();
                                    String spres = preferences.getString("moball",null);
                                    Toast.makeText(LoginScreen.this,spres,Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginScreen.this,DashboardScreen.class));
                                    finish();
                                }
                                else if(res == 0){
                                    Toast.makeText(LoginScreen.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(LoginScreen.this,"Oops! something went wrong!",Toast.LENGTH_LONG).show();
                                }


                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();

                params.put("username",emailTxt.getText().toString());
                params.put("password",passwordTxt.getText().toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginScreen.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE, CALL_PHONE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean wrES = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean pCal = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && wrES && pCal)
                        Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(this,"Permission Denied, App needs these permissions!",Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE, CALL_PHONE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginScreen.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
