package hungteen.opentd.common.impl;

import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.common.codec.BulletSetting;
import hungteen.opentd.util.Util;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-06-23 09:56
 **/
public interface OTDBulletSettings {

    HTCodecRegistry<BulletSetting> SETTINGS = HTRegistryManager.create(Util.prefix("bullet_settings"), () -> BulletSetting.CODEC);

    static void register(BootstapContext<BulletSetting> context){

    }

    static ResourceKey<BulletSetting> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static IHTCodecRegistry<BulletSetting> registry(){
        return SETTINGS;
    }

}
