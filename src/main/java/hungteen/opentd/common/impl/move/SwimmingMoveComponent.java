package hungteen.opentd.common.impl.move;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IMoveComponentType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 11:18
 */
public record SwimmingMoveComponent(int maxTurnX, int maxTurnY, float inWaterSpeedModifier, float outsideWaterSpeedModifier, boolean applyGravity) implements IMoveComponent {

    public static final Codec<SwimmingMoveComponent> CODEC = RecordCodecBuilder.<SwimmingMoveComponent>mapCodec(instance -> instance.group(
            Codec.intRange(0, 1000).optionalFieldOf("max_turn_x", 85).forGetter(SwimmingMoveComponent::maxTurnX),
            Codec.intRange(0, 1000).optionalFieldOf("max_turn_y", 10).forGetter(SwimmingMoveComponent::maxTurnY),
            Codec.floatRange(0, 1000).optionalFieldOf("in_water_speed_modifier", 0.02F).forGetter(SwimmingMoveComponent::inWaterSpeedModifier),
            Codec.floatRange(0, 1000).optionalFieldOf("outside_water_speed_modifier", 0.1F).forGetter(SwimmingMoveComponent::outsideWaterSpeedModifier),
            Codec.BOOL.optionalFieldOf("apply_gravity", true).forGetter(SwimmingMoveComponent::applyGravity)
    ).apply(instance, SwimmingMoveComponent::new)).codec();

    @Override
    public MoveControl create(Mob mob) {
        return new SmoothSwimmingMoveControl(mob, maxTurnX(), maxTurnY(), inWaterSpeedModifier(), outsideWaterSpeedModifier(), applyGravity());
    }

    @Override
    public IMoveComponentType<?> getType() {
        return OTDMoveTypes.SWIMMING;
    }
}
