package a10devs.mg.bdaplication;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import a10devs.mg.bdaplication.db.UserRepository;
import a10devs.mg.bdaplication.local.UserDatabase;
import a10devs.mg.bdaplication.local.UserDatasource;
import a10devs.mg.bdaplication.model.User;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ListView lstUsers;
    private FloatingActionButton fab_add;
    private boolean flag= true;

    //todo -adapter

    List<User> userList=new ArrayList<>();
    ArrayAdapter adapter;

    // todo - Database

    private CompositeDisposable compositeDisposable;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compositeDisposable=new CompositeDisposable();

        lstUsers=(ListView) findViewById(R.id.lstUsers);
        fab_add=(FloatingActionButton) findViewById(R.id.fab);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, userList);
        registerForContextMenu(lstUsers);
        lstUsers.setAdapter(adapter);


        // -todo- Database

        UserDatabase userDatabase=UserDatabase.getmInstance(this); // crate database
        userRepository=UserRepository.getInstance(UserDatasource.getInstance(userDatabase.userDAO()));

        // -todo- carregar os dados para Database
        loadData();


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-todo- adicional novo usuario com random email
                Disposable disposable=io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        // -todo - Adicionair novos usuários nesta linha

                        User user=new User("Ricardo_Teste",
                                UUID.randomUUID().toString()+"@gmail.com");
                        //userList.add(user);
                        userRepository.insertUser(user);
                        e.onComplete();
                    }
                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer() {
                                       @Override
                                       public void accept(Object o) throws Exception {
                                           Toast.makeText(MainActivity.this, "Usuário Adicionado com sucesso", Toast.LENGTH_SHORT).show();
                                       }
                                   }, new Consumer<Throwable>() {
                                       @Override
                                       public void accept(Throwable throwable) throws Exception {
                                           Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                       }
                                   }, new Action() {
                                       @Override
                                       public void run() throws Exception {
                                           loadData(); //-todo- Atualizar data
                                       }
                                   }


                        );
            }
        });


    }

    private void loadData() {
        // - todo - use RXJava
        Disposable disposable=userRepository.getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        onGetAllUserSuccess(users);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void onGetAllUserSuccess(List<User> users) {
        userList.clear();
        userList.addAll(users);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.menu_clear:
                deleteAllUsers();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllUsers() {
        Disposable disposable=io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                // -todo - Chamar metodo para deletar todos os usuários


                userRepository.deleteAllUsers();
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                               @Override
                               public void accept(Object o) throws Exception {
                                   Toast.makeText(MainActivity.this, "base de dados apagado com sucesso", Toast.LENGTH_SHORT).show();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   loadData(); //-todo- Atualizar data
                               }
                           }


                );
        compositeDisposable.add(disposable);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Selecione a ação");
        menu.add(Menu.NONE, 0, Menu.NONE, "Update");
        menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final User user = userList.get(info.position);
        switch (item.getItemId())
        {
            case 0:
            {
                final EditText editText=new EditText(MainActivity.this);
                editText.setText(user.getNome());
                editText.setHint("entre com o nome.");
                new AlertDialog.Builder(MainActivity.this)
                .setTitle("Editar")
                        .setMessage("Edite o nome do usuário")
                        .setView(editText)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(editText.getText().toString()))
                                {return;}
                                else {user.setNome(editText.getText().toString());}
                                updateUser(user);

                            }
                        }).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            }
            break;
            case 1:
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Deletar")
                        .setMessage("Você esta deletando o usuário"+user.toString())
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        deleteUser(user);

                                    }
                                }).setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
            break;


        }//fim do switch
        return true;
    }

    private void updateUser(final User user) {
        Disposable disposable=io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                // -todo - Adicionair novos usuários nesta linha


                userRepository.updateUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                               @Override
                               public void accept(Object o) throws Exception {
                                   Toast.makeText(MainActivity.this, "Usuário Adicionado com sucesso", Toast.LENGTH_SHORT).show();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   loadData(); //-todo- Atualizar data
                               }
                           }


                );
        compositeDisposable.add(disposable);
    }

    private void deleteUser(final User user) {
        Disposable disposable=io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                // -todo - Adicionair novos usuários nesta linha


                userRepository.deleteUser(user);
                e.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                               @Override
                               public void accept(Object o) throws Exception {
                                   Toast.makeText(MainActivity.this, "Usuário Adicionado com sucesso", Toast.LENGTH_SHORT).show();
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   loadData(); //-todo- Atualizar data
                               }
                           }


                );
    }


}
