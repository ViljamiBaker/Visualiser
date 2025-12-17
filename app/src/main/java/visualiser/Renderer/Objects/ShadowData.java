package visualiser.Renderer.Objects;

//color decays with this graph : https://www.desmos.com/calculator/b9mfiuyadz
public class ShadowData {
    public double falloffRate;
    public double falloffDist;
    public double minFalloff;

    public ShadowData(double falloffRate, double falloffDist, double minFalloff){
        this.falloffRate = falloffRate;
        this.falloffDist = falloffDist;
        this.minFalloff = minFalloff;
    }

    public ShadowData(){
        this.falloffRate = 1;
        this.falloffDist = 1;
        this.minFalloff = 1;
    }
}