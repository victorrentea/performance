import org.apache.commons.lang.NotImplementedException;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Stream;

public class TheEternalBuilderProblem {
	public static void main(String[] args) {
		String a = "a";
		String b = "b";
		
		
		String ab = a + b;
		
		System.out.println(ab);
		
		String ab2 = "a" + "b"; // faster :P
		String ab3 = new StringBuilder()
				.append(a)
				.append(b)
				.toString();

	}
	
	String concatenateAll(List<Integer> numbers) {
		throw new NotImplementedException(); // TODO
	}


	// TODO: a smarter solution ?
}
