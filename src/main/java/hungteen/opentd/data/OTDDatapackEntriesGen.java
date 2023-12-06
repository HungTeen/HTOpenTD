package hungteen.opentd.data;

import hungteen.htlib.data.HTDatapackEntriesGen;
import hungteen.opentd.common.impl.OTDSummonEntries;
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
            ;


    public OTDDatapackEntriesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Util.id(), Set.of(Util.mc().getModID(), Util.id()));
    }


}
