package in.maru.pathify.ui.gameSetup;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import in.maru.pathify.PathifyApplication;
import in.maru.pathify.R;
import in.maru.pathify.model.Game;
import in.maru.pathify.model.PlayArea;
import in.maru.pathify.model.UserData;
import in.maru.pathify.ui.gameplay.GameActivity;
import in.maru.pathify.utils.Constants;


public class OnGameChosenWaitFragment extends Fragment {

    private static final String LOG_TAG = OnGameChosenWaitFragment.class.getSimpleName();

    private static final String GAME_ID = "gameId";
    private String mGameId;
    private String otherDisplayName;
    private String otherUserName;
    private String otherProfilePicture;

    private UserData mUserData;
    private FirebaseDatabase database;
    private DatabaseReference gameRef;
    private DatabaseReference playAreaRef;
    private ValueEventListener getHostDataListener;
    private ValueEventListener movedListener;
    private ValueEventListener wordsGetterListener;



    public OnGameChosenWaitFragment() {
        // Required empty public constructor
    }


    public static OnGameChosenWaitFragment newInstance(String gameId) {
        OnGameChosenWaitFragment fragment = new OnGameChosenWaitFragment();
        Bundle args = new Bundle();
        args.putString(GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGameId = getArguments().getString(GAME_ID);
            Log.d(LOG_TAG, "onCreate: " + mGameId);
        }

        mUserData = ((PathifyApplication) getActivity().getApplicationContext()).getUserData();

        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_wait, container, false);

        ((TextView)view.findViewById(R.id.host_display_name)).setText(getString(R.string.loading_opponent_name));
        ((TextView)view.findViewById(R.id.host_user_name)).setText(getString(R.string.loading_opponent_username));

        Glide
                .with(this)
                .load(mUserData.getProfilePicURL())
                .centerCrop()
                .crossFade()
                .into((ImageView) view.findViewById(R.id.joiner_profile_pic));
        ((TextView)view.findViewById(R.id.joiner_display_name)).setText(mUserData.getDisplayName());
        ((TextView)view.findViewById(R.id.joiner_user_name)).setText(mUserData.getUserName());

        getHostData();

        return view;
    }

    private void getHostData() {
        gameRef = database.getReference(Constants.FBKEY_LOBBY).child(mGameId);
        getHostDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    Game g = dataSnapshot.getValue(Game.class);
                    if(g != null && g.getHost() != null && getView() != null) {
                        View view = getView();
                        otherDisplayName = g.getHost().get(Constants.FBKEY_DISPLAY_NAME);
                        otherUserName = g.getHost().get(Constants.FBKEY_USER_NAME);
                        otherProfilePicture = g.getHost().get(Constants.KEY_PROFILE_PIC_URL);
                        Glide
                                .with(getContext())
                                .load(otherProfilePicture)
                                .centerCrop()
                                .crossFade()
                                .into((ImageView) view.findViewById(R.id.host_profile_pic));
                        ((TextView) view.findViewById(R.id.host_display_name))
                                .setText(otherDisplayName);
                        ((TextView) view.findViewById(R.id.host_user_name))
                                .setText(otherUserName);
                        Log.d(LOG_TAG, "removed");
                        gameRef.removeEventListener(getHostDataListener);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gameRef.addValueEventListener(getHostDataListener);

        gameRef.updateChildren(createJoinerData());

        moveToPlayArea();
    }

    private void moveToPlayArea() {
        playAreaRef = database.getReference(Constants.FBKEY_PLAY_AREA).child(mGameId);;

        movedListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game g = dataSnapshot.getValue(Game.class);
                if(g != null && g.isMoved()) {
                    HashMap<String, Object> update = new HashMap<>();
                    update.put("joinerPercentComplete", 0);
                    playAreaRef.updateChildren(update, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d(LOG_TAG, String.valueOf(databaseError.getMessage()));
                            } else {
                                gameRef.setValue(null);
                                getWordsAndPlay();

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gameRef.addValueEventListener(movedListener);
    }

    private void getWordsAndPlay() {
        wordsGetterListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlayArea playArea = dataSnapshot.getValue(PlayArea.class);
                String start, end;
                if(playArea != null) {
                    start = playArea.getStart();
                    end = playArea.getEnd();

                    gameRef.removeEventListener(movedListener);
                    playAreaRef.removeEventListener(wordsGetterListener);
                    Toast.makeText(getActivity(), "Let's go yeah!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), GameActivity.class);
                    intent.putExtra("EXTRA_START_WORD", start);
                    intent.putExtra("EXTRA_END_WORD", end);
                    intent.putExtra("EXTRA_IS_HOST", false);
                    intent.putExtra("EXTRA_GAME_ID", mGameId);
                    intent.putExtra("EXTRA_OTHER_DISPLAY_NAME", otherDisplayName);
                    intent.putExtra("EXTRA_OTHER_USER_NAME", otherUserName);
                    intent.putExtra("EXTRA_OTHER_PROFILE_PICTURE", otherProfilePicture);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        playAreaRef.addValueEventListener(wordsGetterListener);

    }

    private HashMap<String, Object> createJoinerData() {
        HashMap<String, Object> updatedData = new HashMap<>();
        updatedData.put("ready", true);

        HashMap<String, String> joiner = new HashMap<>();
        joiner.put(Constants.FBKEY_DISPLAY_NAME, mUserData.getDisplayName());
        joiner.put(Constants.FBKEY_USER_NAME, mUserData.getUserName());
        joiner.put(Constants.FBKEY_PROFILE_PIC_URL, mUserData.getProfilePicURL());

        updatedData.put("joiner", joiner);

        return updatedData;
    }

}
