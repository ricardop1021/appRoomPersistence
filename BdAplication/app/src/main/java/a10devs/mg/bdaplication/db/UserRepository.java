package a10devs.mg.bdaplication.db;

import java.util.List;

import a10devs.mg.bdaplication.model.User;
import io.reactivex.Flowable;

public class UserRepository implements IUserDatasource {

    private IUserDatasource mLocalDatasource;
    private static UserRepository mInstance;

    public UserRepository(IUserDatasource mLocalDatasource) {
        this.mLocalDatasource = mLocalDatasource;
    }
    public static UserRepository getInstance(IUserDatasource mLocalDatasource)
    {
        if (mInstance==null)
        {
            mInstance=new UserRepository(mLocalDatasource);
        }
        return mInstance;
    }

    @Override
    public Flowable<User> getUserById(int userId) {
        return mLocalDatasource.getUserById(userId);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return mLocalDatasource.getAllUsers();
    }

    @Override
    public void insertUser(User... users) {
        mLocalDatasource.insertUser(users);
    }

    @Override
    public void updateUser(User... users) {
        mLocalDatasource.updateUser(users);
    }

    @Override
    public void deleteUser(User... users) {
        mLocalDatasource.deleteUser(users);
    }

    @Override
    public void deleteAllUsers() {
        mLocalDatasource.deleteAllUsers();
    }
}
