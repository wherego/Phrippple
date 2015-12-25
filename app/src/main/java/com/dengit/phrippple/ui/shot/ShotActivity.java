package com.dengit.phrippple.ui.shot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dengit.phrippple.APP;
import com.dengit.phrippple.R;
import com.dengit.phrippple.data.BucketType;
import com.dengit.phrippple.data.Shot;
import com.dengit.phrippple.ui.TransitionDetailBaseActivityL;
import com.dengit.phrippple.ui.bucket.BucketActivity;
import com.dengit.phrippple.ui.comment.CommentActivity;
import com.dengit.phrippple.ui.fan.FanActivity;
import com.dengit.phrippple.ui.profile.ProfileActivity;
import com.dengit.phrippple.utils.Util;
import com.dengit.phrippple.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dengit on 15/12/8.
 */
public class ShotActivity extends TransitionDetailBaseActivityL implements ShotView {

    @Bind(R.id.shot_author_portrait)
    SimpleDraweeView mAuthorPortrait;

    @Bind(R.id.shot_author_name)
    TextView mAuthorName;

    @Bind(R.id.shot_time)
    TextView mShotTime;

    @Bind(R.id.shot_fan)
    TextView mShotLike;

    @Bind(R.id.shot_bucket)
    TextView mShotBucket;

    @Bind(R.id.shot_view)
    TextView mShotView;

    @Bind(R.id.shot_comment)
    TextView mShotCommented;

    @Bind(R.id.shot_descrip)
    TextView mShotDescrip;

    @Bind(R.id.shot_fab_menu)
    FloatingActionMenu mShotFabMenu;

    private Shot mShot;
    private ShotPresenter mShotPresenter;
    private boolean hasLiked;

    public static Intent createIntent(Shot shot) {
        Intent intent = new Intent(APP.getInstance(), ShotActivity.class);
        intent.putExtra("shot", shot);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot);
        ButterKnife.bind(this);

        mShot = (Shot) getIntent().getSerializableExtra("shot");
        initDetailActivity(mShot.images.normal);
        initSetup();
    }

    @Override
    public void onBackPressed() {
        if (mShotFabMenu.isOpened()) {
            mShotFabMenu.close(true);
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.shot_comment)
    public void onClickShotComment(View v) {
        startActivity(CommentActivity.createIntent(mShot.id, mShot.comments_count));
    }

    @OnClick(R.id.shot_author_portrait)
    public void onClickShotAuthorPortrait(View v) {
        startDetailActivity(v);
    }

    @OnClick(R.id.shot_author_name)
    public void onClickShotAuthorName(View v) {
        startDetailActivity(mAuthorPortrait);
    }

    @OnClick(R.id.shot_fan)
    public void onClickShotFan(View v) {
        startActivity(FanActivity.createIntent(mShot.id, mShot.likes_count));
    }

    @OnClick(R.id.shot_bucket)
    public void onClickShotBucket(View v) {
        startActivity(BucketActivity.createIntent(BucketType.BucketsOfOthers, mShot.id, mShot.buckets_count));
    }

    @Override
    public String getTargetImageUrl() {
        return mShot.images.hidpi != null ? mShot.images.hidpi : mShot.images.normal;
    }

    @Override
    public boolean isTargetAnimate() {
        return mShot.animated;
    }

    @Override
    public int getShotId() {
        return mShot.id;
    }

    @Override
    public void lightenLike() {
        updateLike(true);
    }

    private void initSetup() {
        mShotPresenter = new ShotPresenterImpl(this);
        setupFab();
        setTitle(mShot.title);
        mAuthorPortrait.setImageURI(Uri.parse(mShot.user.avatar_url));
        mAuthorName.setText(mShot.user.name);
        mShotTime.setText(mShot.updated_at);
        mShotLike.setText(mShot.likes_count + " likes");
        mShotBucket.setText(mShot.buckets_count + " buckets");
        mShotView.setText(mShot.views_count + " views");
        mShotCommented.setText(mShot.comments_count + " comments");

        mShotDescrip.setText(Util.textToHtml(mShot.description));

        //        checkLikeStatus();
    }

    private void checkLikeStatus() {
        mShotPresenter.checkLikeStatus();
    }

    private void setupFab() {
        mShotFabMenu.setClosedOnTouchOutside(true);
        mShotFabMenu.hideMenuButton(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mShotFabMenu.showMenuButton(true);
            }
        }, 400);

        FloatingActionButton sendCommentFab = (FloatingActionButton) findViewById(R.id.fab_send_comment);
        sendCommentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShotActivity.this, "send comment", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton likeFab = (FloatingActionButton) findViewById(R.id.fab_like);
        likeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShotActivity.this, "like this shot", Toast.LENGTH_SHORT).show();
                mShotPresenter.setLikeStatus(!hasLiked);
                ShotActivity.this.updateLike(!hasLiked);
                mShotFabMenu.toggle(true);
            }
        });
    }

    private void updateLike(boolean like) {
        hasLiked = like;
        if (like) {
            mShotLike.setText((mShot.likes_count + 1) + " likes");
            mShotLike.setTextColor(Util.getColor(R.color.colorPrimary));
        } else {
            mShotLike.setText(mShot.likes_count + " likes");
            mShotLike.setTextColor(Util.getColor(R.color.colorDark));
        }
    }

    private void startDetailActivity(View view) {
        final Intent intent = ProfileActivity.createIntent(mShot.user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.shot_author_portrait);

        if (Utils.hasLollipop()) {
            startActivityLollipop(image, intent, "photo_hero");
        } else {
            startActivityGingerBread(image, intent);
        }
    }
}
