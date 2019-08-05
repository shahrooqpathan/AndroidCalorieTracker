package com.example.calorietrackerassignment;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {


    EditText emailText;
    EditText passwordText;
    EditText firstNameText;
    EditText lastNameText;
    RadioGroup genderOptions;
    RadioButton genderFemale;
    EditText DOBText;
    EditText heightText;
    EditText weightText;
    Spinner levelOfActivity;
    EditText stepsPerMileText;
    EditText addressText;
    EditText postCodeText;
    Button signUpButton;
    TextView loginLink;
    final Calendar myCalendar = Calendar.getInstance();

    //private int registeredUserCount;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //initializing and setting listener for sign up button
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        firstNameText = findViewById(R.id.input_first_name);
        lastNameText = findViewById(R.id.input_last_name);
        genderOptions = findViewById(R.id.gender_radio_group);
        genderFemale = findViewById(R.id.radio_btn_female);
        DOBText = findViewById(R.id.input_dob);
        heightText = findViewById(R.id.input_height);
        weightText = findViewById(R.id.input_weight);
        levelOfActivity = findViewById(R.id.spinner_activity_level);
        stepsPerMileText = findViewById(R.id.input_steps_per_mile);
        addressText = findViewById(R.id.input_address);
        postCodeText = findViewById(R.id.input_postcode);

        loginLink = findViewById(R.id.link_login);
        signUpButton = findViewById(R.id.btn_signup);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        DOBText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SignupActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    registerNewUser();
                //validate();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finish this activity and go back to the login page
                finish();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        DOBText.setText(sdf.format(myCalendar.getTime()));
    }

    public void registerNewUser() {
        PostRegistrationAsyncTask postRegistrationAsyncTask = new PostRegistrationAsyncTask();
        postRegistrationAsyncTask.execute();

    }

    private class PostRegistrationAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String enteredEmail = getTextFromField(emailText);
            String enteredPassword = getTextFromField(passwordText);
            String enteredFirstName = getTextFromField(firstNameText);
            String enteredLastName = getTextFromField(lastNameText);
            String selectedgender = (genderOptions.getCheckedRadioButtonId() == R.id.radio_btn_male ? "M" : "F");

            String enteredDOB = getTextFromField(DOBText);
            String enteredHeight = getTextFromField(heightText);
            String enteredWeight = getTextFromField(weightText);
            String selectedActivityLevel = levelOfActivity.getSelectedItem().toString();
            String enteredStepPerMile = getTextFromField(stepsPerMileText);
            String enteredAddress = getTextFromField(addressText);
            String enteredPostCode = getTextFromField(postCodeText);
            //DateTimeFormatter format = DateTimeFormatter.ISO_DATE_TIME;

            try {
                // Create MessageDigest instance for MD5
                MessageDigest md = MessageDigest.getInstance("MD5");
                //Add password bytes to digest
                md.update(enteredPassword.getBytes());
                //Get the hash's bytes
                byte[] bytes = md.digest();
                //This bytes[] has bytes in decimal format;
                //Convert it to hexadecimal format
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                //Get complete hashed password in hex format
                enteredPassword = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            String userId = Integer.toString(Integer.parseInt(RestClient.getRegisteredUserCount()) + 1);
            AppUser newUser = new AppUser(userId,
                    enteredFirstName,
                    enteredLastName,
                    enteredEmail,
                    //new SimpleDateFormat("dd-MM-yyyy").parse("10-08-1992"),
                    enteredDOB + "T00:00:00+10:00",
                    enteredHeight,
                    enteredWeight,
                    enteredAddress,
                    selectedgender,
                    selectedActivityLevel,
                    enteredPostCode,
                    enteredStepPerMile);
            //Setting the Sign up date
            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

            Credential newUserCredenial = new Credential(newUser, enteredPassword, sdf.format(new Date()) + "T00:00:00+10:00", userId, newUser);
            String response = RestClient.registerUser(newUser);
            if (!response.equals("500")) {
                RestClient.registerUserCredential(newUserCredenial);
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isRegistered) {
            if (isRegistered) {
                Toast.makeText(getApplicationContext(), "Successfully Registered your account", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_LONG).show();
                emailText.setError("Email already exists");
                emailText.requestFocus();
            }
        }
    }


    public String getTextFromField(EditText e) {
        //Returns the entered Text;
        return e.getText().toString();
    }

    public boolean validate() {
        boolean signUpValuesValid = true;
        //Getting all the entered text for validation
        String enteredEmail = getTextFromField(emailText);
        String enteredPassword = getTextFromField(passwordText);
        String enteredFirstName = getTextFromField(firstNameText);
        String enteredLastName = getTextFromField(lastNameText);
        String selectedgender = (genderOptions.getCheckedRadioButtonId() == R.id.radio_btn_male ? "M" : "F");

        String enteredDOB = getTextFromField(DOBText);
        String enteredHeight = getTextFromField(heightText);
        String enteredWeight = getTextFromField(weightText);
        String selectedActivityLevel = levelOfActivity.getSelectedItem().toString();
        String enteredStepPerMile = getTextFromField(stepsPerMileText);
        String enteredAddress = getTextFromField(addressText);
        String enteredPostCode = getTextFromField(postCodeText);


        //validating email
        if (enteredEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(enteredEmail).matches()) {
            emailText.setError("please enter a valid email address");
            signUpValuesValid = false;
        } else {
            emailText.setError(null);
        }
        //validating first name
        if (enteredFirstName.length() < 2 || enteredFirstName.isEmpty()) {
            firstNameText.setError("please enter a valid first name");
            signUpValuesValid = false;
        } else {
            firstNameText.setError(null);
        }
        //validating password
        if (enteredPassword.length() < 4 || enteredFirstName.isEmpty()) {
            passwordText.setError("please enter a password with atleast 4 characters");
            signUpValuesValid = false;
        } else {
            passwordText.setError(null);
        }
        //validating last name
        if (enteredLastName.isEmpty()) {
            lastNameText.setError("please enter a valid last name");
            signUpValuesValid = false;
        } else {
            lastNameText.setError(null);
        }


        //validating gender
        if (selectedgender.isEmpty()) {
            genderFemale.setError("please select a gender");
            signUpValuesValid = false;
        } else {
            genderFemale.setError(null);
        }

        //validating height
        if (enteredHeight.isEmpty()) {
            heightText.setError("please enter your height");
            signUpValuesValid = false;
        } else {
            heightText.setError(null);
        }

        //validating weight
        if (enteredWeight.isEmpty()) {
            weightText.setError("please enter your weight");
            signUpValuesValid = false;
        } else {
            weightText.setError(null);
        }

        //validating steps per mile
        if (enteredStepPerMile.isEmpty()) {
            stepsPerMileText.setError("please enter your weight");
            signUpValuesValid = false;
        } else {
            stepsPerMileText.setError(null);
        }

        //validating address
        if (enteredAddress.isEmpty()) {
            addressText.setError("please enter your address");
            signUpValuesValid = false;
        } else {
            addressText.setError(null);
        }

        //validating entered postcode
        if (enteredPostCode.isEmpty() || enteredPostCode.length() != 4) {
            postCodeText.setError("please enter a valid postcode of 4 digits");
            signUpValuesValid = false;
        } else {
            postCodeText.setError(null);
        }

        //validating FOB

        if (enteredDOB.isEmpty()) {
            DOBText.setError("please enter your valid date of birth");
            signUpValuesValid = false;
        } else {
            DOBText.setError(null);
        }


        return signUpValuesValid;
    }
}
