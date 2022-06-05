package com.android.munozexpress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RiderAccount extends Fragment {

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    Dialog dialog ;
    String phone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_rider_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        ImageButton signOut = view.findViewById(R.id.signOut);
        Button updateBtn = view.findViewById(R.id.updateBtn);
        EditText aFname = view.findViewById(R.id.Afname);
        EditText  aLname = view.findViewById(R.id.Alname);
        EditText  aEmail = view.findViewById(R.id.Aemail);
        EditText  aPhone = view.findViewById(R.id.Aphone);
        FirebaseUser user = mAuth.getCurrentUser();
        dialog = new Dialog(getActivity());

        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("riders");

        databaseReference.child("riders-id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phone =  snapshot.child(user.getUid()).child("Phone").getValue(String.class);

                databaseReference.child("riders").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        aFname.setText(snapshot.child(phone).child("FirstName").getValue(String.class));
                        aLname.setText(snapshot.child(phone).child("LastName").getValue(String.class));
                        aEmail.setText(snapshot.child(phone).child("Email").getValue(String.class));
                        aPhone.setText(snapshot.child(phone).child("Phone").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlertDialog();
//                if(getFragmentManager().findFragmentByTag("1" )!= null){
//                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("1")).commit();
//                }
                reference.child(phone).child("FirstName").setValue(aFname.getText().toString());
                reference.child(phone).child("LastName").setValue(aLname.getText().toString());
                aFname.clearFocus();
                aLname.clearFocus();
                aEmail.clearFocus();
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Log out");
                builder.setMessage("You will be returned to the login screen.");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                builder.show();

            }
        });



        return view;
    }
    private void openAlertDialog(){
        dialog.setContentView(R.layout.pd_update);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}