package hungteen.opentd.common.entity;

import com.mojang.serialization.Codec;
import hungteen.opentd.common.codec.RenderSetting;
import net.minecraft.nbt.NbtOps;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/7 11:12
 */
public interface IOTDEntity extends GeoEntity, IEntityAdditionalSpawnData, IEntityForKJS{

    Map<String, RawAnimation> RAW_ANIMATION_MAP = new HashMap<>();

    RenderSetting getRenderSetting();

    default <T> void parseComponent(Codec<T> codec, Consumer<T> consumer, Consumer<String> errorRunnable, Runnable emptyRunnable) {
        codec.parse(NbtOps.INSTANCE, this.getComponentTag())
                .resultOrPartial(errorRunnable)
                .ifPresentOrElse(consumer,emptyRunnable);
    }

    default PlayState specificAnimation(AnimationState<?> state) {
        if (this.getCurrentAnimation().isPresent()){
            return state.setAndContinue(getRawAnimation(this.getCurrentAnimation().get()));
        } else {
            state.resetCurrentAnimation();
            return PlayState.STOP;
        }
    }

    default RawAnimation getRawAnimation(String name){
        return RAW_ANIMATION_MAP.computeIfAbsent(name, k -> RawAnimation.begin().thenPlay(name));
    }
}
