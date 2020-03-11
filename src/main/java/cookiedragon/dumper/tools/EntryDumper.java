package cookiedragon.dumper.tools;

import sun.management.Agent;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.zip.ZipOutputStream;

/**
 * @author cookiedragon234 28/Dec/2019
 */
public class EntryDumper extends Agent {
	public static void premain(String args, Instrumentation instrumentation) {
		ClassLogger transformer = new ClassLogger();
		instrumentation.addTransformer(transformer);
	}
	
	public static class ClassLogger implements ClassFileTransformer {
		private static final File dumpFile = new File("dump.jar");
		private static ZipOutputStream out;
		
		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
			try {
				String sane = className.replace('.', '/').replaceAll("[^a-zA-Z0-9./_]+", "_");
				File file = new File("dump/" + sane + ".class");
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				System.out.println("DUMP: " + className + " " + file.getAbsolutePath());
				fileOutputStream.write(classfileBuffer);
				fileOutputStream.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			return classfileBuffer;
		}
	}
	
}
