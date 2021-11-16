package me.myaltsthis.justanothermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class JustAnotherModClient implements ClientModInitializer {
    public static Logger LOGGER = LogManager.getLogger("JustAnotherMod");
    @Override
    public void onInitializeClient() {
        LOGGER.info("Checking options: " + MyGameOptions.zoomAmount);
        LOGGER.info("loaded");
    }
    public static String toPrettyFormat(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(json);
    }

}
