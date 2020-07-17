package ch.ethz.gametheory.gamecreator.jsonhelper;

import ch.ethz.gametheory.gamecreator.data.InformationSet;

public class InformationSetJson {
    public int id;
    public int playerId;
    public String color;

    public static InformationSetJson convert(InformationSet informationSet) {
        InformationSetJson informationSetJson = new InformationSetJson();
        informationSetJson.id = informationSet.getId();
        informationSetJson.playerId = informationSet.getAssignedPlayer() != null ? informationSet.getAssignedPlayer().getId() : -1;
        informationSetJson.color = informationSet.getColor().toString();
        return informationSetJson;
    }
}
