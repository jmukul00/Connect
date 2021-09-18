package com.example.connect.ui.PostUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connect.Adapter.MyPostAdapter;
import com.example.connect.Common.Common;
import com.example.connect.Common.GridSpacingItemDecoration;
import com.example.connect.Model.PostModel;
import com.example.connect.Model.UserFollowingModel;
import com.example.connect.Model.UserInterestModel;
import com.example.connect.Model.UserModel;
import com.example.connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShowPostUserFragment extends Fragment {


    Unbinder unbinder;

    @BindView(R.id.txt_user_name)
    TextView txtPostUserName;

    @BindView(R.id.txt_user_email)
    TextView txtPostUserEmail;

    @BindView(R.id.rv_userPosts)
    RecyclerView rvUserPosts;

    @BindView(R.id.txt_image_initials)
    TextView txt_image_initials;

    @BindView(R.id.txt_posts)
    TextView txt_posts;

    @BindView(R.id.txt_followers)
    TextView txt_followers;


    @BindView(R.id.btn_follow)
    Button btnFollow;

    BottomNavigationView navigationView;
    DatabaseReference databaseReference;

    int followers;
    public ShowPostUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_show_post_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        databaseReference = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        navigationView = getActivity().findViewById(R.id.bottom_navigation);

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFollow.getText().equals("Follow")){
                    btnFollow.setText("Following");
                    insertFollowers();
                    increaseFollowers();
                }
                else {
                    btnFollow.setText("Follow");
                    removeFollowers();
                    decreaseFollowers();
                }
            }
        });

        loadAllPostList();
        showDetails();

        return view;
    }

    private void increaseFollowers() {
        Toast.makeText(getContext(), ""+ followers, Toast.LENGTH_SHORT).show();
        databaseReference.child(Common.SELECTED_PERSON_UID).child("followers").setValue(followers + 1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                        txt_followers.setText(String.valueOf(followers + 1));
                    }
                });

    }

    private void decreaseFollowers() {
        Toast.makeText(getContext(), ""+ followers, Toast.LENGTH_SHORT).show();
        databaseReference.child(Common.SELECTED_PERSON_UID).child("followers").setValue(followers - 1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                        txt_followers.setText(String.valueOf(followers - 1));
                    }
                });

    }

    private void removeFollowers() {

        Query query = databaseReference.child(Common.currentUser.getUid()).child("following").orderByChild("followingUid")
                .equalTo(Common.SELECTED_PERSON_UID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void insertFollowers() {
        UserFollowingModel userFollowingModel = new UserFollowingModel();
        userFollowingModel.setFollowingName((String) txtPostUserName.getText());
        userFollowingModel.setFollowingUid(Common.SELECTED_PERSON_UID);
        databaseReference.child(Common.currentUser.getUid()).child("following").push().setValue(userFollowingModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Following..", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void showDetails() {


        databaseReference.orderByChild("uid").equalTo(Common.SELECTED_PERSON_UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = itemSnapshot.getValue(UserModel.class);
                    String name = userModel.getName();
                    txtPostUserName.setText(userModel.getName());
                    txtPostUserEmail.setText(userModel.getEmail());
                    followers = userModel.getFollowers();
                    txt_followers.setText(String.valueOf(followers));

                    String initials = "";
                    for (String s : name.split(" "))
                    {
                        initials += s.charAt(0);
                    }

                    txt_image_initials.setText(initials);


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadAllPostList() {

        final List<PostModel> postModelList = new ArrayList<>();
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference(Common.POST_REFERENCES);
        postRef.orderByChild("postPersonUid").equalTo(Common.SELECTED_PERSON_UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                            PostModel postModel = itemSnapshot.getValue(PostModel.class);
                            postModelList.add(postModel);
                        }

                        MyPostAdapter myPostAdapter =new MyPostAdapter(getContext(), postModelList);
                        rvUserPosts.setAdapter(myPostAdapter);
                        txt_posts.setText(String.valueOf(myPostAdapter.getItemCount()));
                        init();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void init() {

        int spanCount = 3; // 3 columns
        int spacing = 5; // 50px
        boolean includeEdge = false;

        GridLayoutManager layoutManager= new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvUserPosts.setLayoutManager(layoutManager);
        rvUserPosts.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        rvUserPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (dy > 0 && navigationView.isShown()) {
                    navigationView.setVisibility(View.GONE);

                } else if (dy < 0 ) {
                    navigationView.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        checkFollowing();
    }

    private void checkFollowing() {

        Query query = databaseReference.child(Common.currentUser.getUid()).child("following").orderByChild("followingUid")
                .equalTo(Common.SELECTED_PERSON_UID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   btnFollow.setText("Following");
               }
               else {
                   btnFollow.setText("Follow");
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Common.SELECTED_PERSON_UID = "";
    }
}