package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.util.Util;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public interface OTDEffectComponentTypes {

    HTSimpleRegistry<IEffectComponentType<?>> EFFECT_TYPES = HTRegistryManager.createSimple(Util.prefix("effect_type"));

    IEffectComponentType<ListEffectComponent> LIST_EFFECT = register(new DefaultEffect<>("list",  ListEffectComponent.CODEC));
    IEffectComponentType<RandomEffectComponent> RANDOM_EFFECT = register(new DefaultEffect<>("random",  RandomEffectComponent.CODEC));
    IEffectComponentType<DamageEffectComponent> DAMAGE_EFFECT = register(new DefaultEffect<>("damage",  DamageEffectComponent.CODEC));
    IEffectComponentType<SplashEffectComponent> SPLASH_EFFECT = register(new DefaultEffect<>("splash",  SplashEffectComponent.CODEC));
    IEffectComponentType<PotionEffectComponent> POTION_EFFECT = register(new DefaultEffect<>("potion",  PotionEffectComponent.CODEC));
    IEffectComponentType<ExplosionEffectComponent> EXPLOSION_EFFECT = register(new DefaultEffect<>("explosion",  ExplosionEffectComponent.CODEC));
    IEffectComponentType<AttractEffectComponent> ATTRACT_EFFECT = register(new DefaultEffect<>("attract",  AttractEffectComponent.CODEC));
    IEffectComponentType<NBTEffectComponent> NBT_EFFECT = register(new DefaultEffect<>("nbt",  NBTEffectComponent.CODEC));
    IEffectComponentType<SummonEffectComponent> SUMMON_EFFECT = register(new DefaultEffect<>("summon",  SummonEffectComponent.CODEC));
    IEffectComponentType<FunctionEffectComponent> FUNCTION_EFFECT = register(new DefaultEffect<>("function",  FunctionEffectComponent.CODEC));
    IEffectComponentType<KnockbackEffectComponent> KB_EFFECT = register(new DefaultEffect<>("kb",  KnockbackEffectComponent.CODEC));
    IEffectComponentType<FilterEffectComponent> FILTER_EFFECT = register(new DefaultEffect<>("filter",  FilterEffectComponent.CODEC));
    IEffectComponentType<EffectEffectComponent> EFFECT_EFFECT = register(new DefaultEffect<>("effect",  EffectEffectComponent.CODEC));
    IEffectComponentType<EventEffectComponent> EVENT_EFFECT = register(new DefaultEffect<>("event",  EventEffectComponent.CODEC));
    IEffectComponentType<VanillaHurtEffect> VANILLA_HURT_EFFECT = register(new DefaultEffect<>("vanilla_hurt",  VanillaHurtEffect.CODEC));

    static IHTSimpleRegistry<IEffectComponentType<?>> registry(){
        return EFFECT_TYPES;
    }

    static <T extends IEffectComponent> IEffectComponentType<T> register(IEffectComponentType<T> type){
        return registry().register(type);
    }

    record DefaultEffect<P extends IEffectComponent>(String name, Codec<P> codec) implements IEffectComponentType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return OpenTD.MOD_ID;
        }
    }
}
