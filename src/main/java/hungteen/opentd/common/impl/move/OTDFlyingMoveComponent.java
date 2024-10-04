package hungteen.opentd.common.impl.move;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IMoveComponentType;
import hungteen.opentd.common.entity.movement.OTDFlyingMoveControl;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 11:08
 */
public record OTDFlyingMoveComponent(int maxTurn, boolean hoverInPlace) implements IMoveComponent {

    public static final Codec<OTDFlyingMoveComponent> CODEC = RecordCodecBuilder.<OTDFlyingMoveComponent>mapCodec(instance -> instance.group(
            Codec.intRange(0, 1000).optionalFieldOf("max_turn", 20).forGetter(OTDFlyingMoveComponent::maxTurn),
            Codec.BOOL.optionalFieldOf("hover_in_place", false).forGetter(OTDFlyingMoveComponent::hoverInPlace)
    ).apply(instance, OTDFlyingMoveComponent::new)).codec();

    @Override
    public MoveControl create(Mob mob) {
        return new OTDFlyingMoveControl(mob, maxTurn(), hoverInPlace());
    }

    @Override
    public IMoveComponentType<?> getType() {
        return HTMoveComponents.OTD_FLYING;
    }
}
