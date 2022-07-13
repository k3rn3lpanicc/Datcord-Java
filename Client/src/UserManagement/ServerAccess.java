package UserManagement;

import java.io.Serializable;

public class ServerAccess implements Serializable {
    private boolean createChannel;
    private boolean removeChannel;
    private boolean removeMemberFromServer;
    private boolean restrictMembersOfAChat;
    private boolean removeMembersFromChannel;
    private boolean changeServername;
    private boolean seeChatHistory;
    private boolean abilityToPin;
    public static ServerAccess fromCode(String code){
        ServerAccess result = new ServerAccess();
        result.createChannel = code.charAt(0)=='1';
        result.removeChannel = code.charAt(1)=='1';
        result.removeMemberFromServer = code.charAt(2)=='1';
        result.restrictMembersOfAChat = code.charAt(3)=='1';
        result.removeMembersFromChannel = code.charAt(4)=='1';
        result.changeServername = code.charAt(5)=='1';
        result.seeChatHistory = code.charAt(6)=='1';
        result.abilityToPin = code.charAt(7)=='1';
        return result;
    }
    public static String toCode(ServerAccess in){
        return (in.createChannel?"1":"0")+(in.removeChannel?"1":"0")+(in.removeMemberFromServer?"1":"0")+(in.restrictMembersOfAChat?"1":"0")+(in.removeMembersFromChannel?"1":"0")+(in.changeServername?"1":"0")+(in.seeChatHistory?"1":"0")+(in.abilityToPin?"1":"0");
    }
    public boolean isCreateChannel() {
        return createChannel;
    }
    public void setCreateChannel(boolean createChannel) {
        this.createChannel = createChannel;
    }

    public boolean isRemoveChannel() {
        return removeChannel;
    }

    public void setRemoveChannel(boolean removeChannel) {
        this.removeChannel = removeChannel;
    }

    public boolean isRemoveMemberFromServer() {
        return removeMemberFromServer;
    }

    public void setRemoveMemberFromServer(boolean removeMemberFromServer) {
        this.removeMemberFromServer = removeMemberFromServer;
    }

    public boolean isRestrictMembersOfAChat() {
        return restrictMembersOfAChat;
    }

    public void setRestrictMembersOfAChat(boolean restrictMembersOfAChat) {
        this.restrictMembersOfAChat = restrictMembersOfAChat;
    }

    public boolean isRemoveMembersFromChannel() {
        return removeMembersFromChannel;
    }

    public void setRemoveMembersFromChannel(boolean removeMembersFromChannel) {
        this.removeMembersFromChannel = removeMembersFromChannel;
    }

    public boolean isChangeServername() {
        return changeServername;
    }

    public void setChangeServername(boolean changeServername) {
        this.changeServername = changeServername;
    }

    public boolean isSeeChatHistory() {
        return seeChatHistory;
    }

    public void setSeeChatHistory(boolean seeChatHistory) {
        this.seeChatHistory = seeChatHistory;
    }

    public boolean isAbilityToPin() {
        return abilityToPin;
    }

    public void setAbilityToPin(boolean abilityToPin) {
        this.abilityToPin = abilityToPin;
    }
}
