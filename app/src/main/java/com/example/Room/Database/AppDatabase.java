package com.example.Room.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.Room.Converters;
import com.example.Room.Dao.DbImageDao;
import com.example.Room.Dao.DbMlkitLabelDao;
import com.example.Room.Entity.DbImage;
import com.example.Room.Entity.DbImageGroup;
import com.example.Room.Entity.DbLabel;
import com.example.Room.Entity.DbPost;
import com.example.Room.Entity.DbRepImage;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {
                DbImage.class,
                DbLabel.class,
                DbImageGroup.class,
                DbRepImage.class,
                DbPost.class
            },
          version = 1,
          exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DbImageDao imageDao();
    public abstract DbMlkitLabelDao mlkitLabelDao();

    private static volatile AppDatabase sInstance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                   sInstance = Room
                        .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "liflow_database")
                        .build();
                }
            }
        }

        return sInstance;
    }


    /**
     * Inserts the dummy data into the database if it is currently empty.
     */
    public void populateInitialData() {
        // UI test를 위해서 dummy data 생성할 수 있게 구현
        if (imageDao().count() == 0) {
            runInTransaction(() -> {

                List<DbImage> dbImages = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    DbImage dbImage = new DbImage();
                    dbImage.path = "/" + i;

                    dbImages.add(dbImage);
                }
                imageDao().insertAll(dbImages.toArray(new DbImage[dbImages.size()]));


                List<DbImage> insertedDbImages = imageDao().getAll();
                List<DbLabel> labels = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    DbLabel label = new DbLabel();

                    int imageId = insertedDbImages.get(i).id;
                    label.imageId = imageId;
                    label.confidence = 0.5;
                    label.entityId = "/123456";
                    label.text = "HelloWorld";

                    labels.add(label);
                }
                mlkitLabelDao().insertAll(labels.toArray(new DbLabel[labels.size()]));
            });
        }
    }
}