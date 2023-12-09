package at.htlle.cheatgpt.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.htlle.cheatgpt.bo.Client;
import at.htlle.cheatgpt.bo.Client.ClientAnswer;
import at.htlle.cheatgpt.bo.Ollama;
import at.htlle.cheatgpt.bo.ServerLoadbalancer;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api")
public class HelloController 
{
	static List<Client> clients = new ArrayList<>();

	ServerLoadbalancer servantCluster = new ServerLoadbalancer();

	@Autowired
	Environment env;

	
	@PostConstruct
	public void init() 
	{
		
		int numServers = 1;
		String backendUrl = env.getProperty("ai.backend.urls." + (numServers++));
		while(backendUrl != null)
		{
			servantCluster.add(new Ollama(backendUrl, env));	
			backendUrl = env.getProperty("ai.backend.urls." + (numServers++));
		}

	}
	
	
	
    @GetMapping(path = "/ask")
    public ClientAnswer hello(HttpServletRequest request, @RequestParam(name="question") String question) 
    {
    	String clientIP = request.getRemoteAddr();
    	Optional<Client> actualClient = clients.stream()
    							.filter(c -> c.getIdentifier().equals(clientIP))
    							.findFirst();
   	
    	if(actualClient.isEmpty())
    	{
    		Client c = new Client(clientIP, Integer.parseInt(env.getProperty("client.balance.initial"))); 
    		clients.add(c);
    		
    		actualClient = Optional.of(c);
    	}
    	
        return actualClient.get().getAnswerForQuestion(question, servantCluster);
    }
}
