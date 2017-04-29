package fe_nuol.la.mhacs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fe_nuol.la.mhacs.R;
import fe_nuol.la.mhacs.activity.LoginActivity;
import fe_nuol.la.mhacs.model.DataPref;


@SuppressWarnings("unused")
public class EditUserProfileFragment extends BaseFragment {

    private ImageView imvProfile;
    private ImageButton imbProfile;
    private EditText edtName;
    private EditText edtPassword;
    private Switch swName;
    private Switch swPassword;
    private Button btnChange;
    private DataPref dataPref;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    public EditUserProfileFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static EditUserProfileFragment newInstance() {
        EditUserProfileFragment fragment = new EditUserProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        setHasOptionsMenu(true);
        dataPref = new DataPref();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        };
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        bindView(rootView);
        setUpView();
        HandleClick();
    }

    private void HandleClick() {
        btnChange.setOnClickListener(getChange);
    }

    private void setUpView() {
        edtName.setEnabled(true);
        edtPassword.setEnabled(false);

        swName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtName.setEnabled(true);
                    edtPassword.setEnabled(false);
                    edtName.requestFocus();
                    swPassword.setChecked(false);
                }else {
                    edtName.setEnabled(false);
                    edtPassword.setEnabled(true);
                    edtPassword.requestFocus();
                    swPassword.setChecked(true);
                }
            }
        });

        swPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtPassword.setEnabled(true);
                    edtName.setEnabled(false);
                    edtPassword.requestFocus();
                    swName.setChecked(false);
                }else {
                    edtPassword.setEnabled(false);
                    edtName.setEnabled(true);
                    edtName.requestFocus();
                    swName.setChecked(true);
                }
            }
        });
    }

    private void bindView(View rootView) {
        imvProfile = (ImageView) rootView.findViewById(R.id.imv_profile);
        imbProfile = (ImageButton) rootView.findViewById(R.id.edit_profile_pic);
        edtName = (EditText) rootView.findViewById(R.id.edtName);
        edtPassword = (EditText) rootView.findViewById(R.id.edtPassword);
        swName = (Switch) rootView.findViewById(R.id.swName);
        swPassword = (Switch) rootView.findViewById(R.id.swPass);
        btnChange = (Button) rootView.findViewById(R.id.btnChange);

    }

    View.OnClickListener getChange = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (swName.isChecked()) {
                if (user != null && !edtName.getText().toString().trim().equals("")) {
                    String newEmail = edtName.getText().toString() + "@" + dataPref.getPincode() + ".com";
                    //Toast.makeText(getActivity(), newEmail.trim(), Toast.LENGTH_LONG).show();
                    showLoadingDialog();
                    user.updateEmail(newEmail.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dismissDialog();
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "User is updated. Please sign in with new User id!", Toast.LENGTH_LONG).show();
                                auth.signOut();
                                getActivity().finish();
                            }else {
                                Toast.makeText(getActivity(), "Failed to update User!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if (edtName.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please Enter User!", Toast.LENGTH_LONG).show();
                }
            } else if (swPassword.isChecked()) {
                if (user != null && !edtPassword.getText().toString().trim().equals("")) {
                    //Toast.makeText(getActivity(), "New Pass: " + edtPassword.getText().toString().trim(), Toast.LENGTH_LONG).show();

                    if (edtPassword.getText().toString().length() < 6) {

                    }else {
                        showLoadingDialog();
                        user.updatePassword(edtPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dismissDialog();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Password is updated. Please sign in with new Password!", Toast.LENGTH_LONG).show();
                                            auth.signOut();
                                            getActivity().finish();

                                        } else {
                                            Toast.makeText(getActivity(), "Failed to update Password!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                } else if (edtPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please Enter Password!", Toast.LENGTH_LONG).show();
                }
            }

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
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
