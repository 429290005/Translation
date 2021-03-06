package com.fire.translation.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import com.fire.baselibrary.base.BaseFragment;
import com.fire.baselibrary.utils.ToastUtils;
import com.fire.translation.R;
import com.fire.translation.constant.Constant;
import com.fire.translation.db.entities.Record;
import com.fire.translation.db.entities.TableName;
import com.fire.translation.mvp.presenter.HomePresenter;
import com.fire.translation.mvp.view.HomeView;
import com.fire.baselibrary.rx.DefaultButtonTransformer;
import com.fire.baselibrary.rx.RxBus;
import com.fire.translation.ui.activity.ReviewActivity;
import com.fire.translation.view.NotifyTextView;
import com.fire.baselibrary.rx.EventBase;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.logger.Logger;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author fire
 * @date 2018/1/3
 * Description:
 */

public class HomeFragment extends BaseFragment implements HomeView {

    @BindView(R.id.nv_jcnum)
    NotifyTextView mNvJcnum;
    @BindView(R.id.nv_jsnum)
    NotifyTextView mNvJsnum;
    @BindView(R.id.nv_review)
    NotifyTextView mNvReview;
    @BindView(R.id.nvtype)
    NotifyTextView mNvType;
    @BindView(R.id.nvzwnum)
    NotifyTextView mNvZwnum;
    @BindView(R.id.btn_start)
    Button mBtnStart;
    @BindView(R.id.tv_type)
    TextView mTvType;

    private HomePresenter mHomePresenter;
    private Record mRecord;

    @Override
    public int resourceId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView() {
        mHomePresenter.setStatus(getActivity());
        RxView.clicks(mBtnStart)
                .compose(DefaultButtonTransformer.create())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(o -> {
                    if (mRecord == null) {
                        ToastUtils.showToast("记录不存在");
                        return;
                    }
                    mRecord.setRecordTime(mRecord.getRecordTime() + 1);
                    mHomePresenter.updateJsnum(mRecord);
                    Intent intent = new Intent(mActivity, ReviewActivity.class);
                    intent.putExtra("data", mRecord);
                    startActivity(intent);
                });
        mHomePresenter.rxBus(EventBase.class,getClass());
    }

    @Override
    public void rxBus(Observable<EventBase> observable) {
        observable.compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventBase -> {
                    if (eventBase == null) {
                        return;
                    }
                    if (eventBase.getArg0() == 0) {
                        mHomePresenter.loadRecord();
                    }
                    if (getString(R.string.wordplan).equals(eventBase.getArg2())) {
                        mRecord.setReview(Integer.parseInt(eventBase.getArg3()));
                        mHomePresenter.updateJsnum(mRecord);
                    } else if (getString(R.string.delete).equals(eventBase.getArg2())) {
                        mHomePresenter.deleteRecord(mRecord);
                    }
                }, throwable -> Logger.e(throwable.toString()));
    }

    @Override
    public void deleteRecord(Flowable<DeleteResult> deleteResultFlowable) {
        deleteResultFlowable.compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deleteResult -> {
                    if (deleteResult.numberOfRowsDeleted() == 1) {
                        mHomePresenter.loadRecord();
                    }
                },throwable -> Logger.e(throwable.toString()));
    }

    @Override
    public void setStatus(Flowable<Optional<TableName>> optionalFlowable) {
        optionalFlowable.map(tableNameOptional -> tableNameOptional.get())
                .subscribe(tableName -> {
                    Constant.SQLONENAME = String.format("%s.db",tableName.getName());
                    Constant.SQLTYPE = tableName.getCikuName();
                    mHomePresenter.loadRecord();
                },throwable -> Logger.e(throwable.toString()));
    }

    @Override
    protected void onFragmentCreate(@Nullable Bundle paramBundle) {
        super.onFragmentCreate(paramBundle);
        mHomePresenter = new HomePresenter(this);
    }

    @Override
    public void setRecord(Flowable<Changes> listFlowable) {
        listFlowable.compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .map(changes -> mHomePresenter.getRecord(getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(record -> {
                    mRecord = record;
                    Logger.e(mRecord.toString());
                    mNvJcnum.setLeftText(record.getRecordDays() + "");
                    mNvJsnum.setLeftText(record.getRecordTime() + "");
                    mNvReview.setLeftText(record.getReview() + "");
                    mNvType.setLeftText(record.getRecordCount() + "");
                    mNvZwnum.setLeftText(record.getRecordWords() + "");
                    mTvType.setText(Constant.SQLTYPE);
                }, throwable -> Logger.e(throwable.toString()));
    }

    @Override
    public void updateJsnum(Flowable<PutResult> putResultFlowable) {
        putResultFlowable.compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(putResult -> {
                    if (putResult.wasUpdated()) {
                        RxBus.getDefault().postEventBase(EventBase.builder()
                                        .arg0(0)
                                        .receiver(HomeFragment.class)
                                        .build());
                    }
                });
    }
}
