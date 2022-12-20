package hungteen.opentd.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.impl.tower.HTTowerComponents;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 11:19
 **/
public class HTSummonItems {

    public static final HTCodecRegistry<SummonEntry> SUMMON_ITEMS = HTRegistryManager.create(SummonEntry.class, "tower_defence/summon_items", () -> SummonEntry.CODEC);

    public static final HTRegistryHolder<SummonEntry> PEA_SHOOTER_CARD = SUMMON_ITEMS.innerRegister(
            OpenTD.prefix("pea_shooter_card"),
            new SummonEntry(HTItemSettings.DEFAULT.getValue(), HTTowerComponents.PEA_SHOOTER.getValue())
    );

    public record SummonEntry(HTItemSettings.ItemSettings itemSettings, ITowerComponent towerSettings){

        public static final Codec<SummonEntry> CODEC = RecordCodecBuilder.<SummonEntry>mapCodec(instance -> instance.group(
                HTItemSettings.ItemSettings.CODEC.fieldOf("item_settings").forGetter(SummonEntry::itemSettings),
                HTTowerComponents.getCodec().fieldOf("tower_settings").forGetter(SummonEntry::towerSettings)
        ).apply(instance, SummonEntry::new)).codec();
    }

}
