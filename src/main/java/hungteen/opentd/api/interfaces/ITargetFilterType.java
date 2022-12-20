package hungteen.opentd.api.interfaces;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:01
 **/
public interface ITargetFilterType<P extends ITargetFilter> extends ISimpleEntry {

    /**
     * Get the method to codec target.
     * @return Codec method.
     */
    Codec<P> codec();
}
