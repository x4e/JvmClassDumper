package cookiedragon.dumper.tools;

import cookiedragon.dumper.ClassDump;
import cookiedragon.dumper.EntryPoint;
import sun.jvm.hotspot.tools.Tool;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * @author cookiedragon234 11/Dec/2019
 */
public class DumperTool extends Tool {
	@Override
	public void run() {
		System.out.println("Dumping...");
		Instant dumpStart = Instant.now();
		try {
			ClassDump dumper = new ClassDump();
			dumper.setClassFilter(instanceKlass ->
			{
				for (String s : EntryPoint.classToDump.split(Pattern.quote(","))) {
					if (instanceKlass.getName().asString().startsWith(s))
						return true;
				}
				return false;
			});
			dumper.setJarOutput("dump.jar");
			dumper.run();
			Duration duration = Duration.between(dumpStart, Instant.now());
			System.out.println("Finished dumping in " + duration.toMillis() + "ms, " + duration.getSeconds() + "s");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
