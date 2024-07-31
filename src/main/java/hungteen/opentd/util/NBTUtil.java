package hungteen.opentd.util;

import net.minecraft.nbt.CompoundTag;

public class NBTUtil {

    public static CompoundTag sunflowerPredicate() {
        CompoundTag tag = new CompoundTag();
        {
            CompoundTag tmp = new CompoundTag();
            {
                CompoundTag tmp1 = new CompoundTag();
                tmp1.putString("id", Util.prefix("sun_flower_test").toString());
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
}
