package com.example.connect.ui.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connect.Common.Common;
import com.example.connect.Model.UserModel;
import com.example.connect.R;
import com.example.connect.ui.Interest.InterestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    EditText edt_register_email, edt_register_name, edt_register_password;
    Button btn_lets_go;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private android.app.AlertDialog waitingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent registerIntent = getIntent();
        String register_email = registerIntent.getStringExtra("email");
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        btn_lets_go = findViewById(R.id.btn_letsGo);
        mAuth = FirebaseAuth.getInstance();
        edt_register_email = findViewById(R.id.edt_register_email);
        edt_register_name = findViewById(R.id.edt_register_name);
        edt_register_password = findViewById(R.id.edt_register_password);
        edt_register_email.setText(register_email);
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);

        btn_lets_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edt_register_name.getText().toString()))
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();

                else if (TextUtils.isEmpty(edt_register_password.getText().toString()))
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();

                else {
                    registerUser();
                }
            }
        });






    }

    private void registerUser() {
        waitingDialog.show();
        String email = edt_register_email.getText().toString().trim();
        String password = edt_register_password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            addDataToDatabase(user);
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "" + "Please try login with Google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addDataToDatabase(FirebaseUser user) {
        String name = edt_register_name.getText().toString().trim();
        final UserModel userModel = new UserModel();
        userModel.setUid(user.getUid());
        userModel.setName(name);
        userModel.setEmail(user.getEmail());
        userRef.child(user.getUid()).setValue(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        goToInterestActivity(userModel);
                    }
                });
    }

    private void goToInterestActivity(UserModel userModel) {
        Common.currentUser = userModel;
        Intent intent = new Intent(RegisterActivity.this, InterestActivity.class);
        startActivity(intent);
        finish();
    }


}