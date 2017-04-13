package in.maru.pathify.ui.gameplay;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import in.maru.pathify.PathifyApplication;
import in.maru.pathify.R;
import in.maru.pathify.model.UserData;
import in.maru.pathify.ui.GameOverActivity;
import in.maru.pathify.ui.gameSetup.GameSetupActivity;
import in.maru.pathify.utils.Constants;
import in.maru.pathify.utils.core.Dictionary;
import in.maru.pathify.utils.core.WPTree;

/**
 * A placeholder fragment containing a simple view.
 */
public class GameActivityFragment extends Fragment {

    private static final String LOG_TAG = GameActivityFragment.class.getSimpleName();

    private UserData mUserData;
    private WPTree mWpTree;
    private Dictionary d;

    private String start;
    private String end;
    private boolean isHost;
    private String gameId;
    private String otherDisplayName;
    private String otherUserName;
    private String otherProfilePicture;

    private int current = 0;
    private boolean stopped = false;

    private TextView remaining;
    private LinearLayout wordPathContainer;
    private TextView[] dOneWords;
    private ArrayList<TextView> pathTextView;
    private View myProgressBar;
    private View opponentProgressBar;
    private float density;


    private ArrayList<String> wordPath;
    private int pathLength;
    private HashSet<String> pathSet;

    private DatabaseReference playAreaRef;
    private DatabaseReference opponentProgressRef;
    private DatabaseReference myProgressRef;

    private View.OnClickListener dOneTextClickListener;
    private View.OnClickListener pathTextClickListener;
    private ValueEventListener opponentProgressListener;

    public GameActivityFragment() {
    }

    public static GameActivityFragment newInstance(String start, String end, boolean isHost, String gameId,
                                                   String otherDisplayName, String otherUserName, String otherProfilePicture) {

        Bundle args = new Bundle();
        args.putString("start", start);
        args.putString("end", end);
        args.putBoolean("isHost", isHost);
        args.putString("gameId", gameId);
        args.putString("oDName", otherDisplayName);
        args.putString("oUName", otherUserName);
        args.putString("oProfPic", otherProfilePicture);
        GameActivityFragment fragment = new GameActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(stopped) {
            removeListeners();
            Intent intent = new Intent(getActivity(), GameSetupActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            start = getArguments().getString("start");
            end = getArguments().getString("end");
            isHost = getArguments().getBoolean("isHost");
            gameId = getArguments().getString("gameId");
            otherDisplayName = getArguments().getString("oDName");
            otherUserName = getArguments().getString("oUName");
            otherProfilePicture = getArguments().getString("oProfPic");
            Log.d(LOG_TAG, start + "->" + end + " " + isHost);
        }

        mUserData = ((PathifyApplication) getActivity().getApplicationContext()).getUserData();
        mWpTree = ((PathifyApplication) getActivity().getApplicationContext()).getWpTree();
        d = ((PathifyApplication) getActivity().getApplicationContext()).getDictionary();


        wordPath = new ArrayList<>();
        wordPath.addAll(mWpTree.findPath(start, end));
        pathLength = wordPath.size();
        pathSet = new HashSet<>(wordPath);

        pathTextView = new ArrayList<>();

        dOneTextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = ((TextView) view).getText().toString();
                if (!word.equals(Constants.NO_WORD)) {
                    if(current == pathLength - 3 && !d.getDOne(word).contains(end)) {
                        Toast.makeText(getActivity(), "You can't do that", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        current++;
                        remaining.setText(String.valueOf(pathLength - 2 - current));
                        pathTextView.get(current).setText(word);
                        pathTextView.get(current).setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                        setSixDOneText(getSixDOne(d.getDOne(word), word));
                        int percent = calcProgress(current);
                        if(myProgressRef != null) {
                            if (percent == 100) {
                                myProgressRef.setValue(percent, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if(databaseError != null) {
                                            Log.d(LOG_TAG, String.valueOf(databaseError.getMessage()));
                                        }
                                        gameOver(true);
                                    }
                                });
                            }
                            else {
                                myProgressRef.setValue(percent);
                            }
                        }
                        setProgressBarWidth(myProgressBar, percent);
                    }
                }
            }
        };

        pathTextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView thisView = (TextView) view;
                int index = pathTextView.indexOf(thisView);
                if(index > 0 && index == current) {
                    current--;
                    remaining.setText(String.valueOf(pathLength - 2 - current));
                    thisView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                    String prevWord = pathTextView.get(index - 1).getText().toString();
                    setSixDOneText(getSixDOne(d.getDOne(prevWord), prevWord));
                    int percent = calcProgress(current);
                    if(myProgressRef != null) {
                        myProgressRef.setValue(percent);
                    }
                    setProgressBarWidth(myProgressBar, percent);
                }
            }
        };

        myProgressRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FBKEY_PLAY_AREA)
                .child(gameId).child(percentCompleteKey(true));

        opponentProgressRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FBKEY_PLAY_AREA)
                .child(gameId).child(percentCompleteKey(false));

        playAreaRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FBKEY_PLAY_AREA);


    }

    private void gameOver(boolean win) {
        removeListeners();
        if(win) {
            Toast.makeText(getActivity(), "You won! :)", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "You lose :(", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(getActivity(), GameOverActivity.class);
        intent.putExtra("EXTRA_WIN", win);
        startActivity(intent);
    }

    private String percentCompleteKey(boolean me) {
        return me ?
                isHost ? "hostPercentComplete" : "joinerPercentComplete"
                : isHost ? "joinerPercentComplete" : "hostPercentComplete";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        myProgressBar = view.findViewById(R.id.my_progress);
        opponentProgressBar = view.findViewById(R.id.other_progress);
        remaining = (TextView) view.findViewById(R.id.remaining);
        wordPathContainer = (LinearLayout) view.findViewById(R.id.path_words_container);
        ((TextView) view.findViewById(R.id.start)).setText(start);
        ((TextView) view.findViewById(R.id.end)).setText(end);
        ((TextView) view.findViewById(R.id.num_edit)).setText(String.valueOf(pathLength - 2));
        remaining.setText(String.valueOf(pathLength - 2));

        setDOneWords(view);

        loadProfilePictures(view, R.id.my_profile_pic, mUserData.getProfilePicURL());
        loadProfilePictures(view, R.id.other_profile_pic, otherProfilePicture);

        loadTextViews();

        setSixDOneText(getSixDOne(d.getDOne(start), start));

        opponentProgressListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    if(dataSnapshot.getValue() == null) {
                        Toast.makeText(getActivity(), otherDisplayName + " left the game.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), GameSetupActivity.class);
                        startActivity(intent);
                    }
                    else {
                        int percent = ((Long) (dataSnapshot.getValue())).intValue();
                        if (percent == 100) {
                            gameOver(false);
                        }
                        setProgressBarWidth(opponentProgressBar, percent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        opponentProgressRef.addValueEventListener(opponentProgressListener);

        return view;
    }

    private void setSixDOneText(ArrayList<String> sixDOne) {
        for(int i = 0; i < 6; i++) {
            dOneWords[i].setText(sixDOne.get(i));
        }
    }

    private ArrayList<String> getSixDOne(ArrayList<String> dOne, String word) {
        ArrayList<String> sixDOne = new ArrayList<>(6);
        int size = dOne.size();
        if(size <= 6) {
            sixDOne.addAll(dOne);
            for(int i = size; i < 6; i++) {
                sixDOne.add(Constants.NO_WORD);
            }
        }
        else {
            List<String> subList = dOne.subList(0, 6);
            sixDOne.addAll(subList);
            String nextWord = wordPath.get(wordPath.indexOf(word) + 1);
            if(pathSet.contains(word) && !subList.contains(nextWord)) {
                sixDOne.set((new Random()).nextInt(6), nextWord);
            }
        }
        return sixDOne;
    }

    private void setDOneWords(View view) {
        dOneWords = new TextView[]{
                (TextView) view.findViewById(R.id.one),
                (TextView) view.findViewById(R.id.two),
                (TextView) view.findViewById(R.id.three),
                (TextView) view.findViewById(R.id.four),
                (TextView) view.findViewById(R.id.five),
                (TextView) view.findViewById(R.id.six)
        };
        for (int i = 0; i < 6; i++) {
            dOneWords[i].setOnClickListener(dOneTextClickListener);
        }
    }

    private void setProgressBarWidth(View view, int percentage) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = 4 * (int) density;
        params.width = (int) (percentage * 1.44 * density);
        view.setLayoutParams(params);
    }

    private void loadTextViews() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        density = getActivity().getResources().getDisplayMetrics().density;

        Log.d(LOG_TAG, String.valueOf(density));

        params.setMarginEnd(4 * (int) density);
        params.setMargins(4 * (int) density, 4 * (int) density, 4 * (int) density, 4 * (int) density);

        int padding = 8 * (int) density;

        for(int i = 0; i < pathLength; i++) {
            TextView tv = new TextView(getActivity());
            tv.setText(wordPath.get(i));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tv.setPadding(padding * 2, padding, padding * 2, padding);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tv.setElevation(8 * (int) density);
            }
            if(i == 0 || i == pathLength - 1) {
                tv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            }
            else {
                tv.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                tv.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.path_curve_border, null));

            }
            tv.setLayoutParams(params);
            pathTextView.add(tv);
            wordPathContainer.addView(tv);

            tv.setOnClickListener(pathTextClickListener);
        }
    }

    private void loadProfilePictures(View view, int resId, String url) {
        Glide
                .with(getContext())
                .load(url)
                .centerCrop()
                .crossFade()
                .into((ImageView) view.findViewById(resId));
    }

    private int calcProgress(int curr) {
        return (int) Math.ceil(curr * 1.0 / (pathLength - 2) * 100.0);
    }

    private void removeListeners() {
        if(opponentProgressRef != null) {
            opponentProgressRef.removeEventListener(opponentProgressListener);
        }
        opponentProgressRef = null;
        myProgressRef = null;
        if(playAreaRef != null) {
            playAreaRef.setValue(null);
        }
        playAreaRef = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        stopped = true;
        removeListeners();
    }
}
