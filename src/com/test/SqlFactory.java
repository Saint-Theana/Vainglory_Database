package com.test;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.*;
import java.util.*;
import javax.net.ssl.*;
import org.json.*;
import java.sql.SQLException;
import java.sql.PreparedStatement;


public class SqlFactory
{
	Connection conn;

	private PreparedStatement ps=null;
	private String api_ip = Util.http_dns("api.dc01.gamelockerapp.com");
	private SqlListener listener;
	private Keystore keystore;
	
	
	
	
	
	public SqlFactory(Keystore _store,SqlListener _listener,Connection _conn){
		this.conn=_conn;
		this.conn=_conn;
		this.listener=_listener;
		this.keystore=_store;
	}

	public ResultSet executeQuery(String p0)
	{
		
		try
		{
			return conn.prepareStatement(p0).executeQuery();
		}
		catch (Exception e)
		{e.printStackTrace(); return null;}
	}
	
	
	
	public boolean MatcheRecorded(String id)
	{

		try
		{
			ResultSet rs  =conn.prepareStatement("SELECT _id FROM Matches where game_id = \"" + id + "\" LIMIT 1").executeQuery();
			if (rs.next())
			{
				rs.close();
				return true;
			}else{
				rs.close();
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

	}
	
	
	
	public void record(String id)
	{
		String data = Util.Auth_Curl(null,this.listener,"https://" + this.api_ip + "/shards/cn/matches/" + id, this.keystore.getkey());
		if(data==null){
			return;
		}
		List<ActorStore> actor_store_list = new ArrayList<ActorStore>();
		List<RosterStore> roster_store_list = new ArrayList<RosterStore>();
		String player_names = "";
		try
		{
			JSONObject root = new JSONObject(data);
			JSONObject root_data = root.getJSONObject("data");
			JSONObject root_data_attributes = root_data.getJSONObject("attributes");
			String root_data_attributes_createdAt = root_data_attributes.getString("createdAt");

			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df2.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = df2.parse(root_data_attributes_createdAt);
			long current_gametime = date.getTime();

			int root_data_attributes_duration = root_data_attributes.getInt("duration");
			String root_data_attributes_gameMode = root_data_attributes.getString("gameMode");
			JSONObject root_data_attributes_stats = root_data_attributes.getJSONObject("stats");
			String root_data_attributes_stats_endGameReason = root_data_attributes_stats.getString("endGameReason");
			JSONArray root_included = root.getJSONArray("included");
			for (int i = 0;i < root_included.length();i++)
			{
				JSONObject root_included$x = root_included.getJSONObject(i);
				String root_included$x_type = root_included$x.getString("type");
				if (root_included$x_type.equals("player"))
				{
					ActorStore store = new ActorStore();
					String root_included$x_id = root_included$x.getString("id");
					for (ActorStore store_back : actor_store_list)
					{
						if (store_back.id.equals(root_included$x_id))
						{
							store = store_back;
							actor_store_list.remove(store_back);
							break;
						}
					}
					store.id = root_included$x_id;
				    JSONObject root_included$x_attributes = root_included$x.getJSONObject("attributes");
					String root_included$x_attributes_name = root_included$x_attributes.getString("name");
					store.name = root_included$x_attributes_name;
					player_names += root_included$x_attributes_name + ",";
					this.record_player(root_included$x_id, root_included$x_attributes_name);
				    JSONObject root_included$x_attributes_stats= root_included$x_attributes.getJSONObject("stats");
					String root_included$x_attributes_stats_guildTag = root_included$x_attributes_stats.getString("guildTag");
					store.guild_tag = root_included$x_attributes_stats_guildTag;
			        JSONObject root_included$x_attributes_stats_rankPoints = root_included$x_attributes_stats.getJSONObject("rankPoints");
					int root_included$x_attributes_stats_rankPoints_blitz = root_included$x_attributes_stats_rankPoints.getInt("blitz");
					int root_included$x_attributes_stats_rankPoints_ranked = root_included$x_attributes_stats_rankPoints.getInt("ranked");
					int root_included$x_attributes_stats_rankPoints_ranked_5v5 = root_included$x_attributes_stats_rankPoints.getInt("ranked_5v5");
					store.ranked_points = root_included$x_attributes_stats_rankPoints_ranked;
					store.blitz_points = root_included$x_attributes_stats_rankPoints_blitz;
					store.ranked_5v5_points = root_included$x_attributes_stats_rankPoints_ranked_5v5;
					actor_store_list.add(store);
				}
				else if (root_included$x_type.equals("participant"))
				{
					
						
					
					ActorStore store = new ActorStore();
					JSONObject root_included$x_relationships = root_included$x.getJSONObject("relationships");
					JSONObject root_included$x_relationships_player = root_included$x_relationships.getJSONObject("player");
					JSONObject root_included$x_relationships_player_data = root_included$x_relationships_player.getJSONObject("data");
					String root_included$x_relationships_player_data_id="";
					if (root_included$x_relationships_player_data.getString("type").equals("player"))
					{
						root_included$x_relationships_player_data_id = root_included$x_relationships_player_data.getString("id");
					}
					else
					{
						System.out.println("fuck");
					}
					for (ActorStore store_back : actor_store_list)
					{
						if (store_back.id.equals(root_included$x_relationships_player_data_id))
						{
							store = store_back;
							actor_store_list.remove(store_back);
							break;
						}
					}
					store.id = root_included$x_relationships_player_data_id;
					JSONObject root_included$x_attributes = root_included$x.getJSONObject("attributes");
					String root_included$x_attributes_actor = root_included$x_attributes.getString("actor").replaceAll("\\*", "");
					if(!root_included$x_attributes_actor.equals("spectator")){
						store.hero = root_included$x_attributes_actor;
						JSONObject root_included$x_attributes_stats = root_included$x_attributes.getJSONObject("stats");
						int root_included$x_attributes_stats_assists = root_included$x_attributes_stats.getInt("assists");
						int root_included$x_attributes_stats_deaths = root_included$x_attributes_stats.getInt("deaths");
						int root_included$x_attributes_stats_kills = root_included$x_attributes_stats.getInt("kills");
						int root_included$x_attributes_stats_gold = root_included$x_attributes_stats.getInt("gold");
						int root_included$x_attributes_stats_minionKills = root_included$x_attributes_stats.getInt("minionKills");
						List<String> items = new ArrayList<String>();
						JSONArray root_included$x_attributes_stats_items =root_included$x_attributes_stats.getJSONArray("items");
						for (int b =0;b < root_included$x_attributes_stats_items.length();b++)
						{
							items.add(root_included$x_attributes_stats_items.getString(b));
						}
						boolean root_included$x_attributes_stats_wentAfk = root_included$x_attributes_stats.getBoolean("wentAfk");
						boolean root_included$x_attributes_stats_winner = root_included$x_attributes_stats.getBoolean("winner");
						store.assist = root_included$x_attributes_stats_assists;
						store.kills = root_included$x_attributes_stats_kills;
						store.deaths = root_included$x_attributes_stats_deaths;
						store.gold = root_included$x_attributes_stats_gold;
						store.minionKills = root_included$x_attributes_stats_minionKills;
						store.items = items;
						store.wentAfk = root_included$x_attributes_stats_wentAfk;
						store.winner = root_included$x_attributes_stats_winner;
						actor_store_list.add(store);
					}
				}
				else if (root_included$x_type.equals("roster"))
				{
					RosterStore store = new RosterStore();
					JSONObject root_included$x_attributes = root_included$x.getJSONObject("attributes");
					boolean root_included$x_attributes_won = root_included$x_attributes.getBoolean("won");
					store.won = root_included$x_attributes_won;
					JSONObject root_included$x_attributes_stats = root_included$x_attributes.getJSONObject("stats");
					int root_included$x_attributes_stats_gold = root_included$x_attributes_stats.getInt("gold");
					int root_included$x_attributes_stats_heroKills = root_included$x_attributes_stats.getInt("heroKills");
					String root_included$x_attributes_stats_side = root_included$x_attributes_stats.getString("side");
					int root_included$x_attributes_stats_krakenCaptures = root_included$x_attributes_stats.getInt("krakenCaptures");
					int root_included$x_attributes_stats_turretKills = root_included$x_attributes_stats.getInt("turretKills");
					int root_included$x_attributes_stats_turretsRemaining = root_included$x_attributes_stats.getInt("turretsRemaining");
					store.gold = root_included$x_attributes_stats_gold;
					store.heroKills = root_included$x_attributes_stats_heroKills;
					store.side = root_included$x_attributes_stats_side;
					store.krakenCaptures = root_included$x_attributes_stats_krakenCaptures;
					store.turretKills = root_included$x_attributes_stats_turretKills;
					store.turretsRemaining = root_included$x_attributes_stats_turretsRemaining;
					roster_store_list.add(store);
				}
			}
			JSONObject out_root = new JSONObject();
			out_root.put("id", id);
			out_root.put("createdAt", root_data_attributes_createdAt);
			Util.log(root_data_attributes_createdAt + " " + id + " " + player_names.replaceAll(",$", ""));
			out_root.put("duration", root_data_attributes_duration);
			out_root.put("gameMode", root_data_attributes_gameMode);
			out_root.put("endGameReason", root_data_attributes_stats_endGameReason);
			JSONArray out_root_actors = new JSONArray();
			JSONArray out_root_rosters = new JSONArray();

			for (ActorStore store_back : actor_store_list)
			{
				JSONObject out_root_actors$x = new JSONObject();
				out_root_actors$x.put("assists", store_back.assist);
				out_root_actors$x.put("blitz_points", store_back.blitz_points);
				out_root_actors$x.put("deaths", store_back.deaths);
				out_root_actors$x.put("gold", store_back.gold);
				out_root_actors$x.put("guild_tag", store_back.guild_tag);
				out_root_actors$x.put("hero", store_back.hero);
				out_root_actors$x.put("id", store_back.id);
				out_root_actors$x.put("items", store_back.items);
				out_root_actors$x.put("kills", store_back.kills);
				out_root_actors$x.put("minionKills", store_back.minionKills);
				out_root_actors$x.put("name", store_back.name);
				out_root_actors$x.put("ranked_5v5_points", store_back.ranked_5v5_points);
				out_root_actors$x.put("ranked_points", store_back.ranked_points);
			    out_root_actors$x.put("wentAfk", store_back.wentAfk);
				out_root_actors$x.put("winner", store_back.winner);
				out_root_actors.put(out_root_actors$x);
				String sql = "INSERT INTO Hero_Played (game_time,game_date,game_mode,player_name,hero_name) " + "VALUES (\"" + String.valueOf(current_gametime) + "\",\"" + root_data_attributes_createdAt + "\",\"" + root_data_attributes_gameMode + "\",\"" + store_back.name + "\",\"" + store_back.hero + "\");";
				this.listener.call(sql);
			}
			out_root.put("actors", out_root_actors);
			for (RosterStore store_back : roster_store_list)
			{
				JSONObject out_root_roster$x = new JSONObject();
				out_root_roster$x.put("gold", store_back.gold);
				out_root_roster$x.put("heroKills", store_back.heroKills);
				out_root_roster$x.put("krakenCaptures", store_back.krakenCaptures);
				out_root_roster$x.put("side", store_back.side);
				out_root_roster$x.put("turretKills", store_back.turretKills);
				out_root_roster$x.put("turretsRemaining", store_back.turretsRemaining);
				out_root_roster$x.put("won", store_back.won);
				out_root_rosters.put(out_root_roster$x);
			}
			out_root.put("rosters", out_root_rosters);





			/*开始存入数据库*/


			String sql = "INSERT INTO Matches (game_time,game_date,game_mode,game_id,player_names,data) " + "VALUES (\"" + String.valueOf(current_gametime) + "\",\"" + root_data_attributes_createdAt + "\",\"" + root_data_attributes_gameMode + "\",\"" + id + "\",\"" + player_names.replaceAll(",$", "") + "\",\"" + out_root.toString().replaceAll("\"", "'") + "\");";

			listener.call(sql);

		}
		catch (Exception e)
		{   
		    System.err.println(data);
			e.printStackTrace();
			return;
		}



	}
	public void record_player(String id,String name)
	{
		try
		{
			Statement stm = null;
			ResultSet rs = conn.prepareStatement("SELECT player_name FROM Players where BINARY player_id = \""+id+"\" LIMIT 1").executeQuery();
			if(rs.next()){
				if(!rs.getString("player_name").equals(name)){
					Util.log("更新玩家");
					String sql = "UPDATE Players set player_name = \""+name+"\" where player_id = \""+id+"\"";
				    this.listener.call(sql);
				}
			}else{
				Util.log("记录新玩家: "+name);
				String sql = "INSERT INTO Players (player_name,player_id) " + "VALUES (\""+name+"\",\""+id+"\");";
				this.listener.call(sql);
			}
		
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}
}
