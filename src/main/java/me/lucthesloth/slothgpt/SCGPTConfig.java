package me.lucthesloth.slothgpt;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.*;
@Config.Gui.Background(value = Config.Gui.Background.TRANSPARENT)
@Config(name = "slothgptconf")
public class SCGPTConfig implements ConfigData{
    @ConfigEntry.Gui.Tooltip
    public String prompt = "Write a short welcome back for %player%";
    public boolean generateOnJoin = true;
    @ConfigEntry.Gui.CollapsibleObject
    public APIConfig api = new APIConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public RequestConfig request = new RequestConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public AutoWelcome autoWelcome = new AutoWelcome();
    static class APIConfig {
        String token = "";        
    }
    static class RequestConfig {
        String model = "gpt-3.5-turbo";
        int max_tokens = 100;
        int n = 1;
    }
    public static class AutoWelcome {
        public boolean enabled = false;
        public boolean useChatCapture = false;
        @ConfigEntry.Gui.Tooltip
        public String chatCaptureRegex = "(.*) joined!$";
        @ConfigEntry.Gui.Tooltip
        public
        String message = "Welcome back %player%";
        @ConfigEntry.Gui.Excluded
        int lookBehind = 10;
    }
}
