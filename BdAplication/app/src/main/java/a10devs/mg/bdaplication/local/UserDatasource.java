package a10devs.mg.bdaplication.local;

import java.util.List;

import a10devs.mg.bdaplication.db.IUserDatasource;
import a10devs.mg.bdaplication.model.User;
import io.reactivex.Flowable;

public class UserDatasource implements IUserDatasource {

    private UserDAO userDAO;
    public static UserDatasource mInstance;

    public UserDatasource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserDatasource getInstance(UserDAO userdao)
    {
        if (mInstance==null)
        {
            mInstance=new UserDatasource(userdao);
        }
        return mInstance;
    }


    @Override
    public Flowable<User> getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public void insertUser(User... users) {
        userDAO.insertUser(users);
    }

    @Override
    public void updateUser(User... users) {
        userDAO.updateUser(users);
    }

    @Override
    public void deleteUser(User... users) {
        userDAO.deleteUser(users);
    }

    @Override
    public void deleteAllUsers() {
        userDAO.deleteAllUsers();
    }
}
