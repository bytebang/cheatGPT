package at.htlle.cheatgpt.bo;

import org.springframework.stereotype.Component;

import at.htlle.cheatgpt.bo.Savant.Answer;

/**
 * Represents a single client of the applicaiton
 * @author Gü
 *
 */
@Component
public class Client 
{
	// könnte die IP oder so werden
	String identifier = null;
	
	// Users balance
	Integer balance = 5000;
	
	public record ClientAnswer(Answer answer, Integer remeaningTokens) {}
	
	// für Spring
	public Client() {}
	
	/**
	 * ctor
	 * @param savant
	 */
	public Client(String identifier, Integer initialBalance)
	{
		this.identifier = identifier;
		this.balance = initialBalance;
	}
	
	
	public ClientAnswer getAnswerForQuestion(String question, Savant informationSource)
	{
		if(balance > 0)
		{
			Answer a = informationSource.ask(question);
			this.balance = this.balance - a.tokensUsed();
			if(this.balance < 0)
			{
				this.balance = 0;
			}
			
			return new ClientAnswer(a, this.balance);
		}
		
		return new ClientAnswer(new Answer("I can not answer this question because you ran out of tokens.",0) ,0);
	}


	public String getIdentifier() {
		return identifier;
	}
	
	
}
