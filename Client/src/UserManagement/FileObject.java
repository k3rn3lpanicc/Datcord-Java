package UserManagement;

public class FileObject {
    private String path;
    private String name;
    public FileObject(String path, String name) {
        this.path = path;
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public String getName() {
        return name;
    }
}
