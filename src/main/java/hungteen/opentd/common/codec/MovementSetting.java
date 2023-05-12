package hungteen.opentd.common.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IPathNavigationType;
import hungteen.opentd.common.impl.HTPathNavigations;
import hungteen.opentd.common.impl.move.HTMoveComponents;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:19
 */
public record MovementSetting(Optional<IPathNavigationType> pathNavigationType, Optional<IMoveComponent> moveComponent, List<Pair<String, Float> > nodeWeightList, boolean canRandomMove, double speedModifier, double backwardPercent, double upwardPercent){
    public static final Codec<MovementSetting> CODEC = RecordCodecBuilder.<MovementSetting>mapCodec(instance -> instance.group(
            Codec.optionalField("navigator_type", HTPathNavigations.registry().byNameCodec()).forGetter(MovementSetting::pathNavigationType),
            Codec.optionalField("move_controller", HTMoveComponents.getCodec()).forGetter(MovementSetting::moveComponent),
            Codec.mapPair(
                    Codec.STRING.fieldOf("type"),
                    Codec.FLOAT.fieldOf("weight")
            ).codec().listOf().optionalFieldOf("node_weight_list", List.of()).forGetter(MovementSetting::nodeWeightList),
            Codec.BOOL.optionalFieldOf("can_random_move", true).forGetter(MovementSetting::canRandomMove),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("speed_modifier", 1D).forGetter(MovementSetting::speedModifier),
            Codec.doubleRange(0, 1).optionalFieldOf("backward_percent", 0.3D).forGetter(MovementSetting::backwardPercent),
            Codec.doubleRange(0, 1).optionalFieldOf("upward_percent", 0.7D).forGetter(MovementSetting::upwardPercent)
    ).apply(instance, MovementSetting::new)).codec();

}
