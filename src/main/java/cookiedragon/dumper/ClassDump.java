package cookiedragon.dumper;

import sun.jvm.hotspot.debugger.AddressException;
import sun.jvm.hotspot.debugger.JVMDebugger;
import sun.jvm.hotspot.memory.SystemDictionary;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;
import sun.jvm.hotspot.tools.jcore.ClassFilter;
import sun.jvm.hotspot.tools.jcore.ClassWriter;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * @author cookiedragon234 11/Dec/2019
 */

public class ClassDump extends Tool {
	private ClassFilter classFilter;
	private String outputDirectory;
	private JarOutputStream jarStream;
	private String pkgList;
	
	public ClassDump() {
	}
	
	public ClassDump(JVMDebugger d, String pkgList) {
		super(d);
		this.pkgList = pkgList;
	}
	
	public void setClassFilter(ClassFilter cf) {
		this.classFilter = cf;
	}
	
	public void setOutputDirectory(String od) {
		this.outputDirectory = od;
		if (this.jarStream != null) {
			try {
				this.jarStream.close();
			}
			catch (IOException var3) {
				var3.printStackTrace();
			}
		}
		
		this.jarStream = null;
	}
	
	public void setJarOutput(String jarFileName) throws IOException {
		this.jarStream = new JarOutputStream(new FileOutputStream(jarFileName), new Manifest());
		this.outputDirectory = null;
	}
	
	public void run() {
		try {
			String dirName;
			if (this.classFilter == null) {
				dirName = System.getProperty("sun.jvm.hotspot.tools.jcore.filter", "sun.jvm.hotspot.tools.jcore.PackageNameFilter");
				
				try {
					Class filterClass = Class.forName(dirName);
					if (this.pkgList == null) {
						this.classFilter = (ClassFilter) filterClass.newInstance();
					}
					else {
						Constructor con = filterClass.getConstructor(String.class);
						this.classFilter = (ClassFilter) con.newInstance(this.pkgList);
					}
				}
				catch (Exception var5) {
					System.err.println("Warning: Can not create class filter!");
				}
			}
			
			if (this.outputDirectory == null && this.jarStream == null) {
				dirName = System.getProperty("sun.jvm.hotspot.tools.jcore.outputDir", ".");
				this.setOutputDirectory(dirName);
			}
			
			SystemDictionary dict = VM.getVM().getSystemDictionary();
			dict.classesDo(k ->
			{
				if (k instanceof InstanceKlass) {
					try {
						dumpKlass((InstanceKlass) k);
					}
					catch (Exception var3) {
						System.out.println(k.getName().asString());
						var3.printStackTrace();
					}
				}
			});
		}
		catch (AddressException var6) {
			System.err.println("Error accessing address 0x" + Long.toHexString(var6.getAddress()));
			var6.printStackTrace();
		}
		
		if (this.jarStream != null) {
			try {
				this.jarStream.close();
			}
			catch (IOException var4) {
				var4.printStackTrace();
			}
			
			this.jarStream = null;
		}
		
	}
	
	public String getName() {
		return "jcore";
	}
	
	private void dumpKlass(InstanceKlass kls) {
		if (this.classFilter == null || this.classFilter.canInclude(kls)) {
			String klassName = kls.getName().asString();
			
			try {
				OutputStream os = null;
				if (this.jarStream != null) {
					this.jarStream.putNextEntry(new JarEntry(klassName.replace('\\', '/') + ".class"));
					os = this.jarStream;
				}
				else {
					int index = klassName.lastIndexOf('/');
					File dir = null;
					if (index != -1) {
						String dirName = klassName.substring(0, index);
						dir = new File(this.outputDirectory, dirName);
					}
					else {
						dir = new File(this.outputDirectory);
					}
					
					dir.mkdirs();
					File f = new File(dir, klassName.substring(index + 1) + ".class");
					f.createNewFile();
					os = new BufferedOutputStream(new FileOutputStream(f));
				}
				
				try {
					ClassWriter cw = new ClassWriter(kls, os);
					cw.write();
				}
				catch (Throwable e) {
					System.out.println("Error while dumping " + klassName);
					e.printStackTrace();
				}
				finally {
					if (os != this.jarStream) {
						os.close();
					}
					
				}
			}
			catch (IOException var11) {
				var11.printStackTrace();
			}
			
		}
	}
}

