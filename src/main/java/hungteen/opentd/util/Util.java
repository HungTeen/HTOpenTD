package hungteen.opentd.util;

import com.mojang.logging.LogUtils;
import hungteen.htlib.util.helper.ForgeHelper;
import hungteen.htlib.util.helper.IModIDHelper;
import hungteen.htlib.util.helper.VanillaHelper;
import hungteen.opentd.OpenTD;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class Util {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final IModIDHelper HELPER = () -> OpenTD.MOD_ID;

    public static IModIDHelper get(){
        return HELPER;
    }

    public static IModIDHelper mc(){
        return VanillaHelper.get();
    }

    public static IModIDHelper forge(){
        return ForgeHelper.get();
    }

    public static String id(){
        return get().getModID();
    }

    /**
     * get resource with mod prefix.
     */
    public static ResourceLocation prefix(String name) {
        return get().prefix(name);
    }

    public static boolean in(ResourceLocation location){
        return get().in(location);
    }

    public static String prefixName(String name) {
        return prefix(name).toString();
    }

    public static String toString(ResourceKey<?> resourceKey){
        return resourceKey.registry() + ":" + resourceKey.location();
    }

    public static void error(String message, Object... arguments){
        LOGGER.error(message, arguments);
    }

    public static void debug(String message, Object... arguments){
        LOGGER.debug(message, arguments);
    }

    public static void warn(String message, Object... arguments){
        LOGGER.warn(message, arguments);
    }

    public static void info(String message, Object... arguments){
        LOGGER.info(message, arguments);
    }
}
