package cookiedragon.dumper;

import com.sun.tools.attach.VirtualMachineDescriptor;
import sun.jvm.hotspot.tools.Tool;

import java.lang.reflect.Method;

/**
 * @author cookiedragon234 11/Dec/2019
 */
class Utils {
	static void executeJavaAgent(Tool agent, VirtualMachineDescriptor vm) {
		try {
			Method m = Tool.class.getDeclaredMethod("start", String[].class);
			m.setAccessible(true);
			m.invoke(agent, (Object) new String[]{vm.id()});
		}
		catch (Exception e) {
			throw new RuntimeException("Error starting dumper", e);
		}
		finally {
			agent.stop();
		}
	}
}
