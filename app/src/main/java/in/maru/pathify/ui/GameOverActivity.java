package in.maru.pathify.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.maru.pathify.R;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        boolean win;
        ImageView resultImage = (ImageView) findViewById(R.id.result_image);
        TextView result = (TextView) findViewById(R.id.result_text);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            win = extras.getBoolean("EXTRA_WIN");
            if(win) {
                resultImage.setImageDrawable(getResources().getDrawable(R.drawable.winner));
                result.setText("YOU WIN!");
            }
            else {
                resultImage.setImageDrawable(getResources().getDrawable(R.drawable.loser));
                result.setText("YOU LOSE");
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
