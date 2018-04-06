package app.fitplus.health.ui.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import app.fitplus.health.data.Goals;
import app.fitplus.health.data.Progress;
import app.fitplus.health.data.User;
import app.fitplus.health.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static app.fitplus.health.data.DataManager.GOALS;
import static app.fitplus.health.data.DataManager.PROGRESS;
import static app.fitplus.health.data.DataManager.USER;
import static app.fitplus.health.data.DataManager.USER_PREFS_NAME;
import static app.fitplus.health.data.DataManager.isLogged;
import static app.fitplus.health.system.Application.user;

public class LoginActivity extends RxAppCompatActivity {

    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.password)
    TextView password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.close)
    public void close() {
        finish();
    }


    /**
     * Web Service was having issues, had to hard code the user login
     */
    @OnClick(R.id.login)
    public void login() {

        if (password.getText().toString().equals("") || email.getText().toString().equals(""))
            return;

        user = new User();
        user.email = email.getText().toString();
        user.name = "Adil Iqbal";
        user.weight = 50;
        user.phone = "03361234567";
        user.username = "adil";

        try {
            DB snappydb = DBFactory.open(LoginActivity.this, USER_PREFS_NAME);
            snappydb.put(USER, user);
            snappydb.put(isLogged, true);
            snappydb.put(PROGRESS, new Progress());
            snappydb.put(GOALS, new Goals());
            snappydb.close();

            finishParent(true, false);
        } catch (SnappydbException e) {
            Timber.e(e);
        }


//        UserService userService = RetrofitClient.getClient().create(UserService.class);
//        Observable<Response<String>> login = userService.login(inputs[0], inputs[1]);
//        login.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(bindToLifecycle())
//                .doOnSubscribe(disposable -> progressDialog = ProgressDialog.show(this, "", "Signing in"))
//                .doFinally(() -> progressDialog.dismiss())
//                .subscribe(s -> {
//                    if (s.isSuccessful()) {
//
//                        user = new User();
//                        user.email = inputs[0];
//
//                        DB snappydb = DBFactory.open(LoginActivity.this, USER_PREFS_NAME);
//                        snappydb.put(USER, user);
//                        snappydb.put(isLogged, true);
//                        snappydb.put(PROGRESS, new Progress());
//                        snappydb.put(GOALS, new Goals());
//                        snappydb.close();
//
//                        finishParent(true, false);
//                    }
//                }, e -> {
//                    Timber.e(e);
//
//                    // Retrofit Network Errors
//                    if (e instanceof SocketTimeoutException)
//                        new CustomToast(getBaseContext(), LoginActivity.this, "Timed out").show();
//                });
    }

    @OnClick(R.id.register)
    public void register() {
        finishParent(false, true);
    }

    public void finishParent(final boolean check, final boolean to_register) {
        if (to_register) setResult(20, getIntent());
        else if (check) setResult(10, getIntent());
        finish();
    }
}
