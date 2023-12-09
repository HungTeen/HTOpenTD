package hungteen.opentd.compat.jei;

import hungteen.opentd.common.item.OTDItems;
import hungteen.opentd.util.Util;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class OTDJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = Util.prefix("jei");

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(OTDItems.SUMMON_TOWER_ITEM.get(), SummonCardInterpreter.INSTANCE);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

}
