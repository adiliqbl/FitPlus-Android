package app.fitplus.health.Speech;
/**
 * @author Aleksandar Gotev
 */

public class GoogleVoiceTypingDisabledException extends Exception {
    public GoogleVoiceTypingDisabledException() {
        super("Google voice typing must be enabled");
    }
}
