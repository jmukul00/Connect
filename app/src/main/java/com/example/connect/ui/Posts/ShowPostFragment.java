package com.example.connect.ui.Posts;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.connect.Common.Common;
import com.example.connect.Model.PostModel;
import com.example.connect.R;
import com.example.connect.ui.PostUser.ShowPostUserFragment;
import com.example.connect.ui.Profile.MyPostViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShowPostFragment extends Fragment {

    Unbinder unbinder;

    PostViewModel postViewModel;

    @BindView(R.id.txt_post_title)
    TextView txtPostTitle;
    @BindView(R.id.image_post_image)
    ImageView imageViewPostImage;
    @BindView(R.id.txt_post_desc)
    TextView txtPostDesc;
    @BindView(R.id.txt_postUser_name)
    TextView txtPostUserName;

    @BindView(R.id.txt_postViews)
    TextView txtPostViews;

    @BindView(R.id.scrollView_showPost)
    ScrollView scrollView_showPost;

    @BindView(R.id.btn_post_link)
    Button btnPostLink;

    @BindView(R.id.txt_comments)
    TextView txtComments;

    @BindView(R.id.img_menu)
            ImageView imgMenu;
 String DIRECTORY_DOWNLOADS = "/connect/";
    BottomNavigationView navigationView;

    Bundle bundle = new Bundle();
    DatabaseReference postRef;

    OutputStream outputStream;

    WebView webView;
    public ShowPostFragment() {
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

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        View view =  inflater.inflate(R.layout.fragment_show_post, container, false);
        unbinder = ButterKnife.bind(this, view);
        navigationView = getActivity().findViewById(R.id.bottom_navigation);
        postRef = FirebaseDatabase.getInstance().getReference(Common.POST_REFERENCES );
        postViewModel.getMutableLiveDataProducts().observe(getViewLifecycleOwner(), postModel ->
                displayPostDetails(postModel));


        txtPostTitle.setText(Common.selectedPost.getPostTitle());

        return view;
    }



    private void displayPostDetails(PostModel postModel) {

        Glide.with(getContext()).load(postModel.getPostImage()).into(imageViewPostImage);
        txtPostTitle.setText(new StringBuilder(postModel.getPostTitle()));
        txtPostDesc.setText(new StringBuilder(postModel.getPostDesc()));
        int postViews;
            postViews = Common.selectedPost.getPostViews() + 1;

        postRef.child(postModel.getPostId()).child("postViews").setValue(postViews).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               String views =  new DecimalFormat("##,##,###").format(postModel.getPostViews());
                txtPostViews.setText(views +" views");
            }
        });
        String link = postModel.getPostLink();


        if (link.isEmpty()){
            btnPostLink.setVisibility(View.GONE);
        }

        else {
            btnPostLink.setVisibility(View.VISIBLE);
        }
        btnPostLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  //Toast.makeText(getContext(), ""+ link, Toast.LENGTH_SHORT).show();
               WebViewFragment webViewFragment = new WebViewFragment();
                bundle.putString("link", link);
                webViewFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, webViewFragment).addToBackStack(null)
                        .commit();
            }
        });
        txtPostUserName.setText(new StringBuilder(postModel.getPostPersonName()));
        txtPostUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Common.SELECTED_PERSON_UID = postModel.getPostPersonUid();
                if(Common.SELECTED_PERSON_UID.equals(Common.currentUser.getUid()))
                    Toast.makeText(getContext(), "The Post is posted by you", Toast.LENGTH_SHORT).show();
                else
                    loadFragment(new ShowPostUserFragment());
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), imgMenu);
                popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());
                //Toast.makeText(getContext(), "Show Menu", Toast.LENGTH_SHORT).show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_post_download:
                                int code = getActivity().getPackageManager().checkPermission(
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        getContext().getPackageName());
                                if (code == PackageManager.PERMISSION_GRANTED) {
                                    downloadImage(postModel.getPostImage());
                                    Toast.makeText(getContext(), "Image Saved to Gallary", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);

                                }

                                break;
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        txtComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Show Comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadImage(String postLink) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/connect");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(postLink);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

        SimpleDateFormat dateFormat = new SimpleDateFormat("mmddyyyyhhmmss");
        String date = dateFormat.format(new Date());

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Download")
                    .setDescription("Download")
                    .setDestinationInExternalPublicDir("/connect", date + ".jpg");

            mgr.enqueue(request);

        Toast.makeText(getContext(), "Image Saved to Gallary", Toast.LENGTH_SHORT).show();


    }

    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }





}