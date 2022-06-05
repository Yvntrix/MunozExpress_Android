package com.android.munozexpress;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    DatabaseReference reference;
    Dialog dialog , pd;
    String[] choice =  {"Take Picture","Select From Gallery"};
    ImageView profile;
    String getCurrentPhotoPath;
    StorageReference storageReference;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ImageButton signOut = view.findViewById(R.id.signOut);

        Button updateBtn = view.findViewById(R.id.updateBtn);
        EditText aFname = view.findViewById(R.id.Afname);
        EditText  aLname = view.findViewById(R.id.Alname);
        EditText  aEmail = view.findViewById(R.id.Aemail);
        EditText  aPhone = view.findViewById(R.id.Aphone);
        CardView imgUp = view.findViewById(R.id.imgup);
        profile = view.findViewById(R.id.profp);
        profile.setScaleType(ImageView.ScaleType.CENTER_CROP);

        pd = new Dialog(getActivity());
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pd.setCanceledOnTouchOutside(false);
        Button changePassBtn = view.findViewById(R.id.changePassBtn);
        dialog = new Dialog(getActivity());

        storageReference  = FirebaseStorage.getInstance().getReference();

        reference =  FirebaseDatabase.getInstance().getReference("users");
        final StorageReference image = storageReference.child("pictures/"+ user.getUid());
        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity())
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .thumbnail(Glide
                            .with(getActivity())
                            .load(image)
                            .diskCacheStrategy(DiskCacheStrategy.DATA))
                        .into(profile);
            }
        });

         databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
               aFname.setText(snapshot.child(user.getUid()).child("FirstName").getValue(String.class));
               aLname.setText(snapshot.child(user.getUid()).child("LastName").getValue(String.class));
               aEmail.setText(snapshot.child(user.getUid()).child("Email").getValue(String.class));
               aPhone.setText(snapshot.child(user.getUid()).child("Phone").getValue(String.class));
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

         changePassBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         Intent intent = new Intent(getActivity(), ChangePassword.class);
                         intent.putExtra(Intent.EXTRA_TEXT, snapshot.child(user.getUid()).child("Phone").getValue(String.class));
                         startActivity(intent);
                         startActivity(intent);
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });

             }
         });

         updateBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(getFragmentManager().findFragmentByTag("1" )!= null){
                     getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("1")).commit();
                 }
                 pd.show();

                 reference.child(user.getUid()).child("FirstName").setValue(aFname.getText().toString());
                 reference.child(user.getUid()).child("LastName").setValue(aLname.getText().toString());
                 aFname.clearFocus();
                 aLname.clearFocus();
                 aEmail.clearFocus();
                 pd.dismiss();
                 openAlertDialog();
             }
         });
         imgUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                askCameraPermissions();

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

    private void askCameraPermissions(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},101);
        }else{
            openCamera();
        }
    }

    private  void openCamera(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(choice, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               if(which == 0){
                 dispatchTakePictureIntent();
               }
                if(which == 1){
                  Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  startActivityForResult(gallery,105);
                }
            }
        });

        builder.show();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 101){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                Toast.makeText(getActivity(), "Camera Permission is Required to use Camera", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      if(requestCode == 101){
         if(resultCode == Activity.RESULT_OK){
             File f = new File(getCurrentPhotoPath);
             profile.setImageURI(Uri.fromFile(f));
             Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
             Uri contentUri = Uri.fromFile(f);
             mediaScanIntent.setData(contentUri);
             getActivity().sendBroadcast(mediaScanIntent);
             confirmImage(contentUri);

         }
      }
        if(requestCode == 105){
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
               profile.setImageURI(contentUri);
                confirmImage(contentUri);
            }
        }
    }

    private void confirmImage(Uri contentUri){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Confirm Image?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      pd.show();
                        try {
                            uploadImage(contentUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      profile.setImageURI(null);
                    }
                });
        builder.show();
    }


    private void uploadImage( Uri contentUri) throws IOException {
        final StorageReference image = storageReference.child("pictures/"+ user.getUid());
        Bitmap imageBitmap = SiliCompressor.with(getActivity()).getCompressBitmap(String.valueOf(contentUri));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        //uploading the image
        UploadTask uploadTask2 = image.putBytes(data);
        uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .into(profile);
                        if(getFragmentManager().findFragmentByTag("1" )!= null){
                            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("1")).commit();
                        }
                        pd.dismiss();
                        openAlertDialog();
                    }
                });
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = user.getUid();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        getCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.android.munozexpress.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,101);
            }
        }
    }



    private void openAlertDialog(){
        dialog.setContentView(R.layout.pd_update);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}