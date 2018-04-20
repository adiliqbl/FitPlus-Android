package app.fitplus.health.ui.user;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import com.google.firebase.auth.PhoneAuthProvider;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import app.fitplus.health.R;
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
    @BindView(R.id.phone_container)
    TextInputLayout phoneContainer;

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

    @OnTextChanged(value = R.id.phone, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onPhoneChange(CharSequence charSequence, int i, int i1, int i2) {
        InputChecks.ShowPhoneErrors(phone.getText().toString(), phoneContainer);
    }

    @OnClick(R.id.update_settings)
    public void update() {
        if (!CONNECTED) {
            new InternetDialog(this);
            return;
        }

        if (name.getText().toString().equals("")
                || phone.getText().toString().equals("")
                || phoneContainer.isErrorEnabled()) {
            return;
        }

        // TODO : Call update api here

        finishParent(false);
    }

    private void finishParent(final boolean UPDATE) {
        if (UPDATE) setResult(RESULT_OK, getIntent().putExtra("updated", UPDATE));
        finish();
    }
}
