package in.maru.pathify.ui.gameSetup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import java.util.List;

import in.maru.pathify.BaseActivity;
import in.maru.pathify.PathifyApplication;
import in.maru.pathify.R;

public class GameSetupActivity extends BaseActivity implements
        OnGameCreatedWaitFragment.OnFragmentDetachListener {

    private static final String LOG_TAG = GameSetupActivity.class.getSimpleName();
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        initializeScreen();
    }

    private void initializeScreen() {
        ChooseGameFragment chooseGameFragment = new ChooseGameFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, chooseGameFragment)
                .commit();

        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setMessage("Retrieving active games list...");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
        Log.d(LOG_TAG, "Alert");
    }

    private void showWaitingScreen() {
        OnGameCreatedWaitFragment onGameCreatedWaitFragment = OnGameCreatedWaitFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, onGameCreatedWaitFragment)
                .commit();
    }

    public void dismissLoadingDialog() {
        this.mLoadingDialog.dismiss();
    }

    @Override
    public void onFragmentDetach() {
//        if(fab != null) {
//            fab.show();
//        }
    }
}
