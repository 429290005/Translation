package com.fire.translation.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.fire.baselibrary.base.inter.IBaseModel;
import com.fire.translation.constant.Constant;
import com.fire.translation.db.Dbservice;
import com.fire.translation.db.entities.Record;
import com.fire.translation.db.entities.TableName;
import com.fire.translation.db.entities.Word;
import com.fire.translation.utils.DateUtils;
import com.pushtorefresh.storio3.Optional;
import com.pushtorefresh.storio3.sqlite.operations.put.PutResult;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author fire
 * @date 2018/1/12
 * Description:
 */

public class MainModel implements IBaseModel {
    public Flowable<Optional<TableName>> getExistTableName() {
        return Dbservice.getInstance()
                .defaultDbConfig()
                .getExistTableName();
    }

    public Observable<PutResult> setExistTableStatus() {
        return Observable.create(
                e -> {
                    Record record = Dbservice.getInstance()
                            .defaultDbConfig()
                            .getRecord(DateUtils.getFormatDate1(new Date(),DateUtils.dateFormat1));
                    if (record == null) {
                        List<Word> allWords = Dbservice.getInstance()
                                .setDbConfig(Constant.SQLONENAME)
                                .getAllWords();
                        Record subRecord = Dbservice.getInstance()
                                .defaultDbConfig()
                                .getRecord(
                                        DateUtils.subDate(-1, new Date(), DateUtils.dateFormat1));
                        int recordDays = 1;
                        if (subRecord == null) {
                            recordDays = 1;
                        } else {
                            recordDays = subRecord.getRecordDays() + 1;
                        }
                        record = Record.newRecord((int) System.currentTimeMillis(),
                                DateUtils.getFormatDate1(new Date(), DateUtils.dateFormat1),
                                0, 30, recordDays, allWords.size(), 0);
                        Dbservice.getInstance()
                                .defaultDbConfig()
                                .insertRecord(record);
                    }
                    TableName firstTable = Dbservice.getInstance()
                            .defaultDbConfig()
                            .getFirstTable();
                    if (firstTable == null) {
                        e.onError(new NullPointerException("TableName is null"));
                        return;
                    }
                    firstTable.setFlag1(1);
                    PutResult putResult = Dbservice.getInstance()
                            .defaultDbConfig()
                            .updateTableStatus(firstTable);
                    if (putResult == null) {
                        e.onError(new NullPointerException("Putresult is null"));
                    } else {
                        e.onNext(putResult);
                        e.onComplete();
                    }
                });
    }

    public String loadPath(Context context, Intent data) {
        if (data == null) {
            return "";
        }
        Uri selectedImage = data.getData();
        if (selectedImage == null) {
            return "";
        }
        String[] filePathColumns = { MediaStore.Images.Media.DATA };
        Cursor c = context.getContentResolver()
                .query(selectedImage, filePathColumns, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePathColumns[0]);
        return c.getString(columnIndex);
    }
}
