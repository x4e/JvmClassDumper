package cookiedragon.dumper.tools;

import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.BasicType;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;
import sun.misc.Unsafe;

import java.io.DataInputStream;
import java.io.EOFException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author cookiedragon234 06/Apr/2020
 */
public class MemoryDumperTool extends Tool {
	@Override
	public void run() {
		ObjectHeap objectHeap = VM.getVM().getObjectHeap();
		
		long BYTE_BASE_OFFSET = TypeArray.baseOffsetInBytes(BasicType.T_BYTE);
		long BYTE_SIZE = objectHeap.getByteSize();
		
		objectHeap.iterate(new DefaultHeapVisitor() {
			@Override
			public boolean doObj(Oop oop) {
				if (oop.isTypeArray()) {
					TypeArray typeArray = (TypeArray)oop;
					TypeArrayKlass arrKlass = (TypeArrayKlass) typeArray.getKlass();
					int type = arrKlass.getElementType();
					
					if (type == TypeArrayKlass.T_BYTE) {
						int length = (int) typeArray.getLength();
						
						if (length < 4) return false;
						
						byte[] arr = new byte[length];
						for (int index = 0; index < length; index++) {
							long offset = BYTE_BASE_OFFSET + index * BYTE_SIZE;
							arr[index] = typeArray.getHandle().getJByteAt(offset);
							
							if (index == 3) {
								int magic = getMagic(arr);
								if (magic != -889275714) {
									//System.out.println("----bad magic " + magic);
									return false;
								} else {
									System.out.println("found good magic");
								}
							}
						}
						
						System.out.println("Found class " + getClassName(arr) + " with length " + length);
					}
				}
				return false;
			}
		});
	}
	
	private int getMagic(byte[] arr) {
		if (arr.length < 4) return 0;
		int ch1 = arr[0];
		int ch2 = arr[1];
		int ch3 = arr[2];
		int ch4 = arr[3];
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}
	
	private CustomCl customCl = new CustomCl();
	
	private String getClassName(byte[] bytes) {
		try {
			return customCl.define(bytes).getName();
		} catch (Throwable t) {
			t.printStackTrace();
			return new CustomCl().define(bytes).getName();
		}
	}
}

class CustomCl extends ClassLoader {
	public Class<?> define(byte[] bytes) {
		return defineClass(bytes, 0, bytes.length);
	}
}
