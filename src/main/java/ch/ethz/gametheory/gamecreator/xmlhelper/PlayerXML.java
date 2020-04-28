package ch.ethz.gametheory.gamecreator.xmlhelper;

import ch.ethz.gametheory.gamecreator.Player;

public class PlayerXML {
    public int ID;
    public String name;

    public static PlayerXML convert(Player player){
        PlayerXML playerXML = new PlayerXML();
        playerXML.ID = player.getId();
        playerXML.name = player.getName();
        return playerXML;
    }
}
