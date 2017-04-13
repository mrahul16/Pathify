package in.maru.pathify.utils;

import android.os.Build;

import java.util.concurrent.ThreadLocalRandom;

import in.maru.pathify.model.WordPair;

public class WordsProvider {

    private static final String wordPairs[][];

    static {
        wordPairs = new String[][]{
                {"hello", "world"},
                {"castle", "glass"},
                {"kings", "landing"},
                {"cheap", "thrills"},
                {"road", "taken"},
                {"micro", "soft"},
                {"orange", "black"},
                {"better", "call"},
                {"array", "list"},
                {"earth", "mars"},
                {"summer", "code"},
                {"winter", "coming"},
                {"breaking", "bad"},
                {"karate", "kid"},
                {"harry", "potter"},
                {"angels", "demons"}
        };
    }

    public static WordPair getRandomWordPair() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int index = ThreadLocalRandom.current().nextInt(0, wordPairs.length + 1);
            return new WordPair(wordPairs[index][0], wordPairs[index][1]);
        }
        return new WordPair(wordPairs[0][0], wordPairs[0][1]);
    }

}
