package hungteen.opentd.compat.jei;

import hungteen.opentd.OpenTD;
import hungteen.opentd.common.item.OTDItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class OTDJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = OpenTD.prefix("jei");

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(OTDItems.SUMMON_TOWER_ITEM.get(), SummonCardInterpreter.INSTANCE);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

}
