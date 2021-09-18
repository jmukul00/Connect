package com.example.connect.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.connect.Callback.IClickListener;
import com.example.connect.Callback.IRecyclerClickListener;
import com.example.connect.Common.Common;
import com.example.connect.Model.PostModel;
import com.example.connect.R;
import com.example.connect.ui.Posts.ShowPostFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    List<PostModel> postModelList;


    public PostAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;
    }

    public String getLastItemId(){
        return postModelList.get(postModelList.size()-1).getPostId();
    }

    public void addAll(List<PostModel> newPosts){
        int initSize = postModelList.size();
        postModelList.addAll(newPosts);
        notifyItemRangeChanged(initSize, newPosts.size());
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_posts,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(postModelList.get(position).getPostImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.imageViewLoading.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.imageViewLoading.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.imageViewPost);
        holder.textViewPostTitle.setText(postModelList.get(position).getPostTitle());

        //holder.setIsRecyclable(false);
        holder.setiClickListener(new IClickListener() {
            @Override
            public void OnItemClickListener(View view, int pos) {
                Common.selectedPost = postModelList.get(pos);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ShowPostFragment myFragment = new ShowPostFragment();
                activity.getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, myFragment).addToBackStack(null).commit();

                //EventBus.getDefault().postSticky(new PostClick(true, postModelList.get(pos)));

            }
        });
    }

    @Override
    public int getItemCount() {
       return postModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Unbinder unbinder;

        @BindView(R.id.imageView_post)
        ImageView imageViewPost;
        @BindView(R.id.imageView_loading)
        ImageView imageViewLoading;

        @BindView(R.id.txtView_post_title)
        TextView textViewPostTitle;

        IClickListener iClickListener;

        public void setiClickListener(IClickListener iClickListener) {
            this.iClickListener = iClickListener;
        }


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            iClickListener.OnItemClickListener(v, getAdapterPosition());
        }
    }
}
