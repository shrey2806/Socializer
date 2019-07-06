package com.example.shrey.socializer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Shrey on 4/21/2018.
 */

public class Details {

      DatabaseReference ref;
      String name;

      public Details(DatabaseReference ref){
          this.ref=ref;
      }

     public String getUsername(String Currentuser){




          ref.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  name=dataSnapshot.child("name").getValue(String.class);
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });

          return name;
     }
}
