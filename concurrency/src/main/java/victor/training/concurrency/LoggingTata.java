package victor.training.concurrency;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingTata {
	
	public static void main(String[] args) {
		log.info("Start");
		log.debug("Cu un string: " + args);
		log.debug("Cu un string: {}", args);
//		if (log.isDebugEnabled()) {
//			log.debug("Procesez " + altaMetoda(args));
//		}
		log.debug("Procesez {}", altaMetoda(args));
		log.info("End");
		
	}

	private static String altaMetoda(String[] args) {
		ConcurrencyUtil.sleep2(2000);
		return Arrays.toString(args);
	}
}
