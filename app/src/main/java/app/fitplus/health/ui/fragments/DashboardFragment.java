package app.fitplus.health.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.jetbrains.annotations.NotNull;

import app.fitplus.health.R;
import app.fitplus.health.data.DataProvider;
import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.Stats;
import app.fitplus.health.system.ClearMemory;
import app.fitplus.health.ui.MainActivity;
import app.fitplus.health.ui.tracking.TrackingActivity;
import app.fitplus.health.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static app.fitplus.health.ui.MainActivity.REFRESH_DATA;

public class DashboardFragment extends Fragment implements ClearMemory {

    @BindView(R.id.circle_progress)
    CircleProgress progress;
    @BindView(R.id.calorie_text)
    TextView calorieText;
    @BindView(R.id.steps_text)
    TextView stepsText;
    @BindView(R.id.distance_text)
    TextView distanceCovered;

    @BindView(R.id.adView)
    AdView mAdView;

    private DataProvider dataProvider;
    private Unbinder unbinder;

    private InterstitialAd mInterstitialAd;

    @NonNull
    public static DashboardFragment newInstance(final DataProvider dataProvider) {
        DashboardFragment fragment = new DashboardFragment();
        fragment.dataProvider = dataProvider;
        return fragment;
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
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = new Bundle();
        extras.putBoolean("is_designed_for_families", true);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mAdView != null) {
                    mAdView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (mAdView != null) {
                    mAdView.setVisibility(View.GONE);
                }
            }
        });

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3251178974833355/6522036044");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (REFRESH_DATA) refreshData();
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
                        startActivity(new Intent(getActivity(), TrackingActivity.class)
                                .putExtra("dataProvider", dataProvider));
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }

                });
            } else {
                startActivity(new Intent(getActivity(), TrackingActivity.class)
                        .putExtra("dataProvider", dataProvider));
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    @OnClick(R.id.goals_button)
    public void openPersonalFragment() {
        if (isAdded()) ((MainActivity) getActivity()).openPersonalFragment();
    }

    @OnClick(R.id.facebook_share)
    public void shareOnFacebook() {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(getScreenShot(getView().getRootView()))
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareDialog = new ShareDialog(getActivity());
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }

    public Bitmap getScreenShot(@NotNull View view) {
        view.findViewById(R.id.facebook_share).setVisibility(View.GONE);
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        view.findViewById(R.id.facebook_share).setVisibility(View.VISIBLE);
        return bitmap;
    }

    public void onDataLoaded() {
        if (isAdded()) fillViews();
    }

    private void fillViews() {
        Stats stats = dataProvider.getStats();
        if (stats != null) {
            calorieText.setText(String.format("%s calories burned", String.valueOf(Math.round(stats.getCalorieBurned()))));
            stepsText.setText(String.format("%s steps taken", String.valueOf(Math.round(stats.getSteps()))));
            distanceCovered.setText(String.format("%s km covered", Util.to1Decimal(stats.getDistance())));

            Goals goal = dataProvider.getGoals();
            if (goal == null) {
                progress.setProgress(0);
                return;
            }

            progress.setProgress(Math.round(Util.getTotalProgress(stats, goal)));
        } else {
            calorieText.setHint(R.string.msg_no_activity);
            stepsText.setHint(R.string.msg_no_activity);
            distanceCovered.setHint(R.string.msg_no_activity);
        }
    }

    private void refreshData() {
        // TODO : when goals are updated
        fillViews();
        REFRESH_DATA = false;
    }
}