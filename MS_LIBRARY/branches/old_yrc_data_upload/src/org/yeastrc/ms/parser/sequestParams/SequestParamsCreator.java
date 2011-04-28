/**
 * SequestParamsCreator.java
 * @author Vagisha Sharma
 * Apr 25, 2011
 */
package org.yeastrc.ms.parser.sequestParams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.SequestAminoAcidUtils;

/**
 * Takes as input a directory with .sqt files and produces a sequest.params file
 * based on the headers of the .sqt files
 */
public class SequestParamsCreator {

	
	// These are the parameters we will read
	private String databaseName = null;
	// H	Database	/scratch/yates/SGD_S-cerevisiae_na_12-16-2005_con_reversed.fasta
	
	private String parentMassType = null;
	// H	PrecursorMasses	AVG

	private String fragmentMassType = null;
	//H	FragmentMasses	MONO

	private String peptideMassTolerance = null;
	// H	Alg-PreMassTol	3.000
	
	private String fragmentIonTolerance = null;
	// H	Alg-FragMassTol	0.0
	
	private List<String> staticMods = new ArrayList<String>();
	// H	StaticMod	C=160.139
	// If there is more than 1 static residue modification we should see multiple headers
	
	private List<String> diffMods = new ArrayList<String>();
	// H	DiffMod	STY*=+80.000
	// If there is more than 1 dynamic residue modification we should see multiple headers
	
	private String ionSeries = null;
	// H	Alg-IonSeries	0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0
	
	private String enzyme = null;
	// H	EnzymeSpec	No_Enzyme
	
	
	public void create(String inputDirectory) throws SQTParseException {
		
		File dir = new File(inputDirectory);
		File[] sqtFiles = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".sqt");
			}
		});
		
		int idx = 0;
		
		for(File sqtFile: sqtFiles) {
			
			SequestSQTFileReader reader = new SequestSQTFileReader();
			try {
				reader.open(sqtFile.getAbsolutePath());
				SQTHeader headerSection = reader.getSearchHeader();
				List<SQTHeaderItem> headers = headerSection.getHeaders();
				
				// reading from the first file
				if(idx == 0) {
					
					for(SQTHeaderItem header: headers) {
						if(header.getName().equals("Database"))
							this.databaseName = header.getValue();
						else if(header.getName().equalsIgnoreCase("PrecursorMasses"))
							this.parentMassType = header.getValue();
						else if(header.getName().equalsIgnoreCase("FragmentMasses"))
							this.fragmentMassType = header.getValue();
						else if(header.getName().equalsIgnoreCase("Alg-PreMassTol"))
							this.peptideMassTolerance = header.getValue();
						else if(header.getName().equalsIgnoreCase("Alg-FragMassTol"))
							this.fragmentIonTolerance = header.getValue();
						else if(header.getName().equalsIgnoreCase("Alg-IonSeries"))
							this.ionSeries = header.getValue();
						else if(header.getName().equalsIgnoreCase("EnzymeSpec"))
							this.enzyme = header.getValue();
						else if(header.getName().equalsIgnoreCase("StaticMod"))
							this.staticMods.add(header.getValue());
						else if(header.getName().equalsIgnoreCase("DiffMod"))
							this.diffMods.add(header.getValue());
					}
				}
				// match with what was read from the first file
				else {
					
					List<String> sMods = new ArrayList<String>();
					List<String> dMods = new ArrayList<String>();
					
					for(SQTHeaderItem header: headers) {
						if(header.getName().equals("Database")) 
							match(this.databaseName,header.getValue());
						else if(header.getName().equalsIgnoreCase("PrecursorMasses"))
							match(this.parentMassType, header.getValue());
						else if(header.getName().equalsIgnoreCase("FragmentMasses"))
							match(this.fragmentMassType, header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-PreMassTol"))
							match(this.peptideMassTolerance,header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-FragMassTol"))
							match(this.fragmentIonTolerance,header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-IonSeries"))
							match(this.ionSeries,header.getValue());
						else if(header.getName().equalsIgnoreCase("EnzymeSpec"))
							match(this.enzyme,header.getValue());
						else if(header.getName().equalsIgnoreCase("StaticMod"))
							sMods.add(header.getValue());
						else if(header.getName().equalsIgnoreCase("DiffMod"))
							dMods.add(header.getValue());
					}
					
					match(this.staticMods, sMods);
					match(this.diffMods, dMods);
				}
				idx++;
				
			} catch (DataProviderException e) {
				throw new SQTParseException("Error parsing file: "+sqtFile, e);
			}
			finally {
				reader.close();
			}
		}
		
		writeParamsFile(inputDirectory+File.separator+"my.params");
	}
	
	private void match(String s1, String s2) throws SQTParseException {
		
		boolean match;
		if(s1 == null)	    match = (s2 == null);
		else if(s2 == null)	match = (s1 == null);
		else				match = (s1.equals(s2));
		
		if(!match)
			throw new SQTParseException("File headers do not match: "+s1+" and "+s2);
	}
	
	private void match(List<String> list1, List<String> list2) throws SQTParseException {
		
		if(list1.size() != list2.size())
			throw new SQTParseException("Modification headers do not match");
		
		Collections.sort(list1);
		Collections.sort(list2);
		for(int i = 0; i < list1.size(); i++) {
			if( !list1.get(i).equals(list2.get(i)) )
				throw new SQTParseException("Modification headers do not match");
		}
	}
	
	
	private void writeParamsFile(String filePath) throws SQTParseException {
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filePath));
			
			// write a dummy parameter so that we know this file was generated from the SQT headers
			writer.write("FILE_GENERATOR = SequestParamsCreator  ; File was generated from the SQT headers in the experiment");
			writer.write("\n\n");
			
			writer.write("[SEQUEST]\n");
			
			// write the database name
			writer.write("database_name = "+this.databaseName);
			writer.write("\n\n");
			
			// write the parent mass type
			if(this.parentMassType != null) {
				// eg. mass_type_parent = 0                   ; 0=average masses, 1=monoisotopic masses
				writer.write("mass_type_parent = ");
				if(parentMassType.equalsIgnoreCase("AVG"))
					writer.write("0");
				else if(parentMassType.equalsIgnoreCase("MONO"))
					writer.write("1");
				else
					throw new SQTParseException("unknown parent mass type: "+parentMassType);
				
				writer.write("\t; 0=average masses, 1=monoisotopic masses");
				writer.newLine();
			}
			
			// write the fragment mass type
			if(this.fragmentMassType != null) {
				// e.g. mass_type_fragment = 1                 ; 0=average masses, 1=monoisotopic masses
				writer.write("mass_type_fragment = ");
				if(fragmentMassType.equalsIgnoreCase("AVG"))
					writer.write("0");
				else if(fragmentMassType.equalsIgnoreCase("MONO"))
					writer.write("1");
				else
					throw new SQTParseException("unknown fragment mass type: "+fragmentMassType);
				
				writer.write("\t; 0=average masses, 1=monoisotopic masses");
				writer.newLine();
			}
			
			// write the parent mass tolerance
			if(this.peptideMassTolerance != null) {
				// e.g. peptide_mass_tolerance = 3.000
				writer.write("peptide_mass_tolerance = "+peptideMassTolerance);
				writer.newLine();
			}
			
			// write the fragment mass tolerance
			if(this.fragmentIonTolerance != null) {
				// e.g. fragment_ion_tolerance = 0.0
				writer.write("fragment_ion_tolerance = "+fragmentIonTolerance);
				writer.newLine();
			}
			
			// write the ion series
			if(this.ionSeries != null) {
				// e.g. ion_series = 0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0
				writer.write("ion_series = "+this.ionSeries);
				writer.newLine();
			}
			
			writer.newLine();
			// write the enzyme number
			if(this.enzyme != null) {
				writeEnzymNumber(writer);
			}
			
			// write the print_duplicate_references param
			// This is a hack. We don't really know if this was set to 1 in the original Sequest run
			// But we need this to be set to 1 to get the data uploaded.
			writer.write("\nprint_duplicate_references = 1         ; 0=no, 1=yes\n");
			
			
			writer.newLine();
			// write the difff mods, if any
			if(this.diffMods.size() > 0) {
				
				writeDiffMods(writer);
			}
			
			writer.newLine();
			// write the static mods, if any
			writeStaticMods(writer);
			
			writer.newLine();
			// write the enzyme information, if present
			writeEnzymes(writer);
			
		}
		catch(IOException e) {
			throw new SQTParseException("Error parsing file: "+filePath, e);
		}
		finally {
			if(writer != null) try {writer.close();} catch(IOException e){}
		}
	}

	private void writeStaticMods(BufferedWriter writer) throws SQTParseException, IOException {
		
		// Static mods from the SQT file header should look like this:
		// C=160.139
		
		// key = modified residue (e.g. C, K, R);  value = mass of the modification
		Map<String, String> modificationMap = new HashMap<String, String>();
		
		for(String modString: this.staticMods) {
			String[] tokens = modString.split("=");
			
	        if (tokens.length < 2)
	            throw new SQTParseException("Invalid static modification string: "+modString);
	        if (tokens.length > 2)
	            throw new SQTParseException("Invalid static modification string (appears to have > 1 static modification): "+modString);
		
		
	        // convert modification chars to upper case 
	        String modChars = tokens[0].trim().toUpperCase();
	        String modMass = tokens[1].trim();
	        
	        try {
	            Double.parseDouble(modMass);
	        }
	        catch(NumberFormatException e) {
	            throw new SQTParseException("Error parsing static modification mass: "+modMass);
	        }
	        
	        // this modification may be for multiple residues; 
	        // add one for each residue character
	        for (int i = 0; i < modChars.length(); i++) {
	        	modificationMap.put(String.valueOf(modChars.charAt(i)), modMass);
	        }
		}
		
		/*
		Example: 
		add_C_terminus = 0.0000                ; added to C-terminus (peptide mass & all Y"-ions)
		add_N_terminus = 0.0000                ; added to N-terminus (B-ions)
		add_G_Glycine = 0.0000                 ; added to G - avg.  57.0519, mono.  57.02146
		add_A_Alanine = 0.0000                 ; added to A - avg.  71.0788, mono.  71.03711
		add_S_Serine = 0.0000                  ; added to S - avg.  87.0782, mono.  87.02303
		add_P_Proline = 0.0000                 ; added to P - avg.  97.1167, mono.  97.05276
		add_V_Valine = 0.0000                  ; added to V - avg.  99.1326, mono.  99.06841
		add_T_Threonine = 0.0000               ; added to T - avg. 101.1051, mono. 101.04768
		add_C_Cysteine = 57.000                ; added to C - avg. 103.1388, mono. 103.00919
		add_L_Leucine = 0.0000                 ; added to L - avg. 113.1594, mono. 113.08406
		add_I_Isoleucine = 0.0000              ; added to I - avg. 113.1594, mono. 113.08406
		add_X_LorI = 0.0000                    ; added to X - avg. 113.1594, mono. 113.08406
		add_N_Asparagine = 0.0000              ; added to N - avg. 114.1038, mono. 114.04293
		add_O_Ornithine = 0.0000               ; added to O - avg. 114.1472, mono  114.07931
		add_B_avg_NandD = 0.0000               ; added to B - avg. 114.5962, mono. 114.53494
		add_D_Aspartic_Acid = 0.0000           ; added to D - avg. 115.0886, mono. 115.02694
		add_Q_Glutamine = 0.0000               ; added to Q - avg. 128.1307, mono. 128.05858
		add_K_Lysine = 0.0000                  ; added to K - avg. 128.1741, mono. 128.09496
		add_Z_avg_QandE = 0.0000               ; added to Z - avg. 128.6231, mono. 128.55059
		add_E_Glutamic_Acid = 0.0000           ; added to E - avg. 129.1155, mono. 129.04259
		add_M_Methionine = 0.0000              ; added to M - avg. 131.1926, mono. 131.04049
		add_H_Histidine = 0.0000               ; added to H - avg. 137.1411, mono. 137.05891
		add_F_Phenyalanine = 0.0000            ; added to F - avg. 147.1766, mono. 147.06841
		add_R_Arginine = 0.0000                ; added to R - avg. 156.1875, mono. 156.10111
		add_Y_Tyrosine = 0.0000                ; added to Y - avg. 163.1760, mono. 163.06333
		add_W_Tryptophan = 0.0000              ; added to W - avg. 186.2132, mono. 186.07931
				 
				 */
		
		writer.write("add_C_terminus = 0.0000                ; added to C-terminus (peptide mass & all Y\"-ions)\n");
		writer.write("add_N_terminus = 0.0000                ; added to N-terminus (B-ions)\n");
		
		SequestAminoAcidUtils aaUtils = AminoAcidUtilsFactory.getSequestAminoAcidUtils();
		char[] aminoAcids = aaUtils.getAminoAcidChars();
		for(char aa: aminoAcids) {
			
			StringBuilder buf = new StringBuilder();
			
			buf.append("add_"+aa+"_"+aaUtils.getFullName(aa)+" = ");
			String mod = modificationMap.get(String.valueOf(aa));
			if(mod == null)	buf.append("0.0000");
			else {
			double massDiff = Double.parseDouble(mod) - aaUtils.avgMass(aa);  // static mod in SQT headers is mass of amino acid + modification mass
				buf.append(String.format("%.4f", massDiff));
			}
			
			int length = buf.length();
			for (int i = length; i < 39; i++) {
				buf.append(" ");
			}
			writer.write(buf.toString());
			writer.write("; added to "+aa+" - avg. "+aaUtils.avgMass(aa)+", mono. "+aaUtils.monoMass(aa));
			writer.newLine();
		}
	}

	private void writeDiffMods(BufferedWriter writer) throws SQTParseException, IOException {

		// Diff mods string from the SQT file header should look like this
		// STY*=+80.000
	    // Multiple dynamic modifications should be present on separate DiffMod lines in a SQT file
		
		String asteriskMods = null; // modifications with symbol *
		String atMods = null; // modifications with symbol @
		String hashMods = null; // modifications with symbol #
		
		
		for(String modString: diffMods) {
			
			String[] tokens = modString.split("=");
			
			if (tokens.length < 2)
				throw new SQTParseException("Invalid dynamic modification string: "+modString);
			if (tokens.length > 2)
				throw new SQTParseException("Invalid dynamic modification string (appears to have > 1 dynamic modification): "+modString);

			String modChars = tokens[0].trim();
			// get the modification symbol (this character should follow the modification residue characters)
			// example S* -- S is the modified residue; * is the modification symbol
			if (modChars.length() < 2)
				throw new SQTParseException("No modification symbol found: "+modString);
			char modSymbol = modChars.charAt(modChars.length() - 1);
			if (!isValidDynamicModificationSymbol(modSymbol))
				throw new SQTParseException("Invalid modification symbol: "+modString);

			// remove the modification symbol and convert modification chars to upper case 
			modChars = modChars.substring(0, modChars.length()-1).toUpperCase();
			if (modChars.length() < 1)
				throw new SQTParseException("No residues found for dynamic modification: "+modString);
			

			String modMass = tokens[1].trim();
			modMass = removeSign(modMass); // removes a + or - sign
			if (modMass.length() < 1)
				throw new SQTParseException("No mass found for dynamic modification: "+modString);


			try { Double.parseDouble(modMass);}
			catch(NumberFormatException e) {
				throw new SQTParseException("Error parsing modification mass: "+modMass);
			}

			if(modSymbol == '*')
				asteriskMods = modMass+" "+modChars;
			else if(modSymbol == '@')
				atMods = modMass+" "+modChars;
			else if(modSymbol == '#')
				hashMods = modMass+" "+modChars;
		}
		
		writer.write("diff_search_options = ");
		if(asteriskMods != null)
			writer.write(asteriskMods);
		else
			writer.write("0.0 X");
		if(atMods != null)
			writer.write(" "+atMods);
		else
			writer.write(" 0.0 X");
		if(hashMods != null)
			writer.write(" "+hashMods);
		else
			writer.write(" 0.0 X");
		writer.newLine();
	}
	
	private boolean isValidDynamicModificationSymbol(char modSymbol) {
		return modSymbol == '*' || modSymbol == '@' || modSymbol == '#';
    }
	
	private String removeSign(String massStr) {
        if (massStr.length() == 0)  return massStr;
        if (massStr.charAt(0) == '+' || massStr.charAt(0) == '-')
            return massStr.substring(1);
        return massStr;
    }

	private void writeEnzymNumber(BufferedWriter writer) throws SQTParseException, IOException {
		
		// TODO these are the common enzymes that I know of.  Should I add others?
		// Most SQT files will have "No_Enzyme" or "Trypsin" as the enzyme
		// If the input SQT files have an enzyme other than the ones below we will
		// have to add it to the list.
		writer.write(("enzyme_number = "));
		if(this.enzyme.equalsIgnoreCase("No_Enzyme"))
			writer.write("0\n");
		else if(this.enzyme.equalsIgnoreCase("Trypsin"))
			writer.write("1\n");
		else if(this.enzyme.equalsIgnoreCase("Chymotrypsin"))
			writer.write("2\n");
		else if(this.enzyme.equalsIgnoreCase("Trypsin_K"))
			writer.write("8\n");
		else if(this.enzyme.equalsIgnoreCase("Trypsin_R"))
			writer.write("9\n");
		else if(this.enzyme.equalsIgnoreCase("Elastase"))
			writer.write("12\n");
		else if(this.enzyme.equalsIgnoreCase("Elastase/Tryp/Chymo"))
			writer.write("13\n");
		else
			throw new SQTParseException("Unrecognized enzyme: "+this.enzyme);
			
	}

	private void writeEnzymes(BufferedWriter writer) throws IOException {
		
		/*
		 
		 [SEQUEST_ENZYME_INFO]
0.  No_Enzyme              0      -           -
1.  Trypsin                1      KR          P
2.  Chymotrypsin           1      FWY         P
3.  Clostripain            1      R           -
4.  Cyanogen_Bromide       1      M           -
5.  IodosoBenzoate         1      W           -
6.  Proline_Endopept       1      P           -
7.  Staph_Protease         1      E           -
8.  Trypsin_K              1      K           P
9.  Trypsin_R              1      R           P
10. AspN                   0      D           -
11. Cymotryp/Modified      1      FWYL        P
12. Elastase               1      ALIV        P
13. Elastase/Tryp/Chymo    1      ALIVKRWFY   P


		 */
		
		writer.write("[SEQUEST_ENZYME_INFO]\n");
		writer.write("0.  No_Enzyme              0      -           -\n");
		writer.write("1.  Trypsin                1      KR          P\n");
		writer.write("2.  Chymotrypsin           1      FWY         P\n");
		writer.write("3.  Clostripain            1      R           -\n");
		writer.write("4.  Cyanogen_Bromide       1      M           -\n");
		writer.write("5.  IodosoBenzoate         1      W           -\n");
		writer.write("6.  Proline_Endopept       1      P           -\n");
		writer.write("7.  Staph_Protease         1      E           -\n");
		writer.write("8.  Trypsin_K              1      K           P\n");
		writer.write("9.  Trypsin_R              1      R           P\n");
		writer.write("10. AspN                   0      D           -\n");
		writer.write("11. Cymotryp/Modified      1      FWYL        P\n");
		writer.write("12. Elastase               1      ALIV        P\n");
		writer.write("13. Elastase/Tryp/Chymo    1      ALIVKRWFY   P\n");
		
	}

	public static void main(String[] args) throws SQTParseException {
		// String inputDir = args[0];
		String inputDir = "/Users/silmaril/WORK/UW/JOB_QUEUE/jq_w_mslib_r722_fix/data_dir/parc/";
		
		SequestParamsCreator spc = new SequestParamsCreator();
		spc.create(inputDir);
	}
}
