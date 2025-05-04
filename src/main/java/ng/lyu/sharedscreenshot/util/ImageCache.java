package ng.lyu.sharedscreenshot.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ImageCache {

    private final static int maxImage = 30;

    private final static LinkedHashMap<UUID, ImageData> cache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, ImageData> eldest) {
            return size() > maxImage;
        }
    };

    public static void insertOrUpdate(UUID uuid, String sender, int segCnt) {
        if (!cache.containsKey(uuid))
            cache.put(uuid, new ImageData(sender, segCnt));
        else
            cache.get(uuid).update(sender, segCnt);
    }

    public static void insertOrUpdate(UUID uuid, int segment, byte[] data) {
        if (!cache.containsKey(uuid))
            cache.put(uuid, new ImageData(segment, data));
        else
            cache.get(uuid).update(segment, data);
    }

    public static byte[] getImage(UUID uuid) {
        if (!cache.containsKey(uuid))
            return null;
        return cache.get(uuid).getImage();
    }

    public static String getSender(UUID uuid) {
        if (!cache.containsKey(uuid))
            return null;
        return cache.get(uuid).getSender();
    }
}