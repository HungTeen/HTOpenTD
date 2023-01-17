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
    public static final HTCodecRegistry<IEffectComponent> EFFECTS = HTRegistryManager.create(IEffectComponent.class, "tower_defence/effects", HTEffectComponents::getCodec, true);

    /* Effect types */

    public static final IEffectComponentType<DamageEffectComponent> DAMAGE_EFFECT = new DefaultEffect<>("damage",  DamageEffectComponent.CODEC);
    public static final IEffectComponentType<SplashEffectComponent> SPLASH_EFFECT = new DefaultEffect<>("splash",  SplashEffectComponent.CODEC);
    public static final IEffectComponentType<PotionEffectComponent> POTION_EFFECT = new DefaultEffect<>("potion",  PotionEffectComponent.CODEC);
    public static final IEffectComponentType<ExplosionEffectComponent> EXPLOSION_EFFECT = new DefaultEffect<>("explosion",  ExplosionEffectComponent.CODEC);
    public static final IEffectComponentType<AttractEffectComponent> ATTRACT_EFFECT = new DefaultEffect<>("attract",  AttractEffectComponent.CODEC);
    public static final IEffectComponentType<RandomEffectComponent> RANDOM_EFFECT = new DefaultEffect<>("random",  RandomEffectComponent.CODEC);

    /* Effects */

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerStuffs(){
        Arrays.asList(DAMAGE_EFFECT, SPLASH_EFFECT, POTION_EFFECT, EXPLOSION_EFFECT, ATTRACT_EFFECT, RANDOM_EFFECT).forEach(HTEffectComponents::registerEffectType);
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
