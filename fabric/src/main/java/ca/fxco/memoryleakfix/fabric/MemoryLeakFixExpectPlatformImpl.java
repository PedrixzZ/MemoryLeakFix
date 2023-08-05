package ca.fxco.memoryleakfix.fabric;

import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;

@SuppressWarnings("unused")
public class MemoryLeakFixExpectPlatformImpl {

    private static final FabricLoader FABRIC_LOADER = FabricLoader.getInstance();
    private static final ModMetadata MINECRAFT_METADATA = FABRIC_LOADER.getModContainer("minecraft").get().getMetadata();

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#isModLoaded}.
     */
    public static boolean isModLoaded(String id) {
        return FABRIC_LOADER.isModLoaded(id);
    }

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#compareMinecraftToVersion}.
     */
    public static int compareMinecraftToVersion(String version) throws VersionParsingException {
        return MINECRAFT_METADATA.getVersion().compareTo(Version.parse(version));
    }

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#getMappingType}.
     */
    public static String getMappingType() {
        return "fabric";
    }

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#isDevEnvironment}.
     */
    public static boolean isDevEnvironment() {
        return FABRIC_LOADER.isDevelopmentEnvironment();
    }
}
