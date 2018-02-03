package com.fire.translation.mvp.presenter;

import com.fire.baselibrary.base.inter.IBasePresenter;
import com.fire.translation.mvp.model.SplashModel;
import com.fire.translation.mvp.view.SplashView;

/**
 *
 * @author fire
 * @date 2018/1/29
 * Description:
 */

public class SplashPresenter implements IBasePresenter {

    private SplashView mSplashView;
    private SplashModel mSplashModel;

    public SplashPresenter(SplashView splashView) {
        mSplashView = splashView;
        mSplashModel = new SplashModel();
    }

    public void getDsapi(String date,boolean isShowLoadingView) {
        if (isShowLoadingView) {
            mSplashView.showLoadingView();
        }
        mSplashView.loadData(mSplashModel.getDsapi(date));
    }
}
