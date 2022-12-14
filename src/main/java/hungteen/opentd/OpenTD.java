package hungteen.opentd;

import com.mojang.logging.LogUtils;
import hungteen.htlib.HTLib;
import hungteen.htlib.data.HTTestGen;
import hungteen.opentd.common.capability.OpenTDCapabilities;
import hungteen.opentd.common.effect.OpenTDEffects;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.item.OpenTDItems;
import hungteen.opentd.common.network.NetworkHandler;
import hungteen.opentd.data.OpenTDTestGen;
import hungteen.opentd.impl.HTItemSettings;
import hungteen.opentd.impl.HTSummonItems;
import hungteen.opentd.impl.effect.HTEffectComponents;
import hungteen.opentd.impl.finder.HTTargetFinders;
import hungteen.opentd.impl.requirement.HTSummonRequirements;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.impl.tower.HTTowerComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
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
        modBus.addListener(EventPriority.NORMAL, OpenTDCapabilities::registerCapabilities);
        modBus.addListener(EventPriority.NORMAL, false, GatherDataEvent.class, (event) -> {
            event.getGenerator().addProvider(event.includeServer(), new OpenTDTestGen(event.getGenerator()));
        });
        OpenTDItems.register(modBus);
        OpenTDEntities.register(modBus);
        OpenTDEffects.register(modBus);

        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addGenericListener(Entity.class, OpenTDCapabilities::attachCapabilities);
    }

    public static void setUp(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
            HTTowerComponents.registerStuffs();
            HTSummonRequirements.registerStuffs();
            HTEffectComponents.registerStuffs();
            HTTargetFilters.registerStuffs();
            HTTargetFinders.registerStuffs();
            HTItemSettings.registerStuffs();
            HTSummonItems.registerStuffs();
        });
        NetworkHandler.init();
    }

    public static ResourceLocation prefix(String name) {
        return HTLib.res(MOD_ID, name);
    }

    public static Logger log(){
        return LOGGER;
    }
}
