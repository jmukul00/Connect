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
import com.example.connect.MainActivity;
import com.example.connect.Model.UserModel;
import com.example.connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class NewLoginActivity extends AppCompatActivity {

    EditText edt_login_email, edt_login_password;
    Button btn_login;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private android.app.AlertDialog waitingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        Intent loginIntent = getIntent();
        String login_email = loginIntent.getStringExtra("login_email");
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        mAuth = FirebaseAuth.getInstance();
        edt_login_email = findViewById(R.id.edt_login_email);

        edt_login_password = findViewById(R.id.edt_login_password);
        edt_login_email.setText(login_email);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edt_login_password.getText().toString())){
                    Toast.makeText(NewLoginActivity.this, "Enter the password", Toast.LENGTH_SHORT).show();
                }
                loginUser();
            }
        });
    }

    private void loginUser() {
        waitingDialog.show();
        String email = edt_login_email.getText().toString().trim();
        String password = edt_login_password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                        goToMainActivity(userModel);
                                        Toast.makeText(NewLoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(NewLoginActivity.this, "" + "Please Try To Register First", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(NewLoginActivity.this, "" + "Check your password ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToMainActivity(UserModel userModel) {
        Common.currentUser = userModel;
        Intent intent = new Intent(NewLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}