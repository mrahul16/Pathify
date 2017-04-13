package in.maru.pathify;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import in.maru.pathify.model.UserData;
import in.maru.pathify.utils.Constants;
import in.maru.pathify.utils.core.Dictionary;
import in.maru.pathify.utils.core.DictionaryHashMap;
import in.maru.pathify.utils.core.DictionaryLoader;
import in.maru.pathify.utils.core.NearbyWords;
import in.maru.pathify.utils.core.WPTree;


public class PathifyApplication extends Application {

    public UserData userData;
    public WPTree wpTree;
    public Dictionary d;

    @Override
    public void onCreate() {
        super.onCreate();
        retrieveUserData();

        d = new DictionaryHashMap();
        DictionaryLoader.loadDictionary(this, d, "wordsDOne.txt");
        NearbyWords nw = new NearbyWords(d);
        wpTree = new WPTree(nw);
    }

    private void retrieveUserData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        userData = new UserData(sp.getString(Constants.KEY_DISPLAY_NAME, null),
                sp.getString(Constants.KEY_USER_NAME, null),
                sp.getString(Constants.KEY_PROFILE_PIC_URL, null));
    }

    public UserData getUserData() {
        return this.userData;
    }

    public WPTree getWpTree() {
        return wpTree;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Dictionary getDictionary() {
        return this.d;
    }
}
