package hungteen.opentd.common.item;

import hungteen.htlib.util.helper.CodecHelper;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.codec.ItemSetting;
import hungteen.opentd.common.codec.SummonEntry;
import hungteen.opentd.common.entity.TowerEntity;
import hungteen.opentd.common.event.events.PostSummonTowerEvent;
import hungteen.opentd.common.event.events.SummonTowerEvent;
import hungteen.opentd.common.impl.OTDSummonEntries;
import hungteen.opentd.common.impl.requirement.NoRequirement;
import hungteen.opentd.util.PlayerUtil;
import hungteen.opentd.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.NotNull;
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

    public static final String ENTITY_TAG = "EntityTag";
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private static final String ITEM_SETTING_TAG = "ItemSetting"; // 物品设置直接存为NBT。
    private static final String SUMMON_TAG = "SummonEntry"; // 召唤组件的引用。

    public SummonTowerItem() {
        super(new Properties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemStack);
        } else if (PlayerUtil.isOnCooldown(player, itemStack)) {
            return InteractionResultHolder.fail(itemStack);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            Vec3 vec3 = player.getViewVector(1.0F);
            final double scale = 5.0D;
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(scale)).inflate(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 origin = player.getEyePosition();
                for (Entity entity : list) {
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
                    if (!MinecraftForge.EVENT_BUS.post(new SummonTowerEvent(player, itemStack, hand, blockpos))) {
                        getTowerSetting(level, itemStack).map(towerComponent -> {
                            return towerComponent.createEntity((ServerLevel) level, player, itemStack, blockpos);
                        }).map(entity -> {
                            MinecraftForge.EVENT_BUS.post(new PostSummonTowerEvent(player, itemStack, hand, blockpos, entity));
                            this.consume((ServerLevel) level, player, entity, itemStack, hand);
                            return InteractionResultHolder.consume(itemStack);
                        }).orElse(InteractionResultHolder.pass(itemStack));
                    }
                }
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    public static void use(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getItemStack().getItem() instanceof SummonTowerItem) {
            if (PlayerUtil.isOnCooldown(event.getEntity(), event.getItemStack()) || !Level.isInSpawnableBounds(event.getTarget().blockPosition())) {
                event.setCancellationResult(InteractionResult.FAIL);
                return;
            }
            if (((SummonTowerItem) event.getItemStack().getItem()).canPlace(serverLevel, event.getEntity(), event.getItemStack(), event.getTarget()) & !MinecraftForge.EVENT_BUS.post(new SummonTowerEvent(event.getEntity(), event.getItemStack(), event.getHand(), event.getTarget()))) {
                getTowerSetting(serverLevel, event.getItemStack()).map(towerComponent -> {
                    return towerComponent.createEntity(serverLevel, event.getEntity(), event.getItemStack(), event.getTarget().blockPosition());
                }).ifPresentOrElse(entity -> {
                    //TODO 骑乘
                    MinecraftForge.EVENT_BUS.post(new PostSummonTowerEvent(event.getEntity(), event.getItemStack(), event.getHand(), event.getTarget(), entity));
                    ((SummonTowerItem) event.getItemStack().getItem()).consume(serverLevel, event.getEntity(), entity, event.getItemStack(), event.getHand());
                    event.setCancellationResult(InteractionResult.CONSUME);
                }, () -> {
                    event.setCancellationResult(InteractionResult.PASS);
                });
            } else {
                PlayerUtil.addCooldown(event.getEntity(), event.getItemStack(), 10);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    public void consume(ServerLevel level, Player player, Entity entity, ItemStack stack, InteractionHand hand) {
        if (!player.getAbilities().instabuild) {
            final int damage = getItemSetting(stack).useCost().map(l -> l.sample(player.getRandom())).orElse(1);
            stack.hurtAndBreak(damage, player, (p) -> {
                player.broadcastBreakEvent(hand);
            });
            getSummonRequirement(level, stack).consume(level, player);
        }

        if (entity instanceof TowerEntity tower) {
            tower.setOwnerUUID(player.getUUID());
        }

        PlayerUtil.addCooldown(player, stack, getItemSetting(stack).coolDown());

        player.awardStat(Stats.ITEM_USED.get(this));
        level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
    }

    public boolean canPlace(ServerLevel level, Player player, ItemStack stack, Entity entity) {
        return getSummonRequirement(level, stack).allowOn(level, player, entity, true);
    }

    public boolean canPlace(ServerLevel level, Player player, ItemStack stack, BlockState state, BlockPos pos) {
        return getSummonRequirement(level, stack).allowOn(level, player, state, pos, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        getItemSetting(stack).textComponents().forEach(s -> {
            components.add(Component.translatable(s));
        });
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return getItemSetting(itemStack).name().orElse(super.getDescriptionId(itemStack));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getItemSetting(stack).maxStackSize();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getItemSetting(stack).maxDamage();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return getMaxDamage(stack) > 0; // Fix 0 max damage item can break !
    }

    @Override
    public boolean canBeDepleted() {
        return true; // Fix Summon Item Unbreakable.
    }

    public static Optional<ITowerComponent> getTowerSetting(Level level, ItemStack stack) {
        return get(level, stack).flatMap(SummonEntry::towerSetting).map(Holder::get);
    }

    public static ISummonRequirement getSummonRequirement(Level level, ItemStack stack) {
        return get(level, stack).flatMap(SummonEntry::requirement).map(Holder::get).orElse(NoRequirement.INSTANCE);
    }

    public static Optional<SummonEntry> get(Level level, ItemStack stack) {
        return OTDSummonEntries.registry().getOptValue(level, getSummonEntry(stack));
    }

    public static void setSummonEntry(ItemStack stack, ResourceKey<SummonEntry> resourceKey) {
        stack.getOrCreateTag().putString(SUMMON_TAG, resourceKey.location().toString());
    }

    public static ResourceKey<SummonEntry> getSummonEntry(ItemStack stack) {
        return OTDSummonEntries.registry().createKey(new ResourceLocation(stack.getOrCreateTag().getString(SUMMON_TAG)));
    }

    public static void setItemSetting(ItemStack stack, ItemSetting itemSetting) {
        CodecHelper.encodeNbt(ItemSetting.CODEC, itemSetting)
                .resultOrPartial(msg -> Util.error("ItemSetting encode error : " + msg))
                .ifPresent(tag -> {
                    stack.getOrCreateTag().put(ITEM_SETTING_TAG, tag);
                });
    }

    @NotNull
    public static ItemSetting getItemSetting(ItemStack stack) {
        return CodecHelper.parse(ItemSetting.CODEC, stack.getOrCreateTag().get(ITEM_SETTING_TAG))
                .result()
                .orElse(ItemSetting.DEFAULT);
    }

    /**
     * 更新召唤卡的召唤组件。
     */
    public static void updateSummonTowerItem(ItemStack stack, ResourceKey<SummonEntry> resourceKey, SummonEntry entry) {
        setItemSetting(stack, entry.itemSetting());
        setSummonEntry(stack, resourceKey);
    }

    public static ItemStack create(ResourceKey<SummonEntry> resourceKey, SummonEntry entry){
        ItemStack stack = new ItemStack(OTDItems.SUMMON_TOWER_ITEM.get());
        updateSummonTowerItem(stack, resourceKey, entry);
        return stack;
    }

}
