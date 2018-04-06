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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static app.fitplus.health.data.DataManager.GOALS;
import static app.fitplus.health.data.DataManager.PROGRESS;
import static app.fitplus.health.data.DataManager.USER;
import static app.fitplus.health.data.DataManager.USER_PREFS_NAME;
import static app.fitplus.health.data.DataManager.isLogged;
import static app.fitplus.health.system.Application.user;

public class RegisterActivity extends RxAppCompatActivity {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.password)
    TextView password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
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

//    @OnTextChanged(value = R.id.email, callback = OnTextChanged.Callback.TEXT_CHANGED)
//    public void onEmailChange(CharSequence charSequence, int i, int i1, int i2) {
//        InputChecks.ShowPhoneErrors(email.getText().toString(), emailContainer);
//    }

    @OnClick(R.id.register)
    public void register() {
        final String[] inputs = {name.getText().toString(), email.getText().toString(), username.getText().toString(),
                phone.getText().toString(), password.getText().toString()};

        for (String input : inputs) {
            if (input == null || input.equals(""))
                return;
        }

        user = new User();
        user.name = inputs[0];
        user.email = inputs[1];
        user.username = inputs[2];
        user.phone = inputs[3];

        try {
            DB snappydb = DBFactory.open(RegisterActivity.this, USER_PREFS_NAME);
            snappydb.put(USER, user);
            snappydb.put(isLogged, true);
            snappydb.put(PROGRESS, new Progress());
            snappydb.put(GOALS, new Goals());
            snappydb.close();

            finishParent(true);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

//        UserService userService = RetrofitClient.getClient().create(UserService.class);
//        Observable<Response<String>> login = userService.register(inputs[1], inputs[2], inputs[0],
//                inputs[4], inputs[3]);
//        login.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(bindToLifecycle())
//                .doOnSubscribe(disposable -> progressDialog = ProgressDialog.show(this, "", "Registering"))
//                .doFinally(() -> progressDialog.dismiss())
//                .subscribe(s -> {
//                    if (s.isSuccessful()) {
//
//                        DB snappydb = DBFactory.open(RegisterActivity.this, USER_PREFS_NAME);
//                        snappydb.put(USER, user);
//                        snappydb.put(isLogged, true);
//                        snappydb.put(PROGRESS, new Progress());
//                        snappydb.put(GOALS, new Goals());
//                        snappydb.close();
//
//                        finishParent(true);
//                    }
//                }, e -> {
//                    Timber.e(e);
//
//                    // Retrofit Network Errors
//                    if (e instanceof SocketTimeoutException)
//                        new CustomToast(getBaseContext(), RegisterActivity.this, "Timed out").show();
//                });
    }

    public void finishParent(final boolean check) {
        if (check) setResult(10, getIntent());
        finish();
    }
}
