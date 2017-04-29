package fe_nuol.la.mhacs.fragment.FragmentMain;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fe_nuol.la.mhacs.R;
import fe_nuol.la.mhacs.fragment.BaseFragment;
import fe_nuol.la.mhacs.activity.LoginActivity;
import fe_nuol.la.mhacs.model.DataPref;


@SuppressWarnings("unused")
public class FragmentControls extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextView tvStatusWaterPump;
    private TextView tvStatusLight;
    private TextView tvStatusMode;
    private Button btnControlWaterPump;
    private Button btnControlLight;
    private Button btnControlMode;

    private CardView cardViewWaterPump;
    private CardView cardViewLight;

    private DataPref dataPref;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mSubRootRef;

    private DatabaseReference mWaterPumpRef;
    private DatabaseReference mLightRef;
    private DatabaseReference mModeRef;

    private SwipeRefreshLayout swipeRefreshLayout;

    public FragmentControls() {
        super();
    }

    @SuppressWarnings("unused")
    public static FragmentControls newInstance() {
        FragmentControls fragment = new FragmentControls();
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
        View rootView = inflater.inflate(R.layout.fragment_controls, container, false);
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
        mWaterPumpRef = mSubRootRef.child("Water_Pump");
        mLightRef = mSubRootRef.child("Light");
        mModeRef = mSubRootRef.child("mode");

        bindView(rootView);
        initializes();
    }

    private void Light() {
        mLightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                swipeRefreshLayout.setRefreshing(false);

                if (i == 1) {
                    btnControlLight.setBackgroundResource(R.drawable.selector_button_on);
                    btnControlLight.setText("ON");
                    tvStatusLight.setText("ເປີດ");
                    tvStatusLight.setTextColor(Color.parseColor("#f45145"));

                } else if (i == 0) {
                    btnControlLight.setBackgroundResource(R.drawable.selector_button_off);
                    btnControlLight.setText("OFF");
                    tvStatusLight.setText("ປິດ");
                    tvStatusLight.setTextColor(Color.parseColor("#009491"));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void WaterPump() {
        mWaterPumpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                swipeRefreshLayout.setRefreshing(false);

                if (i == 1) {
                    btnControlWaterPump.setBackgroundResource(R.drawable.selector_button_on);
                    btnControlWaterPump.setText("ON");
                    tvStatusWaterPump.setText("ເປີດ");
                    tvStatusWaterPump.setTextColor(Color.parseColor("#f45145"));
                } else if (i == 0) {
                    btnControlWaterPump.setBackgroundResource(R.drawable.selector_button_off);
                    btnControlWaterPump.setText("OFF");
                    tvStatusWaterPump.setText("ປິດ");
                    tvStatusWaterPump.setTextColor(Color.parseColor("#009491"));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void initializes() {

        loadData();

        swipeRefreshLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        loadData();
                                    }
                                }
        );

    }


    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        Mode();
        WaterPump();
        Light();

    }

    private void Mode() {
        mModeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                swipeRefreshLayout.setRefreshing(false);

                if (i == 1) {
                    btnControlMode.setBackgroundResource(R.drawable.selector_button_off);
                    btnControlMode.setText("Manual");
                    tvStatusMode.setText("Manual");
                    tvStatusMode.setTextColor(Color.parseColor("#009491"));
                    cardViewWaterPump.setVisibility(View.VISIBLE);
                    cardViewLight.setVisibility(View.VISIBLE);
                } else if (i == 0) {
                    btnControlMode.setBackgroundResource(R.drawable.selector_button_on);
                    btnControlMode.setText("Auto");
                    tvStatusMode.setText("Auto");
                    tvStatusMode.setTextColor(Color.parseColor("#f45145"));
                    cardViewWaterPump.setVisibility(View.GONE);
                    cardViewLight.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void bindView(View rootView) {

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.button_blue_normal);
        tvStatusWaterPump = (TextView) rootView.findViewById(R.id.tv_status_water_pump);
        tvStatusLight = (TextView) rootView.findViewById(R.id.tv_status_light);
        tvStatusMode = (TextView) rootView.findViewById(R.id.tv_status_mode);
        btnControlWaterPump = (Button) rootView.findViewById(R.id.btn_on_off_water_pump);
        btnControlLight = (Button) rootView.findViewById(R.id.btn_on_off_light);
        btnControlMode = (Button) rootView.findViewById(R.id.btn_mode);
        cardViewWaterPump = (CardView) rootView.findViewById(R.id.cardView_water_pump);
        cardViewLight = (CardView) rootView.findViewById(R.id.cardView_light);

        btnControlWaterPump.setOnClickListener(getButtonControlWaterPump);
        btnControlLight.setOnClickListener(getButtonControlLight);
        btnControlMode.setOnClickListener(getButtonControlMode);

    }

    View.OnClickListener getButtonControlMode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btnControlMode.getText().toString() == "Auto") {
                try {
                    mModeRef.setValue(1);
                    btnControlMode.setBackgroundResource(R.drawable.selector_button_on);
                    btnControlMode.setText("Manual");
                    tvStatusMode.setText("Manual");
                    cardViewWaterPump.setVisibility(View.VISIBLE);
                    cardViewLight.setVisibility(View.VISIBLE);
                } catch (Exception e) {

                }
            }else{
                try {
                    mModeRef.setValue(0);
                    btnControlMode.setBackgroundResource(R.drawable.selector_button_off);
                    btnControlMode.setText("Auto");
                    tvStatusMode.setText("Auto");
                    cardViewWaterPump.setVisibility(View.GONE);
                    cardViewLight.setVisibility(View.GONE);
                } catch (Exception e) {

                }
            }
        }
    };

    View.OnClickListener getButtonControlWaterPump = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btnControlWaterPump.getText().toString() == "OFF") {
                try {
                    mWaterPumpRef.setValue(1);
                    btnControlWaterPump.setBackgroundResource(R.drawable.selector_button_on);
                    btnControlWaterPump.setText("ON");
                    tvStatusWaterPump.setText("ON");
                } catch (Exception e) {

                }
            } else {
                try {
                    mWaterPumpRef.setValue(0);
                    btnControlWaterPump.setBackgroundResource(R.drawable.selector_button_off);
                    btnControlWaterPump.setText("OFF");
                    tvStatusWaterPump.setText("OFF");
                } catch (Exception e) {

                }

            }
        }
    };

    View.OnClickListener getButtonControlLight = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (btnControlLight.getText().toString() == "OFF") {
                try {
                    mLightRef.setValue(1);
                    btnControlLight.setBackgroundResource(R.drawable.selector_button_on);
                    btnControlLight.setText("ON");
                    tvStatusLight.setText("ON");
                } catch (Exception e) {

                }

            } else {
                try {
                    mLightRef.setValue(0);
                    btnControlLight.setBackgroundResource(R.drawable.selector_button_off);
                    btnControlLight.setText("OFF");
                    tvStatusLight.setText("OFF");
                } catch (Exception e) {

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

    @Override
    public void onRefresh() {
        loadData();
    }
}
