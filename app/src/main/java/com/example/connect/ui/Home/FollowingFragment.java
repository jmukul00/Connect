package com.example.connect.ui.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connect.Adapter.PostAdapter;
import com.example.connect.Common.Common;
import com.example.connect.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class FollowingFragment extends Fragment {


    Unbinder unbinder;

    @BindView(R.id.rv_posts)
    RecyclerView rvPosts;

    BottomNavigationView navigationView;

    HomeViewModel homeViewModel;

    @BindView(R.id.swipe_layout_posts)
    SwipeRefreshLayout swipeRefreshLayoutPosts;

    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        navigationView = getActivity().findViewById(R.id.bottom_navigation);
        swipeRefreshLayoutPosts.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayoutPosts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayoutPosts.setRefreshing(false);
            }
        });

        init();
        showInterest();
        showPosts();

        return  view;
    }


    private void init() {

        int spanCount = 3; // 3 columns
        int spacing = 5; // 50px
        boolean includeEdge = false;

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        /*GridLayoutManager layoutManager= new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);*/
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setItemAnimator(null);
        //rvPosts.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

       /* rvPosts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                homeViewModel.getMorePostList().observe(getViewLifecycleOwner(), postModels -> {
                    PostAdapter postAdapter =new PostAdapter(getContext(), postModels);
                    rvPosts.setAdapter(postAdapter);
                    init();
                });
            }
        });
*/

    }

    public void showPosts(){

        homeViewModel.getFollowingPostList().observe(getViewLifecycleOwner(), postModels -> {

            if (postModels != null){

                PostAdapter postAdapter =new PostAdapter(getContext(), postModels);
                rvPosts.setAdapter(postAdapter);
                init();
                /*txt_posts.setText(String.valueOf(myPostAdapter.getItemCount()));

                if (myPostAdapter.getItemCount() == 0){
                    txt_noPostYet.setVisibility(View.VISIBLE);
                    txt_yourPosts.setVisibility(View.GONE);
                }
                else {
                    txt_noPostYet.setVisibility(View.GONE);
                    txt_yourPosts.setVisibility(View.VISIBLE);
                }*/
            }
            else {
            }

        });



    }

    public void showInterest(){
        homeViewModel.getAllUserFollowingList().observe(getViewLifecycleOwner(), userFollowingModels -> {

            Common.userFollowingList.clear();
            for (int i = 0; i < userFollowingModels.size(); i++) {
                Common.userFollowingList.add(userFollowingModels.get(i).getFollowingUid());
            }
        });
    }

}