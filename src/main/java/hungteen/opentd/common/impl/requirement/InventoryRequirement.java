package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.util.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 14:01
 **/
public record InventoryRequirement(Optional<String> tip, List<ItemStack> ownItems, List<ItemStack> consumeItems) implements ISummonRequirement {

    public static final Codec<InventoryRequirement> CODEC = RecordCodecBuilder.<InventoryRequirement>mapCodec(instance -> instance.group(
            Codec.optionalField("tip", Codec.STRING).forGetter(InventoryRequirement::tip),
            ItemStack.CODEC.listOf().fieldOf("own_items").forGetter(InventoryRequirement::ownItems),
            ItemStack.CODEC.listOf().fieldOf("consume_items").forGetter(InventoryRequirement::consumeItems)
    ).apply(instance, InventoryRequirement::new)).codec();

    @Override
    public boolean allowOn(ServerLevel level, Player player, Entity entity, boolean sendMessage) {
        return check(player, sendMessage);
    }

    @Override
    public boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos, boolean sendMessage) {
        return check(player, sendMessage);
    }

    private boolean check(Player player, boolean sendMessage){
        if( this.ownItems().stream().allMatch(stack -> {
            return stack.getCount() <= PlayerUtil.getItemCount(player, stack);
        }) && this.consumeItems().stream().allMatch(stack -> {
            return stack.getCount() <= PlayerUtil.getItemCount(player, stack);
        })){
            return true;
        } else{
            if(sendMessage) PlayerHelper.sendTipTo(player, getTip());
            return false;
        }
    }

    @Override
    public void consume(ServerLevel level, Player player) {
        this.consumeItems().forEach(stack -> {
            PlayerUtil.removeItem(player, stack, stack.getCount());
        });
    }

    public Component getTip() {
        return Component.translatable(this.tip().orElse("tip.opentd.require_inventory"));
    }

    @Override
    public ISummonRequirementType<?> getType() {
        return OTDRequirementTypes.INVENTORY_REQUIREMENT;
    }
}
