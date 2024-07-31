package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:13
 *
 * Sync manually.
 */
public record RenderSetting(float width, float height, float scale, boolean translucent, ResourceLocation modelLocation,
                            ResourceLocation textureLocation, ResourceLocation animationLocation) {

    public static final RenderSetting DEFAULT = make(0.8F, 0.8F, 1F, false, "pea_shooter");

    public static RenderSetting make(float width, float height, float scale, boolean transparent, String name) {
        return new RenderSetting(width, height, scale, transparent,
                Util.prefix("geo/" + name + ".geo.json"),
                Util.prefix("textures/entity/" + name + ".png"),
                Util.prefix("animations/" + name + ".animation.json")
        );
    }

    public EntityDimensions dimension(){
        return EntityDimensions.scalable(width(), height());
    }

    public static final Codec<RenderSetting> CODEC = RecordCodecBuilder.<RenderSetting>mapCodec(instance -> instance.group(
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("width").forGetter(RenderSetting::width),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("height").forGetter(RenderSetting::height),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("scale", 1F).forGetter(RenderSetting::scale),
            Codec.BOOL.optionalFieldOf("translucent", false).forGetter(RenderSetting::translucent),
            ResourceLocation.CODEC.fieldOf("model").forGetter(RenderSetting::modelLocation),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(RenderSetting::textureLocation),
            ResourceLocation.CODEC.fieldOf("animation").forGetter(RenderSetting::animationLocation)
    ).apply(instance, RenderSetting::new)).codec();

}