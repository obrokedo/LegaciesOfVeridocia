package mb.fc.engine.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.newdawn.slick.util.DefaultLogSystem;

public class FileLogger extends DefaultLogSystem 
{
	@Override
	public void error(String message, Throwable e) {
		super.error(message, e);
		writeError(message);
		writeError("-------");
		error(e);
	}

	@Override
	public void error(Throwable e) {
		super.error(e);
		
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(
				    new File("ErrorLog"), 
				    true /* append = true */)); 
			e.printStackTrace(pw);
			pw.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}						
		writeError("-------");
	}

	@Override
	public void error(String message) {
		super.error(message);
		writeError(message);
		writeError("-------");
	}
	
	private void writeError(String message)
	{
		if (message == null)
			return;

		message += "\n";
		
		try {
			Files.write(FileSystems.getDefault().getPath(".", "ErrorLog"), 
					message.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
