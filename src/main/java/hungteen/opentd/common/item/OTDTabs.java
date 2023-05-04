package hungteen.opentd.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:05
 */
public class OTDTabs {

    public static final CreativeModeTab CARDS = new CreativeModeTab("otd_cards") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(OpenTDItems.SUMMON_TOWER_ITEM.get());
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }
    };

}
