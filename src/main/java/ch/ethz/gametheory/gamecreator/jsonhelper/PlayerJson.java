package ch.ethz.gametheory.gamecreator.jsonhelper;

import ch.ethz.gametheory.gamecreator.data.Player;

public class PlayerJson {
    public int id;
    public String name;

    public static PlayerJson convert(Player player) {
        PlayerJson playerJson = new PlayerJson();
        playerJson.id = player.getId();
        playerJson.name = player.getName();
        return playerJson;
    }
}
