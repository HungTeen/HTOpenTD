package hungteen.opentd.data;

import hungteen.htlib.data.HTDatapackEntriesGen;
import hungteen.opentd.common.impl.OTDSummonEntries;
import hungteen.opentd.common.impl.effect.OTDEffectComponents;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.common.impl.finder.OTDTargetFinders;
import hungteen.opentd.common.impl.requirement.OTDSummonRequirements;
import hungteen.opentd.common.impl.tower.OTDTowerComponents;
import hungteen.opentd.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class OTDDatapackEntriesGen extends HTDatapackEntriesGen {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(OTDSummonEntries.registry().getRegistryKey(), OTDSummonEntries::register)
            .add(OTDSummonRequirements.registry().getRegistryKey(), OTDSummonRequirements::register)
            .add(OTDTowerComponents.registry().getRegistryKey(), OTDTowerComponents::register)
            .add(OTDTargetFilters.registry().getRegistryKey(), OTDTargetFilters::register)
            .add(OTDTargetFinders.registry().getRegistryKey(), OTDTargetFinders::register)
            .add(OTDEffectComponents.registry().getRegistryKey(), OTDEffectComponents::register)
            ;


    public OTDDatapackEntriesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Util.id(), Set.of(Util.mc().getModID(), Util.id()));
    }


}
