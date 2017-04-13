package in.maru.pathify.ui.gameSetup;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

import in.maru.pathify.R;
import in.maru.pathify.model.Game;
import in.maru.pathify.utils.Constants;


class ChooseGameListAdapter extends FirebaseListAdapter<Game> {

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    ChooseGameListAdapter(Activity activity, Class<Game> modelClass, int modelLayout,
                      Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        ((GameSetupActivity)mActivity).dismissLoadingDialog();
    }

    @Override
    protected void populateView(View view, Game game, int position) {
        TextView gameName = (TextView) view.findViewById(R.id.text_view_game_name);
        TextView hostName = (TextView) view.findViewById(R.id.text_view_host);

        if (game != null && game.getHost() != null) {
            gameName.setText(game.getHost().get(Constants.FBKEY_DISPLAY_NAME));
            hostName.setText(game.getHost().get(Constants.FBKEY_USER_NAME));
        }
    }
}
