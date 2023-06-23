package hungteen.opentd.common.impl;

import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.OpenTD;
import hungteen.opentd.common.codec.BulletSetting;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-06-23 09:56
 **/
public class HTBulletSettings {

    public static final HTCodecRegistry<BulletSetting> SETTINGS = HTRegistryManager.create(BulletSetting.class, "tower_defence/bullet_settings", () -> BulletSetting.CODEC, OpenTD.MOD_ID);

    /**
     * {@link OpenTD#setUp(FMLCommonSetupEvent)} ()}
     */
    public static void registerStuffs() {
    }

}
