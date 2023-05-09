package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.impl.spawn.SpawnComponent;
import hungteen.htlib.util.helper.EntityHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.htlib.util.helper.WorldHelper;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-18 15:24
 **/
public record SummonEffectComponent(int count, int radius, boolean self, boolean enableDefaultSpawn, EntityType<?> entityType, CompoundTag nbt) implements IEffectComponent {

    public static final Codec<SummonEffectComponent> CODEC = RecordCodecBuilder.<SummonEffectComponent>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(SummonEffectComponent::count),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("radius", 1).forGetter(SummonEffectComponent::radius),
            Codec.BOOL.optionalFieldOf("self", true).forGetter(SummonEffectComponent::self),
            Codec.BOOL.optionalFieldOf("enable_default_spawn", true).forGetter(SummonEffectComponent::enableDefaultSpawn),
            ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(SummonEffectComponent::entityType),
            CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(SummonEffectComponent::nbt)
            ).apply(instance, SummonEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        for(int i = 0; i < count(); ++ i){
            spawn(owner.level, self() ? owner : entity, nbt());
        }
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        if(self()){
            for(int i = 0; i < count(); ++ i){
                spawn(owner.level, owner, nbt());
            }
        }
    }

    public void spawn(Level level, Entity summoner, CompoundTag compoundTag){
        if (level instanceof ServerLevel serverlevel && Level.isInSpawnableBounds(summoner.blockPosition())) {
            compoundTag.putString("id", EntityHelper.getKey(entityType).toString());

            // position.
            final int dx = RandomHelper.range(summoner.getLevel().getRandom(), radius());
            final int dz = RandomHelper.range(summoner.getLevel().getRandom(), radius());
            final int y = WorldHelper.getSurfaceHeight(level, summoner.getX() + dx, summoner.getZ() + dz);
            final Vec3 position = new Vec3(summoner.getX() + dx, y, summoner.getZ() + dz);

            Entity entity = EntityType.loadEntityRecursive(compoundTag, serverlevel, (e) -> {
                e.moveTo(position.x(), position.y(), position.z(), summoner.getYRot(), summoner.getXRot());
                return e;
            });
            if (entity != null) {
                if (enableDefaultSpawn() && entity instanceof Mob) {
                    ((Mob) entity).finalizeSpawn(serverlevel, serverlevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                }

                serverlevel.tryAddFreshEntityWithPassengers(entity);
            }
        }
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.SUMMON_EFFECT;
    }
}
