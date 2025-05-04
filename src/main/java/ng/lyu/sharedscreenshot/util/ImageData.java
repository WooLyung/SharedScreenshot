package ng.lyu.sharedscreenshot.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class ImageData {

    private String sender = "";
    private int segCnt = -1;
    private final HashMap<Integer, byte[]> segments = new HashMap<>();
    private byte[] imageData = null;

    public ImageData(String sender, int segCnt) {
        this.sender = sender;
        this.segCnt = segCnt;
    }

    public ImageData(int segment, byte[] data) {
        this.segments.put(segment, data);
    }

    public void update(String sender, int segCnt) {
        this.sender = sender;
        this.segCnt = segCnt;

        if (this.segCnt <= segments.size())
            assemble();
    }

    public void update(int segment, byte[] data) {
        this.segments.put(segment, data);

        if (this.segCnt <= segments.size())
            assemble();
    }

    private void assemble() {
        int size = 0;
        for (int i = 0; i < this.segCnt; i++)
            size += segments.get(i).length;

        imageData = new byte[size];
        int p = 0;
        for (int i = 0; i < this.segCnt; i++)
            for (int j = 0; j < segments.get(i).length; j++)
                imageData[p++] = segments.get(i)[j];
    }

    public byte[] getImage() {
        return imageData;
    }

    public String getSender() {
        return sender;
    }
}