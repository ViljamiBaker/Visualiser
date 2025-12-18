package visualiser.Renderer.Util;

import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.ShadowData;

public record RendererStartData(
    Function[] fns,
    int updateCount,
    RendererUpdateType updateType,
    ShadowData shadow
) {
    public static RendererStartData normal(){
        return new RendererStartData(new Function[0], -1, RendererUpdateType.UpdateAll, new ShadowData(0.1,5,0.3));
    }
}
