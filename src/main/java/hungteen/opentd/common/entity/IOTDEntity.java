package hungteen.opentd.common.entity;

import com.mojang.serialization.Codec;
import hungteen.opentd.common.codec.RenderSetting;
import net.minecraft.nbt.NbtOps;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.Consumer;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/7 11:12
 */
public interface IOTDEntity extends GeoEntity, IEntityAdditionalSpawnData, IEntityForKJS{

    RenderSetting getRenderSetting();

    default <T> void parseComponent(Codec<T> codec, Consumer<T> consumer, Consumer<String> errorRunnable, Runnable emptyRunnable) {
        codec.parse(NbtOps.INSTANCE, this.getComponentTag())
                .resultOrPartial(errorRunnable)
                .ifPresentOrElse(consumer,emptyRunnable);
    }

    default PlayState specificAnimation(AnimationState<?> state) {
        // TODO 指定动画。
//        if (this.getCurrentAnimation().isPresent()){
//            builder.addAnimation(this.getCurrentAnimation().get(), ILoopType.EDefaultLoopTypes.PLAY_ONCE);
//        } else {
//            state.resetCurrentAnimation();
//            return PlayState.STOP;
//        }
        return PlayState.CONTINUE;
    }
}
