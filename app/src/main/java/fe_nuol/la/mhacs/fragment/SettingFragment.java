package fe_nuol.la.mhacs.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fe_nuol.la.mhacs.R;
import fe_nuol.la.mhacs.activity.LoginActivity;
import fe_nuol.la.mhacs.model.DataPref;


@SuppressWarnings("unused")
public class SettingFragment extends BaseFragment {

    private EditText min_hum;
    private EditText max_hum;
    private EditText min_tem;
    private EditText max_tem;
    private Switch swOpenClose;
    private Button btnSetting;
    private DataPref dataPref;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mSubRootRef;
    private DatabaseReference minHumidityRef;
    private DatabaseReference maxHumidityRef;
    private DatabaseReference minTemperatureRef;
    private DatabaseReference maxTemperatureRef;

    public SettingFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
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
        mSubRootRef = mRootRef.child(dataPref.getPincode());
        minHumidityRef = mSubRootRef.child("Min_Hum");
        maxHumidityRef = mSubRootRef.child("Max_Hum");
        minTemperatureRef = mSubRootRef.child("Min_Tem");
        maxTemperatureRef = mSubRootRef.child("Max_Tem");

        bindView(rootView);
        setUpView();
        initializes();
        HandleClick();
    }

    private void initializes() {
        minHumidity();
        maxHumidity();
        minTemperature();
        maxTemperature();
    }

    private void maxTemperature() {
        maxTemperatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                max_tem.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void minTemperature() {
        minTemperatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                min_tem.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void maxHumidity() {
        maxHumidityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                max_hum.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void minHumidity() {
        minHumidityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                min_hum.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    View.OnClickListener getSetting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String minHum = min_hum.getText().toString();
            String maxHum = max_hum.getText().toString();
            String minTem = min_tem.getText().toString();
            String maxTem = max_tem.getText().toString();

            if (!minHum.equals("") && !maxHum.equals("") && !minTem.equals("") && !maxTem.equals("")) {

                minHumidityRef.setValue(Integer.parseInt(minHum));
                maxHumidityRef.setValue(Integer.parseInt(maxHum));
                minTemperatureRef.setValue(Integer.parseInt(minTem));
                maxTemperatureRef.setValue(Integer.parseInt(maxTem));
                swOpenClose.setChecked(false);
            }else {
                Toast.makeText(getActivity(), "Please Enter Min & Max Humidity and Temperature!", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void HandleClick() {
        btnSetting.setOnClickListener(getSetting);
    }

    private void setUpView() {

        min_hum.setEnabled(false);
        max_hum.setEnabled(false);
        min_tem.setEnabled(false);
        max_tem.setEnabled(false);

        swOpenClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    min_hum.setEnabled(true);
                    max_hum.setEnabled(true);
                    min_tem.setEnabled(true);
                    max_tem.setEnabled(true);
                    min_hum.requestFocus();
                    btnSetting.setVisibility(View.VISIBLE);
                }else {
                    min_hum.setEnabled(false);
                    max_hum.setEnabled(false);
                    min_tem.setEnabled(false);
                    max_tem.setEnabled(false);
                    btnSetting.setVisibility(View.GONE);
                }
            }
        });

    }

    private void bindView(View rootView) {
        min_hum = (EditText) rootView.findViewById(R.id.min_hum);
        max_hum = (EditText) rootView.findViewById(R.id.max_hum);
        min_tem = (EditText) rootView.findViewById(R.id.min_tem);
        max_tem = (EditText) rootView.findViewById(R.id.max_tem);
        swOpenClose = (Switch) rootView.findViewById(R.id.sw_open_close);
        btnSetting = (Button) rootView.findViewById(R.id.btnSetting);

    }


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
