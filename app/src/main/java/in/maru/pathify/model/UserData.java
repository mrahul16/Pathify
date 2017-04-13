package in.maru.pathify.model;


public class UserData {

    private String displayName;
    private String userName;
    private String profilePicURL;

    public UserData() {
    }

    public UserData(String displayName, String userName, String profilePicURL) {
        this.displayName = displayName;
        this.userName = userName;
        this.profilePicURL = profilePicURL;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }
}
