package fe_nuol.la.mhacs.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import fe_nuol.la.mhacs.R;
import fe_nuol.la.mhacs.fragment.LoginFragment;
import fe_nuol.la.mhacs.fragment.MainFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer, MainFragment.newInstance())
                    .commit();
        }
    }

    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {
            backPressedTime = t;
            Toast.makeText(this, "ກົດ BACK ອີກຄັ້ງເພື່ອອອກຈາກແອັປ",
                    Toast.LENGTH_SHORT).show();
        } else {

            super.onBackPressed();
        }
    } // end of onBackPressed

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }
}
