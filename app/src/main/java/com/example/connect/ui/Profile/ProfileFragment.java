package com.example.connect.ui.Profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connect.Adapter.MyPostAdapter;
import com.example.connect.Common.Common;
import com.example.connect.Common.GridSpacingItemDecoration;
import com.example.connect.Model.PostModel;
import com.example.connect.R;
import com.example.connect.ui.Interest.InterestViewModel;
import com.example.connect.ui.Login.LoginActivity;
import com.example.connect.ui.Posts.CreatePostFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    TextView txt_name, txt_email,txt_image_initials, txt_noPostYet, txt_posts, txt_yourPosts, txt_followers;
    ShapeableImageView imageView_create;
    ImageView img_logout;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    GoogleSignInClient googleSignInClient;

    Unbinder unbinder;

    @BindView(R.id.rv_my_posts)
    RecyclerView rvMyPosts;

    @BindView(R.id.search_view_myPosts)
    androidx.appcompat.widget.SearchView searchViewMyPosts;

    @BindView(R.id.swipe_layout_myPosts)
    SwipeRefreshLayout swipeRefreshLayoutMyPosts;

    @BindView(R.id.search_progressBar)
    ProgressBar searchProgress;

    BottomNavigationView navigationView;
    String TAG_FRAGMENT = "ShowPostFragment";



    Bundle bundle = new Bundle();

    private final int PICK_IMAGE_REQUEST = 71;

    MyPostViewModel myPostViewModel;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_my_post for this fragment
        myPostViewModel = new ViewModelProvider(this).get(MyPostViewModel.class);
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        databaseReference = FirebaseDatabase.getInstance().getReference(Common.POST_REFERENCES);
        googleSignInClient = GoogleSignIn.getClient(getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();
        String name = Common.currentUser.getName();
        String followersCount = String.valueOf(Common.currentUser.getFollowers());
        swipeRefreshLayoutMyPosts.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayoutMyPosts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showPosts();
                swipeRefreshLayoutMyPosts.setRefreshing(false);
            }
        });


        String initials = "";
        for (String s : name.split(" "))
        {
            initials += s.charAt(0);
        }

        showPosts();

        txt_name = view.findViewById(R.id.txt_user_name);
        txt_name.setText(name);

        navigationView = getActivity().findViewById(R.id.bottom_navigation);


        txt_email = view.findViewById(R.id.txt_user_email);
        txt_email.setText("\t" + Common.currentUser.getEmail());

        txt_noPostYet = view.findViewById(R.id.txt_noPostYet);
        txt_yourPosts = view.findViewById(R.id.txt_yourPosts);

        txt_followers = view.findViewById(R.id.txt_followers);
        txt_followers.setText(followersCount);

        txt_image_initials = view.findViewById(R.id.txt_image_initials);
        txt_image_initials.setText(initials.toUpperCase());

        txt_posts  = view.findViewById(R.id.txt_posts);
        imageView_create = view.findViewById(R.id.imageView_create);
        imageView_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new CreatePostFragment());

                chooseImage();
            }
        });
        img_logout = view.findViewById(R.id.img_logout);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        searchViewMyPosts.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {

                startSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    private void startSearch(String query) {
        searchProgress.setVisibility(View.VISIBLE);
        List<PostModel> resultList = new ArrayList<>();
        String text = query.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("postPersonUid").equalTo(Common.currentUser.getUid());
        firebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataS: dataSnapshot.getChildren()) {
                    PostModel postModel = dataS.getValue(PostModel.class);
                    if (postModel.getPostTitle().toLowerCase().contains(text)) {
                        resultList.add(postModel);
                        searchProgress.setVisibility(View.GONE);
                        txt_noPostYet.setVisibility(View.GONE);
                    }
                }

                MyPostAdapter myPostAdapter = new MyPostAdapter(getContext(), resultList);
                rvMyPosts.setAdapter(myPostAdapter);
                myPostAdapter.notifyDataSetChanged();
                if (rvMyPosts.getAdapter().getItemCount() == 0){
                    searchProgress.setVisibility(View.GONE);
                    txt_noPostYet.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signOut() {

        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Sign Out", Toast.LENGTH_SHORT).show();
                Common.selectedPost = null;
                Common.currentUser = null;
                Common.incViews = false;
                Intent loginActivity = new Intent(getActivity(), LoginActivity.class);
                loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginActivity);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
                final Uri imageUri = data.getData();
            CreatePostFragment createPostFragment = new CreatePostFragment();
            try {
               Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                bundle.putParcelable("image", selectedImage);
                bundle.putParcelable("imageUri", imageUri);
                createPostFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, createPostFragment).addToBackStack(null)
                        .commit();

            } catch (IOException e) {
                e.printStackTrace();
            }




        }else {
            Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }

    }

    public void showPosts(){

        myPostViewModel.getAllPostList().observe(getViewLifecycleOwner(), postModels -> {

            if (postModels != null){

                MyPostAdapter myPostAdapter =new MyPostAdapter(getContext(), postModels);
                rvMyPosts.setAdapter(myPostAdapter);
                txt_noPostYet.setVisibility(View.VISIBLE);
                init();
                txt_posts.setText(String.valueOf(myPostAdapter.getItemCount()));

                if (myPostAdapter.getItemCount() == 0){
                    txt_noPostYet.setVisibility(View.VISIBLE);
                    txt_yourPosts.setVisibility(View.GONE);
                }
                else {
                    txt_noPostYet.setVisibility(View.GONE);
                    txt_yourPosts.setVisibility(View.VISIBLE);
                }
            }
            else {
            }

        });
    }

    private void init() {

        int spanCount = 3; // 3 columns
        int spacing = 5; // 50px
        boolean includeEdge = false;

        GridLayoutManager layoutManager= new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvMyPosts.setLayoutManager(layoutManager);
        rvMyPosts.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        rvMyPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.clear();
    }
}