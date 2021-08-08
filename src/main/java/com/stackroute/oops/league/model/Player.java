package com.stackroute.oops.league.model;

/**
 * This class contains five fields playerId,playerName,password,yearExpr and teamTitle
 * It is a subclass of Comparable interface
 */
public class Player extends Thread implements Comparable {

    private String playerId;
    private String playerName;
    private String password;
    private int yearExpr;
    private String teamTitle;

    //Parameterized Constructor to initialize all properties
    public Player(String playerId, String playerName, String password, int yearExpr) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.password = password;
        this.yearExpr = yearExpr;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getPassword() {
        return this.password;
    }

    public int getYearExpr() {
        return this.yearExpr;
    }

    //Return teamTitle if it is present and not empty
    public String getTeamTitle() {
        if(this.teamTitle == null || this.teamTitle.isEmpty() ){
            return null;
        }
        return this.teamTitle;
    }

    public void setTeamTitle(String teamTitle) {
        this.teamTitle = teamTitle;
    }

    /**
     * This overridden method should return player details in below format
     * Player{playerId=xxxxx, playerName=xxxxxx, yearExpr=xxxxxx,teamTitle=xxxxxxxx}-> if league team title is set
     * Player{playerId=xxxxx, playerName=xxxxxx, yearExpr=xxxxxx}-> if league team title not set
     */
    @Override
    public String toString() {

        if (getTeamTitle() == null){
            return "Player{playerId="+ playerId+", " + "playerName="+playerName+", "+"yearExpr="+yearExpr+"}";
        }
        return "Player{playerId="+ playerId+", " + "playerName="+playerName+", "+"yearExpr="+yearExpr+", "+"teamTitle="+teamTitle+"}";
    }

    //Overridden equals method
    @Override
    public boolean equals(Object object) {
        return false;
    }

    //Overridden hashcode method
    @Override
    public int hashCode() {
        return 0;
    }

    //compares player object based on playerId
    @Override
    public int compareTo(Object o) {
        Player player = (Player)o;
        return this.playerId.compareToIgnoreCase(player.getPlayerId());
    }

}
