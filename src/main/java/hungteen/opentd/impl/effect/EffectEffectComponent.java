package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.EntityHelper;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.ParticleHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.common.codec.ParticleSetting;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 16:35
 **/
public record EffectEffectComponent(boolean self, List<ParticleSetting> particleSettings, Optional<SoundEvent> soundEvent) implements IEffectComponent {

    public static final Codec<EffectEffectComponent> CODEC = RecordCodecBuilder.<EffectEffectComponent>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("self", true).forGetter(EffectEffectComponent::self),
            ParticleSetting.CODEC.listOf().optionalFieldOf("particle_settings", Arrays.asList()).forGetter(EffectEffectComponent::particleSettings),
            Codec.optionalField("sound", SoundEvent.CODEC).forGetter(EffectEffectComponent::soundEvent)
    ).apply(instance, EffectEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        particleSettings().forEach(l -> {
            l.spawn(serverLevel, self() ? owner.position() : entity.position(), serverLevel.getRandom());
        });
        soundEvent().ifPresent(l -> (self() ? owner : entity).playSound(l));
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        particleSettings().forEach(l -> {
            l.spawn(serverLevel, self() ? owner.position() : MathHelper.toVec3(pos), serverLevel.getRandom());
        });
        soundEvent().ifPresent(owner::playSound);
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.EFFECT_EFFECT;
    }

}