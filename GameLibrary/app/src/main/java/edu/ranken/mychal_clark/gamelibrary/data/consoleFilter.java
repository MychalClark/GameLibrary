package edu.ranken.mychal_clark.gamelibrary.data;

public class consoleFilter {
    private final String consoleId;

    private final String consoleName;

    public consoleFilter(String consoleId, String consoleName){
        this.consoleId = consoleId;
        this.consoleName= consoleName;
    }

    @Override public String toString(){
        return consoleName;
    }
}
