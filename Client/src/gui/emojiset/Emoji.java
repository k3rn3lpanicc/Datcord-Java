package gui.emojiset;
public class Emoji{
    private String name;
    private String emoji;
    public Emoji(String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmoji() {
        return emoji;
    }
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}