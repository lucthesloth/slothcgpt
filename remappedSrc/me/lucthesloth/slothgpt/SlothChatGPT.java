package me.lucthesloth.slothgpt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class SlothChatGPT implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sloth-chatgpt");
    public static SlothChatGPT instance;
    public static String lastPlayer = "";
    public static Gson gson = new GsonBuilder().create();

    public static SCGPTConfig _Config;

    @Override
    public void onInitialize() {
        instance = this;        
        AutoConfig.register(SCGPTConfig.class, GsonConfigSerializer::new).getConfig();
        _Config = AutoConfig.getConfigHolder(SCGPTConfig.class).getConfig();
    }  
    
    public static List<String> responses(String player){
        Map<String, String> headers = new HashMap<>(); 
        headers.put("Authorization", "Bearer " + _Config.api.token);
        headers.put("Content-Type", "application/json");
        ChatRequestData data = new ChatRequestData(_Config.request.model, List.of(new ChatRequestData.Message("user", _Config.prompt.replace("%player%", player))), _Config.request.max_tokens, _Config.request.n);
        try {
            String response = HTTPRequest.sendPostRequest("https://api.openai.com/v1/chat/completions",headers,SlothChatGPT.gson.toJson(data));
            ChatRequestData.Response responseData = SlothChatGPT.gson.fromJson(response, ChatRequestData.Response.class);
            return responseData.getChoices().stream().map(choice -> choice.getMessage().getContent()).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
