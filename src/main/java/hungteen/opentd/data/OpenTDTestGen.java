package hungteen.opentd.data;

import com.mojang.serialization.Encoder;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.data.HTCodecGen;
import hungteen.htlib.impl.raid.HTRaidComponents;
import hungteen.opentd.OpenTD;
import hungteen.opentd.impl.HTSummonItems;
import hungteen.opentd.impl.effect.HTEffectComponents;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.impl.finder.HTTargetFinders;
import hungteen.opentd.impl.requirement.HTSummonRequirements;
import hungteen.opentd.impl.tower.HTTowerComponents;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 20:50
 **/
public class OpenTDTestGen extends HTCodecGen {

    public OpenTDTestGen(DataGenerator generator) {
        super(generator, OpenTD.MOD_ID);
    }

    public void run(CachedOutput cache) {
        HTTowerComponents.registerStuffs();
        HTSummonRequirements.registerStuffs();
        HTEffectComponents.registerStuffs();
        HTTargetFilters.registerStuffs();
        HTTargetFinders.registerStuffs();
        this.register(cache, HTSummonItems.SUMMON_ITEMS, HTSummonItems.SummonEntry.CODEC);
    }

    protected <E, T extends HTRegistryHolder<E>> void register(CachedOutput cache, HTCodecRegistry<E> registry, Encoder<E> encoder) {
        registry.getEntries().forEach((entry) -> {
            this.register(this.createPath(this.path, registry.getRegistryName(), (ResourceLocation)entry.getKey()), cache, encoder, entry.getValue());
        });
    }

    public String getName() {
        return this.modId + " test gen";
    }
}
