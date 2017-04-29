package fe_nuol.la.mhacs.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fe_nuol.la.mhacs.R;
import fe_nuol.la.mhacs.activity.MainActivity;
import fe_nuol.la.mhacs.model.DataPref;
import fe_nuol.la.mhacs.views.ConfirmDialogFragment;


@SuppressWarnings("unused")
public class LoginFragment extends BaseFragment {

    private EditText edtName;
    private EditText edtPinCode;
    private EditText edtPass;
    private Button btnLogin;
    private Button btnResetUser;
    private String strName, strPassword, strPinCode;
    private ProgressDialog mProgressDialog;
    private DataPref dataPref;
    private FirebaseAuth auth;

    public LoginFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check Login
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        setHasOptionsMenu(true);
        dataPref = new DataPref();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        auth = FirebaseAuth.getInstance();
        bindView(rootView);
        setUpView();
        btnLogin.setOnClickListener(getLogin);
        btnResetUser.setOnClickListener(getResetUser);

    }

    View.OnClickListener getResetUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showConfirmDialog(R.string.title, R.string.message, new ConfirmDialogFragment.OnDialogClickListener() {
                @Override
                public void onDialogClick(ConfirmDialogFragment dialog, int which) {
                    if (which == ConfirmDialogFragment.TYPE_CONFIRM) {
                        edtPinCode.setVisibility(View.VISIBLE);
                        btnResetUser.setVisibility(View.GONE);
                        dataPref.setPincode("N/A");
                    } else if (which == ConfirmDialogFragment.TYPE_CANCEL) {
                        dialog.dismiss();
                    }
                }
            });

        }
    };

    View.OnClickListener getLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doLogin();
        }
    };

    private void doLogin() {
        strName = edtName.getText().toString().trim();
        strPassword = edtPass.getText().toString().trim();

        if (dataPref.getPincode().equals("N/A")) {
            strPinCode = edtPinCode.getText().toString().trim();
        }else {
            strPinCode = dataPref.getPincode();
        }

        if (TextUtils.isEmpty(strName)) {
            Toast.makeText(getActivity(), "Enter Username!", Toast.LENGTH_LONG).show();
            edtName.requestFocus();
            return;
        }

        if (strPinCode.equals("") || strPinCode.equals("N/A")) {
            Toast.makeText(getActivity(), "Enter Pin Code!", Toast.LENGTH_LONG).show();
            edtPinCode.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(strPassword)) {
            Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_LONG).show();
            edtPass.requestFocus();
            return;
        }

        String strEmail = strName + "@" + strPinCode + ".com";

//        Toast.makeText(getActivity(), strPinCode, Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(), "Mail: " + strEmail, Toast.LENGTH_LONG).show();

//        mProgressDialog = ProgressDialog.show(getActivity(), "", "Please wait...");
        showLoadingDialog();

        //authenticate user
        auth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
//                mProgressDialog.dismiss();
                dismissDialog();
                if (!task.isSuccessful()) {
                    // there was an error
                    if (strPassword.length() < 6) {
                        Toast.makeText(getActivity(), "Password < 6", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                } else {
                    dataPref.setUsername(strName);
                    dataPref.setPincode(strPinCode);
                    openActivity(MainActivity.class, true);
                }
            }
        });

    }

    private void setUpView() {
        if (dataPref.getPincode() == "N/A") {
            edtPinCode.setVisibility(View.VISIBLE);
            btnResetUser.setVisibility(View.GONE);
        }else {
            edtPinCode.setVisibility(View.GONE);
            btnResetUser.setVisibility(View.VISIBLE);
            strPinCode = dataPref.getPincode();
        }
    }

    private void bindView(View view) {
        edtName = (EditText) view.findViewById(R.id.edtName);
        edtPinCode = (EditText) view.findViewById(R.id.edtPinCode);
        edtPass = (EditText) view.findViewById(R.id.edtPassword);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnResetUser = (Button) view.findViewById(R.id.btnReset);
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
