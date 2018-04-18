package app.fitplus.health.util;

import android.annotation.SuppressLint;
import android.support.design.widget.TextInputLayout;

import org.jetbrains.annotations.Contract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputChecks {

    /**
     * VALIDATE EMAIL ADDRESS
     *
     * @EditText (EMAIL)
     */
    public static boolean ValidateEmail(String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        @SuppressLint("WrongConstant") Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void ShowEmailErrors(final String input, final TextInputLayout container) {
        if (input.equalsIgnoreCase("")) {
            container.setErrorEnabled(false);
            container.setError(null);
        } else if (!ValidateEmail(input)) {
            container.setErrorEnabled(true);
            container.setError("Invalid email address");
        } else {
            container.setErrorEnabled(false);
            container.setError(null);
        }
    }

    /**
     * VALIDATE PHONE NUMBER
     * MAKE PHONE
     */
    @Contract("null -> false")
    public static boolean ValidatePhone(String phNumber) {

        if (phNumber != null && (phNumber.length() == 12 || phNumber.length() == 11)) return true;

        return !(phNumber == null || phNumber.length() < 10);
    }

    public static void ShowPhoneErrors(final String input, final TextInputLayout container) {
        if (input.equalsIgnoreCase("")) {
            container.setErrorEnabled(false);
            container.setError(null);
        } else if (!ValidatePhone(input)) {
            container.setErrorEnabled(true);
            container.setError("Invalid phone number");
        } else {
            container.setErrorEnabled(false);
            container.setError(null);
        }
    }

    /**
     * VALIDATE NAME
     */
    @Contract("null -> false")
    public static boolean ValidateName(String name) {
        return name != null && name.length() >= 3;
    }

    public static void ShowNameErrors(final String input, final TextInputLayout container) {
        if (input.equalsIgnoreCase("")) {
            container.setErrorEnabled(true);
            container.setError("You can't leave this empty");
        } else if (!ValidateName(input)) {
            container.setErrorEnabled(true);
            container.setError("Name must be 3 characters long");
        } else {
            container.setErrorEnabled(false);
            container.setError(null);
        }
    }
}
