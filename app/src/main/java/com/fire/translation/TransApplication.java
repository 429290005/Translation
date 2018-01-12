package com.fire.translation;

import com.fire.baselibrary.base.App;
import com.fire.translation.constant.Constant;
import com.fire.translation.db.CipherOpenHelper;
import com.fire.translation.db.DBConfig;
import com.fire.translation.db.DbModelSQLiteTypeMapping;
import com.fire.translation.db.TableInfo;
import com.fire.translation.db.entities.DbModel;
import com.fire.translation.utils.AssetsUtils;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite;
import java.io.File;
import java.io.IOException;

/**
 * Created by fire on 2018/1/11.
 * Date：2018/1/11
 * Author: fire
 * Description:
 */

public class TransApplication extends App {

    public static TransApplication mTransApp;
    private CipherOpenHelper mDbOpenHelper;
    private DBConfig mDbConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        mTransApp = this;
        unZipFile();
        initDBHelper();

    }

    private void initDBHelper() {
        mDbConfig = DBConfig.builder().dbDir(getDatabasePath(".").getAbsolutePath()).dbName(Constant.BASESQLNAME).version(1).build();
        mDbOpenHelper = new CipherOpenHelper(this, mDbConfig.getDbDir(), mDbConfig.getDbName(), mDbConfig.getVersion());
    }

    public StorIOSQLite initSqlite(CipherOpenHelper mDbOpenHelper) {
        DefaultStorIOSQLite.CompleteBuilder completeBuilder = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mDbOpenHelper)
                .addTypeMapping(DbModel.class, new DbModelSQLiteTypeMapping());
        return TableInfo.buildTypeMapping(completeBuilder);
    }

    public StorIOSQLite initSqlite() {
        DefaultStorIOSQLite.CompleteBuilder completeBuilder = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mDbOpenHelper)
                .addTypeMapping(DbModel.class, new DbModelSQLiteTypeMapping());
        return TableInfo.buildTypeMapping(completeBuilder);
    }

    private void unZipFile() {
        try {
            File databasePath = getDatabasePath(Constant.BASESQLNAME);
            if (!databasePath.exists()) {
                AssetsUtils.unZip(this, Constant.BASEZIPNAME,getDatabasePath(".").getAbsolutePath());
                AssetsUtils.unZip(this,Constant.ZIPONENAME,getDatabasePath(".").getAbsolutePath());
            } else {
                File file = getDatabasePath(Constant.SQLONENAME);
                if (!file.exists()) {
                    AssetsUtils.unZip(this,Constant.ZIPONENAME,getDatabasePath(".").getAbsolutePath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}