package scut.bbt.passworld.concrete.splash;

import android.content.Context;
import android.view.View;

/**
 * Created by dgliang on 2018/7/18.
 */

public class StartPresenter implements StartContract.presenter {
    private View view;

    public StartPresenter(View view) {
        this.view = view;
    }

    public void checkPermission(Context context) {

    }
}
