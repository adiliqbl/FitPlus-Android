package app.fitplus.health.ui.explore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import app.fitplus.health.R;
import app.fitplus.health.data.model.Nutrient;

import static app.fitplus.health.util.Util.capitalize;

public class NutrientListAdapter extends RecyclerView.Adapter<NutrientListAdapter.ViewHolder> {

    private List<Nutrient> data;
    private Context context;

    public NutrientListAdapter(Context context, List<Nutrient> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nutrient_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(capitalize(data.get(position).getName()));
        holder.calories.setText(String.valueOf(data.get(position).getCalories()));

        String url = data.get(position).getImage();
        if (url != null && !url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .apply(ExploreFragment.REQUEST_OPTION)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView calories;
        ImageView image;

        ViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.food_name);
            calories = v.findViewById(R.id.food_calories);
            image = v.findViewById(R.id.food_image);
        }
    }
}
