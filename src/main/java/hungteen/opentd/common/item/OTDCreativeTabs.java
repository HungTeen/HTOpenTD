package hungteen.opentd.common.item;

import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.registry.ItemHelper;
import hungteen.opentd.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/6 23:34
 **/
public interface OTDCreativeTabs {

    DeferredRegister<CreativeModeTab> TABS = ItemHelper.tab().createRegister(Util.id());

    RegistryObject<CreativeModeTab> MATERIALS = register("materials", builder ->
                    builder.icon(() -> new ItemStack((IMMBlocks.GANODERMA.get())))
                            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                            .displayItems((parameters, output) -> {
                                Util.get().filterValues(ItemHelper.get(), item -> {
//                                    if(itemSet.contains(item)) return false; // 已经被添加，不再考虑。
//                                    if(item instanceof ElixirItem) return false; // 排除丹药。
//                                    if(item instanceof SecretManualItem) return false; // 排除秘籍。
//                                    if(item instanceof IArtifactItem) return false; // 排除物品法器。
//                                    if(item instanceof BlockItem blockItem) {
//                                        if (blockItem.getBlock() instanceof IArtifactBlock) return false; // 排除方块法器。
//                                        if(blockItem.getBlock() instanceof CushionBlock) return false; // 排除坐垫。
//                                    }
//                                    if(item instanceof RuneItem) return false;
                                    return true;
                                }).forEach(output::accept);
                            })
    );

    static void register(IEventBus modBus) {
        TABS.register(modBus);
    }

    private static RegistryObject<CreativeModeTab> register(String name, Consumer<CreativeModeTab.Builder> consumer) {
        return TABS.register(name, () -> {
            final CreativeModeTab.Builder builder = CreativeModeTab.builder().title(Component.translatable(StringHelper.langKey("itemGroup", Util.id(), name)));
            consumer.accept(builder);
            return builder.build();
        });
    }
}
