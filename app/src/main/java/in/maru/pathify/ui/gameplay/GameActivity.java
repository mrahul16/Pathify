package in.maru.pathify.ui.gameplay;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.maru.pathify.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String start, end, gameId, otherDisplayName, otherUserName, otherProfilePicture;
        boolean isHost;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            start = extras.getString("EXTRA_START_WORD");
            end = extras.getString("EXTRA_END_WORD");
            isHost = extras.getBoolean("EXTRA_IS_HOST");
            gameId = extras.getString("EXTRA_GAME_ID");
            otherDisplayName = extras.getString("EXTRA_OTHER_DISPLAY_NAME");
            otherUserName = extras.getString("EXTRA_OTHER_USER_NAME");
            otherProfilePicture = extras.getString("EXTRA_OTHER_PROFILE_PICTURE");


            Fragment fragment = GameActivityFragment.newInstance(start, end, isHost, gameId,
                                otherDisplayName, otherUserName, otherProfilePicture);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_game, fragment)
                    .commit();
        }
    }

}
