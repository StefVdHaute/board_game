package mijnlieff.pionnen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Pionnen {

    private static final Map<String, Supplier<Pion>> map = new HashMap<>();

    static {
        map.put("pusher", Pusher::new);
        map.put("puller", Puller::new);
        map.put("toren", Toren::new);
        map.put("loper", Loper::new);
    }

    public static Pion create(String pion) {
        Supplier<Pion> factory = map.get(pion);
        return factory.get();
    }
}
