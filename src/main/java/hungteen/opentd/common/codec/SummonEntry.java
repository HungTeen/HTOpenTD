package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.impl.requirement.OTDSummonRequirements;
import hungteen.opentd.common.impl.tower.OTDTowerComponents;
import net.minecraft.core.Holder;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/7 18:05
 **/
public record SummonEntry(ItemSetting itemSetting, Optional<Holder<ISummonRequirement>> requirement, Optional<Holder<ITowerComponent>> towerSetting) {

    public static final Codec<SummonEntry> CODEC = RecordCodecBuilder.<SummonEntry>mapCodec(instance -> instance.group(
            ItemSetting.CODEC.fieldOf("item_setting").forGetter(SummonEntry::itemSetting),
            Codec.optionalField("requirement", OTDSummonRequirements.getCodec()).forGetter(SummonEntry::requirement),
            Codec.optionalField("tower_setting", OTDTowerComponents.getCodec()).forGetter(SummonEntry::towerSetting)
    ).apply(instance, SummonEntry::new)).codec();

    public static final Codec<SummonEntry> NETWORK_CODEC = RecordCodecBuilder.<SummonEntry>mapCodec(instance -> instance.group(
            ItemSetting.NETWORK_CODEC.fieldOf("item_setting").forGetter(SummonEntry::itemSetting)
    ).apply(instance, (itemSetting -> new SummonEntry(itemSetting, Optional.empty(), Optional.empty())))).codec();

}
