package app.fitplus.health.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;

import app.fitplus.health.R;
import app.fitplus.health.system.ClearMemory;
import app.fitplus.health.ui.fragments.AssistantFragment;
import app.fitplus.health.ui.fragments.DashboardFragment;
import app.fitplus.health.ui.fragments.ExploreFragment;
import app.fitplus.health.ui.fragments.PersonalFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        ClearMemory, AssistantFragment.OnDismissListener {

    private boolean DASHBOARD_FRAGMENT = true;

    private DashboardFragment dashboardFragment;
    private ExploreFragment exploreFragment;
    private PersonalFragment personalFragment;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Async map load
        new SupportMapFragment().getMapAsync(googleMap ->
                Timber.tag("MapManager").i("Initializing map on launch"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                if (dashboardFragment == null) dashboardFragment = DashboardFragment.newInstance();
                displayFragment(dashboardFragment);
                DASHBOARD_FRAGMENT = true;
                Timber.tag("Layout").d("Switching to home screen");
                return true;
            case R.id.navigation_explore:
                if (exploreFragment == null) exploreFragment = ExploreFragment.newInstance();
                DASHBOARD_FRAGMENT = false;
                displayFragment(exploreFragment);
                Timber.tag("Layout").d("Switching to explore screen");
                return true;
            case R.id.navigation_personal:
                if (personalFragment == null) personalFragment = PersonalFragment.newInstance();
                DASHBOARD_FRAGMENT = false;
                displayFragment(personalFragment);
                Timber.tag("Layout").d("Switching to personal screen");
                return true;
            case R.id.navigation_assistant:
                Assistant();
                return true;
        }
        return true;
    }

    private void Assistant() {
        AssistantFragment assistantFragment = AssistantFragment.newInstance(MainActivity.this,
                this, MainActivity.this);
        assistantFragment.show();
    }

    private void displayFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    public void clearMemory() {
        dashboardFragment = null;
        personalFragment = null;
        exploreFragment = null;
    }

    @Override
    public void onBackPressed() {
        if (!DASHBOARD_FRAGMENT) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            DASHBOARD_FRAGMENT = true;
        } else super.onBackPressed();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Timber.tag("Assistant").d("onAssistantDismiss");
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}