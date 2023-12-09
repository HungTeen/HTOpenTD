package hungteen.opentd.util;

import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.opentd.common.capability.OpenTDCapabilities;
import hungteen.opentd.common.capability.player.PlayerCapability;
import hungteen.opentd.common.capability.player.PlayerDataManager;
import hungteen.opentd.common.item.SummonTowerItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-08 14:02
 **/
public class PlayerUtil {

    public static Optional<PlayerDataManager> getOptManager(Player player) {
        return Optional.ofNullable(getManager(player));
    }

    @Nullable
    public static PlayerDataManager getManager(Player player) {
        if(PlayerHelper.isValidPlayer(player)) {
            final Optional<PlayerCapability> optional = OpenTDCapabilities.getPlayerCapability(player).resolve();
            return optional.map(PlayerCapability::get).orElse(null);
        }
        return null;
    }

    public static <T> T getManagerResult(Player player, Function<PlayerDataManager, T> function, T defaultValue) {
        final PlayerDataManager manager = getManager(player);
        return manager != null ? function.apply(manager) : defaultValue;
    }

    public static boolean isOnCooldown(Player player, ItemStack stack){
        if(stack.getItem() instanceof SummonTowerItem) {
            ResourceLocation location = SummonTowerItem.getSummonEntry(stack).location();
            return getManagerResult(player, l -> l.isOnCooldown(location), true);
        } else{
            return player.getCooldowns().isOnCooldown(stack.getItem());
        }
    }

    public static double getCDPercent(Player player, ItemStack stack){
        ResourceLocation location = SummonTowerItem.getSummonEntry(stack).location();
        return getManagerResult(player, l -> l.getCDPercent(location), 0D);
    }

    public static void addCooldown(Player player, ItemStack stack, int cd){
        if(stack.getItem() instanceof SummonTowerItem) {
            ResourceLocation location = SummonTowerItem.getSummonEntry(stack).location();
            getOptManager(player).ifPresent(l -> l.saveCurrentCD(location, cd));
        } else{
            player.getCooldowns().addCooldown(stack.getItem(), cd);
        }
    }

    public static void removeItem(Player player, ItemStack stack, int count){
        for(int i = 0; i < player.getInventory().getContainerSize(); ++ i) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(stack, itemStack)) {
                if(itemStack.getCount() <= count){
                    count -= itemStack.getCount();
                    player.getInventory().removeItem(i, itemStack.getCount());
                } else{
                    player.getInventory().removeItem(i, count);
                    count = 0;
                }
            }
            if(count <= 0){
                break;
            }
        }
    }

    public static int getItemCount(Player player, ItemStack stack){
        int count = 0;
        for(int i = 0; i < player.getInventory().getContainerSize(); ++ i) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(stack, itemStack)) {
                count += itemStack.getCount();
            }
        }
        return count;
    }

}
