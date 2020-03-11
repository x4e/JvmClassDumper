package cookiedragon.dumper.tools;

import sun.jvm.hotspot.tools.Tool;

/**
 * @author cookiedragon234 11/Dec/2019
 */
public class ClassListTool extends Tool {
	private static final String[] filters = new String[]{
		"net/minecraft/",
		"io/",
		"com/google/",
		"sun/",
		"javax/",
		"java/",
		"it/unimi/",
		"org/apache/",
		"org/spongepowered/",
		"yalter/mousetweaks/",
		"baritone/",
		"net/optifine/",
		"org/objectweb/",
		"net/minecraftforge/",
		"joptsimple/",
		"com/sun/",
		"org/lwjgl/",
		"com/mojang/",
		"com/jcraft/",
		"optifine/",
		"oshi/",
		"paulscode/",
		"jdk/",
		"org/w3c",
		"LZMA/",
		"org/json/",
		"$wrapper/",
		"org/xml/",
		"mcp/",
		"scala/",
		"org/jline/"
	};
	
	@Override
	public void run() {
		System.out.println("Listing...");
		sun.jvm.hotspot.runtime.VM.getVM().getSystemDictionary().classesDo(klass ->
		{
			String className = klass.getName().asString();
			
			boolean excluded = false;
			for (String filter : filters) {
				if (className.startsWith(filter)) {
					excluded = true;
					break;
				}
			}
			
			if (!excluded) {
				System.out.println(className);
			}
		});
	}
}

