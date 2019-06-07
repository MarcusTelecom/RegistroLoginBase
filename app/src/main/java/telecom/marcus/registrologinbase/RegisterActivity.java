package telecom.marcus.registrologinbase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatEditText name, email, password, c_password;
    private TextInputLayout txtLayout_name, txtLayout_email, txtLayout_password, txtLayout_c_password;
    private FloatingActionButton btn_regist;
    private ProgressBar loading;
    private Intent intent;
    private String extraName, extraEmail, getId;
    private Boolean newUser;
    SessionManager sessionManager;
    private static String URL_REGIST = "http://192.168.2.120/bd_users/register.php";
    private static String URL_EDIT = "http://192.168.2.120/bd_users/edit_detail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolbar_logout_register);
        setSupportActionBar(toolbar);


        loading = findViewById(R.id.loading);
        name = findViewById(R.id.name);
        txtLayout_name = findViewById(R.id.layout_name);
        email = findViewById(R.id.email);
        txtLayout_email = findViewById(R.id.layout_email);
        password = findViewById(R.id.password);
        txtLayout_password = findViewById(R.id.layout_password);
        c_password = findViewById(R.id.c_password);
        txtLayout_c_password = findViewById(R.id.layout_c_password);
        btn_regist = findViewById(R.id.btn_regist);

        intent = getIntent();
        newUser = intent.getBooleanExtra("newUser", true);
        if (newUser) {
            getSupportActionBar().setTitle(R.string.txt_bt_register);
        } else {
            extraName = intent.getStringExtra("name");
            extraEmail = intent.getStringExtra("email");
            getId = intent.getStringExtra("id");
            name.setText(extraName);
            email.setText(extraEmail);
            getSupportActionBar().setTitle(R.string.txt_edit_user);
        }


        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    if (newUser) {
                        Regist();
                    } else {
                        SaveEditDetail();
                    }
                }
                finish();
            }
        });
    }

    private void Regist() {
        loading.setVisibility(View.VISIBLE);
        btn_regist.setEnabled(false);

        final String name = this.name.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");

                            if (success.equals("1")) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            } else if (success.equals("2")) {
                                Toast.makeText(getApplicationContext(), "Error!!! \n" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Register Error!" + e.toString(), Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setEnabled(false);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Register Error!" + error.toString(), Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void SaveEditDetail() {

        sessionManager = new SessionManager(this);

        final String name = this.name.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String id = getId;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                sessionManager.createSession(name, email, id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("id", id);

                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private boolean validateForm() {

        Boolean aBoolean = null;

        if (name.getText().toString().isEmpty()) {
            name.setError("Please insert Name");
            aBoolean = false;
        } else if (email.getText().toString().isEmpty()) {
            email.setError("Please insert Email");
            aBoolean = false;
        } else if (password.getText().toString().isEmpty()) {
            password.setError("Please insert Password");
            aBoolean = false;
        } else if (c_password.getText().toString().isEmpty()) {
            c_password.setError("Please Confirm Password");
            aBoolean = false;
        } else if (!password.getText().toString().equals(c_password.getText().toString())) {
            c_password.setError("Please Confirm Password");
            aBoolean = false;
        } else {
            aBoolean = true;
        }

        return aBoolean;

    }
}
