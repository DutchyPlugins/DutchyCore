package nl.thedutchmc.dutchycore.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import nl.thedutchmc.dutchycore.DutchyCore;

public class FileUtils {
	
	public static void saveResource(String name, String targetPath) {
		InputStream in = null;
		
		try {
			in = FileUtils.class.getResourceAsStream("/" + name);
			
			if(name == null) {
				throw new FileNotFoundException("Cannot find file " + name + " in jar!");
			}
			
			if(in == null) {
				throw new FileNotFoundException("Cannot find file " + name + " in jar!");
			}
			
			Path exportPath = Paths.get(targetPath + File.separator + name);
			Files.copy(in, exportPath);
		} catch (FileNotFoundException e) {
			DutchyCore.logWarn(String.format("A FileNotFoundException was thrown whilst trying to save %s.", name));
			DutchyCore.logWarn(Utils.getStackTrace(e));
		} catch (IOException e) {
			DutchyCore.logWarn(String.format("An IOException was thrown whilst trying to save %s.", name));
			DutchyCore.logWarn(Utils.getStackTrace(e));
		}
	}
	
	public static void saveStream(InputStream stream, File target) {
		Path targetPath = Paths.get(target.toURI());
		try {
			Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
