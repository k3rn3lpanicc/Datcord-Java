package DataTransmit;

import java.io.*;

public class DataPacket implements Serializable{
    String type;
    Object data;
    public DataPacket(String type, Object data) {
        this.type = type;
        this.data = data;
    }
    public DataPacket(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public byte[] getBytes() throws IOException {
        byte[] datas;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream oOut = new ObjectOutputStream(outputStream);
        oOut.writeObject(data);
        datas = outputStream.toByteArray();
        return datas;
    }

    public static String getString(DataPacket dataPacket) throws IOException {
        return dataPacket.getType().equals("String")?new String(dataPacket.getBytes()):null;
    }
    public static Object getObject(DataPacket dataPacket) throws IOException, ClassNotFoundException {
        InputStream k = new ByteArrayInputStream(dataPacket.getBytes());
        ObjectInputStream oin = new ObjectInputStream(k);
        return oin.readObject();
    }
}
