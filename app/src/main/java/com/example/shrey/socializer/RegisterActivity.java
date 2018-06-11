package com.example.shrey.socializer;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText tUsername;
    private TextInputEditText tEmail;
    private TextInputEditText tPassword;
    private Toolbar mtoolbar;
    private ProgressDialog regProgress;

    private Button reg;
    private FirebaseAuth mAuth;

    private FirebaseDatabase mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tUsername=(TextInputEditText) findViewById(R.id.reg_username);
        tEmail=(TextInputEditText) findViewById(R.id.reg_email);
        tPassword=(TextInputEditText) findViewById(R.id.reg_password);
        reg=(Button)findViewById(R.id.reg1_button);

        regProgress=new ProgressDialog(this);


        mtoolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name=tUsername.getEditableText().toString();
                String email=tEmail.getEditableText().toString();
                String password=tPassword.getEditableText().toString();

                if(!TextUtils.isEmpty(name)|| !TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)) {
                    regProgress.setTitle("Registering User");
                    regProgress.setMessage("Please wait while we create your account");
                    regProgress.setCanceledOnTouchOutside(false);
                    regProgress.show();

                    registerUser(name, email, password);
                }
            }

            private void registerUser(final String name, String email, String password) {

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){




                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=user.getUid();
                            mdatabase=FirebaseDatabase.getInstance();
                            DatabaseReference ref=mdatabase.getReference().child("users").child(uid);
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();


                            HashMap<String,String> usermap=new HashMap<>();
                            usermap.put("device_token",deviceToken);
                            usermap.put("name",name);
                            usermap.put("image","default");
                            usermap.put("status","Hi there you are using Socializer");
                            usermap.put("thumb_image","default");


                            ref.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        regProgress.dismiss();
                                        Toast.makeText(RegisterActivity.this,"You are succefully registered",Toast.LENGTH_LONG).show();
                                        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();


                                    }
                                }
                            });


                        }else{
                            Toast.makeText(RegisterActivity.this,"You got some error",Toast.LENGTH_LONG).show();

                        }


                    }
                });

            }
        });
    }
}
