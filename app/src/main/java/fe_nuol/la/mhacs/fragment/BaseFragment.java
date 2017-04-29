package fe_nuol.la.mhacs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;

import fe_nuol.la.mhacs.views.ConfirmDialogFragment;
import fe_nuol.la.mhacs.views.LoadingDialogFragment;

public class BaseFragment extends Fragment {
    private static final String TAG_DIALOG_FRAGMENT = "dialog_fragment";

    private ConfirmDialogFragment confirmDialogFragment;
    private LoadingDialogFragment loadingDialogFragment;

    protected void openActivity(Class<?> cls) {
        openActivity(cls, null, false);
    }

    protected void openActivity(Class<?> cls, boolean finishActivity) {
        openActivity(cls, null, finishActivity);
    }

    protected void openActivity(Class<?> cls, Bundle bundle) {
        openActivity(cls, bundle, false);
    }

    protected void openActivity(Class<?> cls, Bundle bundle, boolean finishActivity) {
        Intent intent = new Intent(getActivity(), cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
        if (finishActivity)
            getActivity().finish();
    }

    protected void openActivityAndClearHistory(Class<?> cls) {
        openActivityAndClearHistory(cls, null);
    }

    protected void openActivityAndClearHistory(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(getActivity(), cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showConfirmDialog(int title, int message, ConfirmDialogFragment.OnDialogClickListener listener) {
        showConfirmDialog(getString(title), getString(message), listener);
    }

    public void showConfirmDialog(String title, String message, ConfirmDialogFragment.OnDialogClickListener listener) {
        ConfirmDialogFragment alertDialogFragment = new ConfirmDialogFragment.Builder()
                .setMessage(message)
                .setTitle(title)
                .setOnConfirmListener(listener)
                .setOnCancelListener(listener)
                .build();
        createFragmentDialog(alertDialogFragment);
    }

    private void createFragmentDialog(DialogFragment dialogFragment) {
        try {
            dialogFragment.show(getFragmentManager(), TAG_DIALOG_FRAGMENT);
        } catch (IllegalStateException e) {
        }
    }
    //getSupportFragmentManager()

    public void showLoadingDialog() {
        dismissDialog();
        loadingDialogFragment = new LoadingDialogFragment.Builder().build();
        createFragmentDialog(loadingDialogFragment);
    }

    public void dismissDialog() {
        try {
            if (confirmDialogFragment != null) {
                confirmDialogFragment.dismiss();
            }
            if (loadingDialogFragment != null) {
                loadingDialogFragment.dismiss();
            }
        } catch (IllegalStateException e) {
        }
    }

    public void showMessage(View v, int message) {
        showMessage(v, getString(message));
    }

    public void showMessage(View v, String message) {
        Snackbar.make(v.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public void getFragment() {
        startActivity(getActivity().getIntent());
    }
}
