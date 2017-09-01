package com.driveembetter.proevolutionsoftware.driveembetter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;

import java.util.List;

/**
 * Created by alfredo on 31/08/17.
 */
public class RankingRecyclerAdapter
        extends RecyclerView.Adapter<RankingRecyclerAdapter.PersonViewHolder>{

    private final static String TAG = RankingRecyclerAdapter.class.getSimpleName();

    private List<User> users;
    private Context context;

    public RankingRecyclerAdapter(Context context, List<User> users){
        this.users = users;
        this.context = context;
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView points;
        private TextView rank;
        private ImageView userPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.name_user);
            this.points = (TextView) itemView.findViewById(R.id.point_user);
            this.rank = (TextView) itemView.findViewById(R.id.rank_user);
            this.userPhoto = (ImageView) itemView.findViewById(R.id.user_picture);
        }
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_ranking_user, parent, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
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
            holder.name.setText(this.context.getString(R.string.user_item));
        }

        holder.points.setText(String.valueOf(users.get(position).getPoints()));

        holder.rank.setText("Rank");
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}