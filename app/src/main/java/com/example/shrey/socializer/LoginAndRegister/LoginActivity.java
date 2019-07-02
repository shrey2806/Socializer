package com.example.shrey.socializer.LoginAndRegister;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shrey.socializer.MainActivity;
import com.example.shrey.socializer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText lemail;
    private TextInputEditText lpassword;
    private Toolbar mToolbar;
    private Button logButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loginProgress;
    private DatabaseReference mUserReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();

        lemail=findViewById(R.id.log_email);
        lpassword=findViewById(R.id.log_password);
        mToolbar=findViewById(R.id.login_toolbar);
        logButton=findViewById(R.id.login1_btn);

        loginProgress=new ProgressDialog(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");

        mUserReference= FirebaseDatabase.getInstance().getReference().child("users");

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=lemail.getEditableText().toString();
                String password=lpassword.getEditableText().toString();

                if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){

                    loginProgress.setTitle(getString(R.string.LogginUser));
                    loginProgress.setMessage("Please wait for a while");
                    loginProgress.setCanceledOnTouchOutside(false);
                    loginProgress.show();
                    signInUser(email,password);


                }
            }
        });


    }

    private void signInUser(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    loginProgress.dismiss();


                    String deviceToken= FirebaseInstanceId.getInstance().getToken();

                    String  current_user_id=mAuth.getCurrentUser().getUid();


                    mUserReference.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //FirebaseUser user=mAuth.getCurrentUser();
                            Intent mainIntent=new Intent(LoginActivity .this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                            finish();

                        }
                    });


                }else{

                    loginProgress.hide();
                    Toast.makeText(LoginActivity.this,"Cannot Sign in. Please check your details",Toast.LENGTH_LONG).show();


                }

            }
        });
    }
}
