package app.fitplus.health.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import app.fitplus.health.R;
import app.fitplus.health.data.model.Nutrient;
import app.fitplus.health.ui.explore.NutrientClickListener;
import app.fitplus.health.ui.explore.NutrientListAdapter;

import static app.fitplus.health.data.FirebaseStorage.nutrientsReference;

public class ExploreFragment extends Fragment {

    private boolean LOADED = false;
    public static RequestOptions REQUEST_OPTION = new RequestOptions()
            .placeholder(R.drawable.gray_gradient)
            .error(R.color.textColorDefault)
            .centerCrop();

    private NutrientListAdapter adapter;
    private List<Nutrient> food;

    @NonNull
    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.food_list);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv.setNestedScrollingEnabled(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        food = new ArrayList<>();
        adapter = new NutrientListAdapter(this.getContext(), food);
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new NutrientClickListener(this.getContext(), (view1, position) -> {
            // TODO : Open dialog
        }, rv));
    }

    public void onViewShown() {
        if (LOADED) return;

        nutrientsReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                        for (DataSnapshot snap : snapshots) {
                            food.add(snap.getValue(Nutrient.class));
                        }
                        adapter.notifyDataSetChanged();

                        LOADED = true;

                        if (isAdded() && getView() != null) {
                            getView().findViewById(R.id.progress).setVisibility(View.GONE);
                            getView().findViewById(R.id.food_list).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
