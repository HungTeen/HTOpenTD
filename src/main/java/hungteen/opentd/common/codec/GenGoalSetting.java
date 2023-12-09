package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:23
 **/
public record GenGoalSetting(int coolDown, int startTick, int totalWeight, int emptyCD, boolean needRest,
                             Optional<Holder<SoundEvent>> genSound, List<GenSetting> genSettings) {
    public static final Codec<GenGoalSetting> CODEC = RecordCodecBuilder.<GenGoalSetting>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cool_down", 20).forGetter(GenGoalSetting::coolDown),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 10).forGetter(GenGoalSetting::startTick),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("total_weight", 0).forGetter(GenGoalSetting::totalWeight),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("empty_cd", 100).forGetter(GenGoalSetting::emptyCD),
            Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(GenGoalSetting::needRest),
            Codec.optionalField("gen_sound", SoundEvent.CODEC).forGetter(GenGoalSetting::genSound),
            GenSetting.CODEC.listOf().fieldOf("productions").forGetter(GenGoalSetting::genSettings)
    ).apply(instance, GenGoalSetting::new)).codec();

    public record GenSetting(boolean plantFoodOnly, int weight, int cooldown, int count, EntityType<?> entityType,
                              CompoundTag nbt, Vec3 offset, double horizontalSpeed, double verticalSpeed) implements WeightedEntry {

        public static final Codec<GenSetting> CODEC = RecordCodecBuilder.<GenSetting>mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("plant_food_only", false).forGetter(GenSetting::plantFoodOnly),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("weight", 10).forGetter(GenSetting::weight),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cool_down", 500).forGetter(GenSetting::cooldown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(GenSetting::count),
                ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(GenSetting::entityType),
                CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(GenSetting::nbt),
                Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(GenSetting::offset),
                Codec.DOUBLE.optionalFieldOf("horizontal_speed", 0.25D).forGetter(GenSetting::horizontalSpeed),
                Codec.DOUBLE.optionalFieldOf("vertical_speed", 0.3D).forGetter(GenSetting::verticalSpeed)
        ).apply(instance, GenSetting::new)).codec();

        @Override
        public Weight getWeight() {
            return Weight.of(weight());
        }
    }
}