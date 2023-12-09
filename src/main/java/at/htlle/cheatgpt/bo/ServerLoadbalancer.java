package at.htlle.cheatgpt.bo;

import java.util.List;

import java.util.ArrayList;

public class ServerLoadbalancer implements Savant
{
	List<Savant> servers = new ArrayList<>();
	static int currentServer = 0;
	
	/**
	 * Delivers the next node in the cluster
	 * @return
	 */
	private Savant getNext() 
	{
		currentServer = (currentServer+1) % servers.size();
		return servers.get(currentServer);
	}

	public void add(Savant s) 
	{
		if(s != null)
		{
			this.servers.add(s);
		}
	}

	@Override
	public Answer ask(String question) {
		return getNext().ask(question);
	}

}
