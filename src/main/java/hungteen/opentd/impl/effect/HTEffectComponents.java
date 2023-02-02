package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.impl.tower.PVZPlantComponent;

import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public class HTEffectComponents {

    public static final HTSimpleRegistry<IEffectComponentType<?>> EFFECT_TYPES = HTRegistryManager.create(OpenTD.prefix("effect_type"));
    public static final HTCodecRegistry<IEffectComponent> EFFECTS = HTRegistryManager.create(IEffectComponent.class, "tower_defence/effect", HTEffectComponents::getCodec, true);

    /* Effect types */

    public static final IEffectComponentType<DamageEffectComponent> DAMAGE_EFFECT = new DefaultEffect<>("damage",  DamageEffectComponent.CODEC);
    public static final IEffectComponentType<SplashEffectComponent> SPLASH_EFFECT = new DefaultEffect<>("splash",  SplashEffectComponent.CODEC);
    public static final IEffectComponentType<PotionEffectComponent> POTION_EFFECT = new DefaultEffect<>("potion",  PotionEffectComponent.CODEC);
    public static final IEffectComponentType<ExplosionEffectComponent> EXPLOSION_EFFECT = new DefaultEffect<>("explosion",  ExplosionEffectComponent.CODEC);
    public static final IEffectComponentType<AttractEffectComponent> ATTRACT_EFFECT = new DefaultEffect<>("attract",  AttractEffectComponent.CODEC);
    public static final IEffectComponentType<RandomEffectComponent> RANDOM_EFFECT = new DefaultEffect<>("random",  RandomEffectComponent.CODEC);
    public static final IEffectComponentType<NBTEffectComponent> NBT_EFFECT = new DefaultEffect<>("nbt",  NBTEffectComponent.CODEC);
    public static final IEffectComponentType<SummonEffectComponent> SUMMON_EFFECT = new DefaultEffect<>("summon",  SummonEffectComponent.CODEC);
    public static final IEffectComponentType<FunctionEffectComponent> FUNCTION_EFFECT = new DefaultEffect<>("function",  FunctionEffectComponent.CODEC);
    public static final IEffectComponentType<KnockbackEffectComponent> KB_EFFECT = new DefaultEffect<>("kb",  KnockbackEffectComponent.CODEC);
    public static final IEffectComponentType<ListEffectComponent> LIST_EFFECT = new DefaultEffect<>("list",  ListEffectComponent.CODEC);
    public static final IEffectComponentType<FilterEffectComponent> FILTER_EFFECT = new DefaultEffect<>("filter",  FilterEffectComponent.CODEC);
    public static final IEffectComponentType<EffectEffectComponent> EFFECT_EFFECT = new DefaultEffect<>("effect",  EffectEffectComponent.CODEC);

    /* Effects */

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerStuffs(){
        Arrays.asList(
                DAMAGE_EFFECT, SPLASH_EFFECT, POTION_EFFECT, EXPLOSION_EFFECT, ATTRACT_EFFECT, RANDOM_EFFECT,
                NBT_EFFECT, SUMMON_EFFECT, FUNCTION_EFFECT, KB_EFFECT, LIST_EFFECT, FILTER_EFFECT,
                EFFECT_EFFECT
        ).forEach(HTEffectComponents::registerEffectType);
    }

    public static void registerEffectType(IEffectComponentType<?> type){
        EFFECT_TYPES.register(type);
    }

    public static Codec<IEffectComponent> getCodec(){
        return EFFECT_TYPES.byNameCodec().dispatch(IEffectComponent::getType, IEffectComponentType::codec);
    }

    protected record DefaultEffect<P extends IEffectComponent>(String name, Codec<P> codec) implements IEffectComponentType<P> {

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
