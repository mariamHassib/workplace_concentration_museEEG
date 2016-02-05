package model;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import oscP5.*;
import netP5.*;

	
public class museLogger extends Observable {

		private static final Logger lmuse = Logger.getLogger(museLogger.class.getName());
		
		// Set up Osc stuff
		OscP5 oscP5;
		MuseOscServer myMuseServer;
		NetAddress myRemoteLocation;
		

		// Number of data channels
		int numChannels = 4;
		
		//Number of Buckets (signals)
		String waves[] = {
				  "delta_relative", "theta_relative","alpha_relative", "beta_relative", "gamma_relative", 
				  "delta_session_score", "theta_session_score","alpha_session_score","beta_session_score","gamma_session_score",
				  "blink", "jaw_clench", 
				  "experimental/mellow", "experimental/concentration"
				};
				
		int numBuckets = waves.length;
		
		// Store the current data as it comes in. lastPos contains the n-1th data point, and values contains the nth.
		float lastPos[][]; //= new float[numBuckets][numChannels];
		float values[][]; //= new float[numBuckets][numChannels];
		
		
		// Captured data from muse -- Status 1:good , 2:ok, 3: bad
		float [] status;
		int battery;
		
		public museLogger(){
			
		//myMuseServer.setOsc(5000);
		// oscP5 = new OscP5(this, 5000);
		// myRemoteLocation = new NetAddress("127.0.0.1", 5000);
		 lastPos = new float[numBuckets][numChannels];
		 values = new float[numBuckets][numChannels];
			
		 
		}
		
		public void startMuseLogging(){
			oscP5 = new OscP5(this, 5000);
			 myRemoteLocation = new NetAddress("127.0.0.1", 5000);
		}
		
		
		public void stopMuseLogging(){
			oscP5.stop();
			oscP5.disconnect(myRemoteLocation);
			
			 
		}
		
		/* This function gets called every time any OSC message comes in.
		// Within the function, we can check if a given message matches the one we're after, and react accordingly.
		// This is in contrast to other OSC interfaces, which set up handlers for each type of message that can come in.*/
		void oscEvent(OscMessage theOscMessage) {

			// For each type of wave [alpha, beta, ...]
			for (int curWave = 0; curWave < waves.length; curWave++) {

				// If the wave matches the DSP data for that wave
				if (theOscMessage.checkAddrPattern("/muse/elements/" + waves[curWave]) ) {
			     
			        // For each of the channels of data (4, in preset 10)
			        for (int i=0; i<numChannels; i++) {
			        	
			        	try {
			        		
				        	 if (theOscMessage.checkTypetag("i")) {
				        		 
				        		 int current=theOscMessage.get(i).intValue();
				        		 System.out.println(" this is int: "+ current);
				        		 values[curWave][i] = current;
	
				        	 }
				        	 
				        	 else if (theOscMessage.checkTypetag("f")) {
				        	 
				        		 float currentFloat= theOscMessage.get(i).floatValue();
				        		 values[curWave][i] = currentFloat;
						         System.out.println(" this is float: "+ currentFloat);
						        
				        	 }
			        	 
			        	
			        	}
			        	
			        	catch(NumberFormatException e) {
			        		
			        		e.printStackTrace();
			        	} 
		        	 }
			     }
				
				
				
				
			   }

			 lmuse.log(Level.INFO, "delta_relative: {0} | theta_relative : {1} | alpha_relative : {2} | beta_relative : {3} | gamma_relative : {4} | "+
							"delta_session_score : {5} | theta_session_score : {6} | alpha_session_score : {7} | beta_session_score : {8} |"+
							"gamma_session_score : {9} | blink : {10} | jaw_clench : {11} | experimental/mellow : {12} |"
							+ "experimental/concentration: {13} " , returnRecord(values));
			
			
			        //     "TP9 : {0}| FP1 :{1}| FP2:{2}| TP10:{3}"        

			}
		
		 
	
		public String [] returnRecord(float[][] values){
			
			String [] finalRecord=new String[numBuckets];
			
			for (int i = 0; i<numBuckets; i++){
				String currentWave="";
				for (int j=0; j<numChannels; j++){
					if(j==0)
						currentWave= String.valueOf(values[i][j]);
					else 
					currentWave= currentWave+ "," +String.valueOf(values[i][j]);
					
					 System.out.println(currentWave);
				}
				finalRecord[i]=currentWave;
			}
			
			return finalRecord;
		}
		
		public static void main(String[]args){
			
			museLogger museL=new museLogger();
			
		}
		
		
}
	
