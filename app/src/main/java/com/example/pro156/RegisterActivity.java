package com.example.pro156;



import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;

import android.util.Log;

import android.view.View;

import android.widget.ArrayAdapter;

import android.widget.AdapterView;

import android.widget.Spinner;

import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;



import com.google.android.material.button.MaterialButton;

import com.google.android.material.textfield.TextInputEditText;

import com.google.android.material.textfield.TextInputLayout;



public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {



    private TextInputEditText registerEmailEditText, registerPasswordEditText;

    private TextInputLayout registerEmailTextInputLayout, registerPasswordTextInputLayout;

    private MaterialButton registerButton;

    private FirebaseAuth mAuth;

    private Spinner roleSpinner;

    private String selectedRole = "user"; // Default role



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);



        registerEmailEditText = findViewById(R.id.registerEmailEditText);

        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);

        registerEmailTextInputLayout = findViewById(R.id.registerEmailTextInputLayout);

        registerPasswordTextInputLayout = findViewById(R.id.registerPasswordTextInputLayout);

        registerButton = findViewById(R.id.registerButton);

        roleSpinner = findViewById(R.id.roleSpinner);



        mAuth = FirebaseAuth.getInstance();



// Set up the spinner for role selection

        String[] roles = {"User", "Admin"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        roleSpinner.setAdapter(adapter);

        roleSpinner.setOnItemSelectedListener(this);



        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                String email = registerEmailEditText.getText().toString();

                String password = registerPasswordEditText.getText().toString();



                if (TextUtils.isEmpty(email)) {

                    registerEmailTextInputLayout.setError("Please enter email");

                    return;

                } else {

                    registerEmailTextInputLayout.setError(null);

                }



                if (TextUtils.isEmpty(password)) {

                    registerPasswordTextInputLayout.setError("Please enter password");

                    return;

                } else {

                    registerPasswordTextInputLayout.setError(null);

                }



                mAuth.createUserWithEmailAndPassword(email, password)

                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override

                            public void onComplete(Task<AuthResult> task) {

                                if (task.isSuccessful()) {

// Registration successful

                                    Log.d("RegisterActivity", "Registration successful");



// Save user role in Firebase Realtime Database

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {

                                        String userId = user.getUid();

                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();



// Save the role in the database

                                        database.child("users").child(userId).child("role").setValue(selectedRole)

                                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    @Override

                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);

                                                            startActivity(intent);

                                                            finish();

                                                        } else {

                                                            Log.e("RegisterActivity", "Failed to save user role: " + task.getException());

                                                            Toast.makeText(RegisterActivity.this, "Failed to save user role", Toast.LENGTH_SHORT).show();

                                                        }

                                                    }

                                                });

                                    }

                                } else {

// Registration failed, handle error

                                    Exception e = task.getException();

                                    if (e != null) {

                                        Log.w("RegisterActivity", "Registration failed: " + e.getMessage());

                                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();

                                    }

                                }

                            }

                        });

            }

        });

    }



    @Override

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String[] roles = {"User", "Admin"};

        selectedRole = roles[position];

    }



    @Override

    public void onNothingSelected(AdapterView<?> parent) {

// Handle case where no role is selected

    }

}

