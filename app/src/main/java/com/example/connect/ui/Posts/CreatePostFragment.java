package com.example.connect.ui.Posts;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.connect.Common.Common;
import com.example.connect.Model.PostModel;
import com.example.connect.Model.UserModel;
import com.example.connect.R;
import com.example.connect.ui.Login.RegisterActivity;
import com.example.connect.ui.Profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;


public class CreatePostFragment extends Fragment {


    @BindView(R.id.edt_title)
    EditText edt_title;

    @BindView(R.id.edt_desc)
    EditText edt_desc;

    @BindView(R.id.edt_link)
    EditText edt_link;

    ImageView img_selected;
    Unbinder unbinder;

    private android.app.AlertDialog waitingDialog;

    Spinner spinner_interest;
    private DatabaseReference interestRef, postRef;

    FirebaseStorage storage;
    StorageReference storageReference;

    public CreatePostFragment() {
        // Required empty public constructor
    }


    @OnClick(R.id.btn_post)
    void post(){
        createPost();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_my_post for this fragment
        View view =  inflater.inflate(R.layout.fragment_create_post, container, false);
        unbinder = ButterKnife.bind(this, view);
        storage = FirebaseStorage.getInstance();
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        waitingDialog.setMessage("Posting");
        storageReference = storage.getReference();
        interestRef = FirebaseDatabase.getInstance().getReference(Common.INTEREST_REFERENCES );
        postRef = FirebaseDatabase.getInstance().getReference(Common.POST_REFERENCES );
        img_selected = view.findViewById(R.id.img_selected);
        spinner_interest = view.findViewById(R.id.spinner_interest);


        Bundle args = getArguments();
        if (args != null) {
            //byte[] byteArray = getArguments().getByteArray("image");
            Bitmap bitmap = getArguments().getParcelable("image");
            getArguments().remove("image");
            //Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            img_selected.setImageBitmap(bitmap);
        }
        else {
            Toast.makeText(getContext(), "You have not selected any image", Toast.LENGTH_SHORT).show();
        }
        getSpinnerData();

        return view;
    }

    private void getSpinnerData() {
        interestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("name").getValue(String.class);
                    areas.add(areaName);
                }

                ArrayAdapter<String> interestAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, areas);
                interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_interest.setAdapter(interestAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void createPost(){
        waitingDialog.show();
        /*String title = edt_title.getText().toString().trim();
        String desc = edt_desc.getText().toString().trim();
        String category = spinner_interest.getSelectedItem().toString();*/
        uploadImage();
       /* final PostModel postModel = new PostModel();
        postModel.setPostPersonUid(Common.currentUser.getUid());
        postModel.setPostPersonName(Common.currentUser.getName());
        postModel.setPostTitle(title);
        postModel.setPostDesc(desc);
        postModel.setPostCategory(category);

        postRef.push().setValue(postModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Your Post is submitted", Toast.LENGTH_SHORT).show();

                    }
                });*/
    }

    private void uploadImage() {
        if(img_selected != null)
        {
            StorageReference ref = storageReference.child("postImages/"+ UUID.randomUUID().toString());

            ref.putFile(getArguments().getParcelable("imageUri"))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                              Uri downloadUrl = uri;
                              String download_url = downloadUrl.toString();
                                   String title = edt_title.getText().toString().trim();
                                   String desc = edt_desc.getText().toString().trim();
                                   String web_link = edt_link.getText().toString().trim();
                                   String category = spinner_interest.getSelectedItem().toString();
                                   final PostModel postModel = new PostModel();
                                   postModel.setPostPersonUid(Common.currentUser.getUid());
                                   postModel.setPostPersonName(Common.currentUser.getName());
                                   String key = postRef.push().getKey();
                                   postModel.setPostId(key);
                                   postModel.setPostTitle(title);
                                   postModel.setPostDesc(desc);
                                   postModel.setPostLink(web_link);
                                   postModel.setPostCategory(category);
                                   postModel.setPostImage(download_url);
                                   postModel.setPostViews(0);
                                   Map<String, Object> serverTimeStamp = new HashMap<>();
                                   serverTimeStamp.put("timestamp", ServerValue.TIMESTAMP);
                                   postModel.setPostTimeStamp(serverTimeStamp);
                                   postModel.setPostStatus(1);
                                   postRef.child(key).setValue(postModel)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   waitingDialog.dismiss();
                                                   loadFragment(new ProfileFragment());
                                                   Toast.makeText(getContext(), "Your Post is submitted", Toast.LENGTH_SHORT).show();

                                               }
                                           });
                               }
                           });
                            //Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
    }



    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);

        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}