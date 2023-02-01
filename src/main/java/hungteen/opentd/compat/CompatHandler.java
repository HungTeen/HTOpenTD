package hungteen.opentd.compat;

import net.minecraftforge.fml.ModList;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-01 10:48
 **/
public class CompatHandler {

    public static final String KUBEJS = "kubejs";

    public static boolean isModLoaded(String mod) {
        return ModList.get().isLoaded(mod);
    }

    public static boolean isKubeJSLoaded() {
        return isModLoaded(KUBEJS);
    }
}
