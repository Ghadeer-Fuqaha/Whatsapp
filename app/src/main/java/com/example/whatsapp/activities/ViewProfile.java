package com.example.whatsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewProfile extends AppCompatActivity {

    Toolbar toolbar;
    TextView username, email, status;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    CircleImageView profile_img;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        toolbar = (Toolbar) findViewById(R.id.viewProfileToolbar);

        profile_img = (CircleImageView) findViewById(R.id.imgProfile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        setSupportActionBar(toolbar);
        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        status = (TextView) findViewById(R.id.status);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Profile");
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfile.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        String UserID = FirebaseAuth.getInstance().getUid();
        reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot !=null){

                    User user = snapshot.child(UserID).getValue(User.class);

                    username.setText(user.getUsername());
                    email.setText(user.getEmail());
                    status.setText(user.getStatus());

                    if(user.getImgUrl().equals("default")){
                        profile_img.setImageResource(R.mipmap.ic_launcher);
                    }else{
                        Glide.with(getApplicationContext()).load(user.getImgUrl()).into(profile_img);
                    }
                }else{
                    Toast.makeText(ViewProfile.this, "no data selected!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
            }
        });
    }//End onCreate

    private void updateImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage(){

        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();


                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("imgUrl",mUri);

                        reference.updateChildren(map);
                    }else{
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"No image selected!",Toast.LENGTH_LONG).show();
        }
    }//End of uploadImage method :)


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode ==  RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Upload in progress!",Toast.LENGTH_LONG).show();
            }else{
                uploadImage();
            }
        }
    }//End of onActivityResult method

}