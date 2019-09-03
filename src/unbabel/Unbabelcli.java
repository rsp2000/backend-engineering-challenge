package unbabel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class Unbabelcli {
	
	private static SortedMap<Calendar, Transdata> mapTrans  = new TreeMap<Calendar, Transdata>();
	private static final Logger LOGGER = Logger.getLogger(Unbabelcli.class.getName());
	
	public static void main(String[] args) {

		String sArqJSON = null;
		int    iWindow = 0;
		int    iCont = 0;
		

		// Parsing arguments
		try {
	        while (iCont < args.length) {

	            String arg = args[iCont].toLowerCase().trim();

	            if ("--input_file".equals(arg)) {
	            	
	            	if(iCont < args.length-1)
	            	{
	            		sArqJSON = args[++iCont];
	            	}
	            	
	            } else if ("--window_size".equals(arg)) {
	            	
	            	if(iCont < args.length-1)
	            	{
	            		iWindow = Integer.parseInt(args[++iCont]);
	            	}
	 
	            } else {
	                LOGGER.warning("Wrong parameter [" + arg + "]. Ignored !");
	            }
	            
	            iCont++;
	        }
		} catch (Exception e) {
			Showsyntax();
        	LOGGER.log(Level.SEVERE,"Sintax error");
        	System.exit(1);
		}
		
		// Main Loop
        if((sArqJSON != null) && (sArqJSON.length() > 0) && (iWindow > 0))
        {	        
			float fAvg;
			Calendar calActual = Calendar.getInstance();		
			int   iWindowCount = 0;
			int   iNumLoaded;
			
			// Adjust the Window beginning time
			calActual.add(Calendar.MINUTE, -iWindow);
			calActual.set(Calendar.SECOND, 0);
			calActual.set(Calendar.MILLISECOND, 0);			
			
			iNumLoaded = loadFile(sArqJSON);
						
			if(iNumLoaded >= 0)
			{
				try {
					BufferedWriter bufwriter = new BufferedWriter( new FileWriter("average.json"));
					
					while (iWindowCount++ < iWindow)
					{						
						// Search for date in TreeMap
						Transdata tdsavedtranslation = mapTrans.get(calActual);
						if( tdsavedtranslation != null )
							fAvg = tdsavedtranslation.getavg();
						else
							fAvg = 0;	
						
						// Save to Output file
						SimpleDateFormat sfaux2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String sOutDate = sfaux2.format(calActual.getTime());
						bufwriter.write("{\"date\": \"" + sOutDate + "\", \"average_delivery_time\": " + fmt(fAvg) + "}");
						bufwriter.newLine();						
					
						// Next minute
						calActual.add(Calendar.MINUTE, 1);
					}
					bufwriter.flush();
					bufwriter.close();
					
					System.out.println("Done! Results on file: average.json");

				} catch (IOException e) {
					LOGGER.log(Level.SEVERE,"Error: Openning/Writing the output file: average.json");					
					
				}
			}
						
        }
        else
        {
        	Showsyntax();
        	System.exit(1);
        }
		
	}
	
	/*
	 * Showsyntax: Show the right syntax for use
	 */
	static void Showsyntax() {
		LOGGER.info("Syntax: unbabel_cli --input_file <file pathname> --window_size <window size in minutes>");		
	}
	
	/*
	 * fmt: Format the float number
	 * Receive: Float number
	 * Returns: String with the number formatted
	 */
	private static String fmt(double d) {
		if (d == Math.floor(d)) {
		    return String.format("%.0f", d);
		} else {
		    return Double.toString(d);
		}
	}
	
	/*
	 * loadFile: Load the input json file with stats of translations
	 *           Inserts the read data into a SortedMap tree (sorted by Timestamp) with the durations already summed for each minute
	 * Receive: JSON file pathname
	 * Returns: Number of line read OR error code 
	 */
	private static int loadFile(String sArqJSON) {
		int iLineCount = 0;
		int iduration = 0;
		Date ddate = null;
		
		try {
			BufferedReader bufreader = new BufferedReader(new FileReader( sArqJSON ));
			
			String line = bufreader.readLine();
			while ( line != null ) 
			{

					iLineCount++;
					
					// Convert JSON
					
					JSONObject joTranslation;
					String stimestamp;
					SimpleDateFormat sfaux = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						joTranslation = new JSONObject(line);
						stimestamp = joTranslation.getString("timestamp");
						try {
							ddate = sfaux.parse(stimestamp);
						} catch (ParseException e) {
							LOGGER.log(Level.SEVERE,"Error: Parsing [" + stimestamp + "] failed");
							iLineCount = -1;
							break;
						}
						iduration = joTranslation.getInt("duration");
					} catch (JSONException e1) {
						LOGGER.log(Level.SEVERE,"Error: Parsing failed");
						iLineCount = -2;
						break;
					}
					
					// Search the key
					Calendar cal = Calendar.getInstance(); 
					cal.setTime(ddate);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					
					Transdata tdsavedtranslation = mapTrans.get(cal);
					if( tdsavedtranslation != null )
					{ // update the key
						tdsavedtranslation.addtrans(iduration);
						mapTrans.put(cal, tdsavedtranslation);
					}
					else
					{
						// insert new
						Transdata tdtranslation = new Transdata(cal, iduration);
						mapTrans.put(cal, tdtranslation);
					}
					
					
					// read next line
					line = bufreader.readLine();
			}
			bufreader.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,"Error: Openning/Reading the file:" + sArqJSON);
			iLineCount = -3;
		}		
					
		return iLineCount;
	}
	
	Unbabelcli() {
		// NOT USED Constructor
	}	
	
}

