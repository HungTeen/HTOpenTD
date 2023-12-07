package hungteen.opentd.common.item;

import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.world.item.CreativeModeTab;
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
            return new ItemStack(OTDItems.SUMMON_TOWER_ITEM.get());
        }

        @Override
        public boolean hasSearchBar() {
            return true;
        }
    }.setBackgroundImage(StringHelper.containerTexture("minecraft", "creative_inventory/tab_item_search"));

}
