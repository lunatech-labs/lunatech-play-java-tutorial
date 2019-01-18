package actors.messages;

/**
 * This class is used as the Message used by the DB Actor to store a file path corresponding with a description.
 */
public class DBUpdateImageActorMsg {

    private String description;
    private String filePath;

    public DBUpdateImageActorMsg() {
        // Default Ctor
    }

    public DBUpdateImageActorMsg(String description, String filePath) {
        this.filePath = filePath;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
