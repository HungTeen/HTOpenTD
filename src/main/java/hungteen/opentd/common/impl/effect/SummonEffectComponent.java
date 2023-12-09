package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.htlib.util.helper.WorldHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-18 15:24
 **/
public record SummonEffectComponent(int count, int radius, Optional<Integer> maxHeightOffset, int tries, boolean self, boolean enableDefaultSpawn, EntityType<?> entityType, CompoundTag nbt) implements IEffectComponent {

    public static final Codec<SummonEffectComponent> CODEC = RecordCodecBuilder.<SummonEffectComponent>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(SummonEffectComponent::count),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("radius", 1).forGetter(SummonEffectComponent::radius),
            Codec.optionalField("max_height_offset", Codec.intRange(0, Integer.MAX_VALUE)).forGetter(SummonEffectComponent::maxHeightOffset),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("tries", 16).forGetter(SummonEffectComponent::tries),
            Codec.BOOL.optionalFieldOf("self", true).forGetter(SummonEffectComponent::self),
            Codec.BOOL.optionalFieldOf("enable_default_spawn", true).forGetter(SummonEffectComponent::enableDefaultSpawn),
            ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(SummonEffectComponent::entityType),
            CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(SummonEffectComponent::nbt)
            ).apply(instance, SummonEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        for(int i = 0; i < count(); ++ i){
            spawn(serverLevel, self() ? owner : entity, nbt());
        }
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        if(self()){
            for(int i = 0; i < count(); ++ i){
                spawn(serverLevel, owner, nbt());
            }
        }
    }

    public void spawn(Level level, Entity summoner, CompoundTag compoundTag){
        if (level instanceof ServerLevel serverlevel && Level.isInSpawnableBounds(summoner.blockPosition())) {
            compoundTag.putString("id", EntityHelper.get().getKey(entityType).toString());

            // position.
            Vec3 position = summoner.position(); // 保底。
            if(maxHeightOffset().isPresent()){
                int dx = RandomHelper.range(serverlevel.getRandom(), radius());
                int dz = RandomHelper.range(serverlevel.getRandom(), radius());
                Optional<Integer> opt = Optional.empty();
                for(int i = 0; i < tries(); ++ i){
                    opt = getGroundPos(level, summoner, summoner.getX() + dx, summoner.getY() + RandomHelper.range(serverlevel.getRandom(), maxHeightOffset().get()), summoner.getZ() + dz);
                    if(opt.isPresent()){
                        break;
                    }
                    dx = RandomHelper.range(serverlevel.getRandom(), radius());
                    dz = RandomHelper.range(serverlevel.getRandom(), radius());
                }
                if(opt.isPresent()){
                    position = new Vec3(summoner.getX() + dx, opt.get(), summoner.getZ());
                }
            } else {
                int dx = RandomHelper.range(serverlevel.getRandom(), radius());
                int dz = RandomHelper.range(serverlevel.getRandom(), radius());
                int y = WorldHelper.getSurfaceHeight(level, summoner.getX() + dx, summoner.getZ() + dz);
                position = new Vec3(summoner.getX() + dx, y, summoner.getZ());
            }

            Vec3 finalPosition = position;
            Entity entity = EntityType.loadEntityRecursive(compoundTag, serverlevel, (e) -> {
                e.moveTo(finalPosition.x(), finalPosition.y(), finalPosition.z(), summoner.getYRot(), summoner.getXRot());
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

    private Optional<Integer> getGroundPos(Level level, Entity summoner, double x, double y, double z){
        BlockPos blockpos = MathHelper.toBlockPos(new Vec3(x, y, z));
        if (level.hasChunkAt(blockpos)) {
            while(blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos posBelow = blockpos.below();
                BlockState blockstate = level.getBlockState(posBelow);
                if (blockstate.blocksMotion()) {
                    if (level.noCollision(summoner) && !level.containsAnyLiquid(summoner.getBoundingBox())) {
                        return Optional.of(blockpos.getY());
                    }
                    break;
                } else {
                    blockpos = posBelow;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.SUMMON_EFFECT;
    }
}
