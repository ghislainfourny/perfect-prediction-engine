package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.xmlhelper.InformationSetXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.PlayerXML;
import ch.ethz.gametheory.gamecreator.xmlhelper.TreeXML;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "gamedata")
public class GameDataWrapper {

    private List<PlayerXML> players;
    private List<InformationSetXML> informationSets;
    private TreeXML mainTree;
    private List<TreeXML> components;

    @XmlElementWrapper(name = "players")
    @XmlElement(name = "player")
    public List<PlayerXML> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerXML> players) {
        this.players = players;
    }

    @XmlElementWrapper(name = "informationsets")
    @XmlElement(name = "informationset")
    public List<InformationSetXML> getInformationSets() {
        return informationSets;
    }

    public void setInformationSets(List<InformationSetXML> informationSets) {
        this.informationSets = informationSets;
    }

    @XmlElement(name = "maintree")
    public TreeXML getMainTree() {
        return mainTree;
    }

    public void setMainTree(TreeXML mainTree) {
        this.mainTree = mainTree;
    }

    @XmlElementWrapper(name = "components")
    @XmlElement(name = "tree")
    public List<TreeXML> getComponents() {
        return components;
    }

    public void setComponents(List<TreeXML> components) {
        this.components = components;
    }

}
