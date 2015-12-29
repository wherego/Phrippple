package com.dengit.phrippple.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dengit.phrippple.APP;
import com.dengit.phrippple.R;
import com.dengit.phrippple.data.Fan;
import com.dengit.phrippple.data.User;
import com.dengit.phrippple.ui.TransitionBaseActivity;
import com.dengit.phrippple.ui.profile.ProfileActivity;
import com.dengit.phrippple.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dengit on 15/12/14.
 */
public class FansAdapter extends RecyclerViewTransitionBaseAdapter<Fan> {

    public FansAdapter(List<Fan> fans, View footer, TransitionBaseActivity<Fan> activity) {
        super(fans, footer, activity);
    }

    @Override
    protected int getItemLayoutResId() {
        return R.layout.item_fan;
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolderItem(View itemView) {
        return new FanVHItem(itemView);
    }

    protected void setUpItems(VHItemBase holder, final int position) {
        FanVHItem itemHolder = (FanVHItem) holder;
        Fan fan = (Fan) getItem(position);
        itemHolder.fanPortrait.setImageURI(Uri.parse(fan.user.avatar_url));
        itemHolder.fanName.setText(fan.user.name);
        itemHolder.fanPrettyTime.setText(fan.created_at);

        if (fan.user.shots_count > 0) {
            itemHolder.fanShotCount.setText(fan.user.shots_count + " shots");
            itemHolder.fanShotCount.setVisibility(View.VISIBLE);
        }

        if (fan.user.followers_count > 0) {
            itemHolder.fanFollowerCount.setText(fan.user.followers_count + " followers");
            itemHolder.fanFollowerCount.setVisibility(View.VISIBLE);
        }

        if (fan.user.shots_count > 0 && fan.user.followers_count > 0) {
            itemHolder.fanItemDivider.setVisibility(View.VISIBLE);
        }

        if (fan.user.location != null && !TextUtils.isEmpty(fan.user.location.trim())) {
            itemHolder.fanLocation.setVisibility(View.VISIBLE);
            itemHolder.fanLocation.setText(fan.user.location);
        }

        itemHolder.fanItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileDetailActivity(v, position);
            }
        });
    }

    private void startProfileDetailActivity(View view, int position) {
        Fan fan = (Fan) getItem(position);
        final Intent intent = ProfileActivity.createIntent(fan.user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startDetailActivity(view, intent, R.id.fan_portrait_image);
    }

    static class FanVHItem extends VHItemBase {
        View fanItemView;

        @Bind(R.id.fan_portrait_image)
        SimpleDraweeView fanPortrait;

        @Bind(R.id.fan_name)
        TextView fanName;

        @Bind(R.id.fan_pretty_time)
        TextView fanPrettyTime;

        @Bind(R.id.fan_shot_count)
        TextView fanShotCount;

        @Bind(R.id.fan_follower_count)
        TextView fanFollowerCount;

        @Bind(R.id.fan_location)
        TextView fanLocation;

        @Bind(R.id.fan_item_divider)
        View fanItemDivider;

        public FanVHItem(View view) {
            super(view);
            ButterKnife.bind(this, view);
            fanItemView = view;
        }
    }
}
