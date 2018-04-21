package app.fitplus.health.ui.user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import app.fitplus.health.R;
import app.fitplus.health.system.Application;
import app.fitplus.health.system.component.CustomToast;
import app.fitplus.health.system.component.InternetDialog;
import app.fitplus.health.util.InputChecks;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static app.fitplus.health.system.Application.CONNECTED;
import static app.fitplus.health.system.Application.getUser;

public class UpdateUserActivity extends RxAppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.name_container)
    TextInputLayout nameContainer;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_settings);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name.setText(getUser().getDisplayName());
        if (getUser().getProviderId().equals(PhoneAuthProvider.PROVIDER_ID)) {
            phone.setText(getUser().getPhoneNumber());
        } else {
            phone.setText(getUser().getEmail());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @OnTextChanged(value = R.id.name, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onNameChange(CharSequence charSequence, int i, int i1, int i2) {
        InputChecks.ShowNameErrors(name.getText().toString(), nameContainer);
    }

    @OnClick(R.id.update_settings)
    public void update() {
        if (!CONNECTED) {
            new InternetDialog(this);
            return;
        }

        if (name.getText().toString().equals("") || nameContainer.isErrorEnabled()) {
            return;
        }

        progressDialog = ProgressDialog.show(this, "", getString(R.string.msg_profile_update));
        progressDialog.show();

        UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        FirebaseUser user = getUser();
        user.updateProfile(updates)
                .addOnCompleteListener(task -> {
                    Application.user = FirebaseAuth.getInstance().getCurrentUser();
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        setResult(RESULT_OK);
                        finish();
                    } else new CustomToast(this, this, getString(R.string.error_failed_update))
                            .show();
                });
    }
}
