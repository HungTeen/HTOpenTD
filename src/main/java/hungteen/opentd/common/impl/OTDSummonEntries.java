package hungteen.opentd.common.impl;

import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.codec.ItemSetting;
import hungteen.opentd.common.codec.SummonEntry;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.common.impl.requirement.OTDSummonRequirements;
import hungteen.opentd.common.impl.tower.OTDTowerComponents;
import hungteen.opentd.util.Util;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 11:19
 **/
public interface OTDSummonEntries {

    HTCodecRegistry<SummonEntry> SUMMON_ITEMS = HTRegistryManager.create(Util.prefix("summon_items"), () -> SummonEntry.CODEC, () -> SummonEntry.NETWORK_CODEC);

    ResourceKey<SummonEntry> PEA_SHOOTER_CARD = create("pea_shooter_card");
    ResourceKey<SummonEntry> PEA_PULT_CARD = create("pea_pult_card");
    ResourceKey<SummonEntry> DIAMOND_SHOOTER_CARD = create("diamond_shooter_card");
    ResourceKey<SummonEntry> SUN_FLOWER_CARD = create("sun_flower_card");
    ResourceKey<SummonEntry> NUT_FLOWER_CARD = create("nut_flower_card");
    ResourceKey<SummonEntry> LASER_FLOWER_CARD = create("laser_flower_card");

    static void register(BootstapContext<SummonEntry> context) {
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        final HolderGetter<ITowerComponent> towerComponents = OTDTowerComponents.registry().helper().lookup(context);
        final HolderGetter<ISummonRequirement> requirements = OTDSummonRequirements.registry().helper().lookup(context);
        context.register(PEA_SHOOTER_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("pea_shooter_card"))
                        .name("item.opentd.pea_shooter_card")
                        .texts(List.of(Util.get().langKey("tip", "pea_shooter_card")))
                        .build(),
                Optional.of(requirements.getOrThrow(OTDSummonRequirements.AROUND_SUN_FLOWER)),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.PEA_SHOOTER))
        ));
        context.register(PEA_PULT_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("cabbage_pult_card"))
                        .name("item.opentd.pea_pult_card")
                        .texts(List.of(Util.get().langKey("tip", "pea_pult_card")))
                        .build(),
                Optional.of(requirements.getOrThrow(OTDSummonRequirements.ON_CREEPER)),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.PEA_PULT))
        ));
        context.register(DIAMOND_SHOOTER_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("pea_shooter_enjoy_card"))
                        .name("item.opentd.diamond_shooter_card")
                        .texts(List.of(Util.get().langKey("tip", "diamond_shooter_card")))
                        .build(),
                Optional.of(requirements.getOrThrow(OTDSummonRequirements.NEED_DIAMOND)),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.DIAMOND_SHOOTER))
        ));
        context.register(SUN_FLOWER_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("sun_flower_card"))
                        .name("item.opentd.sun_flower_card")
                        .texts(List.of(Util.get().langKey("tip", "sun_flower_card")))
                        .damage(10)
                        .build(),
                Optional.of(requirements.getOrThrow(OTDSummonRequirements.NOTHING)),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.SUN_FLOWER))
        ));
        context.register(NUT_FLOWER_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("wall_nut_card"))
                        .name("item.opentd.nut_flower_card")
                        .texts(List.of(Util.get().langKey("tip", "nut_flower_card")))
                        .build(),
                Optional.of(requirements.getOrThrow(OTDSummonRequirements.ON_GRASS)),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.NUT_FLOWER))
        ));
        context.register(LASER_FLOWER_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("sun_flower_enjoy_card"))
                        .name("item.opentd.laser_flower_card")
                        .texts(List.of(Util.get().langKey("tip", "laser_flower_card")))
                        .build(),
                Optional.of(requirements.getOrThrow(OTDSummonRequirements.ON_GRASS)),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.LASER_FLOWER))
        ));
    }

    static ResourceKey<SummonEntry> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static IHTCodecRegistry<SummonEntry> registry() {
        return SUMMON_ITEMS;
    }

}
