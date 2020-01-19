package com.apk.sagitsri.help4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_Registration extends AppCompatActivity {

    TextInputEditText fullnameTxt,usernameTxt,mobileTxt,passwordTxt,confirmpassTxt;
    Button registerBtn;
    SharedPreferences preferences;
    String allmob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__registration);

        fullnameTxt = findViewById(R.id.fullnameEditText);
        usernameTxt = findViewById(R.id.usernameEditText);
        mobileTxt = findViewById(R.id.mobnoEditText);
        passwordTxt = findViewById(R.id.passwordEditText);
        confirmpassTxt = findViewById(R.id.confirmpasswordEditText);
        registerBtn = findViewById(R.id.registerTextView);
        registerBtn.setEnabled(false);

//        preferences = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//        allmob = preferences.getString("moball",null);
//        Toast.makeText(this,"fragmob: "+allmob.toString(),Toast.LENGTH_LONG).show();

        confirmpassTxt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String strPass1 = passwordTxt.getText().toString();
                String strPass2 = confirmpassTxt.getText().toString();
                if (strPass1.equals(strPass2)) {
                    Toast.makeText(getApplicationContext(), "same", Toast.LENGTH_SHORT).show();
                    registerBtn.setEnabled(true);

                } else {
                    confirmpassTxt.setError("Password not matching!");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vdate();
            }
        });

    }

    public void vdate() {
        String fullname= fullnameTxt.getText().toString().trim();
        String username = usernameTxt.getText().toString().trim();
        String mobileno = mobileTxt.getText().toString().trim();
        String passwd = passwordTxt.getText().toString().toLowerCase().trim();
        String cpasswd = confirmpassTxt.getText().toString().toLowerCase().trim();
        Toast.makeText(this,"clicked",Toast.LENGTH_LONG).show();
        if (fullname.length() == 0) {
            fullnameTxt.setError("Name should not empty");
        }
        if(username.length() == 0) {
            usernameTxt.setError("username is empty");
        }
        if(mobileno.length() < 10 || mobileno.length() > 10) {
            mobileTxt.setError("Invalid mobile no");
        }
        if(passwd.length() == 0){
            passwordTxt.setError("password is empty");
        }
        if(cpasswd.length() == 0){
            confirmpassTxt.setError("confirm password is empty");
        }
        saveit();
    }


    private void saveit() {
        final String S_URL = "http://pyky.000webhostapp.com/Help4U/signin.php";
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting to Server...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest postRequest = new StringRequest(Request.Method.POST, S_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();

                        Toast.makeText(Activity_Registration.this, response.toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(response.toString());


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("ErrorResponse", error.getMessage());
                        Toast.makeText(Activity_Registration.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();


                params.put("name", fullnameTxt.getText().toString());
                params.put("uname", usernameTxt.getText().toString());
                params.put("mob", mobileTxt.getText().toString());
                params.put("psd", confirmpassTxt.getText().toString());

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);

    }

}




