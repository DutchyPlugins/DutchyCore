package nl.thedutchmc.dutchycore.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;

import org.bukkit.ChatColor;

import nl.thedutchmc.dutchycore.Triple;

public class Utils {
	
	/**
	 * Get the stacktrace from a Throwable
	 * @param t Throwable to get the stacktrace from
	 * @return Returns the stacktrace as a String
	 */
	public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return sw.getBuffer().toString();
	}
	
    /**
     * Verify if a String is a positive number, less than Integer.MAX_VALUE
     * @param input The input to verify
     * @return Returns true if the provided String is a valid positive Integer
     */
    public static boolean verifyPositiveInteger(String input) {
		if(input.matches("-?\\d+")) {
			
			BigInteger bigInt = new BigInteger(input);
			if(bigInt.compareTo(BigInteger.valueOf((long) Integer.MAX_VALUE)) > 0) {
				return false;
			}
			
			if(Integer.valueOf(input) <= 0) {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
    }
    
    /**
     * Add colors in a String
     * @param input The input to process
     * @param colors A = The string to replace, B = The color it should be, C = The color the text after A should be
     * @return Returns a processed String
     */
    @SafeVarargs
	public static String processColours(String input, Triple<String, ChatColor, ChatColor>... colors) {
    	String processed = input;
    	for(Triple<String, ChatColor, ChatColor> color : colors) {
    		processed = processed.replace(color.getA(), color.getB() + color.getA() + color.getC());
    	}
    	
    	return processed;
    }
}
