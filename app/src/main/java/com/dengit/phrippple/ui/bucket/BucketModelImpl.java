package com.dengit.phrippple.ui.bucket;

import com.dengit.phrippple.api.DribbbleAPI;
import com.dengit.phrippple.api.DribbbleAPIHelper;
import com.dengit.phrippple.data.Bucket;
import com.dengit.phrippple.data.BucketType;
import com.dengit.phrippple.utils.EventBusUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dengit on 15/12/14.
 */
public class BucketModelImpl implements BucketModel {

    private DribbbleAPI mDribbbleAPI;
    private String mAccessToken;

    public BucketModelImpl() {
        mDribbbleAPI = DribbbleAPIHelper.getInstance().getDribbbleAPI();
        mAccessToken = DribbbleAPIHelper.getInstance().getAccessToken();
    }

    @Override
    public void fetchBuckets(BucketType bucketType, int id) {
        final ArrayList<Bucket> newItems = new ArrayList<>();

        Observable<List<Bucket>> observable;
        if (bucketType == BucketType.Mine) {
            observable = mDribbbleAPI.getMineBuckets(id, mAccessToken);
        } else {
            observable = mDribbbleAPI.getOthersBuckets(id, mAccessToken);
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Bucket>>() {
                    @Override
                    public void onCompleted() {
                        EventBusUtil.getInstance().post(newItems); //todo may conflict with main activity as same type
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Bucket> buckets) {
                        newItems.addAll(buckets);
                    }
                });

    }
}
