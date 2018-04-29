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
}