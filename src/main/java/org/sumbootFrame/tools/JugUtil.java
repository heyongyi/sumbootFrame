package org.sumbootFrame.tools;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.util.UUID;

public class JugUtil {
	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",  
        "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",  
        "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",  
        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",  
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",  
        "W", "X", "Y", "Z" };  
	
	public static UUID getRawUuid() {
		  EthernetAddress nic = EthernetAddress.fromInterface();
		  // or bogus which would be gotten with: EthernetAddress.constructMulticastAddress()
		  TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(nic);
		  // also: we don't specify synchronizer, getting an intra-JVM syncer; there is
		  // also external file-locking-based synchronizer if multiple JVMs run JUG
		return uuidGenerator.generate();
	}	
	public static String getLongUuid() {		  
		return getRawUuid().toString().replace("-", "");
	}
	
	/*本算法利用62个可打印字符，通过随机生成32位UUID，由于UUID都为十六进制，
	 *所以将UUID分成8组，每4个为一组，然后通过模62操作，结果作为索引取出字符，*/
	public static String getShortUuid() {  
	    StringBuffer shortBuffer = new StringBuffer();
	    String uuid = getLongUuid();
	    for (int i = 0; i < 8; i++) {  
	        String str = uuid.substring(i * 4, i * 4 + 4);  
	        int x = Integer.parseInt(str, 16);  
	        shortBuffer.append(chars[x % 0x3E]);  
	    }
	    return shortBuffer.toString();  
	}  
}
