package ng.lyu.sharedscreenshot.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ImageCache {
    private final static int maxImage = 30;

    private final static LinkedHashMap<UUID, byte[]> cache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, byte[]> eldest) {
            return size() > maxImage;
        }
    };

    public static void addImage(UUID uuid, byte[] imageData) {
        cache.put(uuid, imageData);
    }

    public static byte[] getImage(UUID uuid) {
        return cache.get(uuid);
    }
}