/**
 * OldMS2Converter.java
 * @author Vagisha Sharma
 * Jan 26, 2011
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 
 */
public class OldMS2Converter {

	private static final DecimalFormat format = new DecimalFormat("#0.0000");
	
	private static final OldMS2Converter instance = new OldMS2Converter();
	
	private OldMS2Converter () {}
	
	public static OldMS2Converter getInstance() {
		return instance;
	}
	
	public void convert (String inputFilePath, String outputFilePath) throws IOException {
		
		
		File infile = new File(inputFilePath);
		
		if(isValidMs2(infile)) {
			System.out.println("File: "+infile.getAbsolutePath()+" is a valid MS2 file. Copying as is...");
			copyFile(inputFilePath, outputFilePath);
			return;
		}
		
		System.out.println("Converting file: "+inputFilePath);
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			reader = new BufferedReader(new FileReader(infile));
			writer = new BufferedWriter(new FileWriter(outputFilePath));
			
			// write the header
			writer.write("H\tCreationDate\t"+(new Date())+"\n");
			writer.write("H\tComments\tConverted from old MS2 format\n");
			
			String line = null;
			double mz = -1.0;
			
			int scanNum = 1;
			while((line = reader.readLine()) != null) {
				
				if(line.startsWith(":")) {
					// Example:
					// :0002.0002.2
					// 1894.72 2
					
					line = line.substring(1); // remove the ":"
					String[] tokens1 = line.trim().split("\\.");
					String scanNumS = tokens1[0];
					String scanNumE = tokens1[1];
					String chgline1 = tokens1[2]; // charge string from the first line
					
					// read the next line
					String line2 = reader.readLine();
					String[] tokens2 = line2.split("\\s+");
					String mplusH = tokens2[0];
					String chgline2 = tokens2[1];
					
					if(!(chgline1.equals(chgline2))) {
						System.err.println("Charge not the same");
						System.err.println("\t"+line);
						System.err.println("\t"+line2);
						System.exit(-1);
					}
					
					// m/z = ( neutralMass + (charge * MASS_PROTON) ) / charge;
					int chg = Integer.parseInt(chgline2);
					if(mz == -1.0) {
						mz = MzMplusHConverter.toMz(Double.parseDouble(mplusH), chg);
						writer.write("S\t"+scanNum+"\t"+scanNum+"\t"+format.format(mz)+"\n");
						writer.write("I\tOriginalScan\t"+scanNumS+"-"+scanNumE+"\n");
						scanNum++;
					}
					
					if(mz != -1.0) {
						mplusH = getMplusH(mz, chg);
					}
					
					writer.write("Z\t"+chg+"\t"+mplusH+"\n");
					
				}
				else {
					mz = -1.0;
					writer.write(line+"\n");
				}
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
			if(writer != null) try {writer.close();} catch(IOException e) {}
		}
		
		System.out.println("\tConverted file: "+outputFilePath);
	}

	private boolean isValidMs2(File f) throws IOException {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String line = reader.readLine();
			if(line.startsWith(":"))
				return false;
			else if(line.startsWith("H"))
				return true;
			else {
				System.err.println("Cannot recognize file: "+f.getAbsolutePath());
				System.exit(-1);
				return true;
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e) {}
		}
	}
	
	private static String getMplusH(double mz, int charge){
		double mph = MzMplusHConverter.toMplusH(mz, charge);
		return format.format(mph);
	}
	
	// Copy a file
	public void copyFile(String infile, String outfile) throws IOException {
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			reader = new BufferedReader(new FileReader(infile));
			writer = new BufferedWriter(new FileWriter(outfile));
			
			String line = null;
			while((line = reader.readLine()) != null) {
				writer.write(line+"\n");
			}
			
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
			if(writer != null) try {writer.close();} catch(IOException e) {}
		}
	}
	
}
