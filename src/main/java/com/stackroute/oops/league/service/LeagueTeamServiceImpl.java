package com.stackroute.oops.league.service;

import com.stackroute.oops.league.dao.PlayerDao;
import com.stackroute.oops.league.dao.PlayerDaoImpl;
import com.stackroute.oops.league.dao.PlayerTeamDao;
import com.stackroute.oops.league.dao.PlayerTeamDaoImpl;
import com.stackroute.oops.league.exception.PlayerAlreadyAllottedException;
import com.stackroute.oops.league.exception.PlayerAlreadyExistsException;
import com.stackroute.oops.league.exception.PlayerNotFoundException;
import com.stackroute.oops.league.exception.TeamAlreadyFormedException;
import com.stackroute.oops.league.model.Player;
import com.stackroute.oops.league.model.PlayerTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This class implements leagueTeamService
 * This has four fields: playerDao, playerTeamDao and registeredPlayerList and playerTeamSet
 */
public class LeagueTeamServiceImpl implements LeagueTeamService {

    PlayerDao playerDao;
    PlayerTeamDao playerTeamDao;
    List<Player> registeredPlayerList;
    Set<PlayerTeam> playerTeamSet;

    /**
     * Constructor to initialize playerDao, playerTeamDao
     * empty ArrayList for registeredPlayerList and empty TreeSet for playerTeamSet
     */
    public LeagueTeamServiceImpl() {
        playerDao = new PlayerDaoImpl();
        playerTeamDao = new PlayerTeamDaoImpl();
        registeredPlayerList = new ArrayList<>();
        playerTeamSet = new TreeSet<>();
    }

    //Add player data to file using PlayerDao object
    @Override
    public boolean addPlayer(Player player) throws PlayerAlreadyExistsException {
        return playerDao.addPlayer(player);
    }

    /**
     * Register the player for the given teamTitle
     * Throws PlayerNotFoundException if the player does not exists
     * Throws PlayerAlreadyAllottedException if the player is already allotted to team
     * Throws TeamAlreadyFormedException if the maximum number of players has reached for the given teamTitle
     * Returns null if there no players available in the file "player.csv"
     * Returns "Registered" for successful registration
     * Returns "Invalid credentials" when player credentials are wrong
     */
    @Override
    public synchronized String registerPlayerToLeague(String playerId, String password, LeagueTeamTitles teamTitle)
            throws PlayerNotFoundException, TeamAlreadyFormedException, PlayerAlreadyAllottedException {
        if(checkIffileIsEmpty()){
            return null;
        }
        playerDao.findPlayer(playerId);
        if(checkIfPlayerAlreadyAllotedTeamTitle(playerId,teamTitle)){
            throw new PlayerAlreadyAllottedException();
        }
        if(checkIfMaxNumberOfPlayersHaveAlreadyBeenAllotedToTheTeam(teamTitle)) {
            throw new TeamAlreadyFormedException();
        }

        if(registerPlayerWithGivenTeamTitle(playerId, password, teamTitle)){
            return "Registered";
        }
        return "Invalid credentials";

    }

    public boolean searchPlayerInList(String playerId){
        List<Player> playerList= playerDao.getAllPlayers();
        return playerList.stream()
                .filter(player ->player.getPlayerId().equals(playerId))
                .findAny()
                .isPresent();
    }

    public boolean registerPlayerWithGivenTeamTitle(String playerId, String password, LeagueTeamTitles teamTitles){
        Player player = playerDao.findPlayer(playerId);
            if(player.getPlayerId().equals(playerId) && player.getPassword().equals(password)){
                player.setTeamTitle(teamTitles.name());
                return true;
            }

        return false;
    }

    public boolean checkIffileIsEmpty(){
        List<Player> playerList = playerDao.getAllPlayers();
        if(playerList.isEmpty()){
            return true;
        }
        return false;
    }

    public boolean checkIfPlayerAlreadyAllotedTeamTitle(String playerId, LeagueTeamTitles teamTitles){
        Set<PlayerTeam> playerTeamSet = playerTeamDao.getAllPlayerTeams();
        if(playerTeamSet.isEmpty()){
            return false;
        }
       for(PlayerTeam playerTeam : playerTeamSet){
           if(playerTeam.getPlayerId().equals(playerId)){
               return true;
           }
       }
        return false;
    }

    public boolean checkIfMaxNumberOfPlayersHaveAlreadyBeenAllotedToTheTeam(LeagueTeamTitles teamTitles){
        List<Player> playerList= playerDao.getAllPlayers();
        int count = 0;
        for(Player player : playerList){
            if(player.getTeamTitle()== null){
                continue;
            }else if(player.getTeamTitle().equalsIgnoreCase(teamTitles.name())){
                count = count + 1;
            }
        }

        if(count<=11){
            return false;
        }
        return true;
    }

    /**
     * Return the list of all registered players
     */
    @Override
    public List<Player> getAllRegisteredPlayers() {
         registeredPlayerList = playerDao.getAllPlayers();
         return registeredPlayerList.stream()
                 .filter(player -> player.getTeamTitle() != null)
                 .collect(Collectors.toList());
    }

    /**
     * Return the existing number of players for the given title
     */
    @Override
    public int getExistingNumberOfPlayersInTeam(LeagueTeamTitles teamTitle) {
        registeredPlayerList = getAllRegisteredPlayers();
        int count = 0;
        if (registeredPlayerList.isEmpty()) {
            return 0;
        }
            else {
                for(Player player : registeredPlayerList){
                    if(player.getTeamTitle().equalsIgnoreCase(teamTitle.name())){count = count + 1;}
                }
        }
            return count;
    }

    /**
     * Admin credentials are authenticated and registered players are allotted to requested teams if available
     * If the requested teams are already formed,admin randomly allocates to other available teams
     * PlayerTeam object is added to "finalteam.csv" file allotted by the admin using PlayerTeamDao
     * Return "No player is registered" when registeredPlayerList is empty
     * Throw TeamAlreadyFormedException when maximum number is reached for all teams
     * Return "Players allotted to teams" when registered players are successfully allotted
     * Return "Invalid credentials for admin" when admin credentials are wrong
     */
    @Override
    public String allotPlayersToTeam(String adminName, String password, LeagueTeamTitles teamTitle)
            throws TeamAlreadyFormedException, PlayerNotFoundException {
        registeredPlayerList = getAllRegisteredPlayers();
        if(checkTheCredentials(adminName,password)){
            if(registeredPlayerList.isEmpty()){
                return "No player is registered";
            }
            for(Player player : registeredPlayerList){
                boolean result = assignRandonteamIFPreviousFull(teamTitle,player);
                if(!result){
                    throw new TeamAlreadyFormedException();
                }
            }
            return "Players allotted to teams";
        }
        return "Invalid credentials for admin";
    }

    public boolean checkTheCredentials(String adminName, String password){
        return AdminCredentials.admin.equals(adminName) && AdminCredentials.password.equals(password)?true:false;
    }

    public boolean checkIfTeamwITHtEAMtITLEIsAlreadyFull(LeagueTeamTitles teamTitle){
        List<Player> playerList = getAllRegisteredPlayers();
        int count = getExistingNumberOfPlayersInTeam(teamTitle);
        if(count<=11){
            return false;
        }
        return true;
    }

    public boolean assignRandonteamIFPreviousFull(LeagueTeamTitles teamTitle, Player player){
            if(checkIfTeamwITHtEAMtITLEIsAlreadyFull(teamTitle)){
                for(LeagueTeamTitles leagueTeamTitles : LeagueTeamTitles.values()) {
                    if (!checkIfTeamwITHtEAMtITLEIsAlreadyFull(leagueTeamTitles)) {
                        player.setTeamTitle(teamTitle.name());
                        return playerTeamDao.addPlayerToTeam(player);
                    }
                }
            }else{
                    player.setTeamTitle(teamTitle.name());
                    return  playerTeamDao.addPlayerToTeam(player);
            }
            return false;
    }
    /**
     * static nested class to initialize admin credentials
     * admin name='admin' and password='pass'
     */
    static class AdminCredentials {
        private static String admin = "admin";
        private static String password = "pass";
    }
}
