package a10devs.mg.bdaplication.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import a10devs.mg.bdaplication.model.User;

import static a10devs.mg.bdaplication.local.UserDatabase.DATABASE_VERSION;

@Database(entities = User.class, version = DATABASE_VERSION)
public abstract class UserDatabase extends RoomDatabase{



    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EDMT-Database-Room";
    private static UserDatabase mInstance;

    public abstract UserDAO userDAO();

    public static UserDatabase getmInstance(Context context)
    {
        if (mInstance==null){mInstance= Room.databaseBuilder(context, UserDatabase.class, DATABASE_NAME)
        .fallbackToDestructiveMigration()
                .build();
        }
        return mInstance;

    }
}
