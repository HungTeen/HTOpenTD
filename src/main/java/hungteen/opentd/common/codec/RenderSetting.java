package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.OpenTD;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:13
 */
public record RenderSetting(float width, float height, float scale, ResourceLocation modelLocation,
                             ResourceLocation textureLocation, ResourceLocation animationLocation) {

    public static final RenderSetting DEFAULT = make(0.8F, 0.8F, 1F, "pea_shooter");

    public static RenderSetting make(float width, float height, float scale, String name) {
        return new RenderSetting(width, height, scale,
                OpenTD.prefix("geo/" + name + ".geo.json"),
                OpenTD.prefix("textures/entity/" + name + ".png"),
                OpenTD.prefix("animations/" + name + ".animation.json")
        );
    }

    public static final Codec<RenderSetting> CODEC = RecordCodecBuilder.<RenderSetting>mapCodec(instance -> instance.group(
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("width").forGetter(RenderSetting::width),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("height").forGetter(RenderSetting::height),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("scale", 1F).forGetter(RenderSetting::scale),
            ResourceLocation.CODEC.fieldOf("model").forGetter(RenderSetting::modelLocation),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(RenderSetting::textureLocation),
            ResourceLocation.CODEC.fieldOf("animation").forGetter(RenderSetting::animationLocation)
    ).apply(instance, RenderSetting::new)).codec();
}