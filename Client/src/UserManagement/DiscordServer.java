package UserManagement;

import java.io.Serializable;

public class DiscordServer implements Serializable {
    private String id;
    private String name;
    private String ownerId;
    private String members;
    public DiscordServer(String id, String name, String ownerId, String members) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.members = members;
    }
    public DiscordServer(){}
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public String getMembers() {
        return members;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "DiscordServer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", members='" + members + '\'' +
                '}';
    }
}
