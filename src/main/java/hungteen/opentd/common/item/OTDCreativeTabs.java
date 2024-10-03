package hungteen.opentd.common.item;

import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.registry.ItemHelper;
import hungteen.opentd.OTDConfigs;
import hungteen.opentd.common.codec.SummonEntry;
import hungteen.opentd.common.impl.OTDSummonEntries;
import hungteen.opentd.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
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

    RegistryObject<CreativeModeTab> SUMMON_CARDS = register("summon_cards", builder ->
                    builder.icon(() -> new ItemStack((OTDItems.SUMMON_TOWER_ITEM.get())))
                            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                            .displayItems((parameters, output) -> {
                                HolderLookup.RegistryLookup<SummonEntry> summonEntries = parameters.holders().lookupOrThrow(OTDSummonEntries.registry().getRegistryKey());
                                summonEntries.listElementIds().filter(key -> {
                                    if(! OTDConfigs.displayDefaultCards()){
                                        return ! Util.in(key.location());
                                    }
                                    return true;
                                }).map(key -> {
                                    return SummonTowerItem.create(key, summonEntries.getOrThrow(key).get());
                                }).forEach(output::accept);
                            }).withSearchBar().withBackgroundLocation(Util.mc().containerTexture("creative_inventory/tab_item_search"))
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
