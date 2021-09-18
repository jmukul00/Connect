package com.example.connect.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.connect.Callback.IRecyclerClickListener;
import com.example.connect.Common.Common;
import com.example.connect.EventBus.PostClick;
import com.example.connect.Model.InterestModel;
import com.example.connect.Model.PostModel;
import com.example.connect.Model.UpdatePostModel;
import com.example.connect.R;
import com.example.connect.ui.Posts.ShowPostFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.MyViewHolder> {

    Context context;
    List<PostModel> postModelList;
    UpdatePostModel updatePostModel;




    public MyPostAdapter(Context context, List<PostModel> postModelList) {
        this.context = context;
        this.postModelList = postModelList;

        updatePostModel = new UpdatePostModel();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyPostAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_my_post,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Resources res = context.getResources();
        final PostModel postModel = postModelList.get(position);
        final int newColor = res.getColor(R.color.colorImage);
        Glide.with(context).load(postModelList.get(position).getPostImage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.imageViewMyPost.setImageResource(R.drawable.image_not_found);

                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                //holder.progressBarMyPosts.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.imageViewMyPost);

        /*holder.imageViewMyPost.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                postModel.setSelected(!postModel.isSelected());
                if (postModel.isSelected()){

                    Glide.with(context).load(postModelList.get(position).getPostImage()).into(holder.imageViewMyPost);
                    holder.imageViewMyPost.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
                    //holder.imageViewCheck.setVisibility(View.VISIBLE);

                }
                else
                {
                    Glide.with(context).load(postModelList.get(position).getPostImage()).into(holder.imageViewMyPost);
                    //holder.imageViewCheck.setVisibility(View.GONE);

                    holder.imageViewMyPost.setColorFilter(null);
                }
               return true;
            }
        });*/

        holder.setiRecyclerClickListener(new IRecyclerClickListener() {
            @Override
            public void OnItemClickListener(View view, int pos) {
                Common.selectedPost = postModelList.get(pos);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ShowPostFragment myFragment = new ShowPostFragment();
                activity.getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, myFragment).addToBackStack(null).commit();

                //EventBus.getDefault().postSticky(new PostClick(true, postModelList.get(pos)));

            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int pos) {
                 Common.selectedPost = postModelList.get(pos);;


           menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    ShowPostFragment myFragment = new ShowPostFragment();
                    activity.getSupportFragmentManager().beginTransaction().
                            replace(R.id.frame_container, myFragment).addToBackStack(null).commit();
                    return false;
                }
            });
            menu.add(0, v.getId(), 0, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    /*postModelList.remove(position);
                    MyPostAdapter.this.notifyItemRemoved(position);
                    updatePostModel.setPostModelList(postModelList);*/
                    return false;
                }
            });
            }


        });
    }


    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        Unbinder unbinder;

        @BindView(R.id.imageView_my_post)
        ImageView imageViewMyPost;

       /* @BindView(R.id.progress_myPosts)
        ProgressBar progressBarMyPosts;
*/

        IRecyclerClickListener iRecyclerClickListener;

        public void setiRecyclerClickListener(IRecyclerClickListener iRecyclerClickListener) {
            this.iRecyclerClickListener = iRecyclerClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);


            itemView.setOnCreateContextMenuListener(this);


        }



        @Override
        public void onClick(View v) {

            iRecyclerClickListener.OnItemClickListener(v, getAdapterPosition());

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            /*Common.selectedPost = postModelList.get(pos);
            Common.selectedPost.setKey(String.valueOf(pos));

            menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    ShowPostFragment myFragment = new ShowPostFragment();
                    activity.getSupportFragmentManager().beginTransaction().
                            replace(R.id.frame_container, myFragment).addToBackStack(null).commit();
                    return false;
                }
            });
            menu.add(0, v.getId(), 0, "Delete");*/

            iRecyclerClickListener.onCreateContextMenu(menu, v, menuInfo, getAdapterPosition());
        }
    }

}
