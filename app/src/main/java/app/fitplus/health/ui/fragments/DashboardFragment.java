package app.fitplus.health.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import app.fitplus.health.R;
import app.fitplus.health.data.DataManager;
import app.fitplus.health.data.model.Stats;
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

    @BindView(R.id.adView)
    AdView mAdView;

    private Stats userStats;

    private Unbinder unbinder;

    private InterstitialAd mInterstitialAd;

    @NonNull
    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    public DashboardFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);

        userStats = DataManager.getProgress(getActivity());

        if (userStats != null) {
            calorieText.setText(String.valueOf(userStats.getCalorieBurned()) + " calories burned");
            stepsText.setText(String.valueOf(userStats.getSteps()) + " steps taken");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progress.setProgress(40);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3251178974833355/6522036044");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
        mInterstitialAd = null;
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
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();

                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(getActivity(), TrackingActivity.class));
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }

                });
            } else {
                startActivity(new Intent(getActivity(), TrackingActivity.class));
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }
    }
}