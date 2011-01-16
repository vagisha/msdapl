/**
 * TideParamParser.java
 * @author Vagisha Sharma
 * Jan 15, 2011
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.impl.Enzyme;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.impl.ParamBean;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.SearchDatabase;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SearchParamsDataProvider;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;

/**
 * 
 */
public class TideParamsParser implements SearchParamsDataProvider {

	private String remoteServer;
	
	private List<Param> paramList;

    
    private MsSearchDatabaseIn database;
    private MsEnzymeIn enzyme;
    private boolean printAllProteinMatches = false;
    private List<MsResidueModificationIn> staticResidueModifications;
    private List<MsResidueModificationIn> dynamicResidueModifications;

    public TideParamsParser() {
        paramList = new ArrayList<Param>();
        staticResidueModifications = new ArrayList<MsResidueModificationIn>();
        dynamicResidueModifications = new ArrayList<MsResidueModificationIn>();
    }
    
    public MsSearchDatabaseIn getSearchDatabase() {
        return database;
    }

    public MsEnzymeIn getSearchEnzyme() {
        return enzyme;
    }

    public List<MsResidueModificationIn> getDynamicResidueMods() {
        return dynamicResidueModifications;
    }

    public List<MsResidueModificationIn> getStaticResidueMods() {
        return staticResidueModifications;
    }

    public List<MsTerminalModificationIn> getStaticTerminalMods() {
    	return new ArrayList<MsTerminalModificationIn>(0);
    }

    public List<MsTerminalModificationIn> getDynamicTerminalMods() {
        return new ArrayList<MsTerminalModificationIn>(0);
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.TIDE;
    }
    
    public String paramsFileName() {
        throw new UnsupportedOperationException("Tide parameters are read from SQT file headers");
    }
    
    @Override
	public void parseParams(String remoteServer, String paramFileDir) throws DataProviderException {
		
    	this.remoteServer = remoteServer;
    	List<Param> params = getSearchParams(paramFileDir);
    	parseParams(params);
		
	}

    private List<Param> getSearchParams(String paramFileDir) throws DataProviderException {

    	Map<String, String> headerMap = new HashMap<String, String>();

    	for(String fileName: getSqtFiles(new File(paramFileDir))) {

    		String filePath = paramFileDir+File.separator+fileName;

    		SequestSQTFileReader provider = new SequestSQTFileReader();

    		provider.open(filePath);
    		SQTRunSearchIn search = provider.getSearchHeader();

    		// These are the headers we are interested in
    		// H	CommandLineIndex	--digestion=full-digest........
    		// H	CommandLineSearch	--proteins=.......
    		// H	CommandLineResults	--results_file
    		for(SQTHeaderItem header: search.getHeaders()) {

    			if(header.getName().equalsIgnoreCase("CommandLineIndex") ||
    					header.getName().equalsIgnoreCase("CommandLineSearch") ||
    					header.getName().equalsIgnoreCase("CommandLineResults")) {

    				// We want to make sure that all SQT file in an experiment directory have the 
    				// same header values
    				String oldValue = headerMap.get(header.getName());
    				if(oldValue != null) {
    					if(!(oldValue.equalsIgnoreCase(header.getValue()))) {
    						throw new DataProviderException("!!!SQT header mismatch");
    					}
    					else
    						headerMap.put(header.getName(), header.getValue());
    				}
    			}
    		}
    		provider.close(); // close the file
    		
    	}

    	// TEMPORARY
    	// If we did not find the required headers in the SQT file add the values that were used
    	// to process YRC peptide atlas data
    	if(!headerMap.containsKey("CommandLineIndex")) {
    		String value = "./index --digestion=full-digest --enzyme=none "
    			+"--fasta=/net/pr/vol3/nobackups/tide_yrc/dbase/SGDyeast.20101221.fasta "+
    			"--max_mass=5000 --min_mass=600 "+
    			"--mods_spec=C+57.021464 "+
    			"--monoisotopic_precursor=true --missed_cleavages=false --max_length=50 --min_length=6";

    		headerMap.put("CommandLineIndex", value);
    	}

    	List<Param> params = new ArrayList<Param>();
    	for(String paramsString: headerMap.values()) {
    		String[] opts = paramsString.split("\\s+");
    		for(String opt: opts) {
    			String[] optTokens = opt.split("=");
    			if(!(optTokens.length == 2)) {
    				String name = optTokens[0];
    				name.replaceFirst("-*", "");
    				ParamBean param = new ParamBean(name, optTokens[1]);
    				params.add(param);
    			}
    		}
    	}
    	return params;
    }
    
    private List<String> getSqtFiles(File dir) {

    	List<String> mySqtFiles = new ArrayList<String>();

    	File[] files = dir.listFiles(new FilenameFilter() {
    		@Override
    		public boolean accept(File dir, String name) {
    			String name_lc = name.toLowerCase();
    			return (name_lc.endsWith(".sqt") && !(name_lc.endsWith("reverse.sqt")));
    		}});
    	for (int i = 0; i < files.length; i++) {
    		mySqtFiles.add(files[i].getName());
    	}
    	return mySqtFiles;
    }
    
    public boolean printAllProteinMatches() {
        return printAllProteinMatches;
    }

    public List<Param> getParamList() {
        return this.paramList;
    }
    
    public boolean isEnzymeUsedForSearch() {
        return enzyme != null;
    }
    
    private void parseParams(List<Param> params) throws DataProviderException {
    	
    	for(Param param: params) {
    		
    		/* FLAGS used by the program - index
    		-fasta (Input FASTA file) type: string default: ""
			-peptides (File of peptides to create) type: string default: ""
			-proteins (File of raw proteins to create, as raw_proteins.proto)
						type: string default: ""
			-digestion (Digestion completeness. May be full-digest or partial-digest)
						type: string default: "full-digest"
			-enzyme (Digestion enzyme. May be none, trypsin, chymotrypsin, elastase,
						clostripain, cyanogen-bromide, idosobenzoate, proline-endopeptidase,
						staph-protease, modified-chymotrypsin, elastase-trypsin-chymotrypsin,
						aspn.) type: string default: "none"
			-max_length (Peptide length inclusion threshold) type: int32 default: 50
			-max_mass (Peptide mass inclusion threshold) type: double default: 7200
			-min_length (Peptide length inclusion threshold) type: int32 default: 6
			-min_mass (Peptide mass inclusion threshold) type: double default: 200
			-missed_cleavages (Allow missed cleavages in enzymatic digestion)
						type: bool default: false
			-mods_spec (Expression for static modifications to include. Specify a
						comma-separated list of the form --mods=C+57.0,...) type: string
						default: ""
			-mods_table (Modification specification filename. May be given instead of
			--mods_spec.) type: string default: ""
			-monoisotopic_precursor (Use monoisotopic precursor masses rather than
						average masses for residues.) type: bool default: false
    		 */
    		
    		/* FLAGS used by the program - search
    		-mass_window (Precursor mass tolerance in Daltons) type: double default: 3
			-peptides (File of unfragmented peptides, as peptides.proto) type: string
						default: ""
			-proteins (File of proteins corresponding to peptides, as
						raw_proteins.proto) type: string default: ""
			-results (Results format. Can be text or protobuf.) type: string
						default: "text"
			-results_file (Results output file) type: string default: "results.tideres"
			-spectra (Spectrum input file) type: string default: ""
			-top_matches (Number of matches to report for each spectrum) type: int32
						default: 5
    		 */
    		
    		/*
    		-aux_locations (File of auxiliary locations corresponding to peptides in
						the results) type: string default: ""
			-match_fields (A comma delimited set of fields to show for a matching
						peptide in the order listed. Available options are: xcorr,sequence)
						type: string default: "xcorr,sequence"
			-out_filename (Name of the output file to generate. An extension will be
						added based on the out_format.) type: string default: ""
			-out_format (The output format to be generated. Can be text, pep.xml or
						sqt. Default is text.) type: string default: "text"
			-protein_fields (A comma delimited set of fields to show for a protein
						associated with a matching peptide. Available options are:
						protein_name,pos, aa_before,aa_after) type: string
						default: "protein_name,pos,aa_before,aa_after"
			-proteins (File of proteins corresponding to peptides, as
						raw_proteins.proto) type: string default: ""
			-results_file (Results file generated via Search, as results.proto)
						type: string default: ""
			-show_all_proteins (Display all the proteins the peptide was found in.)
						type: bool default: false
			-show_mods (Display modifications in the peptide sequence.) type: bool
						default: false
			-spectra (Spectrum input file) type: string default: ""
			-spectrum_fields (A comma delimited set of fields to show for an
						experimental spectrum in the order listed. Available options are:
						spectrum_num,mz,charge) type: string default: "spectrum_num,mz,charge"
    		 */
    		
    		// fasta file used for search
    		if(param.getParamName().equalsIgnoreCase("fasta")) {
    			SearchDatabase db = new SearchDatabase();
                db.setServerAddress(remoteServer);
                db.setServerPath(param.getParamValue());
                database = db;
    		}
    		
    		// enzyme
            else if (param.getParamName().equalsIgnoreCase("enzyme")) {
            	Enzyme enz = new Enzyme();
                enz.setName(param.getParamName());
                this.enzyme = enz;
            }
    		
    		// static modifications
    		if(param.getParamName().equalsIgnoreCase("mods_spec")) {
    			getStaticResidueMods(param);
    		}
    		
    		// TODO Don't know the command-line option for dynamic modifications
    		
    		// are all protein matches for a peptide printed
    		if(param.getParamName().equalsIgnoreCase("-show_all_proteins")) {
    			this.printAllProteinMatches = Boolean.parseBoolean(param.getParamValue());
    		}
    		
    	}
    }


	private void getStaticResidueMods(Param param) throws DataProviderException {
		
		// mods_spec=C+57.021464
		List<ResidueModification> modifications = parseModifications(param);
		staticResidueModifications.addAll(modifications);
	}
	
	private List<ResidueModification> parseModifications(Param param) throws DataProviderException {
		
		List<ResidueModification> modifications = new ArrayList<ResidueModification>();
		// Example: mods_spec=C+57.021464
		// paramName = mods_spec; paramValue=C+57.021464
		String[] mods = param.getParamValue().split(",");
		for(String modStr: mods) {
			int idx = modStr.indexOf("+");
			if(idx == -1)
				idx = modStr.indexOf("-");
			
			if(idx == -1) {
				throw new DataProviderException("Cannot parse modification string: "+modStr);
			}
			
			char modResidue = modStr.charAt(0);
	        BigDecimal modMass = null;
	        try {modMass = new BigDecimal(modStr.substring(idx+1));}
	        catch(NumberFormatException e) {throw new DataProviderException("Error parsing modification mass: "+modStr);}

	        if (modMass.doubleValue() != 0.0) {
	            ResidueModification mod = new ResidueModification();
	            mod.setModificationMass(modMass);
	            mod.setModifiedResidue(modResidue);
	            modifications.add(mod);
	        }
		}
		return modifications;
	}
    
}
