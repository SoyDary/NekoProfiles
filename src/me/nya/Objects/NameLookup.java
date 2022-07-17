package me.nya.Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;


public class NameLookup {


    private static final String LOOKUP_URL = "https://api.mojang.com/user/profiles/%s/names";

 
    private static final String GET_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?t=0";
    private static final Gson JSON_PARSER = new Gson();


    public static PreviousPlayerNameEntry[] getPlayerPreviousNames(UUID player) {
        return getPlayerPreviousNames(player.toString());
    }


    public static PreviousPlayerNameEntry[] getPlayerPreviousNames(OfflinePlayer player) {
        return getPlayerPreviousNames(player.getUniqueId());
    }


    public static PreviousPlayerNameEntry[] getPlayerPreviousNames(String uuid) {
        if (uuid == null || uuid.isEmpty())
            return null;
        String response = null;
		try {
			response = getRawJsonResponse(new URL(String.format(LOOKUP_URL, uuid)));
		} catch (IOException e) {
			e.printStackTrace();
		}
        PreviousPlayerNameEntry[] names = JSON_PARSER.fromJson(response, PreviousPlayerNameEntry[].class);
        return names;
    }


    public static String getPlayerUUID(String name) throws IOException {
        String response = getRawJsonResponse(new URL(String.format(GET_UUID_URL, name)));
        JsonObject o = JSON_PARSER.fromJson(response, JsonObject.class);
        if (o == null)
            return null;
        return o.get("id") == null ? null : o.get("id").getAsString();
    }


    private static String getRawJsonResponse(URL u) throws IOException {
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setDoInput(true);
        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);
        con.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response = in.readLine();
        in.close();
        return response;
    }


    public class PreviousPlayerNameEntry {
        private String name;
        
        @SerializedName("changedToAt")
        private long changeTime;

        public String getPlayerName() {
            return name;
        }
        
        public long getChangeTime() {
            return changeTime;
        }
        
        public boolean isPlayersInitialName() {
            return getChangeTime() == 0;
        }

        @Override
        public String toString() {
            return "Name: " + name + " Date of change: " + new Date(changeTime).toString();
        }
    }

} 