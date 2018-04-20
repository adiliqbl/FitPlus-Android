package app.fitplus.health.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import java.lang.ref.WeakReference;
import java.util.List;

import app.fitplus.health.R;
import app.fitplus.health.ui.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AssistantFragment extends BottomSheetDialog implements SpeechDelegate {

    @BindView(R.id.progress)
    SpeechProgressView progress;

    private WeakReference<MainActivity> activity;

    public static AssistantFragment newInstance(Context context, OnDismissListener listener,
                                                MainActivity activity) {
        AssistantFragment assistantFragment = new AssistantFragment(context, activity);
        assistantFragment.setOnDismissListener(listener);
        return assistantFragment;
    }

    private AssistantFragment(@NonNull Context context, MainActivity activity) {
        super(context);
        View contentView = View.inflate(getContext(), R.layout.assistant, null);
        setContentView(contentView);
        ButterKnife.bind(this, contentView);

        setOwnerActivity(activity);
        this.activity = new WeakReference<>(activity);

        configureBottomSheetBehavior(contentView);
        setCanceledOnTouchOutside(true);

        handleAssistant();
    }

    private void configureBottomSheetBehavior(View contentView) {
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            Speech.getInstance().shutdown();
                            dismiss();
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

    @SuppressLint("CheckResult")
    private void handleAssistant() {
        if (activity.get() != null) Speech.init(activity.get());

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else if (activity.get() != null) {
            RxPermissions rxPermissions = new RxPermissions(activity.get());
            rxPermissions
                    .request(Manifest.permission.RECORD_AUDIO)
                    .subscribe(granted -> {
                        if (granted) { // Always true pre-M
                            onRecordAudioPermissionGranted();
                        } else {
                            Toast.makeText(activity.get(), "Permission is required", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void onRecordAudioPermissionGranted() {
        try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(progress, this);

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {
        //Log.d(getClass().getSimpleName(), "Speech recognition rms is now " + value +  "dB");
    }

    @Override
    public void onSpeechResult(String result) {
        if (result.isEmpty()) {
            Speech.getInstance().say("Could you please repeat?");
        } else {
            Speech.getInstance().say(result);
        }
    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
    }

    private void showSpeechNotSupportedDialog() {
        OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    SpeechUtil.redirectUserToGoogleAppOnPlayStore(getOwnerActivity());
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getOwnerActivity());
        builder.setMessage("Speech isn't available")
                .setCancelable(false)
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getOwnerActivity());
        builder.setMessage("Enable google voice typing")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    // do nothing
                })
                .show();
    }
}
