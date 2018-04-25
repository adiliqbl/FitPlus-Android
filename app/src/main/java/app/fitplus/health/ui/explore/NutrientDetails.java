package app.fitplus.health.ui.explore;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.fitplus.health.R;
import app.fitplus.health.data.model.Nutrient;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static app.fitplus.health.util.Util.capitalize;

public class NutrientDetails extends DialogFragment {

    @BindView(R.id.food_name)
    TextView name;
    @BindView(R.id.calcium)
    TextView calcium;
    @BindView(R.id.calories)
    TextView calories;
    @BindView(R.id.carbs)
    TextView carbs;
    @BindView(R.id.cholesterol)
    TextView cholesterol;
    @BindView(R.id.fibre)
    TextView fibre;
    @BindView(R.id.iron)
    TextView iron;
    @BindView(R.id.polysaturated)
    TextView polysaturated;
    @BindView(R.id.potassium)
    TextView potassium;
    @BindView(R.id.protein)
    TextView protein;
    @BindView(R.id.saturated)
    TextView saturated;
    @BindView(R.id.sodium)
    TextView sodium;
    @BindView(R.id.fat)
    TextView fat;
    @BindView(R.id.vitamina)
    TextView vitamina;
    @BindView(R.id.vitaminc)
    TextView vitaminc;

    private Nutrient nutrient;

    private Unbinder unbinder;

    public static NutrientDetails newInstance(final Nutrient nutrient) {
        NutrientDetails fragment = new NutrientDetails();
        fragment.nutrient = nutrient;
        return fragment;
    }

    public NutrientDetails() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nutrient_details, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fillViews();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) unbinder.unbind();
        unbinder = null;
        super.onDestroyView();
    }

    private void fillViews() {
        name.setText(capitalize(nutrient.getName()));

        calcium.setText(nutrient.getCalcium());
        cholesterol.setText(nutrient.getCholestrol());
        calories.setText(String.valueOf(nutrient.getCalories()));
        carbs.setText(nutrient.getCarbs());
        fibre.setText(nutrient.getDietary_Fibre());
        iron.setText(nutrient.getIron());
        polysaturated.setText(nutrient.getPolysaturated());
        potassium.setText(nutrient.getPotassium());
        protein.setText(nutrient.getProtein());
        saturated.setText(nutrient.getSaturated());
        sodium.setText(nutrient.getSodium());
        fat.setText(nutrient.getTotal_Fat());
        vitamina.setText(nutrient.getVitamin_A());
        vitaminc.setText(nutrient.getVitamin_C());
    }
}
