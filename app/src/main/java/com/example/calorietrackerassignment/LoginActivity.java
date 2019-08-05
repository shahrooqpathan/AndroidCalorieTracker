package com.example.calorietrackerassignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_login);
        final EditText emailText = findViewById(R.id.input_email);
        final EditText passwordText = findViewById(R.id.input_password);
        Button loginButton = findViewById(R.id.btn_login);
        TextView signUp = findViewById(R.id.link_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast toast = Toast.makeText(getApplicationContext(),"Log in Button working",Toast.LENGTH_SHORT);
                //toast.show();
                userLogin(emailText.getText().toString(), passwordText.getText().toString());
            }
        });

        //need to make one more thing for signup
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    public void userLogin(String email, String password) {
        //Log.d("test","What is this");
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        System.out.println(generatedPassword);


        if(!validate(email, password))
            return;
        LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
        loginAsyncTask.execute(email,generatedPassword);

    }

    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return RestClient.findByUsernamePassword(params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String userInfo) {
            final ProgressBar loginProgress = findViewById(R.id.loginProgressBar);
            loginProgress.setVisibility(View.VISIBLE);
            System.out.println(userInfo);
            Log.d("LOGIN",userInfo);

            if(!userInfo.equals("[]")  && !userInfo.equals("")){ // Checking for blank string in case of network error

                loginProgress.setVisibility(View.INVISIBLE);
                onLoginSuccess(userInfo);
            }
            else{
                loginProgress.setVisibility(View.INVISIBLE);
                Log.d("LOGIN","Login Failure");
                onLoginFailure();
            }


        }


    }

    public void onLoginSuccess(String userInfo) {
        Intent intent = new Intent(getApplicationContext(), UserOptions.class);
        intent.putExtra("LoggedUser",userInfo);
        startActivity(intent);
    }

    public void onLoginFailure() {
       Toast.makeText(getApplicationContext(),"Login Failure. Do you want to singup instead?",Toast.LENGTH_LONG).show();
    }


    public boolean validate(String email, String password) {
        boolean valid = true;

        final EditText emailText = findViewById(R.id.input_email);
        final EditText passwordText = findViewById(R.id.input_password);

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;


    }
}
