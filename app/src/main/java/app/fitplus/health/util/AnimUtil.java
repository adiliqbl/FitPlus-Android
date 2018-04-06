package app.fitplus.health.util;

import android.view.ViewGroup;

import com.transitionseverywhere.ChangeText;
import com.transitionseverywhere.Recolor;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

public class AnimUtil {

    public static void setChangeText(ViewGroup view) {
        TransitionManager.beginDelayedTransition(view, new ChangeText()
                .setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN));
        view = null;
    }

    public static void setRecolor(ViewGroup view) {
        TransitionManager.beginDelayedTransition(view, new Recolor());
        view = null;
    }

    public static void setSlideShow(ViewGroup view, final int GRAVITY) {
        TransitionManager.beginDelayedTransition(view, new Slide(GRAVITY));
        view = null;
    }
}