package hungteen.opentd;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2024/10/3 11:34
 **/
public class OTDConfigs {

    private static Common COMMON_CONFIG;
    private static Client CLIENT_CONFIG;

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void init(){
        {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, specPair.getRight());
            COMMON_CONFIG = specPair.getLeft();
        }
        {
            final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
            ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, specPair.getRight());
            CLIENT_CONFIG = specPair.getLeft();
        }
    }

    public static class Common {

        public Common(ForgeConfigSpec.Builder builder) {

        }

    }

    public static class Client {

        public Client(ForgeConfigSpec.Builder builder) {
            displayDefaultCards = builder
                    .translation("config.otd.display_default_cards")
                    .comment("Set to false to disable default OTD cards in creative tab.")
                    .define("DisplayDefaultCards", true);
        }

        public ForgeConfigSpec.BooleanValue displayDefaultCards;

    }

    /* Common */

    /* Client */

    public static boolean displayDefaultCards() {
        return CLIENT_CONFIG.displayDefaultCards.get();
    }
}
