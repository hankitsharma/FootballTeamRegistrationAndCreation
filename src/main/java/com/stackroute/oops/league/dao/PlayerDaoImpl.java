package com.stackroute.oops.league.dao;

import com.stackroute.oops.league.exception.PlayerAlreadyExistsException;
import com.stackroute.oops.league.exception.PlayerNotFoundException;
import com.stackroute.oops.league.model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This class is implementing the PlayerDao interface
 * This class has one field playerList and a String constant for storing file name
 */
public class PlayerDaoImpl implements PlayerDao {
    private static final String PLAYER_FILE_NAME = "src/main/resources/player.csv";
    private List<Player> playerList;

    /**
     * Constructor to initialize an empty ArrayList for playerList
     */
    public PlayerDaoImpl() {
        playerList = new LinkedList<>();
    }

    /**
     * Return true if  player object is stored in "player.csv" as comma separated fields successfully
     * when password length is greater than six and yearExpr is greater than zero
     */
    @Override
    public boolean addPlayer(Player player) throws PlayerAlreadyExistsException {
        if (player.getPassword().length() > 6 && player.getYearExpr() > 0) {
            return AddLineToAFile(player);
        }
        return false;
    }

    public Boolean AddLineToAFile(Player player){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter((new FileWriter(PLAYER_FILE_NAME)));
            bufferedWriter.write(player.getPlayerId()+","+player.getPlayerName()+","+player.getPassword()+","+player.getYearExpr()+","+player.getTeamTitle());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            return true;
        }catch(IOException e){
            return false;
        }
    }

    public BufferedReader initialiseBufferReader(){
        try {
            return new BufferedReader((new FileReader(PLAYER_FILE_NAME)));
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



    //Return the list of player objects by reading data from the file "player.csv"
    @Override
    public List<Player> getAllPlayers() {
        BufferedReader bufferedReader= initialiseBufferReader();
        String line = readLineFromAFile(bufferedReader);
        System.out.println(line);

        if(line == null){
            return new ArrayList<>();
        }

        while(line != null) {
            String[] arr = splitStringToArray(line);
            Player player = new Player(arr[0], arr[1], arr[2], Integer.parseInt(arr[3]));
            playerList.add(player);
            line = readLineFromAFile(bufferedReader);
        }
        return playerList;
    }

    /**
     * Return Player object given playerId to search
     */
    @Override
    public Player findPlayer(String playerId) throws PlayerNotFoundException {
        List<Player> players = getAllPlayers();

        if(players == null || players.isEmpty()){
            throw new PlayerNotFoundException();
        }

        Optional<Player> optional = IfPlayerExistReturnPlayerElseReturnEmpty(players, playerId);

        if(optional.isPresent()){
            return optional.get();
        }

        throw new PlayerNotFoundException();
    }

    public Optional<Player> IfPlayerExistReturnPlayerElseReturnEmpty(List<Player> playersList, String playerId){
        return playersList.stream()
                .filter(player -> player.getPlayerId().equalsIgnoreCase(playerId))
                .findAny();
    }

}
