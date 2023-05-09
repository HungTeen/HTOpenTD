package hungteen.opentd.common.impl.tower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.common.codec.MovementSetting;
import hungteen.opentd.common.impl.effect.HTEffectComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:11
 */
public record PlantHeroComponent(Optional<MovementSetting> movementSetting) implements ITowerComponent {

    public static final Codec<PlantHeroComponent> CODEC = RecordCodecBuilder.<PlantHeroComponent>mapCodec(instance -> instance.group(
            Codec.optionalField("movement_setting", MovementSetting.CODEC).forGetter(PlantHeroComponent::movementSetting)
    ).apply(instance, PlantHeroComponent::new)).codec();

    @Override
    public Entity createEntity(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        return null;
    }

    @Override
    public ITowerComponentType<?> getType() {
        return HTTowerComponents.PLANT_HERO;
    }

}
