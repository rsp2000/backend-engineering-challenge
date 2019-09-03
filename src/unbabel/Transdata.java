package unbabel;

import java.util.Calendar;

public class Transdata {
	Calendar dtimestamp;
	int  isumduration;
	int  inum;
	
	Transdata(Calendar caldate, int iduration)
	{
		dtimestamp = caldate;
		isumduration = iduration;
		inum = 1;
	}
	
	/* 
	 * addTrans: Sum new duration time
	 */
	void addtrans(int iduration)
	{
		isumduration += iduration;
		inum++;		
	}
	
	/*
	 * getavg: Returns the average
	 */
	float getavg()
	{
		if(inum > 0)
			return (float) isumduration/inum;
		else
			return 0;
	}
}
