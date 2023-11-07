package ch.ethz.gametheory.gamecreator.xmlhelper;

import ch.ethz.gametheory.gamecreator.InformationSet;

public class InformationSetXML {
    public int ID;
    public int playerID;
    public String color;

    public static InformationSetXML convert(InformationSet informationSet){
        InformationSetXML informationSetXML = new InformationSetXML();
        informationSetXML.ID = informationSet.getId();
        informationSetXML.playerID = informationSet.getAssignedPlayer()!=null?informationSet.getAssignedPlayer().getId():-1;
        informationSetXML.color = informationSet.getColor().toString();
        return informationSetXML;
    }

}
