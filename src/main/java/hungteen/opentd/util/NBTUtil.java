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
}
