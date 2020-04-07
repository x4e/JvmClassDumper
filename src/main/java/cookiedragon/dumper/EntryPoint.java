package cookiedragon.dumper;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import cookiedragon.dumper.tools.ClassListTool;
import cookiedragon.dumper.tools.DumperTool;
import cookiedragon.dumper.tools.MemoryDumperTool;
import sun.jvm.hotspot.tools.Tool;

import java.util.List;

/**
 * @author cookiedragon234 11/Dec/2019
 */
public class EntryPoint {
	public static String classToDump = "";
	private static List<VirtualMachineDescriptor> vms;
	
	public static void main(String[] args) {
		if (args.length <= 0) {
			throw new RuntimeException("Bad num args");
		}
		
		try {
			Class.forName("sun.tools.attach.WindowsAttachProvider");
		} catch (Throwable t) {
			throw new IllegalStateException("Invalid class path", t);
		}
		
		System.setProperty("sun.jvm.hotspot.runtime.VM.disableVersionCheck", "true");
		vms = VirtualMachine.list();
		
		String arg0 = args[0];
		
		if (arg0.equals("list")) {
			if (args[1].startsWith("vm")) {
				vms.stream()
					.map(VirtualMachineDescriptor::toString)
					.forEach(System.out::println);
				
				return;
			}
			if (args[1].equals("classes")) {
				int id = Integer.parseInt(args[2]);
				VirtualMachineDescriptor vm = getVm(id);
				
				ClassListTool agent = new ClassListTool();
				Utils.executeJavaAgent(agent, vm);
				return;
			}
		}
		
		if (arg0.equals("dump") || arg0.equals("memorydump")) {
			int id = Integer.parseInt(args[1]);
			if (args.length == 3) {
				classToDump = args[2];
			}
			VirtualMachineDescriptor vm = getVm(id);
			
			Tool agent;
			if (arg0.equals("dump")) {
				agent = new DumperTool();
			} else if (arg0.equals("memorydump")) {
				agent = new MemoryDumperTool();
			} else {
				throw new IllegalStateException(args[0]);
			}
			Utils.executeJavaAgent(agent, vm);
			return;
		}
		
		System.out.println("You shouldnt be here...");
	}
	
	private static VirtualMachineDescriptor getVm(int pid) {
		VirtualMachineDescriptor vm =
			vms.stream()
				.filter(thisVM -> thisVM.id().equals(Integer.toString(pid)))
				.findFirst().orElse(null);
		
		if (vm == null)
			throw new RuntimeException("Couldnt find vm with pid " + pid);
		
		return vm;
	}
}
