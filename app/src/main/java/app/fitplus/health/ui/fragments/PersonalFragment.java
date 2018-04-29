package app.fitplus.health.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.PhoneAuthProvider;

import app.fitplus.health.R;
import app.fitplus.health.data.DataProvider;
import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.User;
import app.fitplus.health.system.ClearMemory;
import app.fitplus.health.system.component.CustomToast;
import app.fitplus.health.ui.MainActivity;
import app.fitplus.health.ui.user.UpdateUserActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
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
    @BindView(R.id.session_length)
    EditText sessionLength;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.email)
    TextView email;

    private Unbinder unbinder;
    private DataProvider dataProvider;

    private Goals tempGoal;

    @NonNull
    public static PersonalFragment newInstance(final DataProvider dataProvider) {
        PersonalFragment fragment = new PersonalFragment();
        fragment.dataProvider = dataProvider;
        return fragment;
    }

    public PersonalFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        unbinder = ButterKnife.bind(this, view);
        setRetainInstance(true);
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name.setText(getUser().getDisplayName());
        if (getUser().getProviderId().equals(PhoneAuthProvider.PROVIDER_ID)) {
            email.setText(getUser().getPhoneNumber());
        } else email.setText(getUser().getEmail());
        tempGoal = null;
    }

    public void onDataLoaded() {
        if (isAdded()) fillViews();
    }

    private void fillViews() {
        if (dataProvider.getGoals() != null) {
            Goals goals = dataProvider.getGoals();
            if (goals.getCalorie() != 0) {
                calorieGoal.setText(String.format("%s calories", String.valueOf(goals.getCalorie())));
            }

            if (goals.getDistance() != 0) {
                distanceGoal.setText(String.format("%s km", String.valueOf(goals.getDistance())));
            }

            if (goals.getSteps() != 0) {
                stepGoal.setText(String.format("%s steps", String.valueOf(goals.getSteps())));
            }
        }

        if (dataProvider.getUser() != null) {
            weight.setText(Integer.toString(dataProvider.getUser().getWeight()));
            int sL = dataProvider.getUser().getSessionLength();
            if (sL > 0) sessionLength.setText(Integer.toString(sL));
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
                if (resultCode == RESULT_OK) {
                    name.setText(getUser().getDisplayName());
                    if (isAdded()) {
                        new CustomToast(getActivity(), getActivity(), getString(R.string.msg_profile_update_success))
                                .show();
                    }
                }
                break;
        }
    }

    @OnClick(R.id.cal_icon_container)
    public void setCalorieGoal() {
        @SuppressLint("SetTextI18n") AddGoalFragment addGoalFragment = AddGoalFragment.newInstance(getActivity(), value -> {
            calorieGoal.setText(String.valueOf(value) + " calories");

            if (tempGoal == null) {
                tempGoal = new Goals(-1, -1, -1);
                getView().findViewById(R.id.save_goals).setVisibility(View.VISIBLE);
            }
            tempGoal.setCalorie(Integer.valueOf(value));

            getView().findViewById(R.id.save_goals).setVisibility(View.VISIBLE);
        }, 1);
        addGoalFragment.show();
    }

    @OnClick(R.id.steps_icon_container)
    public void setStepGoal() {
        @SuppressLint("SetTextI18n") AddGoalFragment addGoalFragment = AddGoalFragment.newInstance(getActivity(), value -> {
            stepGoal.setText(String.valueOf(value) + " steps");

            if (tempGoal == null) {
                tempGoal = new Goals(-1, -1, -1);
                getView().findViewById(R.id.save_goals).setVisibility(View.VISIBLE);
            }
            tempGoal.setSteps(Integer.valueOf(value));

            getView().findViewById(R.id.save_goals).setVisibility(View.VISIBLE);
        }, 2);
        addGoalFragment.show();
    }

    @OnClick(R.id.distance_icon_container)
    public void setDistanceGoal() {
        @SuppressLint("SetTextI18n") AddGoalFragment addGoalFragment = AddGoalFragment.newInstance(getActivity(), value -> {
            distanceGoal.setText(value + " km");

            if (tempGoal == null) {
                tempGoal = new Goals(-1, -1, -1);
                getView().findViewById(R.id.save_goals).setVisibility(View.VISIBLE);
            }
            tempGoal.setDistance(Integer.valueOf(value));
        }, 3);
        addGoalFragment.show();
    }

    @OnClick(R.id.save_goals)
    public void saveGoals() {
        boolean add = false;

        if (dataProvider.getGoals() == null) {
            dataProvider.setGoals(new Goals());
            dataProvider.getGoals().setUserId(getUser().getUid());
            add = true;
        }

        if (tempGoal.getDistance() != -1) {
            dataProvider.getGoals().setDistance(tempGoal.getDistance());
        }
        if (tempGoal.getCalorie() != -1) {
            dataProvider.getGoals().setCalorie(tempGoal.getCalorie());
        }
        if (tempGoal.getSteps() != -1) {
            dataProvider.getGoals().setSteps(tempGoal.getSteps());
        }

        dataProvider.updateGoals(add, getActivity());
        getView().findViewById(R.id.save_goals).setVisibility(View.GONE);

        // Updating progress in dashboard fragment
        ((MainActivity) getActivity()).updateProgress();
    }

    @OnEditorAction(R.id.weight)
    boolean saveWeight(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (weight.getText().toString().equals("")) return true;

            final int w = Integer.valueOf(weight.getText().toString());

            boolean add = false;
            if (dataProvider.getUser() == null) {
                dataProvider.setUser(new User());
                dataProvider.getUser().setId(getUser().getUid());
                add = true;
            }

            dataProvider.getUser().setWeight(w);
            dataProvider.updateUser(add, getActivity());

            if (w == 0) weight.setText("");

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(weight.getWindowToken(), 0);

            return true;
        }

        return false;
    }

    @OnEditorAction(R.id.session_length)
    boolean saveSessionLength(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (sessionLength.getText().toString().equals("")) return true;

            final int sess = Integer.valueOf(sessionLength.getText().toString());

            if (sess < 5) {
                new CustomToast(getActivity(), getActivity(), "Minimum session length is 5 minutes")
                        .show();
                return true;
            }

            boolean add = false;
            if (dataProvider.getUser() == null) {
                dataProvider.setUser(new User());
                dataProvider.getUser().setId(getUser().getUid());
                add = true;
            }

            dataProvider.getUser().setSessionLength(sess);
            dataProvider.updateUser(add, getActivity());

            if (sess == 0) sessionLength.setText("");

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(sessionLength.getWindowToken(), 0);

            return true;
        }

        return false;
    }

    @OnClick(R.id.edit_button)
    public void onEditClick() {
        startActivityForResult(new Intent(getActivity(), UpdateUserActivity.class), 1);
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.logout)
    public void logout() {
        ((MainActivity) getActivity()).logout();
    }
}
