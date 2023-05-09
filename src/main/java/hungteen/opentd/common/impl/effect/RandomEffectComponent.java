package hungteen.opentd.common.impl.effect;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.WeightList;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-15 15:40
 **/
public record RandomEffectComponent(int totalWeight, int effectTimes, boolean different, List<Pair<IEffectComponent, Integer>> effects) implements IEffectComponent {

    public static final Codec<RandomEffectComponent> CODEC = RecordCodecBuilder.<RandomEffectComponent>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("total_weight", 0).forGetter(RandomEffectComponent::totalWeight),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("effect_times", 1).forGetter(RandomEffectComponent::effectTimes),
            Codec.BOOL.optionalFieldOf("different", true).forGetter(RandomEffectComponent::different),
            Codec.mapPair(
                    HTEffectComponents.getCodec().fieldOf("effect"),
                    Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("weight", 0)
            ).codec().listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(RandomEffectComponent::effects)
    ).apply(instance, RandomEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        getEffects(owner.level.getRandom()).forEach(e -> e.effectTo(serverLevel, owner, entity));
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        getEffects(owner.level.getRandom()).forEach(e -> e.effectTo(serverLevel, owner, pos));
    }

    private List<IEffectComponent> getEffects(RandomSource random){
        List<hungteen.htlib.util.Pair<IEffectComponent, Integer>> list = effects.stream().map(p -> hungteen.htlib.util.Pair.of(p.getFirst(), p.getSecond())).collect(Collectors.toList());
        int totalWeight = totalWeight();
        if(totalWeight == 0){
            totalWeight = list.stream().map(hungteen.htlib.util.Pair::getSecond).reduce(0, Integer::sum);
        }
        WeightList<IEffectComponent> weightList = WeightList.of(list.toArray(new hungteen.htlib.util.Pair[0]));
        weightList.setTotalWeight(totalWeight);
        return weightList.getRandomItems(random, effectTimes(), different());
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.RANDOM_EFFECT;
    }
}
