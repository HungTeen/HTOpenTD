package hungteen.opentd.common.entity;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.entity.HTBoat;
import hungteen.htlib.common.entity.HTChestBoat;
import hungteen.opentd.OpenTD;
import hungteen.opentd.common.item.SummonTowerItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 09:41
 **/
public class OpenTDEntities {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OpenTD.MOD_ID);

    public static final RegistryObject<EntityType<PlantEntity>> PLANT_ENTITY = ENTITY_TYPES.register("plant_entity", () -> {
        return EntityType.Builder.of(PlantEntity::new, MobCategory.CREATURE).sized(1.375F, 0.5625F).build(OpenTD.prefix("plant_entity").toString());
    });

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
