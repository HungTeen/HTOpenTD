package hungteen.opentd.common.impl;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.codec.BulletSetting;
import hungteen.opentd.common.codec.ParticleSetting;
import hungteen.opentd.common.codec.RenderSetting;
import hungteen.opentd.common.impl.effect.OTDEffectComponents;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-06-23 09:56
 **/
public interface OTDBulletSettings {

    HTCodecRegistry<BulletSetting> SETTINGS = HTRegistryManager.create(Util.prefix("bullet_settings"), OTDBulletSettings::getDirectCodec);

    ResourceKey<BulletSetting> PEA = create("pea");

    static void register(BootstapContext<BulletSetting> context){
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        final HolderGetter<IEffectComponent> effects = OTDEffectComponents.registry().helper().lookup(context);
        context.register(PEA, new BulletSetting(
                filters.getOrThrow(OTDTargetFilters.CREEPER_ONLY),
                effects.getOrThrow(OTDEffectComponents.PEA_DAMAGE),
                0.2F, 1, 300, 0.0001F, 0.99999F, 0.8F, false, true, true,
                RenderSetting.make(0.5F, 0.5F, 0.6F, false, "pea_shooter"),
                Optional.empty(),
                Optional.of(
                        new ParticleSetting(
                                ParticleTypes.FLAME, 1, true, new Vec3(1, 1, 1), new Vec3(0.1, 0.1, 0.1)
                        )
                )
        ));
//        new ListEffectComponent(Arrays.asList(
//                new DamageEffectComponent(false, 5F, 0),
//                new SplashEffectComponent(5, 5, true, new OrTargetFilter(List.of()), new DamageEffectComponent(false, 2F, 0.1F)),
//                new NBTEffectComponent(get(), false),
//                new RandomEffectComponent(10, 1, true, List.of(
//                        Pair.of(
//                                new FunctionEffectComponent(false, new CommandFunction.CacheableFunction(Util.prefix("test"))),
//                                1
//                        )
//                ))
//        )),
    }

    static ResourceKey<BulletSetting> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<BulletSetting> getDirectCodec(){
        return BulletSetting.CODEC;
    }

    static Codec<Holder<BulletSetting>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static IHTCodecRegistry<BulletSetting> registry(){
        return SETTINGS;
    }

}
