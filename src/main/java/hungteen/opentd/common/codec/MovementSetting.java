package hungteen.opentd.common.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IPathNavigationType;
import hungteen.opentd.common.impl.OTDPathNavigations;
import hungteen.opentd.common.impl.move.OTDMoveComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:19
 */
public record MovementSetting(Optional<NavigationSetting> navigationSetting, Optional<Holder<IMoveComponent>> moveComponent, boolean canRandomMove, boolean avoidWater, boolean keepDistance, double speedModifier, double backwardPercent, double upwardPercent){
    public static final Codec<MovementSetting> CODEC = RecordCodecBuilder.<MovementSetting>mapCodec(instance -> instance.group(
            Codec.optionalField("navigator_setting", NavigationSetting.CODEC).forGetter(MovementSetting::navigationSetting),
            Codec.optionalField("move_controller", OTDMoveComponents.getCodec()).forGetter(MovementSetting::moveComponent),
            Codec.BOOL.optionalFieldOf("can_random_move", true).forGetter(MovementSetting::canRandomMove),
            Codec.BOOL.optionalFieldOf("avoid_water", true).forGetter(MovementSetting::avoidWater),
            Codec.BOOL.optionalFieldOf("keep_distance", false).forGetter(MovementSetting::keepDistance),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("speed_modifier", 1D).forGetter(MovementSetting::speedModifier),
            Codec.doubleRange(0, 1).optionalFieldOf("backward_percent", 0.3D).forGetter(MovementSetting::backwardPercent),
            Codec.doubleRange(0, 1).optionalFieldOf("upward_percent", 0.7D).forGetter(MovementSetting::upwardPercent)
    ).apply(instance, MovementSetting::new)).codec();

    public record NavigationSetting(IPathNavigationType pathNavigationType, List<Pair<String, Float> > nodeWeightList, boolean canOpenDoors, boolean canPassDoors, boolean canFloat){
        public static final Codec<NavigationSetting> CODEC = RecordCodecBuilder.<NavigationSetting>mapCodec(instance -> instance.group(
                OTDPathNavigations.registry().byNameCodec().fieldOf("navigator").forGetter(NavigationSetting::pathNavigationType),
                Codec.mapPair(
                        Codec.STRING.fieldOf("type"),
                        Codec.FLOAT.fieldOf("weight")
                ).codec().listOf().optionalFieldOf("node_weight_list", List.of()).forGetter(NavigationSetting::nodeWeightList),
                Codec.BOOL.optionalFieldOf("can_open_doors", false).forGetter(NavigationSetting::canOpenDoors),
                Codec.BOOL.optionalFieldOf("can_pass_doors", false).forGetter(NavigationSetting::canPassDoors),
                Codec.BOOL.optionalFieldOf("can_float", true).forGetter(NavigationSetting::canFloat)
        ).apply(instance, NavigationSetting::new)).codec();

        public PathNavigation getNavigator(Level level, Mob mob){
            final PathNavigation navigator = pathNavigationType.create(level, mob);
            navigator.getNodeEvaluator().setCanOpenDoors(canOpenDoors());
            navigator.getNodeEvaluator().setCanPassDoors(canPassDoors());
            navigator.setCanFloat(canFloat());
            return navigator;
        }
    }

}
