package com.example.shrey.socializer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


//import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Settings extends AppCompatActivity {

    private DatabaseReference mUserRef;
    private FirebaseUser mCurrentUser;

    private TextView DisplayName,Status;

    private CircleImageView pimage;

    private Button upImg;
    private Button upStatus;

    private static final int GALLERY_PICK = 1;
    private ProgressDialog mProgress;
    //Storage Reference
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        DisplayName = findViewById(R.id.DisplayName);
        Status =  findViewById(R.id.status);
        pimage =  findViewById(R.id.circleImageView);
        upImg = (Button) findViewById(R.id.update_img);
        upStatus = (Button) findViewById(R.id.update_status);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String uid = mCurrentUser.getUid();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        mUserRef.keepSynced(true);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                String thumb_image = dataSnapshot.child("thumb_image").getValue(String.class);

                if (name != null) {
                    DisplayName.setText(name);
                }

                Status.setText(status);

                if (!image.equals("default")) {

                    Glide.with(Settings.this).load(image).override(150, 150).into(pimage);
                    //Picasso.with(Settings.this).load(image).into(pimage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        upImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


            }
        });


        upStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent statusi=new Intent(Settings.this,StatusActivity.class);
                startActivity(statusi);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
            //Toast.makeText(Settings.this,imageUri,Toast.LENGTH_LONG).show();

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(Settings.this);
                mProgress.setTitle("Uploading Image");
                mProgress.setMessage("Please wait while we upload and process the image");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                Uri resultUri = result.getUri();


                final File thumb_filePath = new File(resultUri.getPath());


                String currentuid = mCurrentUser.getUid();

                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                    StorageReference filepath = mStorageRef.child("profile_images").child(currentuid + ".jpg");

                    final StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(currentuid + ".jpg");


                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                final String download_url = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        if (thumb_task.isSuccessful()) {
                                            String thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();

                                            Map updateUrls = new HashMap<>();
                                            updateUrls.put("image", download_url);
                                            updateUrls.put("thumb_image", thumb_downloadurl);


                                            mUserRef.updateChildren(updateUrls).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        mProgress.dismiss();

                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(Settings.this, "Error t", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

//                                mUserRef.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        if(task.isSuccessful()){
//                                            mProgress.dismiss();
//
//                                        }
//                                    }
//                                });


                            } else {

                                Toast.makeText(Settings.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


//
//        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
//             Uri resultUri = UCrop.getOutput(data);
//
//
//            mProgress=new ProgressDialog(Settings.this);
//                mProgress.setTitle("Uploading Image");
//                mProgress.setMessage("Please wait while we upload and process the image");
//                mProgress.setCanceledOnTouchOutside(false);
//                mProgress.show();
//
//
//
//                String currentuid=mCurrentUser.getUid();
//
//                StorageReference filepath=mStorageRef.child("profile_images").child(currentuid+".jpg");
//                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if(task.isSuccessful()){
//
//                                String download_url=task.getResult().getDownloadUrl().toString();
//
//                                mUserRef.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        if(task.isSuccessful()){
//                                            mProgress.dismiss();
//
//                                        }
//                                    }
//                                });
//
//
//
//                            }else{
//
//                                Toast.makeText(Settings.this,"Error",Toast.LENGTH_LONG).show();
//                            }
//                    }
//                });
//
//
//
//
//
//
//        } else if (resultCode == UCrop.RESULT_ERROR) {
//            final Throwable cropError = UCrop.getError(data);
//        }
    }


    //to generate random string to be set at the image file name in Storage


}
