package victor.training.concurrency.interviu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class BoxingUnboxing {
	
	public static void main(String[] args) {
		List<Long> list =LongStream.range(1, 10_000_000).boxed().collect(toList());
		
		long t0 = System.currentTimeMillis();
		
		Long sumL = 0L;
//		long suml = 0L;
		for (Long i : list) {
			sumL = new Long(sumL.longValue() + i);
//			suml += i;
		}
		
		long t1 = System.currentTimeMillis();
		System.out.println(sumL);
		System.out.println(t1-t0);
	}

}
