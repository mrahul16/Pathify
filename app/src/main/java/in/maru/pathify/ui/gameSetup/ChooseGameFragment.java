package in.maru.pathify.ui.gameSetup;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.maru.pathify.R;
import in.maru.pathify.model.Game;
import in.maru.pathify.utils.Constants;
import in.maru.pathify.utils.Utils;

public class ChooseGameFragment extends Fragment {
    
    private static final String TAG = ChooseGameFragment.class.getSimpleName();

    private ListView mListView;
    private FloatingActionButton fab;
    private ChooseGameListAdapter mChooseGameListAdapter;

    private String mGameId;

    public ChooseGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_game, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view_active_games);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(Constants.FBKEY_LOBBY);

        mChooseGameListAdapter = new ChooseGameListAdapter(getActivity(), Game.class,
                R.layout.list_item_game, reference);

        mListView.setAdapter(mChooseGameListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mGameId = Utils.encodeUserName(((TextView)view.findViewById(R.id.text_view_host))
                        .getText().toString());
                showWaitingScreen(OnGameChosenWaitFragment.newInstance(mGameId));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitingScreen(OnGameCreatedWaitFragment.newInstance());
            }
        });

        return view;
    }



    @Override
    public Context getContext() {
        Log.i(TAG, "getContext: ");
        return super.getContext();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        if(fab != null) {
            fab.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        if(fab != null) {
            fab.hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        mChooseGameListAdapter.cleanup();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }

    private void showWaitingScreen(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
