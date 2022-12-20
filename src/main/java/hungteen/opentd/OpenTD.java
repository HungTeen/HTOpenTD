package hungteen.opentd;

import com.mojang.logging.LogUtils;
import hungteen.htlib.HTLib;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.item.OpenTDItems;
import hungteen.opentd.impl.requirement.HTSummonRequirements;
import hungteen.opentd.impl.target.HTTargetFilters;
import hungteen.opentd.impl.tower.HTTowerComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 12:41
 **/
@Mod(OpenTD.MOD_ID)
public class OpenTD {

    public static final String MOD_ID = "opentd";
    private static final Logger LOGGER = LogUtils.getLogger();

    public OpenTD() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.NORMAL, OpenTD::setUp);
        modBus.addListener(EventPriority.NORMAL, OpenTDEntities::addEntityAttributes);
        OpenTDItems.register(modBus);
        OpenTDEntities.register(modBus);
    }

    public static void setUp(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
            HTTowerComponents.registerStuffs();
            HTSummonRequirements.registerStuffs();
            HTTargetFilters.registerStuffs();
        });
    }

    public static ResourceLocation prefix(String name) {
        return HTLib.res(MOD_ID, name);
    }

    public static Logger log(){
        return LOGGER;
    }
}
