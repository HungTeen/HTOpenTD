package hungteen.opentd.common.item;

import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.impl.HTItemSettings;
import hungteen.opentd.impl.HTSummonItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:23
 **/
public class SummonTowerItem extends Item {

    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private static final String SUMMON_TAG = "SummonEntry";

    public SummonTowerItem() {
        super(new Properties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemstack);
        } else if(hitResult.getType() == HitResult.Type.BLOCK){
            Vec3 vec3 = player.getViewVector(1.0F);
            final double scale = 5.0D;
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(scale)).inflate(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 vec31 = player.getEyePosition();

                for(Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            BlockPos blockpos = hitResult.getBlockPos();
            BlockState state = level.getBlockState(blockpos);
            if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, hitResult.getDirection(), itemstack)) {
                if (Level.isInSpawnableBounds(blockpos) && canPlace(level, player, itemstack, state, blockpos)) {
                    Entity entity = getTowerSettings(itemstack).createEntity((ServerLevel) level, player, itemstack, blockpos);
                    if (entity == null) {
                        return InteractionResultHolder.pass(itemstack);
                    } else {
                        if (!player.getAbilities().instabuild) {
                            itemstack.hurtAndBreak(1, player, (p) -> {
                                player.broadcastBreakEvent(hand);
                            });
                        }

                        player.awardStat(Stats.ITEM_USED.get(this));
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
                        return InteractionResultHolder.consume(itemstack);
                    }
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    public static void use(PlayerInteractEvent.EntityInteractSpecific event){
        if (event.getLevel() instanceof ServerLevel
                && event.getItemStack().getItem() instanceof SummonTowerItem
                && Level.isInSpawnableBounds(event.getTarget().blockPosition())
                && ((SummonTowerItem) event.getItemStack().getItem()).canPlace(event.getLevel(), event.getEntity(), event.getItemStack(), event.getTarget())) {
            Entity entity = getTowerSettings(event.getItemStack()).createEntity((ServerLevel) event.getLevel(), event.getEntity(), event.getItemStack(), event.getTarget().blockPosition());
            if (entity != null) {
                if (!event.getEntity().getAbilities().instabuild) {
                    event.getItemStack().hurtAndBreak(1, event.getEntity(), (p) -> {
                        event.getEntity().broadcastBreakEvent(event.getHand());
                    });
                }

                event.getEntity().awardStat(Stats.ITEM_USED.get(event.getItemStack().getItem()));
                event.getLevel().gameEvent(event.getEntity(), GameEvent.ENTITY_PLACE, entity.position());
                event.setCancellationResult(InteractionResult.SUCCESS);
            } else{
                event.setCancellationResult(InteractionResult.PASS);
            }
        }
    }

    public boolean canPlace(Level level, Player player, ItemStack stack, Entity entity){
        return getItemSettings(stack).requirements().stream()
                .allMatch(r -> r.allowOn(level, player, entity));
    }

    public boolean canPlace(Level level, Player player, ItemStack stack, BlockState state, BlockPos pos){
        return getItemSettings(stack).requirements().stream()
                .allMatch(r -> r.allowOn(level, player, state, pos));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> itemStacks) {
        if (this.allowedIn(tab)) {
            HTSummonItems.SUMMON_ITEMS.getValues().forEach(entry -> {
                ItemStack stack = new ItemStack(OpenTDItems.SUMMON_TOWER_ITEM.get());
                set(stack, entry);
                itemStacks.add(stack);
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        getItemSettings(stack).textComponents().forEach(s -> {
            components.add(Component.literal(s));
        });
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getItemSettings(stack).maxStackSize();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getItemSettings(stack).maxDamage();
    }

    public static HTItemSettings.ItemSettings getItemSettings(ItemStack stack) {
        return get(stack).map(HTSummonItems.SummonEntry::itemSettings).orElse(HTItemSettings.DEFAULT.getValue());
    }

    public static ITowerComponent getTowerSettings(ItemStack stack) {
        //TODO default.
        return get(stack).map(HTSummonItems.SummonEntry::towerSettings).orElse(null);
    }

    public static Optional<HTSummonItems.SummonEntry> get(ItemStack stack) {
        return HTSummonItems.SummonEntry.CODEC
                .parse(NbtOps.INSTANCE, stack.getOrCreateTag().get(SUMMON_TAG))
                .result();
    }

    public static void set(ItemStack stack, HTSummonItems.SummonEntry entry) {
        HTSummonItems.SummonEntry.CODEC
                .encodeStart(NbtOps.INSTANCE, entry)
                .result().ifPresent(tag -> {
                    stack.getOrCreateTag().put(SUMMON_TAG, tag);
                });
    }

}
