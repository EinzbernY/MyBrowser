package com.example.mybrowser.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.mybrowser.bean.Collection;
import com.example.mybrowser.bean.CollectionDao;
import com.example.mybrowser.bean.DaoMaster;
import com.example.mybrowser.bean.DaoSession;
import com.example.mybrowser.bean.History;
import com.example.mybrowser.bean.HistoryDao;
import com.example.mybrowser.bean.Quick;
import com.example.mybrowser.bean.QuickDao;
import com.example.mybrowser.bean.User;
import com.example.mybrowser.bean.UserDao;

import java.util.List;

public class DBController {
    private static final String DB_NAME = "mbrowser_db.db";
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase mDb;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private Context mContext;
    private UserDao mUserDao;
    private HistoryDao mHistoryDao;
    private CollectionDao mCollectionDao;
    private QuickDao mQuickDao;
    private static DBController sDbController;

    private SQLiteDatabase getWritableDatabase() {
        if (mHelper == null) {
            mHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
        }
        mDb = mHelper.getWritableDatabase();
        return mDb;
    }
    public DBController(Context context) {
        mContext = context;
        mHelper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
        mDaoMaster = new DaoMaster(getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
        mUserDao = mDaoSession.getUserDao();
        mHistoryDao = mDaoSession.getHistoryDao();
        mCollectionDao = mDaoSession.getCollectionDao();
        mQuickDao = mDaoSession.getQuickDao();
    }


    public long insertUser(User user) {
        return mUserDao.insert(user);
    }

    public User checkUserAndPassword(String whereUser, String wherePassword) {
        User user = mUserDao.queryBuilder()
                .where(UserDao.Properties.Name.eq(whereUser),
                        UserDao.Properties.Password.eq(wherePassword)).build().unique();
        return user;
    }

    public boolean checkUserExist(String whereUser) {
        List<User>users = (List<User>)mUserDao.queryBuilder()
                .where(UserDao.Properties.Name.eq(whereUser)).build().list();
        return !users.isEmpty();
    }

    public void insertHistory(List<History> histories) {
        for (int i = 0; i < histories.size(); i++) {
            List<History>res = mHistoryDao.queryBuilder()
                    .where(HistoryDao.Properties.Url.eq(histories.get(i).getUrl()),
                            HistoryDao.Properties.Time.eq(histories.get(i).getTime()))
                    .build().list();
            if (res == null || res.isEmpty()) {
                mHistoryDao.insert(histories.get(i));
            } else {
                mHistoryDao.queryBuilder().where(HistoryDao.Properties.Url.eq(histories.get(i).getUrl()),
                        HistoryDao.Properties.Time.eq(histories.get(i).getTime())).buildDelete()
                        .executeDeleteWithoutDetachingEntities();
                mHistoryDao.insert(histories.get(i));
            }
        }
    }

    public void deleteAllHistory() {
        mHistoryDao.deleteAll();
    }

    public void deleteSelectedHistory(List<History> historiesToBeDeleted) {
        if (mDb.isOpen()) {
            try {
                mDb.beginTransaction();
                for (int i = 0; i < historiesToBeDeleted.size(); i++) {
                    mHistoryDao.queryBuilder()
                            .where(HistoryDao.Properties.Url.eq(historiesToBeDeleted.get(i).getUrl()),
                                    HistoryDao.Properties.Time.eq(historiesToBeDeleted.get(i).getTime()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                mDb.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDb.endTransaction();
            }
        }
    }

    public List<History> getAll() {
        List<History> all = (List<History>)mHistoryDao.queryBuilder().build().list();
        return all;
    }

    public List<Collection> getUserCollection(String username) {
        List<Collection> res = mCollectionDao.queryBuilder()
                .where(CollectionDao.Properties.Name.eq(username))
                .build()
                .list();
        return res;
    }

    public void addCollection(String username, String url, String title) {
        List<Collection> exist = mCollectionDao.queryBuilder()
                .where(CollectionDao.Properties.Name.eq(username),
                        CollectionDao.Properties.Url.eq(url), CollectionDao.Properties.Title.eq(title))
                .build().list();
        if (exist == null || exist.isEmpty()) {
            mCollectionDao.insert(new Collection(username, url, title));
        } else {
            return;
        }
    }

    public void deleteAllCollection(String username) {
        mCollectionDao.queryBuilder().where(CollectionDao.Properties.Name.eq(username))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public void deleteSelectedCollection(List<Collection> toBeDeleted) {
        if (mDb.isOpen()) {
            try {
                mDb.beginTransaction();
                for (int i = 0; i < toBeDeleted.size(); i++) {
                    mCollectionDao.queryBuilder()
                            .where(CollectionDao.Properties.Name.eq(toBeDeleted.get(i).getName()),
                                    CollectionDao.Properties.Url.eq(toBeDeleted.get(i).getUrl()),
                                    CollectionDao.Properties.Title.eq(toBeDeleted.get(i).getTitle()))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                }
                mDb.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDb.endTransaction();
            }
        }
    }

    public void changeUsername(String name, String newName) {
        User findUser = mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(name))
                .build().unique();
        if (findUser != null) {
            findUser.setCustomizeName(newName);
            mUserDao.update(findUser);
        }
    }

    public String getProfile(String name) {
        User findUser = mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(name))
                .build().unique();
        return findUser.getProfile();
    }

    public void setProfile(String name, String profile) {
        User findUser = mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(name))
                .build().unique();
        if (findUser != null) {
            findUser.setProfile(profile);
            mUserDao.update(findUser);
        }
    }

    public void changePassword(String name, String newPassword) {
        User findUser = mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(name))
                .build().unique();
        if (findUser != null) {
            findUser.setPassword(newPassword);
            mUserDao.update(findUser);
        }
    }

    public void deleteUser(String name) {
        mCollectionDao.queryBuilder().where(CollectionDao.Properties.Name.eq(name))
                .buildDelete().executeDeleteWithoutDetachingEntities();
        mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(name))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public boolean insertQuick(String title, String url) {
        Quick quick = new Quick(null, title, url);
        Quick res = mQuickDao.queryBuilder().where(QuickDao.Properties.Title.eq(title),
                QuickDao.Properties.Url.eq(url)).build().unique();
        if (res != null) {
            return false;
        } else {
            mQuickDao.insert(quick);
            return true;
        }
    }

    public List<Quick> getQuick() {
        return mQuickDao.loadAll();
    }

    public void deleteQuick(String title, String url) {
        mQuickDao.queryBuilder().where(QuickDao.Properties.Title.eq(title),
                QuickDao.Properties.Url.eq(url))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public void changeQuick(String title, String url, String newTitle, String newUrl) {
        Quick res = mQuickDao.queryBuilder().where(QuickDao.Properties.Title.eq(title),
                QuickDao.Properties.Url.eq(url)).build().unique();
        res.setTitle(newTitle);
        res.setUrl(newUrl);
        mQuickDao.update(res);
    }

    public static DBController getInstance(Context context){
        if(sDbController == null){
            synchronized (DBController.class){
                if(sDbController == null){
                    sDbController = new DBController(context);
                }
            }
        }
        return sDbController;
    }

}
