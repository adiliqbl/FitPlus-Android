package app.fitplus.health.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import app.fitplus.health.R;
import app.fitplus.health.data.AppDatabase;
import app.fitplus.health.data.DataProvider;
import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.Stats;
import app.fitplus.health.data.model.User;
import app.fitplus.health.system.Application;
import app.fitplus.health.system.ClearMemory;
import app.fitplus.health.ui.explore.ExploreFragment;
import app.fitplus.health.ui.fragments.AssistantFragment;
import app.fitplus.health.ui.fragments.DashboardFragment;
import app.fitplus.health.ui.fragments.PersonalFragment;
import app.fitplus.health.ui.tracking.TrackingActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static app.fitplus.health.data.FirebaseStorage.goalsReference;
import static app.fitplus.health.data.FirebaseStorage.statsReference;
import static app.fitplus.health.data.FirebaseStorage.usersReference;

public class MainActivity extends RxAppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        ClearMemory, AssistantFragment.AssistantListener {
    public static boolean REFRESH_DATA = false;
    private boolean DASHBOARD_FRAGMENT = true;
    private int FETCHED = 0;
    private boolean DATA_LOADED = false;

    private DashboardFragment dashboardFragment;
    private ExploreFragment exploreFragment;
    private PersonalFragment personalFragment;
    private Fragment active;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private DataProvider dataProvider = new DataProvider();
    private FragmentManager mFragmentManager;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        addHideFragment(exploreFragment = ExploreFragment.newInstance());
        addHideFragment(personalFragment = PersonalFragment.newInstance(dataProvider));

        dashboardFragment = DashboardFragment.newInstance(dataProvider);
        mFragmentManager.beginTransaction().add(R.id.frame_layout, dashboardFragment).commit();
        active = dashboardFragment;

        if (getIntent().getBooleanExtra("login", false)) {
            changeLoadingVisibility(View.VISIBLE);
            fetchData();
        } else loadData();

        // Async map load
        new SupportMapFragment().getMapAsync(googleMap ->
                Timber.tag("MapManager").i("Initializing map on launch"));

        MobileAds.initialize(this, "ca-app-pub-3251178974833355~6592011781");

        Observable.fromCallable(() -> AppDatabase.getSession(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(s -> {
                    if (s && dashboardFragment != null) {
                        dashboardFragment.beginActivity();
                    }
                });

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, data -> {
                    if (data == null) {
                        Timber.d("getInvitation: no data");
                        return;
                    }

                    // Get the deep link
                    Uri deepLink = data.getLink();

                    // Extract invite
                    FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                    if (invite != null) {
                        String invitationId = invite.getInvitationId();
                    }

                    // TODO : Test this
                    startActivity(new Intent(this, TrackingActivity.class));
                })
                .addOnFailureListener(this, e -> Timber.e("getDynamicLink:onFailure", e));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!DATA_LOADED || REFRESH_DATA) loadData();
    }

    private void addHideFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().add(R.id.frame_layout, fragment).hide(fragment).commit();
    }

    private void hideShowFragment(Fragment show) {
        if (active == show) return;
        mFragmentManager.beginTransaction().hide(active).show(show).commit();
        active = show;
    }

    public void fetchData() {
        FETCHED = 0;
        goalsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Goals goals = dataSnapshot.getValue(Goals.class);
                if (goals != null) {
                    Timber.d("Fetched Goals from online data : %s", goals);

                    AppDatabase.getInstance(MainActivity.this).goalsDao().add(goals)
                            .compose(bindToLifecycle())
                            .subscribe();

                    dataProvider.setGoals(goals);
                }

                FETCHED++;
                if (FETCHED == 3) dismissLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e(databaseError.toException());
            }
        });
        usersReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Timber.d("Fetched Users from online data : %s", user);

                    AppDatabase.getInstance(MainActivity.this).userDao().add(user)
                            .compose(bindToLifecycle())
                            .subscribe();

                    dataProvider.setUser(user);
                }

                FETCHED++;
                if (FETCHED == 3) dismissLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        statsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Stats stats = dataSnapshot.getValue(Stats.class);

                if (stats != null) {
                    Timber.d("Fetched Stats from online data : %s", stats);

                    AppDatabase.getInstance(MainActivity.this).statsDao().add(stats)
                            .compose(bindToLifecycle())
                            .subscribe();

                    dataProvider.setStats(stats);
                }

                FETCHED++;
                if (FETCHED == 3) dismissLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void dismissLoading() {
        Timber.d("Data fetched from server");
        findViewById(R.id.progress).setVisibility(View.GONE);
        changeLoadingVisibility(View.GONE);

        if (dashboardFragment != null) dashboardFragment.onDataLoaded();
        if (personalFragment != null) personalFragment.onDataLoaded();
    }

    private void changeLoadingVisibility(final int VISIBILITY) {
        findViewById(R.id.progress).setVisibility(VISIBILITY);
        findViewById(R.id.progress_background).setVisibility(VISIBILITY);
    }

    @SuppressLint("CheckResult")
    public void loadData() {
        Observable.fromCallable(() -> {
            final String id = Application.getUser().getUid();
            dataProvider.setGoals(AppDatabase.getInstance(this).goalsDao().getGoalsById(id));
            dataProvider.setStats(AppDatabase.getInstance(this).statsDao().getStatsById(id));
            dataProvider.setUser(AppDatabase.getInstance(this).userDao().getUserById(id));
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (dashboardFragment != null) dashboardFragment.onDataLoaded();
                    if (personalFragment != null) personalFragment.onDataLoaded();

                    DATA_LOADED = true;
                }, Timber::e);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                hideShowFragment(dashboardFragment);
                DASHBOARD_FRAGMENT = true;
                return true;
            case R.id.navigation_explore:
                hideShowFragment(exploreFragment);
                exploreFragment.onViewShown();
                DASHBOARD_FRAGMENT = false;
                return true;
            case R.id.navigation_personal:
                hideShowFragment(personalFragment);
                DASHBOARD_FRAGMENT = false;
                return true;
            case R.id.navigation_assistant:
                Assistant();
                return false;
        }
        return false;
    }

    private void Assistant() {
        AssistantFragment assistantFragment = AssistantFragment.newInstance(MainActivity.this, dataProvider, this);
        assistantFragment.show();
    }

    @Override
    public void clearMemory() {
        dashboardFragment = null;
        personalFragment = null;
        exploreFragment = null;

        dataProvider = null;
    }

    @Override
    public void onBackPressed() {
        if (!DASHBOARD_FRAGMENT) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            DASHBOARD_FRAGMENT = true;
        } else super.onBackPressed();
    }

    public void logout() {
        changeLoadingVisibility(View.VISIBLE);
        Observable.fromCallable(() -> {
            AppDatabase.getInstance(this).clearAllTables();
            return true;
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .doOnComplete(() -> {
                    changeLoadingVisibility(View.GONE);
                    Application.getInstance().Logout(this);
                })
                .subscribe();
    }

    public void openPersonalFragment() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_personal);
    }

    public void updateProgress() {
        if (dashboardFragment != null) {
            dashboardFragment.onDataLoaded();
        }
    }

    @Override
    public void onAssistantClosed() {

    }

    @Override
    public void onCommand(int COMMAND_TYPE) {
        if (COMMAND_TYPE == 1) {
            if (dashboardFragment != null)
                dashboardFragment.beginActivity();
        }
    }
}