package a10devs.mg.bdaplication.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import a10devs.mg.bdaplication.model.User;
import io.reactivex.Flowable;

@Dao
public interface UserDAO {

    @Query("SELECT*FROM users WHERE id=:userId")
    Flowable<User> getUserById (int userId);

    @Query("SELECT*FROM users")
    Flowable<List<User>> getAllUsers ();

    @Insert
    public void insertUser(User...users);

    @Update
    public void updateUser(User...users);

    @Delete
    public void deleteUser(User...users);

    @Query("DELETE FROM users")
    public void deleteAllUsers();


}
