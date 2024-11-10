package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.htlib.util.helper.registry.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 16:37
 **/
public record ParticleSetting(ParticleType<?> particleType, int amount, boolean isRandom, Vec3 offset, Vec3 speed) {

    public static final Codec<ParticleSetting> CODEC = RecordCodecBuilder.<ParticleSetting>mapCodec(instance -> instance.group(
            ForgeRegistries.PARTICLE_TYPES.getCodec().fieldOf("particle_type").forGetter(ParticleSetting::particleType),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(ParticleSetting::amount),
            Codec.BOOL.optionalFieldOf("is_random", true).forGetter(ParticleSetting::isRandom),
            Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(ParticleSetting::offset),
            Vec3.CODEC.optionalFieldOf("speed", Vec3.ZERO).forGetter(ParticleSetting::speed)
    ).apply(instance, ParticleSetting::new)).codec();

    public Optional<ParticleOptions> getType(){
        if(particleType() instanceof SimpleParticleType){
            return Optional.of((SimpleParticleType) particleType());
        }
        return Optional.empty();
    }

    public void spawn(Level level, Vec3 center, RandomSource rand){
        if(this.getType().isPresent()){
            for(int i = 0; i < amount(); ++ i){
                Vec3 pos = center;
                Vec3 speed = speed();
                if(isRandom()){
                    pos = center.add(RandomHelper.doubleRange(rand, offset().x()), RandomHelper.doubleRange(rand, offset().y()), RandomHelper.doubleRange(rand, offset().z()));
                    speed = new Vec3(RandomHelper.doubleRange(rand, speed().x()), RandomHelper.doubleRange(rand, speed().y()), RandomHelper.doubleRange(rand, speed().z()));
                }
                if(level.isClientSide()){
                    level.addParticle(getType().get(), pos.x(), pos.y(), pos.z(), speed.x(), speed.y(), speed.z());
                } else {
                    ParticleHelper.spawnParticles(level, getType().get(), pos.x(), pos.y(), pos.z(), 1, 0.1, 0.1, speed.length());
                }
            }
        }
    }
}