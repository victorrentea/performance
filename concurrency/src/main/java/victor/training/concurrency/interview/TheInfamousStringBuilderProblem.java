package victor.training.concurrency.interview;

import static java.util.stream.Collectors.toList;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TheInfamousStringBuilderProblem {
	public static void main(String[] args) throws IOException {

		String a = "a";
		String b = "b";

		String ab = a + b;
		System.out.println(ab);

		String a1 = a + 1;
		System.out.println(a1);

		;

		try (OutputStreamWriter writer = new OutputStreamWriter(System.out);
			Stream<String> lines = Files.lines(new File("data.txt").toPath())) {

			Iterable<String> interablePesteLinii = lines::iterator;
			infamous(interablePesteLinii, writer);
		}
	}

	private static void infamous(Iterable<String> list, Writer writer) throws IOException {
		writer.write("start X");
		for (String string : list) {
			writer.write(string + " \n");
		}
	}
}
