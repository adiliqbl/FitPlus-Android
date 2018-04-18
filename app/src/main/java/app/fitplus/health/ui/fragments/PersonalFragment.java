package app.fitplus.health.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.PhoneAuthProvider;

import app.fitplus.health.R;
import app.fitplus.health.data.DataManager;
import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.Health;
import app.fitplus.health.system.Application;
import app.fitplus.health.system.ClearMemory;
import app.fitplus.health.system.component.CustomToast;
import app.fitplus.health.ui.user.UpdateUserActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static app.fitplus.health.system.Application.getUser;

public class PersonalFragment extends Fragment implements ClearMemory {

    @BindView(R.id.calorie_burned)
    TextView calorieGoal;
    @BindView(R.id.total_steps)
    TextView stepGoal;
    @BindView(R.id.distance_covered)
    TextView distanceGoal;
    @BindView(R.id.weight)
    EditText weight;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;

    private Unbinder unbinder;

    private Goals goals = null;
    private Health health = null;

    @NonNull
    public static PersonalFragment newInstance() {
        return new PersonalFragment();
    }

    public PersonalFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goals = DataManager.getGoals(getActivity());

        name.setText(getUser().getDisplayName());

        if (getUser().getProviderId().equals(PhoneAuthProvider.PROVIDER_ID)) {
            email.setText(getUser().getPhoneNumber());
        } else email.setText(getUser().getEmail());

        if (goals == null) goals = new Goals();
        if (goals.getCalorie() != 0) {
            calorieGoal.setText(String.valueOf(goals.getCalorie()) + " calorieBurned");
        }

        if (goals.getDistance() != 0) {
            distanceGoal.setText(String.valueOf(goals.getDistance()) + " km");
        }

        if (goals.getSteps() != 0) {
            stepGoal.setText(String.valueOf(goals.getSteps()) + " steps");
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) unbinder.unbind();
        clearMemory();
        super.onDestroyView();
    }

    @Override
    public void clearMemory() {
        unbinder = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && data.getBooleanExtra("update", false)) {
                    new CustomToast(getActivity(), getActivity(), "Profile updated successfully").show();
                }
                break;
        }
    }

    @OnClick(R.id.cal_icon_container)
    public void setCalorieGoal() {
        @SuppressLint("SetTextI18n") AddGoalFragment addGoalFragment = AddGoalFragment.newInstance(getActivity(), value -> {
            // TODO : add calorieBurned goal here
            calorieGoal.setText(String.valueOf(value) + " calorieBurned");

            goals.setCalorie(Integer.valueOf(value));
            DataManager.saveGoals(getActivity(), goals);
        }, 1);
        addGoalFragment.show();
    }

    @OnClick(R.id.steps_icon_container)
    public void setStepGoal() {
        @SuppressLint("SetTextI18n") AddGoalFragment addGoalFragment = AddGoalFragment.newInstance(getActivity(), value -> {
            // TODO : add step goal here
            calorieGoal.setText(String.valueOf(value) + " steps");

            goals.setSteps(Integer.valueOf(value));
            DataManager.saveGoals(getActivity(), goals);
        }, 2);
        addGoalFragment.show();
    }

    @OnClick(R.id.distance_icon_container)
    public void setDistanceGoal() {
        @SuppressLint("SetTextI18n") AddGoalFragment addGoalFragment = AddGoalFragment.newInstance(getActivity(), value -> {
            calorieGoal.setText(String.valueOf(value) + " distance");

            goals.setDistance(Integer.valueOf(value));
            DataManager.saveGoals(getActivity(), goals);
        }, 3);
        addGoalFragment.show();
    }

    @OnTextChanged(value = R.id.weight, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void saveWeight(Editable editable) {
        if (weight.getText().toString().equals("")) return;

//        user.setWeight(Integer.valueOf(weight.getText().toString()));
    }

    @OnClick(R.id.edit_button)
    public void onEditClick() {
        startActivityForResult(new Intent(getActivity(), UpdateUserActivity.class), 1);
    }

    @OnClick(R.id.logout)
    public void logout() {
        Application.getInstance().Logout(getActivity());
    }
}
