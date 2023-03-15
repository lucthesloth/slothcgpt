package me.lucthesloth.slothgpt;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.*;
@Config.Gui.Background(value = Config.Gui.Background.TRANSPARENT)
@Config(name = "slothgptconf")
public class SCGPTConfig implements ConfigData{
    
    public String prompt = "Write a short welcome back for %player%";
    public boolean generateOnJoin = true;
    @ConfigEntry.Gui.CollapsibleObject
    public APIConfig api = new APIConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public RequestConfig request = new RequestConfig();

    static class APIConfig {
        String token = "";        
    }
    static class RequestConfig {
        String model = "gpt-3.5-turbo";
        int max_tokens = 100;
        int n = 1;
    }
}
