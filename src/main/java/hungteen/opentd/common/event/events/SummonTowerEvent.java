package hungteen.opentd.common.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-01 22:53
 **/
@Cancelable
public class SummonTowerEvent extends PlayerEvent {

    private final ItemStack itemStack;
    private final InteractionHand hand;
    private BlockPos targetPos;
    private Entity targetEntity;

    public SummonTowerEvent(Player player, ItemStack itemStack, InteractionHand hand, BlockPos targetPos) {
        super(player);
        this.itemStack = itemStack;
        this.hand = hand;
        this.targetPos = targetPos;
    }

    public SummonTowerEvent(Player player, ItemStack itemStack, InteractionHand hand, Entity targetEntity) {
        super(player);
        this.itemStack = itemStack;
        this.hand = hand;
        this.targetEntity = targetEntity;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Nullable
    public BlockPos getTargetPos() {
        return targetPos;
    }

    @Nullable
    public Entity getTargetEntity() {
        return targetEntity;
    }
}
