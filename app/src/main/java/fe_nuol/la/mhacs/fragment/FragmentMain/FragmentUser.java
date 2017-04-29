package fe_nuol.la.mhacs.fragment.FragmentMain;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fe_nuol.la.mhacs.R;
import fe_nuol.la.mhacs.fragment.BaseFragment;
import fe_nuol.la.mhacs.activity.EditUserProfileActivity;
import fe_nuol.la.mhacs.activity.LoginActivity;
import fe_nuol.la.mhacs.model.DataPref;
import fe_nuol.la.mhacs.views.ConfirmDialogFragment;


@SuppressWarnings("unused")
public class FragmentUser extends BaseFragment {

    private Button btnLogOut, btnChangePinCode;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DataPref dataPref;

    private TextView tvUsername;
    private TextView tvPinCode;

    private View dimView;
    private FloatingActionMenu famMenu;
    private FloatingActionButton fabLogOut;
    private FloatingActionButton fabLogPinCode;
    private FloatingActionButton fabUserEdit;
    private Handler delayAction = new Handler();

    public FragmentUser() {
        super();
    }

    @SuppressWarnings("unused")
    public static FragmentUser newInstance() {
        FragmentUser fragment = new FragmentUser();
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
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        //get firebase auth instance
        dataPref = new DataPref();
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
        fabMenu(rootView);
        tvUsername.setText(dataPref.getUsername());
        tvPinCode.setText(dataPref.getPincode());
        btnLogOut.setOnClickListener(getLogOut);
        btnChangePinCode.setOnClickListener(getChangePinCode);
    }

    private void bindView(View rootView) {
        tvUsername = (TextView) rootView.findViewById(R.id.username);
        tvPinCode = (TextView) rootView.findViewById(R.id.pin_code);
        btnLogOut = (Button) rootView.findViewById(R.id.btnLogOut);
        btnChangePinCode = (Button) rootView.findViewById(R.id.btnC);
    }

    View.OnClickListener getLogOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showConfirmDialog(R.string.title, R.string.message_logout, new ConfirmDialogFragment.OnDialogClickListener() {
                @Override
                public void onDialogClick(ConfirmDialogFragment dialog, int which) {
                    if (which == ConfirmDialogFragment.TYPE_CONFIRM) {
                        auth.signOut();
                        getActivity().finish();
                    } else if (which == ConfirmDialogFragment.TYPE_CANCEL) {
                        dialog.dismiss();
                    }
                }
            });

        }
    };

    View.OnClickListener getChangePinCode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showConfirmDialog(R.string.title, R.string.message, new ConfirmDialogFragment.OnDialogClickListener() {
                @Override
                public void onDialogClick(ConfirmDialogFragment dialog, int which) {
                    if (which == ConfirmDialogFragment.TYPE_CONFIRM) {
                        dataPref.setPincode("N/A");
                        auth.signOut();
                        getActivity().finish();
                    } else if (which == ConfirmDialogFragment.TYPE_CANCEL) {
                        dialog.dismiss();
                    }
                }
            });

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


    private void fabMenu(View rootView) {

        dimView = rootView.findViewById(R.id.dim_view);
        famMenu = (FloatingActionMenu) rootView.findViewById(R.id.famMenu);
        fabLogOut = (FloatingActionButton) rootView.findViewById(R.id.fab_logout);
        fabLogPinCode = (FloatingActionButton) rootView.findViewById(R.id.fab_log_pin_code);
        fabUserEdit = (FloatingActionButton) rootView.findViewById(R.id.fab_edit_user);

        final int famAnimationDuration = 50;
        famMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean open) {
                if (open) {
                    dimView.setVisibility(View.VISIBLE);
                    dimView.animate().alpha(0.7f).setDuration(300).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            famMenu.getMenuIconView().animate()
                                    .alpha(0f)
                                    .setDuration(famAnimationDuration)
                                    .start();
                            delayAction.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    famMenu.getMenuIconView().setImageResource(R.drawable.fab_add);
                                    famMenu.getMenuIconView().invalidate();
                                    famMenu.getMenuIconView().animate()
                                            .alpha(1f)
                                            .setDuration(famAnimationDuration)
                                            .start();
                                }
                            }, famAnimationDuration);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
                } else {
                    dimView.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            delayAction.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    famMenu.getMenuIconView().animate()
                                            .alpha(0f)
                                            .setDuration(famAnimationDuration)
                                            .start();
                                    delayAction.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            famMenu.getMenuIconView().setImageResource(R.drawable.ic_menu_white_36dp);
                                            famMenu.getMenuIconView().invalidate();
                                            famMenu.getMenuIconView().animate()
                                                    .alpha(1f)
                                                    .setDuration(famAnimationDuration)
                                                    .start();
                                        }
                                    }, famAnimationDuration);
                                }
                            }, 150);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            dimView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
                }
            }
        });
        dimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                famMenu.close(true);
            }
        });

        fabLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: fabLogOut Here
                showConfirmDialog(R.string.title, R.string.message_logout, new ConfirmDialogFragment.OnDialogClickListener() {
                    @Override
                    public void onDialogClick(ConfirmDialogFragment dialog, int which) {
                        if (which == ConfirmDialogFragment.TYPE_CONFIRM) {
                            auth.signOut();
                            getActivity().finish();
                        } else if (which == ConfirmDialogFragment.TYPE_CANCEL) {
                            dialog.dismiss();
                        }
                    }
                });

                famMenu.close(true);
            }
        });

        fabLogPinCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: fabLogPinCode Here
                showConfirmDialog(R.string.title, R.string.message, new ConfirmDialogFragment.OnDialogClickListener() {
                    @Override
                    public void onDialogClick(ConfirmDialogFragment dialog, int which) {
                        if (which == ConfirmDialogFragment.TYPE_CONFIRM) {
                            dataPref.setPincode("N/A");
                            auth.signOut();
                            getActivity().finish();
                        } else if (which == ConfirmDialogFragment.TYPE_CANCEL) {
                            dialog.dismiss();
                        }
                    }
                });

                famMenu.close(true);
            }
        });


        fabUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: fabUserEdit Here
//                Toast.makeText(getActivity(), "fabUserEdit is clicked",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), EditUserProfileActivity.class));
                famMenu.close(true);
            }
        });



    }

}
