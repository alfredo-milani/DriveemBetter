package com.driveembetter.proevolutionsoftware.driveembetter.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;

import java.util.List;

/**
 * Created by alfredo on 31/08/17.
 */
public class RankingRecyclerViewAdapter
        extends RecyclerView
        .Adapter<RankingRecyclerViewAdapter.UserViewHolder> {

    private final static String TAG = RankingRecyclerViewAdapter.class.getSimpleName();

    private List<User> users;
    private Context context;
    private final OnItemClickListener listener;



    public interface OnItemClickListener {
        void onItemClick(User item);
    }

    public RankingRecyclerViewAdapter(Context context, List<User> users, OnItemClickListener listener){
        this.users = users;
        this.context = context;
        this.listener = listener;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView points;
        private TextView rank;
        private ImageView userPhoto;

        UserViewHolder(View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.name_user);
            this.points = (TextView) itemView.findViewById(R.id.point_user);
            this.rank = (TextView) itemView.findViewById(R.id.rank_user);
            this.userPhoto = (ImageView) itemView.findViewById(R.id.user_picture);
        }

        void bind(final User item, final OnItemClickListener listener) {
            // itemView defined in super class
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_ranking_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        boolean currentUser = false;
        if (SingletonUser.getInstance() != null &&
                users.get(position).getUid() != null) {
            currentUser = users
                    .get(position)
                    .getUid()
                    .equals(SingletonUser.getInstance().getUid());
        }

        if (this.users.get(position).getPhotoUrl() != null) {
            Glide.with(this.context)
                    .load(
                            this.users
                                    .get(position)
                                    .getPhotoUrl()
                                    .toString()
                    )
                    .dontTransform()
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.userPhoto);
        } else {
            Glide.with(this.context)
                    .load(R.mipmap.user_black_icon)
                    .dontTransform()
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.userPhoto);
        }

        if (users.get(position).getUsername() != null) {
            holder.name.setText(users.get(position).getUsername());
        } else {
            holder.name.setText(users.get(position).getUsernameFromUid());
        }

        holder.points.setText(String.valueOf(users.get(position).getPoints()));

        holder.rank.setText(String.valueOf(position + 1).concat("°"));

        holder.bind(this.users.get(position), this.listener);

        if (currentUser) {
            holder.name.setTextColor(ContextCompat.getColor(this.context, R.color.blue_800));
            holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
            holder.name.setTextSize(15);

            holder.points.setTextColor(ContextCompat.getColor(this.context, R.color.blue_800));
            holder.points.setTypeface(holder.points.getTypeface(), Typeface.BOLD);
            holder.points.setTextSize(15);

            holder.rank.setTextSize(20);
            holder.points.setTypeface(holder.points.getTypeface(), Typeface.BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return this.users == null ?
                0 : this.users.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}