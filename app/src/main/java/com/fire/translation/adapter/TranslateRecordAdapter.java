package com.fire.translation.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.fire.translation.R;
import com.fire.translation.db.entities.Tanslaterecord;
import com.fire.translation.mvp.presenter.TranslationPresenter;
import com.fire.translation.mvp.view.TranslationView;
import com.fire.translation.widget.BasesViewHolder;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fire on 2018/1/17.
 * Date：2018/1/17
 * Author: fire
 * Description:
 */

public class TranslateRecordAdapter extends EasyRecyclerArrayAdapter<Tanslaterecord> {

    private TranslationPresenter mTranslationPresenter;

    public TranslateRecordAdapter(Context context,
            List<Tanslaterecord> objects,TranslationPresenter mTranslationPresenter) {
        super(context, objects);
        this.mTranslationPresenter = mTranslationPresenter;
    }

    public TranslateRecordAdapter(Context context,TranslationPresenter mTranslationPresenter) {
        this(context, new ArrayList<>(),mTranslationPresenter);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new TranslateRecordViewHolder(parent,mTranslationPresenter);
    }

    class TranslateRecordViewHolder extends BasesViewHolder<Tanslaterecord>{

        @BindView(R.id.tv_content)
        TextView mTvContent;
        @BindView(R.id.tv_query)
        TextView mTvQuery;
        @BindView(R.id.iv_star)
        ImageView mIvStar;

        private TranslationPresenter mTranslationPresenter;

        public TranslateRecordViewHolder(ViewGroup parent,TranslationPresenter mTranslationPresenter) {
            super(parent, R.layout.item_translaterecord);
            this.mTranslationPresenter = mTranslationPresenter;
        }

        @Override
        public void setData(Tanslaterecord data) {
            super.setData(data);
            if (data == null) {
                return;
            }
            mTvContent.setText(data.getTranslations());
            mTvQuery.setText(data.getMquery());
            if (data.getStart() == 1) {
                mIvStar.setImageResource(R.drawable.ic_star);
            } else {
                mIvStar.setImageResource(R.drawable.ic_star1);
            }

            mIvStar.setOnClickListener(v -> {
                mTranslationPresenter.updateStar(data);
            });
        }
    }
}