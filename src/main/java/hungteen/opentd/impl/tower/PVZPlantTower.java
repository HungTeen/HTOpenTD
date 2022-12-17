package hungteen.opentd.impl.tower;

import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:40
 **/
public class PVZPlantTower implements ITowerComponent {

    @Override
    public Entity createEntity(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        PlantEntity plantEntity = OpenTDEntities.PLANT_ENTITY.get().spawn(level, stack, player, pos, MobSpawnType.SPAWN_EGG, false, false)
        return plantEntity;
    }

    @Override
    public ITowerComponentType<?> getType() {
        return HTTowerComponents.PVZ_PLANT_TOWER;
    }
}
