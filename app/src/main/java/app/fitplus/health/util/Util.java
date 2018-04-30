package app.fitplus.health.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.view.ViewGroup;

import com.transitionseverywhere.ChangeText;
import com.transitionseverywhere.TransitionManager;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.Stats;

public class Util {

    public static void setChangeText(ViewGroup view) {
        TransitionManager.beginDelayedTransition(view, new ChangeText()
                .setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN));
        view = null;
    }

    public static String capitalize(String capString) {
        if (capString == null) return null;

        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static boolean isMyServiceRunning(@NotNull Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.getName().equals(service.service.getClassName())) return true;
        return false;
    }

    @SuppressLint("DefaultLocale")
    public static String to1Decimal(float value) {
        return String.format("%.1f", value);
    }

    public static float getTotalProgress(Stats stats, Goals goal) {
        float totalProgress = 0, tmp;

        // Getting goal percentage
        int gls = 0;
        if (goal.getSteps() > 0) gls++;
        if (goal.getCalorie() > 0) gls++;
        if (goal.getDistance() > 0) gls++;
        float goalPercentage = 100f / (float) gls;

        // Calculating total progress
        if (goal.getSteps() > 0) {
            tmp = ((stats.getSteps() / goal.getSteps()) * goalPercentage);
            if (tmp > 33.33f) tmp = 33.33f;

            totalProgress += tmp;
        }
        if (goal.getCalorie() > 0) {
            tmp = ((stats.getCalorieBurned() / goal.getCalorie()) * goalPercentage);
            if (tmp > 33.33f) tmp = 33.3f;

            totalProgress += tmp;
        }
        if (goal.getDistance() > 0) {
            tmp = ((stats.getDistance() / goal.getDistance()) * goalPercentage);
            if (tmp > 33.33f) tmp = 33.33f;

            totalProgress += tmp;
        }
        return totalProgress;
    }
}