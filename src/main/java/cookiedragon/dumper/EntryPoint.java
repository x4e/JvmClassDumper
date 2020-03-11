package cookiedragon.dumper;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import cookiedragon.dumper.tools.ClassListTool;
import cookiedragon.dumper.tools.DumperTool;

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
		
		System.setProperty("sun.jvm.hotspot.runtime.VM.disableVersionCheck", "true");
		vms = VirtualMachine.list();
		
		if (args[0].equals("list")) {
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
		
		if (args[0].equals("dump")) {
			int id = Integer.parseInt(args[1]);
			if (args.length == 3) {
				classToDump = args[2];
			}
			VirtualMachineDescriptor vm = getVm(id);
			
			DumperTool agent = new DumperTool();
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
