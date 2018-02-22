package com.logiforge.ballo.sync.protocol.conversion;

import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {
    private static Map<String, MPackSyncEntityConverter> converters = new HashMap<>();

    public static void registerSyncEntityConverter(String className, MPackSyncEntityConverter converter) {
        converters.put(className, converter);
    }

    public static MPackSyncEntityConverter getSyncEntityConverter(String className) {
        return converters.get(className);
    }

    public static MPackSyncEntityConverter getSyncEntityConverter(Class calzz) {
        return converters.get(calzz.getSimpleName());
    }
}
