package app.fitplus.health.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import app.fitplus.health.R;
import app.fitplus.health.system.Application;
import app.fitplus.health.system.component.InternetDialog;
import app.fitplus.health.ui.MainActivity;
import app.fitplus.health.util.InputChecks;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static app.fitplus.health.system.Application.CONNECTED;
import static app.fitplus.health.system.Application.getUser;

public class CompleteRegistration extends RxAppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.name_container)
    TextInputLayout nameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_settings);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phone.setText(getUser().getPhoneNumber());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnTextChanged(value = R.id.name, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onNameChange(CharSequence charSequence, int i, int i1, int i2) {
        InputChecks.ShowNameErrors(phone.getText().toString(), nameContainer);
    }

    @OnClick(R.id.save)
    public void update() {
        if (!CONNECTED) {
            new InternetDialog(this);
            return;
        }

        if (name.getText().toString().equals("") || nameContainer.isErrorEnabled()) {
            return;
        }

        UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        FirebaseUser user = getUser();
        user.updateProfile(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                });
    }

    @OnClick(R.id.logout)
    public void logout() {
        Application.getInstance().Logout(this);
    }
}
