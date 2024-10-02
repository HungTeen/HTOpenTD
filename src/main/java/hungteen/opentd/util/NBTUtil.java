package hungteen.opentd.util;

import com.mojang.datafixers.util.Pair;
import hungteen.htlib.util.helper.registry.EntityHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.List;
import java.util.Objects;

public class NBTUtil {

    public static CompoundTag sunflowerPredicate() {
        CompoundTag tag = new CompoundTag();
        {
            CompoundTag tmp = new CompoundTag();
            {
                CompoundTag tmp1 = new CompoundTag();
                tmp1.putString("id", Util.prefix("sun_flower").toString());
                tmp.put("plant_setting", tmp1);
            }
            tag.put("ComponentTag", tmp);
        }
        return tag;
    }

    public static CompoundTag fiveHealth() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Health", 5F);
        return tag;
    }

    public static CompoundTag onFire() {
        CompoundTag tag = new CompoundTag();
        tag.putShort("Fire", (short) 100);
        return tag;
    }

    public static CompoundTag attributeTags(List<Pair<Attribute, Double>> attributes) {
        CompoundTag tag = new CompoundTag();
        {
            ListTag listtag = new ListTag();
            for (Pair<Attribute, Double> pair : attributes) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putString("Name", Objects.requireNonNull(EntityHelper.attribute().getKey(pair.getFirst()).toString()));
                compoundtag.putDouble("Base", pair.getSecond());
                listtag.add(compoundtag);
            }
            tag.put("Attributes", listtag);
        }
        return tag;
    }
}
