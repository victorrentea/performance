package victor.training.concurrency.interviu;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.IntStream;

public class TheInfamousStringBuilderProblem {
	public static void main(String[] args) throws IOException {
		
		String a = "a"; // alocat in String Pool
		String b = "b";
		if (a == "a") {
			System.out.println("Cum adica true");
		}
		
		String c = a + b + a; //2 
		c += a; // inca unu
		
		
		
		
		List<String> list =
				IntStream.range(1, 1_000_000)
				.mapToObj(i -> i + "")
				.collect(toList());
				//;asList("a","b","c");
		
		
		// unde poti sa faci flush? Catre: disk, CLOB, httpServletResponse.getWriter()
		try (Writer writer = new FileWriter("a.txt")) {
			f(list, writer);
		}
		
//		try (BufferedReader r = new BufferedReader(FileReader("10GB.xml"))) {
//			while (r.read(buf) > 0)  {
//				writer.write(buf);
//			}
//		}
		// Varianta 2: de olimpiada: folosind NIO. channels
		
		
		System.out.println("Gata");
	}

	private static void f(List<String> list, Writer writer) throws IOException {
		writer.write("start X" + list.size());
		for (String string : list) {
			writer.write(string+"\n");
		}
	}
	private static String deAnalizat(List<String> list) {
		String s = "";
		for (String string : list) {
//			s  += string;
//			s =  s + string;
			s = new StringBuilder(s).append(string).toString();
		}
		return s;
	}
//	private static String f2(List<String> list) {
//		StringBuilder s = new StringBuilder();
//		for (String string : list) { // daca lista intre 100 - 100_000
//			s.append(string);
//		}
//		return s.toString();
//	}
}
