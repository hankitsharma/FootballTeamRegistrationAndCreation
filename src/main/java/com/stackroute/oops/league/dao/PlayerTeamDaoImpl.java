package com.stackroute.oops.league.dao;

import com.stackroute.oops.league.exception.PlayerNotFoundException;
import com.stackroute.oops.league.model.Player;
import com.stackroute.oops.league.model.PlayerTeam;
import com.sun.source.tree.BinaryTree;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This class implements the PlayerTeamDao interface
 * This class has two fields playerTeamSet,playerDao and a String constant for storing file name.
 */
public class PlayerTeamDaoImpl implements PlayerTeamDao {
    private static final String TEAM_FILE_NAME = "src/main/resources/finalteam.csv";
    Set<PlayerTeam> playerTeamSet ;
    PlayerDao playerDao ;
    /**
     * Constructor to initialize an empty TreeSet and PlayerDao object
     */
    public PlayerTeamDaoImpl() {
        playerTeamSet = new TreeSet<>();
        playerDao = new PlayerDaoImpl();
    }

    /*
    Returns the list of players belonging to a particular teamTitle by reading
    from the file finalteam.csv
     */
    @Override
    public Set<PlayerTeam> getPlayerSetByTeamTitle(String teamTitle) {
        Set<PlayerTeam> setOfPlayerTeam = getAllPlayerTeams();

        return setOfPlayerTeam.stream()
                .filter(playerTeam ->  playerTeam.getTeamTitle().equalsIgnoreCase(teamTitle))
                .collect(Collectors.toSet());

    }

    //Add he given PlayerTeam Object to finalteam.csv file
    @Override
    public boolean addPlayerToTeam(Player player) throws PlayerNotFoundException {
        playerDao.findPlayer(player.getPlayerId());
        return AddLineToAFile(player);
    }

    public Boolean AddLineToAFile(Player player){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter((new FileWriter(TEAM_FILE_NAME)));
            bufferedWriter.write(player.getPlayerId()+","+player.getPlayerName()+","+player.getPassword()+","+player.getYearExpr()+","+player.getTeamTitle());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            return true;
        }catch(IOException e){
            return false;
        }
    }

    //Return the set of all PlayerTeam by reading the file content from finalteam.csv file
    @Override
    public Set<PlayerTeam> getAllPlayerTeams() {
        BufferedReader bufferedReader= initialiseBufferReader();
        String line = readLineFromAFile(bufferedReader);
        System.out.println(line);

        if(line == null){
            return new TreeSet<>();
        }

        while(line != null) {
            String[] arr = splitStringToArray(line);
            PlayerTeam playerTeam = new PlayerTeam(arr[0], arr[1]);
            playerTeamSet.add(playerTeam);
            line = readLineFromAFile(bufferedReader);
        }
        return playerTeamSet;

    }

    public BufferedReader initialiseBufferReader(){
        try {
            return new BufferedReader((new FileReader(TEAM_FILE_NAME)));
        }catch(FileNotFoundException e){return null;}
    }

    public String readLineFromAFile(BufferedReader bufferedReader){
        try {
            return bufferedReader.readLine();
        }catch(IOException e){
            return null;
        }
    }

    public String[] splitStringToArray(String str){
        return str.split(",");
    }

}
