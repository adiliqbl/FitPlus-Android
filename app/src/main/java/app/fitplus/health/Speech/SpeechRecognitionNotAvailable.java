package app.fitplus.health.Speech;
/**
 * @author Aleksandar Gotev
 */
public class SpeechRecognitionNotAvailable extends Exception {
    public SpeechRecognitionNotAvailable() {
        super("Speech recognition not available");
    }
}
