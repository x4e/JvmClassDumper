package cookiedragon.dumper.tools;

import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.BasicType;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

import java.io.DataInputStream;
import java.io.EOFException;

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
						byte[] arr = new byte[length];
						for (int index = 0; index < length; index++) {
							long offset = BYTE_BASE_OFFSET + index * BYTE_SIZE;
							arr[index] = typeArray.getHandle().getJByteAt(offset);
						}
						
						if (matchesMagic(arr)) {
							System.out.println("Found class arr " + length);
						} else {
							System.out.println("Invalid class ");
						}
					}
				}
				return false;
			}
		});
	}
	
	public boolean matchesMagic(byte[] arr) {
		if (arr.length < 4) return false;
		
		int ch1 = arr[0];
		int ch2 = arr[1];
		int ch3 = arr[2];
		int ch4 = arr[3];
		int magic = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
		
		//return magic == -889275714;
		
		if (magic == -889275714) {
			System.out.println("Found class arr " + arr.length);
			return true;
		} else {
			System.out.println("Non magic " + magic + " length " + arr.length);
			return false;
		}
	}
}
