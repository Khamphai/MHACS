package fe_nuol.la.mhacs.fragment.FragmentMain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;
import fe_nuol.la.mhacs.R;
import fe_nuol.la.mhacs.fragment.BaseFragment;
import fe_nuol.la.mhacs.activity.LoginActivity;
import fe_nuol.la.mhacs.model.DataPref;
import fe_nuol.la.mhacs.utils.FormatDate;
import pl.tajchert.sample.DotsTextView;


@SuppressWarnings("unused")
public class FragmentStatus extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private DataPref dataPref;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private PieView pieViewHum;
    private PieView pieViewTem;
    private TextView tvDateTime;
    private TextView tvWaterPump;
    private TextView tvLight;
    private TextView tvFanIn;
    private TextView tvFanOut;
    private DotsTextView dotW;
    private DotsTextView dotL;
    private DotsTextView dotFI;
    private DotsTextView dotFO;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mSubRootRef;
    private DatabaseReference mHumidityRef;
    private DatabaseReference mTemperatureRef;
    private DatabaseReference mDateTimeRef;
    private DatabaseReference mWaterPumpRef;
    private DatabaseReference mLightRef;
    private DatabaseReference mFanInRef;
    private DatabaseReference mFanOutRef;

    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    public FragmentStatus() {
        super();
    }

    @SuppressWarnings("unused")
    public static FragmentStatus newInstance() {
        FragmentStatus fragment = new FragmentStatus();
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
        View rootView = inflater.inflate(R.layout.fragment_status, container, false);
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
        mHumidityRef = mSubRootRef.child("Hum");
        mTemperatureRef = mSubRootRef.child("Tem");
        mDateTimeRef = mSubRootRef.child("DateTime");
        mWaterPumpRef = mSubRootRef.child("Water_Pump");
        mLightRef = mSubRootRef.child("Light");
        mFanInRef = mSubRootRef.child("Fan_In");
        mFanOutRef = mSubRootRef.child("Fan_Out");

        bindView(rootView);
        initializes();

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
//        mProgressDialog = ProgressDialog.show(getActivity(), "", "Please wait...");

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        //showLoadingDialog();

        WaterPump();
        Light();
        FanIn();
        FanOut();
        Humidity();
        Temperature();
        DateTime();
    }

    private void FanOut() {
        mFanOutRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                if (i == 1) {
                    tvFanOut.setText("ກຳລັງເຮັດວຽກຢູ່");
                    tvFanOut.setTextColor(Color.parseColor("#f45145"));
                    dotFO.setVisibility(View.VISIBLE);

                }else if (i == 0){
                    tvFanOut.setText("ຢຸດເຮັດວຽກແລ້ວ");
                    tvFanOut.setTextColor(Color.parseColor("#009491"));
                    dotFO.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //dismissDialog();
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void FanIn() {
        mFanInRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                if (i == 1) {
                    tvFanIn.setText("ກຳລັງເຮັດວຽກຢູ່");
                    tvFanIn.setTextColor(Color.parseColor("#f45145"));
                    dotFI.setVisibility(View.VISIBLE);

                }else if (i == 0){
                    tvFanIn.setText("ຢຸດເຮັດວຽກແລ້ວ");
                    tvFanIn.setTextColor(Color.parseColor("#009491"));
                    dotFI.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                dismissDialog();
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }


    private void Light() {
        mLightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                if (i == 1) {
                    tvLight.setText("ກຳລັງເຮັດວຽກຢູ່");
                    tvLight.setTextColor(Color.parseColor("#f45145"));
                    dotL.setVisibility(View.VISIBLE);

                }else if (i == 0){
                    tvLight.setText("ຢຸດເຮັດວຽກແລ້ວ");
                    tvLight.setTextColor(Color.parseColor("#009491"));
                    dotL.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                dismissDialog();
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void WaterPump() {
        mWaterPumpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = dataSnapshot.getValue(Integer.class);
                if (i == 1) {
                    tvWaterPump.setText("ກຳລັງເຮັດວຽກຢູ່");
                    tvWaterPump.setTextColor(Color.parseColor("#f45145"));
                    dotW.setVisibility(View.VISIBLE);
                }else if (i == 0){
                    tvWaterPump.setText("ຢຸດເຮັດວຽກແລ້ວ");
                    tvWaterPump.setTextColor(Color.parseColor("#009491"));
                    dotW.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //dismissDialog();
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void Temperature() {
        mTemperatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float i = dataSnapshot.getValue(Float.class);
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //dismissDialog();
                if (i != 0) {
                    pieViewTem.setPercentage(i);
                    pieViewTem.setInnerText(String.valueOf(i) + " \u2103");

                    PieAngleAnimation animationTem = new PieAngleAnimation(pieViewTem);
                    animationTem.setDuration(2500); //This is the duration of the animation in millis
                    pieViewTem.startAnimation(animationTem);
                }else {
                    pieViewTem.setPercentage(0);
                    pieViewTem.setInnerText(String.valueOf(0) + " \u2103");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //dismissDialog();
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void Humidity() {
        mHumidityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float i = dataSnapshot.getValue(Float.class);
//                mProgressDialog.dismiss();
                if (i != 0) {
                    pieViewHum.setPercentage(i);
                    pieViewHum.setInnerText(String.valueOf(i) + " %");

                    PieAngleAnimation animationHum = new PieAngleAnimation(pieViewHum);
                    animationHum.setDuration(2500); //This is the duration of the animation in millis
                    pieViewHum.startAnimation(animationHum);
                }else{
                    pieViewHum.setPercentage(0);
                    pieViewHum.setInnerText(String.valueOf(0) + " %");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                mProgressDialog.dismiss();
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void DateTime() {
        mDateTimeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue(String.class);
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //dismissDialog();
                if (s != null) tvDateTime.setText(FormatDate.formatCurrentDateTime(s));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //dismissDialog();
                Log.d("F", "Failed: " + databaseError.getMessage());
            }
        });
    }

    private void bindView(View rootView) {

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.button_blue_normal);
        pieViewHum = (PieView) rootView.findViewById(R.id.pieViewHum);
        pieViewTem = (PieView) rootView.findViewById(R.id.pieViewTem);

        tvDateTime = (TextView) rootView.findViewById(R.id.tv_date_time);
        tvWaterPump = (TextView) rootView.findViewById(R.id.tv_water_pump);
        tvLight = (TextView) rootView.findViewById(R.id.tv_light);
        tvFanIn = (TextView) rootView.findViewById(R.id.tv_fan_in);
        tvFanOut = (TextView) rootView.findViewById(R.id.tv_fan_out);

        dotW = (DotsTextView) rootView.findViewById(R.id.dotW);
        dotL = (DotsTextView) rootView.findViewById(R.id.dotL);
        dotFI = (DotsTextView) rootView.findViewById(R.id.dotFI);
        dotFO = (DotsTextView) rootView.findViewById(R.id.dotFO);

        pieViewHum.setPercentageBackgroundColor(getResources().getColor(R.color.indigo));
        pieViewTem.setPercentageBackgroundColor(getResources().getColor(R.color.dark_blue));

    }// End BindView

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    @Override
    public void onRefresh() {
        loadData();
    }
}
