package hungteen.opentd.common.entity;

import com.mojang.serialization.Codec;
import hungteen.opentd.OpenTD;
import hungteen.opentd.common.codec.RenderSetting;
import net.minecraft.nbt.NbtOps;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import java.util.function.Consumer;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/7 11:12
 */
public interface IOTDEntity extends IAnimatable, IEntityAdditionalSpawnData, IEntityForKJS{

    RenderSetting getRenderSetting();

    default <T> void parseComponent(Codec<T> codec, Consumer<T> consumer, Consumer<String> errorRunnable, Runnable emptyRunnable) {
        codec.parse(NbtOps.INSTANCE, this.getComponentTag())
                .resultOrPartial(errorRunnable)
                .ifPresentOrElse(consumer,emptyRunnable);
    }

    default PlayState specificAnimation(AnimationEvent<?> event) {
        final AnimationBuilder builder = new AnimationBuilder();
        if (this.getCurrentAnimation().isPresent()){
            builder.addAnimation(this.getCurrentAnimation().get(), ILoopType.EDefaultLoopTypes.PLAY_ONCE);
        } else {
            event.getController().markNeedsReload();
        }
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }
}
