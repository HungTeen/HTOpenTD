package hungteen.opentd.common.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.impl.filter.EntityPredicateFilter;
import hungteen.opentd.common.impl.filter.TypeTargetFilter;
import hungteen.opentd.common.impl.requirement.*;
import hungteen.opentd.common.impl.tower.OTDTowerComponents;
import hungteen.opentd.common.item.ItemSetting;
import hungteen.opentd.util.NBTUtil;
import hungteen.opentd.util.Util;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 11:19
 **/
public interface OTDSummonEntries {

    HTCodecRegistry<SummonEntry> SUMMON_ITEMS = HTRegistryManager.create(Util.prefix("summon_items"), () -> SummonEntry.CODEC, () -> SummonEntry.NETWORK_CODEC);

    ResourceKey<SummonEntry> PEA_SHOOTER_CARD = create("pea_shooter_card");
    ResourceKey<SummonEntry> SUN_FLOWER_CARD = create("sun_flower_card");

    static void register(BootstapContext<SummonEntry> context) {
        final HolderGetter<ITowerComponent> towerComponents = OTDTowerComponents.registry().helper().lookup(context);
        context.register(PEA_SHOOTER_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("pea_shooter_card"))
                        .name("item.opentd.test_pea_shooter_card")
                        .requirement(Holder.direct(
                                new AroundEntityRequirement(3, 3, 1, 10, Optional.empty(), new EntityPredicateFilter(EntityPredicate.ANY, EntityPredicate.Builder.entity().nbt(new NbtPredicate(NBTUtil.sunflowerPredicate())).build()))
                        )).build(),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.PEA_SHOOTER))
        ));
        context.register(SUN_FLOWER_CARD, new SummonEntry(
                ItemSetting.builder().card()
                        .model(Util.prefix("sun_flower_card"))
                        .name("item.opentd.test_sun_flower_card")
                        .damage(10)
                        .requirement(Holder.direct(new AndRequirement(List.of(
                                        new OrRequirement(
                                                List.of(
                                                        new EntityRequirement(
                                                                Optional.of("Entity Not Fit !"),
                                                                new TypeTargetFilter(List.of(EntityType.CREEPER))
                                                        ),
                                                        new BlockRequirement(
                                                                Optional.of("Block Not Fit !"),
                                                                Optional.empty(),
                                                                Optional.empty(),
                                                                Optional.of(List.of(
                                                                        Blocks.GRASS_BLOCK
                                                                ))
                                                        )
                                                ),
                                                Optional.empty()
                                        )
                                ), Optional.empty())
                        )).build(),
                Optional.of(towerComponents.getOrThrow(OTDTowerComponents.SUN_FLOWER))
        ));
    }

    static ResourceKey<SummonEntry> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static IHTCodecRegistry<SummonEntry> registry() {
        return SUMMON_ITEMS;
    }

    record SummonEntry(ItemSetting itemSetting, Optional<Holder<ITowerComponent>> towerSetting) {

        public static final Codec<SummonEntry> CODEC = RecordCodecBuilder.<SummonEntry>mapCodec(instance -> instance.group(
                ItemSetting.CODEC.fieldOf("item_setting").forGetter(SummonEntry::itemSetting),
                Codec.optionalField("tower_setting", OTDTowerComponents.getCodec()).forGetter(SummonEntry::towerSetting)
        ).apply(instance, SummonEntry::new)).codec();

        public static final Codec<SummonEntry> NETWORK_CODEC = RecordCodecBuilder.<SummonEntry>mapCodec(instance -> instance.group(
                ItemSetting.NETWORK_CODEC.fieldOf("item_setting").forGetter(SummonEntry::itemSetting)
        ).apply(instance, (itemSetting -> new SummonEntry(itemSetting, Optional.empty())))).codec();
    }

}
