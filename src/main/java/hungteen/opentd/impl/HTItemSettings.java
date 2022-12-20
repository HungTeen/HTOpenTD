package hungteen.opentd.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.impl.requirement.HTSummonRequirements;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 11:38
 **/
public class HTItemSettings {

    public static final HTCodecRegistry<ItemSettings> SUMMON_ITEMS = HTRegistryManager.create(ItemSettings.class, "tower_defence/item_settings", () -> ItemSettings.CODEC);

    public static final HTRegistryHolder<ItemSettings> DEFAULT = SUMMON_ITEMS.innerRegister(OpenTD.prefix("default"), new ItemSettings(64, 0, 20, new ArrayList<>(), new ArrayList<>()));

    public record ItemSettings(int maxStackSize, int maxDamage, int cooldown, List<String> textComponents, List<ISummonRequirement> requirements) {
        public static final Codec<ItemSettings> CODEC = RecordCodecBuilder.<ItemSettings>mapCodec(instance -> instance.group(
                Codec.intRange(0, 1023).optionalFieldOf("max_stack_size", 1).forGetter(ItemSettings::maxStackSize),
                Codec.intRange(0, 65535).optionalFieldOf("max_damage", 0).forGetter(ItemSettings::maxDamage),
                Codec.intRange(5, 1000000).optionalFieldOf("cool_down", 5).forGetter(ItemSettings::cooldown),
                Codec.STRING.listOf().optionalFieldOf("texts", new ArrayList<>()).forGetter(ItemSettings::textComponents),
                HTSummonRequirements.getCodec().listOf().optionalFieldOf("requirements", new ArrayList<>()).forGetter(ItemSettings::requirements)
        ).apply(instance, ItemSettings::new)).codec();
    }
}
