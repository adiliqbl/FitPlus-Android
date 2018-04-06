package app.fitplus.health.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;

import app.fitplus.health.data.DataManager;
import app.fitplus.health.data.Progress;
import app.fitplus.health.R;
import app.fitplus.health.system.ClearMemory;
import app.fitplus.health.ui.tracking.TrackingActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DashboardFragment extends Fragment implements ClearMemory {

    @BindView(R.id.circle_progress)
    CircleProgress progress;
    @BindView(R.id.calorie_text)
    TextView calorieText;
    @BindView(R.id.steps_text)
    TextView stepsText;

    private Progress userProgress;

    private Unbinder unbinder;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    public DashboardFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);

        userProgress = DataManager.getProgress(getActivity());

        if (userProgress != null) {
            calorieText.setText(String.valueOf(userProgress.calorie) + " calories burned");
            stepsText.setText(String.valueOf(userProgress.steps) + " steps taken");
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progress.setProgress(40);
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

    @OnClick(R.id.calorie)
    public void showCalorieDetails() {
        //TODO : message dialog
    }

    @OnClick(R.id.steps)
    public void showStepsDetails() {
        //TODO : message dialog
    }

    @OnClick(R.id.start_activity)
    public void beginActivity() {
        if (isAdded()) {
            startActivity(new Intent(getActivity(), TrackingActivity.class));
        }
    }
}