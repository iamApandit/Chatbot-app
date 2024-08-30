package com.example.savaalkaro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText userNameEdt, pwdEdt;
    public Button loginBtn;
    private ProgressBar loadingPB;
    private FirebaseAuth mAuth;
    public TextView registerTV;
    private GoogleSignInClient mGoogleSignInClient;



    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Handle the result here
                            Intent data = result.getData();
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                            try {
                                task.getResult(ApiException.class);
                                navigateToMainActivity();

                            } catch (ApiException e) {
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameEdt = findViewById(R.id.idEDTUserName);
        pwdEdt = findViewById(R.id.idEdtPassword);
        loginBtn = findViewById(R.id.buttonLogin);
        loadingPB = findViewById(R.id.PBLoading);
        registerTV = findViewById(R.id.idTVRegister);
        ImageButton googleBtn = findViewById(R.id.imageButton);


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            navigateToMainActivity();
        }


        googleBtn.setOnClickListener(v -> signIn());

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        }


        registerTV.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);

        });

        loginBtn.setOnClickListener(v -> {
            loadingPB.setVisibility(View.VISIBLE);

            String userName = Objects.requireNonNull(userNameEdt.getText()).toString();
            String pwd = Objects.requireNonNull(pwdEdt.getText()).toString();

            if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd)) {
                Toast.makeText(LoginActivity.this, "Enter your credentials", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(userName, pwd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    loadingPB.setVisibility(View.GONE);
                                    mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();


                                } else {
                                    loadingPB.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();


                                }

                            }

                        });


            }

        });

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();

        }
    }

    private void navigateToMainActivity() {
        finish();
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }
}
