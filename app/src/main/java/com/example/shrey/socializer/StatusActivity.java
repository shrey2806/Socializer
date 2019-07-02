package com.example.shrey.socializer;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    Toolbar mtoolbar;

    TextInputLayout mStatus;
    private Button mSaveButton;

    private ProgressDialog mprogressd;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mStatusReference;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mtoolbar=findViewById(R.id.status_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Status Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mStatus=findViewById(R.id.statusInput);
        mSaveButton=findViewById(R.id.status_save_button);



        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();

        String current_uid=mCurrentUser.getUid();
        mStatusReference= FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);

        //




        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status= mStatus.getEditText().getText().toString();

                mprogressd=new ProgressDialog(StatusActivity.this);
                mprogressd.setTitle("Saving changes");
                mprogressd.setMessage("Please wait while we save changes");
                mprogressd.show();

                if(!TextUtils.isEmpty(status)){
                    mStatusReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mprogressd.dismiss();


                            }else{

                                Toast.makeText(StatusActivity.this,"There was some error",Toast.LENGTH_SHORT);
                            }
                        }
                    });

                }
            }
        });


    }
}
