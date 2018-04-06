package app.fitplus.health.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.fitplus.health.R;

class AddGoalFragment extends BottomSheetDialog {

    EditText goal;

    private AddGoalListener callback;

    static AddGoalFragment newInstance(Context context, AddGoalListener listener,
                                       final int type) {
        AddGoalFragment assistantFragment = new AddGoalFragment(context, type);
        assistantFragment.callback = listener;
        return assistantFragment;
    }

    @SuppressLint("SetTextI18n")
    private AddGoalFragment(@NonNull Context context, int type) {
        super(context);
        View contentView = View.inflate(getContext(), R.layout.bottom_add, null);
        setContentView(contentView);

        configureBottomSheetBehavior(contentView);
        setCanceledOnTouchOutside(true);

        goal = findViewById(R.id.add_goal);
        contentView.findViewById(R.id.add_button).setOnClickListener(v -> onAddClick());

        if (type == 1) ((TextView) contentView.findViewById(R.id.goal_name)).setText("Calories");
        else if (type == 2) ((TextView) contentView.findViewById(R.id.goal_name)).setText("Steps");
        else if (type == 3)
            ((TextView) contentView.findViewById(R.id.goal_name)).setText("Distance");
    }

    private void configureBottomSheetBehavior(View contentView) {
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());

        if (mBottomSheetBehavior != null) {

            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            dismiss(); //if you want the modal to be dismissed
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }

    void onAddClick() {
        if (goal.getText().toString().equals("")) return;

        callback.onGoalAdded(goal.getText().toString());
        dismiss();
    }

    public interface AddGoalListener {
        void onGoalAdded(final String value);
    }
}
