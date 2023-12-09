package hungteen.opentd.compat.jei;

import hungteen.opentd.common.item.SummonTowerItem;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class SummonCardInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {

    public static final SummonCardInterpreter INSTANCE = new SummonCardInterpreter();

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        if (!itemStack.hasTag() || !(itemStack.getItem() instanceof SummonTowerItem)) {
            return IIngredientSubtypeInterpreter.NONE;
        }
        return SummonTowerItem.getSummonEntry(itemStack).location().toString();
    }
}
