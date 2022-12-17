package hungteen.opentd.common.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 21:10
 **/
public class PlantEntity extends TowerEntity implements IEntityAdditionalSpawnData{

    public PlantEntity(EntityType<? extends TowerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return null;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {

    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {

    }

    @Override
    public EntityDimensions getDimensions(Pose p_21047_) {
        return ;
    }

    public record PlantSettings() {

    }

}
