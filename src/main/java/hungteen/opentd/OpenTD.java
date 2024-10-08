package hungteen.opentd;

import hungteen.opentd.common.OpenTDSounds;
import hungteen.opentd.common.capability.OpenTDCapabilities;
import hungteen.opentd.common.effect.OpenTDEffects;
import hungteen.opentd.common.entity.OTDSerializers;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.impl.OTDPathNavigations;
import hungteen.opentd.common.impl.OTDBulletSettings;
import hungteen.opentd.common.impl.OTDSummonEntries;
import hungteen.opentd.common.impl.effect.OTDEffectComponentTypes;
import hungteen.opentd.common.impl.effect.OTDEffectComponents;
import hungteen.opentd.common.impl.filter.ClassFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilterTypes;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.common.impl.finder.OTDTargetFinderTypes;
import hungteen.opentd.common.impl.finder.OTDTargetFinders;
import hungteen.opentd.common.impl.move.OTDMoveComponents;
import hungteen.opentd.common.impl.move.OTDMoveTypes;
import hungteen.opentd.common.impl.requirement.OTDRequirementTypes;
import hungteen.opentd.common.impl.requirement.OTDSummonRequirements;
import hungteen.opentd.common.impl.tower.OTDTowerComponents;
import hungteen.opentd.common.impl.tower.OTDTowerTypes;
import hungteen.opentd.common.item.OTDCreativeTabs;
import hungteen.opentd.common.item.OTDItems;
import hungteen.opentd.common.network.NetworkHandler;
import hungteen.opentd.data.OTDDatapackEntriesGen;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
        register(modBus);
        modBus.addListener(EventPriority.NORMAL, OpenTD::setUp);
        modBus.addListener(EventPriority.NORMAL, OpenTDEntities::addEntityAttributes);
        modBus.addListener(EventPriority.NORMAL, OpenTDCapabilities::registerCapabilities);
        modBus.addListener(EventPriority.NORMAL, false, GatherDataEvent.class, (event) -> {
            event.getGenerator().addProvider(event.includeServer(), new OTDDatapackEntriesGen(event.getGenerator().getPackOutput(), event.getLookupProvider()));
        });

        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addGenericListener(Entity.class, OpenTDCapabilities::attachCapabilities);

        OTDConfigs.init();
    }

    public void register(IEventBus modBus){
        OTDItems.register(modBus);
        OpenTDEntities.register(modBus);
        OpenTDEffects.register(modBus);
        OpenTDSounds.register(modBus);
        OTDSerializers.register(modBus);
        OTDCreativeTabs.register(modBus);

        OTDSummonEntries.registry().register(modBus);
        OTDRequirementTypes.registry().register(modBus);
        OTDSummonRequirements.registry().register(modBus);
        OTDTowerTypes.registry().register(modBus);
        OTDTowerComponents.registry().register(modBus);
        OTDTargetFilterTypes.registry().register(modBus);
        OTDTargetFilters.registry().register(modBus);
        OTDTargetFinderTypes.registry().register(modBus);
        OTDTargetFinders.registry().register(modBus);
        OTDMoveTypes.registry().register(modBus);
        OTDMoveComponents.registry().register(modBus);
        OTDBulletSettings.registry().register(modBus);
        OTDEffectComponentTypes.registry().register(modBus);
        OTDEffectComponents.registry().register(modBus);
        ClassFilter.registry().register(modBus);
        OTDPathNavigations.registry().register(modBus);
    }

    public static void setUp(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
        });
        NetworkHandler.init();
    }

}
