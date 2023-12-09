package hungteen.opentd.common.impl.move;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IMoveComponentType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 11:08
 */
public record FlyingMoveComponent(int maxTurn, boolean hoverInPlace) implements IMoveComponent {

    public static final Codec<FlyingMoveComponent> CODEC = RecordCodecBuilder.<FlyingMoveComponent>mapCodec(instance -> instance.group(
            Codec.intRange(0, 1000).optionalFieldOf("max_turn", 20).forGetter(FlyingMoveComponent::maxTurn),
            Codec.BOOL.optionalFieldOf("hover_in_place", false).forGetter(FlyingMoveComponent::hoverInPlace)
    ).apply(instance, FlyingMoveComponent::new)).codec();

    @Override
    public MoveControl create(Mob mob) {
        return new FlyingMoveControl(mob, maxTurn(), hoverInPlace());
    }

    @Override
    public IMoveComponentType<?> getType() {
        return OTDMoveTypes.FLYING;
    }
}
