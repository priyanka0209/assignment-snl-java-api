package com.qainfotech.tap.training.snl.api;


import static org.testng.Assert.assertEquals;

import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.qainfotech.tap.training.snl.api.Board;
import com.qainfotech.tap.training.snl.api.GameInProgressException;
import com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption;
import com.qainfotech.tap.training.snl.api.NoUserWithSuchUUIDException;
import com.qainfotech.tap.training.snl.api.PlayerExistsException;

public class BoardTest1 {

	Board board;
	
	@BeforeMethod
	public void createBoard() throws Exception{
		board= new Board();
		board.registerPlayer("Priyanka");
		board.registerPlayer("Rupali");
		
		
		
	}
	
	@Test(expectedExceptions=PlayerExistsException.class)
	public void aTestPlayerExistsException() throws Exception{
		board.registerPlayer("Priyanka");
		
	}
	
	@Test(expectedExceptions=MaxPlayersReachedExeption.class)
	public void bTestMaxPlayersReachedException() throws Exception{
		board.registerPlayer("3rdPlayer");
		board.registerPlayer("4thPlayer");
		board.registerPlayer("5thPlayer");	
	}
	
	@Test
	public void cTestInitialPosition() throws Exception{
		
		JSONArray player=new JSONArray();
		player=board.registerPlayer("ABC");
		Object pos=player.get(0);
		JSONObject jpos=(JSONObject) pos;
		assertEquals(jpos.getInt("position"),0);
		
	}
	
	
	@Test(expectedExceptions=GameInProgressException.class)
	public void dTestGameInProgress() throws Exception{
		
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(0).get("uuid");
		board.rollDice(uuid);
		board.registerPlayer("Priya");
	}
	
	
	@Test(expectedExceptions=InvalidTurnException.class)
	public void eTestInvalidTurnException() throws Exception{
		
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(1).get("uuid");
		board.rollDice(uuid);
	}
	
	@Test
	public void fTest_if_player_gets_deleted() throws Exception{
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(1).get("uuid");
		board.deletePlayer(uuid);
		assertThat(board.getData().getJSONArray("players").length()).isEqualTo(1);
	}
	
	@Test(expectedExceptions=NoUserWithSuchUUIDException.class)
	public void eTestNoUserWithSuchUUIDException() throws Exception{
		
		UUID uuid=UUID.randomUUID();
		board.deletePlayer(uuid);
	}
	
	@Test
	public void fTest_if_player_reaches_correct_position_after_rolling_dice() throws Exception{
		
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(0).get("uuid");
		Integer currentPos=(Integer)board.getData().getJSONArray("players").getJSONObject(0).get("position");
		Integer dice=(Integer) (board.rollDice(uuid)).get("dice");
		Integer newPos=(Integer)board.getData().getJSONArray("players").getJSONObject(0).get("position");
		int type=board.getData().getJSONArray("steps").getJSONObject(0).getInt("type");
		assertEquals(type,0);
		assertThat(currentPos+dice).isEqualTo(newPos);
	}
	
	@Test
	public void gPlayer_rolls_dice_afetr_reaching_100_InvalidTurn_for_same_player() throws Exception{
		
		(board.getData().getJSONArray("players").getJSONObject(0)).put("position", 100);
		Integer currentPos=(Integer)board.getData().getJSONArray("players").getJSONObject(0).get("position");
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(0).get("uuid");
		board.rollDice(uuid);
		Integer newPos=(Integer)board.getData().getJSONArray("players").getJSONObject(0).get("position");
		assertEquals(currentPos,newPos);
	}
	
	@Test(expectedExceptions=InvalidTurnException.class)
	public void gPlayer_rolls_dice_afetr_reaching_100_InvalidTurn_for_next_player() throws Exception{
		
		(board.getData().getJSONArray("players").getJSONObject(0)).put("position", 100);
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(1).get("uuid");
		board.rollDice(uuid);	
	}
		
	@Test
	public void hPosition_decreases_if_snake_is_encountered() throws Exception{
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(0).get("uuid");
		(board.getData().getJSONArray("players").getJSONObject(0)).put("position", 68);
		(board.rollDice(uuid)).put("dice",2);
		Integer newPos=(Integer)board.getData().getJSONArray("players").getJSONObject(0).get("position");
		assertThat(newPos<68);
		
	}
	
	@Test
	public void iPosition_increases_if_ladder_is_encountered() throws Exception{
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(0).get("uuid");
		(board.getData().getJSONArray("players").getJSONObject(0)).put("position", 66);
		(board.rollDice(uuid)).put("dice",2);
		Integer newPos=(Integer)board.getData().getJSONArray("players").getJSONObject(0).get("position");
		assertThat(newPos>66);
		
	}
	
	@Test
	public void jInvalidTurn_if_not_enough_steps_left() throws Exception{
		UUID uuid=(UUID)board.getData().getJSONArray("players").getJSONObject(0).get("uuid");
		(board.getData().getJSONArray("players").getJSONObject(0)).put("position", 96);
		(board.rollDice(uuid)).put("dice",5);
		Integer newPos=(Integer)board.getData().getJSONArray("players").getJSONObject(0).get("position");
		assertThat(newPos==96);
		
		
	}
		
}
