package hungteen.opentd;

import hungteen.htlib.HTLib;
import hungteen.opentd.common.item.OpenTDItems;
import hungteen.opentd.impl.requirement.HTSummonRequirements;
import hungteen.opentd.impl.tower.HTTowerComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 12:41
 **/
@Mod(OpenTD.MOD_ID)
public class OpenTD {

    public static final String MOD_ID = "opentd";

    public OpenTD() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        OpenTDItems.register(modBus);


        HTTowerComponents.registerStuffs();
        HTSummonRequirements.registerStuffs();
    }

    public static ResourceLocation prefix(String name) {
        return HTLib.res(MOD_ID, name);
    }
}
