package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.util.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemSetting(Optional<String> name, ResourceLocation model, int maxStackSize, int maxDamage,
                          int coolDown, List<String> textComponents) {
    public static final Codec<ItemSetting> CODEC = RecordCodecBuilder.<ItemSetting>mapCodec(instance -> instance.group(
            Codec.optionalField("name", Codec.STRING).forGetter(ItemSetting::name),
            ResourceLocation.CODEC.fieldOf("model").forGetter(ItemSetting::model),
            Codec.intRange(0, 1023).optionalFieldOf("max_stack_size", 1).forGetter(ItemSetting::maxStackSize),
            Codec.intRange(0, 65535).optionalFieldOf("max_damage", 0).forGetter(ItemSetting::maxDamage),
            Codec.intRange(5, 1000000).optionalFieldOf("cool_down", 5).forGetter(ItemSetting::coolDown),
            Codec.STRING.listOf().optionalFieldOf("texts", new ArrayList<>()).forGetter(ItemSetting::textComponents)
    ).apply(instance, ItemSetting::new)).codec();

//    public static final Codec<ItemSetting> NETWORK_CODEC = RecordCodecBuilder.<ItemSetting>mapCodec(instance -> instance.group(
//            Codec.optionalField("name", Codec.STRING).forGetter(ItemSetting::name),
//            ResourceLocation.CODEC.fieldOf("model").forGetter(ItemSetting::model),
//            Codec.STRING.listOf().optionalFieldOf("texts", List.of()).forGetter(ItemSetting::textComponents)
//    ).apply(instance, (name, model, texts) -> {
//        return new ItemSetting(name, model, 0, 0, 0, texts);
//    })).codec();

    public static final ItemSetting DEFAULT = builder().build();

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String name = null;
        private ResourceLocation model = Util.prefix("pea_shooter_card");
        private int maxStackSize = 1;
        private int maxDamage = 0;
        private int coolDown = 20;
        private List<String> textComponents = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder card() {
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

        public Builder cd(int coolDown) {
            this.coolDown = coolDown;
            return this;
        }

        public Builder texts(List<String> textComponents) {
            this.textComponents = textComponents;
            return this;
        }

        public ItemSetting build() {
            return new ItemSetting(Optional.ofNullable(name), model, maxStackSize, maxDamage, coolDown, textComponents);
        }
    }
}
