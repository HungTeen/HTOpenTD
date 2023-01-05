package hungteen.opentd.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.impl.requirement.HTSummonRequirements;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 11:38
 **/
public class HTItemSettings {

    public static final HTCodecRegistry<ItemSettings> SUMMON_ITEMS = HTRegistryManager.create(ItemSettings.class, "tower_defence/item_settings", () -> ItemSettings.CODEC, true);

    public static final HTRegistryHolder<ItemSettings> DEFAULT = SUMMON_ITEMS.innerRegister(
            OpenTD.prefix("default"),
            new Builder().build()
    );

    public static final HTRegistryHolder<ItemSettings> PEA_SHOOTER = SUMMON_ITEMS.innerRegister(
            OpenTD.prefix("pea_shooter"),
            new Builder().model(OpenTD.prefix("pea_shooter_card")).card().build()
    );

    public static final HTRegistryHolder<ItemSettings> SUN_FLOWER = SUMMON_ITEMS.innerRegister(
            OpenTD.prefix("sun_flower"),
            new Builder().model(OpenTD.prefix("sun_flower_card")).card().build()
    );

    public static class Builder {
        private ResourceLocation model = OpenTD.prefix("pea_shooter_card");
        private int maxStackSize = 1;
        private int maxDamage = 0;
        private int coolDown = 5;
        private List<String> textComponents = new ArrayList<>();
        private List<ISummonRequirement> requirements = new ArrayList<>();

        public Builder card(){
            return this.stack(64).damage(0).cd(20);
        }

        public Builder model(ResourceLocation model) {
            this.model = model;
            return this;
        }

        public Builder stack(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public Builder damage(int damage) {
            this.maxDamage = damage;
            return this;
        }

        public Builder cd(int coolDown){
            this.coolDown = coolDown;
            return this;
        }

        public Builder texts(List<String> textComponents){
            this.textComponents = textComponents;
            return this;
        }

        public Builder requirements(List<ISummonRequirement> requirements){
            this.requirements = requirements;
            return this;
        }

        public ItemSettings build(){
            return new ItemSettings(model, maxStackSize, maxDamage, coolDown, textComponents, requirements);
        }
    }

    public record ItemSettings(ResourceLocation model, int maxStackSize, int maxDamage, int coolDown, List<String> textComponents, List<ISummonRequirement> requirements) {
        public static final Codec<ItemSettings> CODEC = RecordCodecBuilder.<ItemSettings>mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(ItemSettings::model),
                Codec.intRange(0, 1023).optionalFieldOf("max_stack_size", 1).forGetter(ItemSettings::maxStackSize),
                Codec.intRange(0, 65535).optionalFieldOf("max_damage", 0).forGetter(ItemSettings::maxDamage),
                Codec.intRange(5, 1000000).optionalFieldOf("cool_down", 5).forGetter(ItemSettings::coolDown),
                Codec.STRING.listOf().optionalFieldOf("texts", new ArrayList<>()).forGetter(ItemSettings::textComponents),
                HTSummonRequirements.getCodec().listOf().optionalFieldOf("requirements", new ArrayList<>()).forGetter(ItemSettings::requirements)
        ).apply(instance, ItemSettings::new)).codec();
    }

}
