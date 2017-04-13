package in.maru.pathify.ui.gameSetup;

import android.content.Context;
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
import com.google.firebase.database.ChildEventListener;
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
import in.maru.pathify.model.WordPair;
import in.maru.pathify.ui.gameplay.GameActivity;
import in.maru.pathify.utils.Constants;
import in.maru.pathify.utils.Utils;
import in.maru.pathify.utils.WordsProvider;

public class OnGameCreatedWaitFragment extends Fragment {

    private static final String LOG_TAG = OnGameCreatedWaitFragment.class.getSimpleName();

    private UserData mUserData;
    private String otherDisplayName;
    private String otherUserName;
    private String otherProfilePicture;

    private DatabaseReference newGameRef;
    private DatabaseReference playAreaRef;

    private OnFragmentDetachListener mListener;
    private ValueEventListener gameReadyListener;
    private ValueEventListener joinerConnectedListener;

    private String start;
    private String end;

    public OnGameCreatedWaitFragment() {
    }

    public static OnGameCreatedWaitFragment newInstance() {
        OnGameCreatedWaitFragment fragment = new OnGameCreatedWaitFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        mUserData = ((PathifyApplication) getActivity().getApplicationContext()).getUserData();

        newGameRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FBKEY_LOBBY)
                .child(Utils.encodeUserName(mUserData.getUserName()));
        HashMap<String, String> host = new HashMap<>();
        host.put(Constants.FBKEY_DISPLAY_NAME, mUserData.getDisplayName());
        host.put(Constants.FBKEY_USER_NAME, mUserData.getUserName());
        host.put(Constants.FBKEY_PROFILE_PIC_URL, mUserData.getProfilePicURL());
        Game game = new Game(host);
        newGameRef.setValue(game);

        gameReadyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game g = dataSnapshot.getValue(Game.class);
                if(g != null && g.getJoiner() != null) {
                    View view = getView();
                    otherDisplayName = g.getJoiner().get(Constants.FBKEY_DISPLAY_NAME);
                    otherUserName = g.getJoiner().get(Constants.FBKEY_USER_NAME);
                    otherProfilePicture = g.getJoiner().get(Constants.KEY_PROFILE_PIC_URL);
                    Glide
                            .with(getContext())
                            .load(otherProfilePicture)
                            .centerCrop()
                            .crossFade()
                            .into((ImageView) view.findViewById(R.id.joiner_profile_pic));
                    ((TextView) view.findViewById(R.id.joiner_display_name))
                            .setText(otherDisplayName);
                    ((TextView) view.findViewById(R.id.joiner_user_name))
                            .setText(otherUserName);
                    Log.i(LOG_TAG, "Lets go yeah!");
                    createPlayArea();
                    newGameRef.removeEventListener(gameReadyListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        newGameRef.addValueEventListener(gameReadyListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_wait, container, false);
        Glide
                .with(this)
                .load(mUserData.getProfilePicURL())
                .centerCrop()
                .crossFade()
                .into((ImageView) view.findViewById(R.id.host_profile_pic));
        ((TextView)view.findViewById(R.id.host_display_name)).setText(mUserData.getDisplayName());
        ((TextView)view.findViewById(R.id.host_user_name)).setText(mUserData.getUserName());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentDetachListener) {
            mListener = (OnFragmentDetachListener) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mListener == null) {
            getActivity().onBackPressed();
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (newGameRef != null) {
            newGameRef.removeEventListener(gameReadyListener);
            newGameRef.setValue(null);
        }
        if (mListener != null) {
            mListener.onFragmentDetach();
        }
        mListener = null;
    }

    private void createPlayArea() {
        WordPair pair = WordsProvider.getRandomWordPair();
        start = pair.getStart();
        end = pair.getEnd();
        playAreaRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FBKEY_PLAY_AREA)
                .child(Utils.encodeUserName(mUserData.getUserName()));
        PlayArea playArea = new PlayArea(start, end);
        playAreaRef.setValue(playArea, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null) {
                    Log.d(LOG_TAG, String.valueOf(databaseError.getMessage()));
                }
                else {
                    newGameRef.child("moved").setValue(true);
                    newGameRef = null;
                    waitForJoiner();
                }
            }
        });

    }

    private void waitForJoiner() {
        joinerConnectedListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlayArea playArea = dataSnapshot.getValue(PlayArea.class);
                if(playArea != null && playArea.getJoinerPercentComplete() != -1) {
                    playAreaRef.removeEventListener(joinerConnectedListener);
                    Toast.makeText(getActivity(), "Let's go yeah!", Toast.LENGTH_LONG).show();
                    playAreaRef.removeEventListener(joinerConnectedListener);
                    Intent intent = new Intent(getActivity(), GameActivity.class);
                    intent.putExtra("EXTRA_START_WORD", start);
                    intent.putExtra("EXTRA_END_WORD", end);
                    intent.putExtra("EXTRA_IS_HOST", true);
                    intent.putExtra("EXTRA_GAME_ID", Utils.encodeUserName(mUserData.getUserName()));
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
        playAreaRef.addValueEventListener(joinerConnectedListener);
    }

    public interface OnFragmentDetachListener {
        void onFragmentDetach();
    }
}
