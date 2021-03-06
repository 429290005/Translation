package com.fire.translation.mvp.model;

import android.content.Context;
import android.preference.PreferenceManager;
import com.fire.baselibrary.base.inter.IBaseModel;
import com.fire.baselibrary.utils.ListUtils;
import com.fire.translation.R;
import com.fire.translation.constant.Constant;
import com.fire.translation.db.Dbservice;
import com.fire.translation.db.entities.Record;
import com.fire.translation.db.entities.TableName;
import com.fire.translation.db.entities.Word;
import com.fire.translation.db.entities.DailyEntity;
import com.fire.translation.network.RetrofitClient;
import com.fire.translation.utils.DateUtils;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fire
 * @date 2018/1/3
 * Description:
 */

public class HomeModel implements IBaseModel {

    public Observable<DailyEntity> getDsapi(String data) {
        return RetrofitClient.getInstance()
                .getServiceApi()
                .beforeNews(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Changes> loadRecord() {
        Set<String> tables = new HashSet<>();
        tables.add(TableName.__TABLE__);
        tables.add(Record.__TABLE__);
        return Dbservice.getInstance()
                .defaultDbConfig()
                .getStorIOSQLite()
                .observeChangesInTables(tables, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .startWith(Changes.newInstance(""));
    }

    public Record getRecord(Context context) {
        Record record = Dbservice.getInstance()
                .defaultDbConfig()
                .getRecord(DateUtils.getFormatDate1(new Date(),DateUtils.dateFormat1));
        List<Word> allWords = Dbservice.getInstance()
                .setDbConfig(Constant.SQLONENAME)
                .getAllWords();
        if (record == null) {
            Record subRecord = Dbservice.getInstance()
                    .defaultDbConfig()
                    .getRecord(DateUtils.subDate(-1, new Date(), DateUtils.dateFormat1));
            int recordDays = 1;
            if (subRecord == null) {
                recordDays = 1;
            } else {
                recordDays = subRecord.getRecordDays() + 1;
            }
            String review = ListUtils.stringToString(context, R.array.newword,
                    R.array.newword_value, PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("word_plan", "2"));
            record = Record.newRecord((int) System.currentTimeMillis(),
                    DateUtils.getFormatDate1(new Date(), DateUtils.dateFormat1),
                    0, Integer.parseInt(review), recordDays, allWords.size(), 0);
            PutResult result = Dbservice.getInstance()
                    .defaultDbConfig()
                    .insertRecord(record);
            if (result.wasInserted()) {
                return record;
            } else {
                return null;
            }
        } else {
            if (record.getRecordCount() != allWords.size()) {
                record.setRecordCount(allWords.size());
                Dbservice.getInstance()
                        .defaultDbConfig()
                        .updateRecord(record);
                return record;
            }
        }
        return record;
    }

    public Flowable<PutResult> updateJsnum(Record record) {
        return Dbservice.getInstance()
                .defaultDbConfig()
                .updateJsnum(record);
    }

    public Flowable<DeleteResult> deleteRecord(Record record) {
        return Dbservice.getInstance()
                .defaultDbConfig()
                .deleteRecord(record);
    }

    public Flowable<Optional<TableName>> setStatus(Context context) {
        return Dbservice.getInstance()
                .defaultDbConfig()
                .getExistTableName(
                        ListUtils.stringToString(context, R.array.plan, R.array.plan_value,
                                PreferenceManager.getDefaultSharedPreferences(context)
                                        .getString("study_plan", "1")));
        /* */
    }
}
