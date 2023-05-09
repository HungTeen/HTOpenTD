package hungteen.opentd.common.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.common.impl.filter.EntityPredicateFilter;
import hungteen.opentd.common.impl.requirement.AroundEntityRequirement;
import hungteen.opentd.common.impl.requirement.HTSummonRequirements;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 11:38
 **/
public class HTItemSettings {

    public static final HTCodecRegistry<ItemSetting> SUMMON_ITEMS = HTRegistryManager.create(ItemSetting.class, "tower_defence/item_settings", () -> ItemSetting.CODEC, OpenTD.MOD_ID);

    public static final HTRegistryHolder<ItemSetting> DEFAULT = SUMMON_ITEMS.innerRegister(
            OpenTD.prefix("default"),
            new Builder().build()
    );

    public static final HTRegistryHolder<ItemSetting> PEA_SHOOTER = SUMMON_ITEMS.innerRegister(
            OpenTD.prefix("pea_shooter"),
            new Builder().model(OpenTD.prefix("pea_shooter_card")).name("item.opentd.test_pea_shooter_card").card().requirement(new AroundEntityRequirement(3, 3, 1, 10, Optional.empty(), new EntityPredicateFilter(EntityPredicate.ANY, EntityPredicate.Builder.entity().nbt(new NbtPredicate(get())).build()))).build()
    );

    public static final HTRegistryHolder<ItemSetting> SUN_FLOWER = SUMMON_ITEMS.innerRegister(
            OpenTD.prefix("sun_flower"),
            new Builder().model(OpenTD.prefix("sun_flower_card")).name("item.opentd.test_sun_flower_card").card().build()
    );

    private static CompoundTag get(){
        CompoundTag tag = new CompoundTag();
        {
            CompoundTag tmp = new CompoundTag();
            {
                CompoundTag tmp1 = new CompoundTag();
                tmp1.putString("id", OpenTD.prefix("sun_flower_test").toString());
                tmp.put("plant_setting", tmp1);
            }
            tag.put("ComponentTag", tmp);
        }
        return tag;
    }

    /**
     * {@link OpenTD#setUp(FMLCommonSetupEvent)} ()}
     */
    public static void registerStuffs(){
    }

    public static class Builder {
        private String name = null;
        private ResourceLocation model = OpenTD.prefix("pea_shooter_card");
        private int maxStackSize = 1;
        private int maxDamage = 0;
        private int coolDown = 5;
        private List<String> textComponents = new ArrayList<>();
        private List<ISummonRequirement> requirements = new ArrayList<>();

        public Builder name(String name){
            this.name = name;
            return this;
        }

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

        public Builder requirement(ISummonRequirement requirement){
            this.requirements.add(requirement);
            return this;
        }

        public Builder requirements(List<ISummonRequirement> requirements){
            this.requirements = requirements;
            return this;
        }

        public ItemSetting build(){
            return new ItemSetting(Optional.ofNullable(name), model, maxStackSize, maxDamage, coolDown, textComponents, requirements);
        }
    }

    public record ItemSetting(Optional<String> name, ResourceLocation model, int maxStackSize, int maxDamage, int coolDown, List<String> textComponents, List<ISummonRequirement> requirements) {
        public static final Codec<ItemSetting> CODEC = RecordCodecBuilder.<ItemSetting>mapCodec(instance -> instance.group(
                Codec.optionalField("name", Codec.STRING).forGetter(ItemSetting::name),
                ResourceLocation.CODEC.fieldOf("model").forGetter(ItemSetting::model),
                Codec.intRange(0, 1023).optionalFieldOf("max_stack_size", 1).forGetter(ItemSetting::maxStackSize),
                Codec.intRange(0, 65535).optionalFieldOf("max_damage", 0).forGetter(ItemSetting::maxDamage),
                Codec.intRange(5, 1000000).optionalFieldOf("cool_down", 5).forGetter(ItemSetting::coolDown),
                Codec.STRING.listOf().optionalFieldOf("texts", new ArrayList<>()).forGetter(ItemSetting::textComponents),
                HTSummonRequirements.getCodec().listOf().optionalFieldOf("requirements", new ArrayList<>()).forGetter(ItemSetting::requirements)
        ).apply(instance, ItemSetting::new)).codec();
    }

}
