package at.htlle.cheatgpt.bo;


/**
 * Jemand der alles weiß
 * @author Gü
 *
 */
public interface Savant 
{
	
	// Answer how many tokens the answer generation took, and the answer itself
	public record Answer(String answer, Integer tokensUsed) {}
	
	/**
	 * Ask a question in natural language, and gat an answer for it
	 * @param question
	 * @return
	 */
	Answer ask(String question);
	
}
