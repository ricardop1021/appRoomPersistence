package a10devs.mg.bdaplication.db;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import a10devs.mg.bdaplication.model.User;
import io.reactivex.Flowable;

public interface IUserDatasource {


    Flowable<User> getUserById (int userId);


    Flowable<List<User>> getAllUsers ();


    public void insertUser(User...users);


    public void updateUser(User...users);

    public void deleteUser(User...users);


    public void deleteAllUsers();

}
