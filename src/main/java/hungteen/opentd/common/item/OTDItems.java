package hungteen.opentd.common.item;

import hungteen.opentd.OpenTD;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 12:08
 **/
public class OTDItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OpenTD.MOD_ID);

    public static final RegistryObject<Item> SUMMON_TOWER_ITEM = ITEMS.register("summon_tower_item", SummonTowerItem::new);

//    public static final RegistryObject<Item> RECORD = ITEMS.register("record", () -> new RecordItem(0, OpenTDSounds.ZOMBOSS.get(), new Item.Properties(), 178));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
