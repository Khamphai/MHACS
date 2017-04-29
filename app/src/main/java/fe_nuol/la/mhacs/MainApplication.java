package fe_nuol.la.mhacs;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import fe_nuol.la.mhacs.model.Contextor;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by K'Phai on 03/20/2017.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(getApplicationContext());
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        initFont();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/phetsarath_ot.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

}
