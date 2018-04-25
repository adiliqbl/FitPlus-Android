package app.fitplus.health.util;

import android.view.ViewGroup;

import com.transitionseverywhere.ChangeText;
import com.transitionseverywhere.TransitionManager;

public class AnimUtil {

    public static void setChangeText(ViewGroup view) {
        TransitionManager.beginDelayedTransition(view, new ChangeText()
                .setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN));
        view = null;
    }
}