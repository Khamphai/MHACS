package fe_nuol.la.mhacs.model;

import android.content.Context;

/**
 * Created by K'Phai on 03/20/2017.
 */

public class Contextor {
    private static Contextor instance;

    public static Contextor getInstance() {
        if (instance == null) {
            instance = new Contextor();
        }
        return instance;
    }

    private Context mContext;

    private Contextor() {

    }

    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
