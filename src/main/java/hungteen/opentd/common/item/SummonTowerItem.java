package hungteen.opentd.common.item;

import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.event.events.PostSummonTowerEvent;
import hungteen.opentd.common.event.events.SummonTowerEvent;
import hungteen.opentd.common.impl.HTItemSettings;
import hungteen.opentd.common.impl.HTSummonItems;
import hungteen.opentd.common.impl.tower.HTTowerComponents;
import hungteen.opentd.util.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
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
    public static final String ENTITY_TAG = "EntityTag";

    public SummonTowerItem() {
        super(new Properties().tab(OTDTabs.CARDS));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemStack);
        } else if(PlayerUtil.isOnCooldown(player, itemStack)){
            return InteractionResultHolder.fail(itemStack);
        } else if(hitResult.getType() == HitResult.Type.BLOCK){
            Vec3 vec3 = player.getViewVector(1.0F);
            final double scale = 5.0D;
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(scale)).inflate(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 origin = player.getEyePosition();
                for(Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (aabb.contains(origin)) {
                        return InteractionResultHolder.pass(itemStack);
                    }
                }
            }

            BlockState state = level.getBlockState(hitResult.getBlockPos());
            BlockPos blockpos;
            if (state.getCollisionShape(level, hitResult.getBlockPos()).isEmpty()) {
                blockpos = hitResult.getBlockPos();
            } else {
                blockpos = hitResult.getBlockPos().relative(hitResult.getDirection());
            }

            if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, hitResult.getDirection(), itemStack)) {
                if (Level.isInSpawnableBounds(blockpos) && canPlace((ServerLevel) level, player, itemStack, state, blockpos)) {
                    // KubeJs Event Inject.
                    if(! MinecraftForge.EVENT_BUS.post(new SummonTowerEvent(player, itemStack, hand, blockpos))){
                        Entity entity = getTowerSettings(itemStack).createEntity((ServerLevel) level, player, itemStack, blockpos);
                        if (entity == null) {
                            return InteractionResultHolder.pass(itemStack);
                        } else {
                            MinecraftForge.EVENT_BUS.post(new PostSummonTowerEvent(player, itemStack, hand, blockpos, entity));
                            this.consume((ServerLevel)level, player, entity, itemStack, hand);
                            return InteractionResultHolder.consume(itemStack);
                        }
                    }
                }
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    public static void use(PlayerInteractEvent.EntityInteractSpecific event){
        if (event.getLevel() instanceof ServerLevel
                && event.getItemStack().getItem() instanceof SummonTowerItem
                && ! PlayerUtil.isOnCooldown(event.getEntity(), event.getItemStack())
                && Level.isInSpawnableBounds(event.getTarget().blockPosition())
                && ((SummonTowerItem) event.getItemStack().getItem()).canPlace((ServerLevel) event.getLevel(), event.getEntity(), event.getItemStack(), event.getTarget())) {
            if(! MinecraftForge.EVENT_BUS.post(new SummonTowerEvent(event.getEntity(), event.getItemStack(), event.getHand(), event.getTarget()))) {
                Entity entity = getTowerSettings(event.getItemStack()).createEntity((ServerLevel) event.getLevel(), event.getEntity(), event.getItemStack(), event.getTarget().blockPosition());
                if (entity != null) {
                    //TODO 骑乘
                    MinecraftForge.EVENT_BUS.post(new PostSummonTowerEvent(event.getEntity(), event.getItemStack(), event.getHand(), event.getTarget(), entity));
                    ((SummonTowerItem) event.getItemStack().getItem()).consume((ServerLevel) event.getLevel(), event.getEntity(), entity, event.getItemStack(), event.getHand());
                    event.setCancellationResult(InteractionResult.SUCCESS);
                } else {
                    event.setCancellationResult(InteractionResult.PASS);
                }
            }
        }
    }

    public void consume(ServerLevel level, Player player, Entity entity, ItemStack itemstack, InteractionHand hand){
        if (!player.getAbilities().instabuild) {
            itemstack.hurtAndBreak(1, player, (p) -> {
                player.broadcastBreakEvent(hand);
            });
            getItemSettings(itemstack).requirement().consume(level, player);
        }

        PlayerUtil.addCooldown(player, itemstack, getItemSettings(itemstack).coolDown());

        player.awardStat(Stats.ITEM_USED.get(this));
        level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
    }

    public boolean canPlace(ServerLevel level, Player player, ItemStack stack, Entity entity){
        return getItemSettings(stack).requirement().allowOn(level, player, entity, true);
    }

    public boolean canPlace(ServerLevel level, Player player, ItemStack stack, BlockState state, BlockPos pos){
        return getItemSettings(stack).requirement().allowOn(level, player, state, pos, true);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> itemStacks) {
        if (tab == CreativeModeTab.TAB_SEARCH || this.allowedIn(tab)) {
            HTSummonItems.SUMMON_ITEMS.getIds().forEach(entry -> {
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
    public String getDescriptionId(ItemStack itemStack) {
        return getItemSettings(itemStack).name().orElse(super.getDescriptionId(itemStack));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getItemSettings(stack).maxStackSize();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getItemSettings(stack).maxDamage();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return getMaxDamage(stack) > 0; // Fix 0 max damage item can break !
    }

    @Override
    public boolean canBeDepleted() {
        return true; // Fix Summon Item Unbreakable.
    }

    public static HTItemSettings.ItemSetting getItemSettings(ItemStack stack) {
        return get(stack).map(HTSummonItems.SummonEntry::itemSettings).orElse(HTItemSettings.DEFAULT.getValue());
    }

    public static ITowerComponent getTowerSettings(ItemStack stack) {
        return get(stack).map(HTSummonItems.SummonEntry::towerSettings).orElse(HTTowerComponents.PEA_SHOOTER.getValue());
    }

    public static Optional<HTSummonItems.SummonEntry> get(ItemStack stack) {
        return HTSummonItems.SUMMON_ITEMS.getValue(stack.getOrCreateTag().getString(SUMMON_TAG));
    }

    public static Optional<ResourceLocation> getId(ItemStack stack) {
        return Optional.ofNullable(stack.getOrCreateTag().contains(SUMMON_TAG) ? new ResourceLocation(stack.getOrCreateTag().getString(SUMMON_TAG)) : null);
    }

    public static void set(ItemStack stack, ResourceLocation entry) {
        stack.getOrCreateTag().putString(SUMMON_TAG, entry.toString());
    }

}
