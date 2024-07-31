package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.util.NBTUtil;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:32
 **/
public interface OTDEffectComponents {

    HTCodecRegistry<IEffectComponent> EFFECTS = HTRegistryManager.create(Util.prefix("effects"), OTDEffectComponents::getDirectCodec);

    ResourceKey<IEffectComponent> FOUR_POINT_DAMAGE = create("four_point_damage");
    ResourceKey<IEffectComponent> KNOCKBACK_DAMAGE = create("knockback_damage");
    ResourceKey<IEffectComponent> SPLASH_DAMAGE_TO_ALL_ENTITIES = create("splash_damage_to_all_entities");
    ResourceKey<IEffectComponent> STRENGTH_EFFECT = create("strength_effect");
    ResourceKey<IEffectComponent> EXPLOSION_EFFECT = create("explosion_effect");
    ResourceKey<IEffectComponent> ATTRACT_ALL_EFFECT = create("attract_all_effect");
    ResourceKey<IEffectComponent> SET_FIVE_HEALTH = create("set_five_health");
    ResourceKey<IEffectComponent> SET_ON_FIRE = create("set_on_fire");
    ResourceKey<IEffectComponent> SUMMON_XP_AROUND = create("summon_xp_around");
    ResourceKey<IEffectComponent> DIAMOND_AND_EMERALD = create("diamond_and_emerald");
    ResourceKey<IEffectComponent> KNOCKBACK_EFFECT = create("knockback_effect");
    ResourceKey<IEffectComponent> PEA_DAMAGE = create("pea_damage");

    static void register(BootstapContext<IEffectComponent> context){
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        final HolderGetter<IEffectComponent> effects = OTDEffectComponents.registry().helper().lookup(context);
        context.register(FOUR_POINT_DAMAGE, new DamageEffectComponent(false, 4F, 0F));
        context.register(KNOCKBACK_DAMAGE, new DamageEffectComponent(false, 1F, 1F));
        context.register(SPLASH_DAMAGE_TO_ALL_ENTITIES, new SplashEffectComponent(
                3F, 2F, false, filters.getOrThrow(OTDTargetFilters.ALL), effects.getOrThrow(FOUR_POINT_DAMAGE)
        ));
        context.register(STRENGTH_EFFECT, new PotionEffectComponent(
                MobEffects.DAMAGE_BOOST, 1200, 1, true, true
        ));
        context.register(EXPLOSION_EFFECT, new ExplosionEffectComponent(true, true, 3, false));
        context.register(ATTRACT_ALL_EFFECT, new AttractEffectComponent(
                filters.getOrThrow(OTDTargetFilters.ALL), Optional.empty()
        ));
        context.register(SET_FIVE_HEALTH, new NBTEffectComponent(NBTUtil.fiveHealth(), false));
        context.register(SET_ON_FIRE, new NBTEffectComponent(NBTUtil.onFire(), false));
        context.register(SUMMON_XP_AROUND, new SummonEffectComponent(
                1, 5, Optional.of(5), 16, true, true, EntityType.EXPERIENCE_ORB, new CompoundTag()
        ));
        context.register(DIAMOND_AND_EMERALD, new FunctionEffectComponent(false, Util.prefix("test")));
        context.register(KNOCKBACK_EFFECT, new KnockbackEffectComponent(
                false, false, 1F, 0.5F, Vec3.ZERO
        ));
        context.register(PEA_DAMAGE, new ListEffectComponent(List.of(
                effects.getOrThrow(FOUR_POINT_DAMAGE), effects.getOrThrow(KNOCKBACK_DAMAGE)
        )));
    }

    static ResourceKey<IEffectComponent> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<Holder<IEffectComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<IEffectComponent> getDirectCodec(){
        return OTDEffectComponentTypes.registry().byNameCodec().dispatch(IEffectComponent::getType, IEffectComponentType::codec);
    }

    static IHTCodecRegistry<IEffectComponent> registry(){
        return EFFECTS;
    }
}
