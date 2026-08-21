package com.rakibul.chorpolish.utils;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.Task;
import com.rakibul.chorpolish.ui.update.UpdateDialog;

/**
 * Manages the app update check process using Google Play In-App Updates API.
 */
public class AppUpdateManager {

    private static boolean hasCheckedThisSession = false;
    private final com.google.android.play.core.appupdate.AppUpdateManager playUpdateManager;

    public AppUpdateManager(Context context) {
        this.playUpdateManager = AppUpdateManagerFactory.create(context);
    }

    /**
     * Checks for a newer version of the app on Google Play.
     * If an update is available, it shows a custom update dialog.
     * 
     * @param activity The FragmentActivity context to show the dialog.
     */
    public void checkForUpdate(FragmentActivity activity) {
        // Requirement 9: Do NOT show the popup repeatedly during the same app session.
        if (hasCheckedThisSession) {
            return;
        }

        Task<AppUpdateInfo> appUpdateInfoTask = playUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            // Requirement 2 & 3: If a newer version exists, show the dialog.
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                showUpdateDialog(activity);
            }
            // Mark as checked to prevent repeated popups in the same session
            hasCheckedThisSession = true;
        }).addOnFailureListener(e -> {
            // Silently fail if Play Services are unavailable or other errors occur
            hasCheckedThisSession = true;
        });
    }

    private void showUpdateDialog(FragmentActivity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        try {
            UpdateDialog dialog = new UpdateDialog();
            dialog.show(activity.getSupportFragmentManager(), UpdateDialog.TAG);
        } catch (Exception e) {
            // Handle edge cases where fragment transaction might fail
            e.printStackTrace();
        }
    }
}
