package com.example.connect.ui.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connect.Callback.IAllInterestCallbackListener;
import com.example.connect.Callback.IUserInterestCallbackListener;
import com.example.connect.Common.Common;
import com.example.connect.MainActivity;
import com.example.connect.Model.InterestModel;
import com.example.connect.Model.UserInterestModel;
import com.example.connect.Model.UserModel;
import com.example.connect.R;
import com.example.connect.ui.Interest.InterestActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;

    GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    SignInButton signInButton;
    Button btn_continue;
    EditText edt_email;
    private DatabaseReference userRef;

    private IUserInterestCallbackListener iUserInterestCallbackListener;

    private android.app.AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        signInButton = findViewById(R.id.google_signIn);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        edt_email = findViewById(R.id.edt_email);
        btn_continue = findViewById(R.id.btn_continue);
        final String email = edt_email.getText().toString().trim();
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidEmail(edt_email.getText().toString().trim()) || TextUtils.isEmpty(edt_email.getText().toString()))
                    Toast.makeText(LoginActivity.this, "Please Enter Valid email", Toast.LENGTH_SHORT).show();


                else {
                    firebaseAuth.fetchSignInMethodsForEmail(edt_email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.getResult().getSignInMethods().isEmpty()) {
                                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                        intent.putExtra("email", edt_email.getText().toString());
                                        startActivity(intent);
                                    } else {
                                        Intent loginIntent = new Intent(LoginActivity.this, NewLoginActivity.class);
                                        loginIntent.putExtra("login_email", edt_email.getText().toString());
                                        startActivity(loginIntent);
                                    }
                                }
                            });
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        waitingDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        String provider = credential.getSignInMethod();
        //Toast.makeText(this, ""+ provider, Toast.LENGTH_SHORT).show();
        if (provider == "google.com" || provider == "") {
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = firebaseAuth.getCurrentUser();

                                userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                            if (!dataSnapshot.hasChild("interest")){
                                                goToInterestActivity(userModel);
                                            }
                                            else {
                                                goToMainActivity(userModel);
                                            }
                                            Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                                            goToMainActivity(userModel);

                                        } else {
                                            addDataToDatabase(user);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        else
        {
            Toast.makeText(this, "You have registered using Email", Toast.LENGTH_SHORT).show();
        }

    }

    private void addDataToDatabase(FirebaseUser user) {
        final UserModel userModel = new UserModel();
        userModel.setUid(user.getUid());
        userModel.setName(user.getDisplayName());
        userModel.setEmail(user.getEmail());
        userRef.child(user.getUid()).setValue(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoginActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        goToInterestActivity(userModel);
                    }
                });
    }

    private void goToMainActivity(UserModel userModel) {
        Common.currentUser = userModel;
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToInterestActivity(UserModel userModel){
        Common.currentUser = userModel;
        Intent intent = new Intent(LoginActivity.this, InterestActivity.class);
        startActivity(intent);
        finish();
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            checkUser(currentUser);
            waitingDialog.show();
        }
    }

    private void checkUser(FirebaseUser currentUser) {


        userRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (!dataSnapshot.hasChild("interest")){
                        goToInterestActivity(userModel);
                    }
                    else {
                        goToMainActivity(userModel);
                    }
                    //goToMainActivity(userModel);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    protected boolean isValidEmail(String edtEmail) {
        String USER_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(USER_PATTERN);
        Matcher matcher = pattern.matcher((CharSequence) edtEmail);
        return matcher.matches();
    }

}