package telecom.marcus.registrologinbase;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
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

    private AppCompatEditText name, email, password, c_password;
    private TextInputLayout txtLayout_name, txtLayout_email, txtLayout_password, txtLayout_c_password;
    private FloatingActionButton btn_regist;
    private ProgressBar loading;
    private static String URL_REGIST = "http://192.168.2.120/bd_users/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loading = findViewById(R.id.loading);
        name = findViewById(R.id.name);
        txtLayout_name = findViewById(R.id.txtLayout_name);
        email = findViewById(R.id.email);
        txtLayout_email = findViewById(R.id.txtLayout_email);
        password = findViewById(R.id.password);
        txtLayout_password = findViewById(R.id.txtLayout_password);
        c_password = findViewById(R.id.c_password);
        txtLayout_c_password = findViewById(R.id.txtLayout_c_password);
        btn_regist = findViewById(R.id.btn_regist);

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    Regist();
                }
            }
        });
    }

    private boolean validateForm() {

        Boolean aBoolean = null;

        if (c_password.getText().toString().isEmpty()) {
            txtLayout_c_password.setErrorEnabled(true);
            txtLayout_c_password.setError("Confirm Password");
            aBoolean = false;
        } else {
            txtLayout_c_password.setErrorEnabled(false);
            if (!password.getText().toString().equals(c_password.getText().toString())) {
                txtLayout_password.setErrorEnabled(true);
                txtLayout_c_password.setErrorEnabled(true);
                txtLayout_c_password.setError("Password not Confirm");
            } else {
                txtLayout_password.setErrorEnabled(false);
                txtLayout_c_password.setErrorEnabled(false);
                aBoolean = true;
            }
        }

        if (name.getText().toString().isEmpty()) {
            txtLayout_name.setErrorEnabled(true);
            txtLayout_name.setError("Input your Name");
            aBoolean = false;
        } else {
            txtLayout_name.setErrorEnabled(false);
        }

        if (email.getText().toString().isEmpty()) {
            txtLayout_email.setErrorEnabled(true);
            txtLayout_email.setError("Input your Email");
            aBoolean = false;
        } else {
            txtLayout_email.setErrorEnabled(false);
        }

        if (password.getText().toString().isEmpty()) {
            txtLayout_password.setErrorEnabled(true);
            txtLayout_password.setError("Input your Password");
            aBoolean = false;
        } else {
            txtLayout_password.setErrorEnabled(false);
        }

        return aBoolean;

    }

    private void Regist() {
        loading.setVisibility(View.VISIBLE);
        // btn_regist.setVisibility(View.GONE);

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

                            if (success.equals("1")) {
                                Toast.makeText(RegisterActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Register Error!" + e.toString(), Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                            // btn_regist.setVisibility(View.VISIBLE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Register Error!" + error.toString(), Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                        // btn_regist.setVisibility(View.VISIBLE);
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
}
