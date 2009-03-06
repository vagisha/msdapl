package org.yeastrc.www.proteinfer.alignment;

import jaligner.Alignment;
import jaligner.Sequence;
import jaligner.SmithWatermanGotoh;
import jaligner.matrix.Matrix;
import jaligner.matrix.MatrixLoader;
import jaligner.matrix.MatrixLoaderException;
import jaligner.util.SequenceParser;
import jaligner.util.SequenceParserException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;

import edu.uwpr.protinfer.PeptideDefinition;

public class SequenceAligner {

    private static final SequenceAligner instance = new SequenceAligner();
    
    private static final Logger log = Logger.getLogger(SequenceAligner.class.getName());
    
    public static SequenceAligner instance() {
        return instance;
    }
    
    public void alignCluster(WIdPickerCluster cluster) throws AlignmentException {
        
        WIdPickerProtein anchorProtein = getAnchorProtein(cluster);
        
        
        
    }
    
    public void alignProteins(WIdPickerProtein protein1, WIdPickerProtein protein2) throws AlignmentException {
        
        String a_seq = getProteinSequence(protein1.getProtein().getNrseqProteinId());
        Sequence s1 = null;
        try {
            s1 = SequenceParser.parse(a_seq);
            s1.setId(protein1.getAccession());
        }
        catch (SequenceParserException e) {
            throw new AlignmentException("Error parsing protein sequence:\n"+a_seq, e);
        }
        
        String b_seq = getProteinSequence(protein2.getProtein().getNrseqProteinId());
        
        Sequence s2 = null;
        try {
            s2 = SequenceParser.parse(b_seq);
            s2.setId(protein2.getAccession());
        }
        catch (SequenceParserException e) {
            throw new AlignmentException("Error parsing protein sequence:\n"+b_seq, e);
        }
        
        Matrix matrix = null;
        try {
            matrix = MatrixLoader.load("BLOSUM62");
        }
        catch (MatrixLoaderException e) {
            throw new AlignmentException("Error reading substitution matrix"+a_seq, e);
        }
        
        Alignment alignment = SmithWatermanGotoh.align(s1, s2, matrix, 10f, 0.5f);
        
        
        log.info(alignment.getSummary());
    }
    
    public static void main(String[] args) throws SequenceParserException, MatrixLoaderException {
        
        
        WIdPickerCluster cluster = IdPickerResultsLoader.getIdPickerCluster(14, 1, new PeptideDefinition(), false);
        
        WIdPickerProtein anchorProtein = getAnchorProtein(cluster);
        String a_seq = getProteinSequence(anchorProtein.getProtein().getNrseqProteinId());
        Sequence s1 = null;
        try {
            s1 = SequenceParser.parse(a_seq);
            s1.setId(anchorProtein.getAccession());
        }
        catch (SequenceParserException e) {
            e.printStackTrace();
            return;
        }
        
        
        try {
            Matrix matrix = MatrixLoader.load("BLOSUM62");
            Alignment alignment = SmithWatermanGotoh.align(s1, s2, matrix, 10f, 0.5f);
            System.out.println(alignment.getSummary());
        }
        catch (MatrixLoaderException e) {
            e.printStackTrace();
        }
       
//        alignAnchorProteinGroup(anchorProtein, cluster);
        
//        alignOtherProteinGroups(anchorProtein, cluster);
        
        for(WIdPickerProteinGroup prGrp: cluster.getProteinGroups()) {
            
            for(WIdPickerProtein prot: prGrp.getProteins()) {
                if(prot.getProtein().getId() == anchorProtein.getProtein().getId())
                    continue;
                
                String b_seq = getProteinSequence(prot.getProtein().getNrseqProteinId());
                
                if(b_seq.startsWith("FSKSQVEWMSDK")) {
                    b_seq = b_seq.replaceFirst("FSKSQVEWMSDK", "FSKSQVVWMSDK");
                }
                
                Sequence s2 = null;
                try {
                    s2 = SequenceParser.parse(b_seq);
                    s2.setId(prot.getAccession());
                }
                catch (SequenceParserException e) {
                    e.printStackTrace();
                    return;
                }
                
                try {
                    Alignment alignment = SmithWatermanGotoh.align(s1, s2, MatrixLoader.load("BLOSUM62"), 10f, 0.5f);
                    System.out.println(alignment.getSummary());
                }
                catch (MatrixLoaderException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static String getProteinSequence(int nrseqId) {

        return NrSeqLookupUtil.getProteinSequence(nrseqId);
    }

    private static WIdPickerProtein getAnchorProtein(WIdPickerCluster cluster) {
        
        WIdPickerProtein protein = null;
        for(WIdPickerProteinGroup prGrp: cluster.getProteinGroups()) {
            for(WIdPickerProtein prot: prGrp.getProteins()) {
                if(protein == null)
                    protein = prot;
                else {
                    if(prot.getProtein().getPeptideCount() > protein.getProtein().getPeptideCount()) 
                        protein = prot;
                }
            }
        }
        return protein;
    }
    
    public static String blosum62() {
        StringBuilder buf = new StringBuilder();
        buf.append("#  Matrix made by matblas from blosum62.iij\n");
        buf.append("#  * column uses minimum score\n");
        buf.append("#  BLOSUM Clustered Scoring Matrix in 1/2 Bit Units\n");
        buf.append("#  Blocks Database = /data/blocks_5.0/blocks.dat\n");
        buf.append("#  Cluster Percentage: >= 62\n");
        buf.append("#  Entropy =   0.6979, Expected =  -0.5209\n");
        buf.append(" A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  B  Z  X  *\n");
        buf.append("A  4 -1 -2 -2  0 -1 -1  0 -2 -1 -1 -1 -1 -2 -1  1  0 -3 -2  0 -2 -1  0 -4\n"); 
        buf.append("R -1  5  0 -2 -3  1  0 -2  0 -3 -2  2 -1 -3 -2 -1 -1 -3 -2 -3 -1  0 -1 -4\n"); 
        buf.append("N -2  0  6  1 -3  0  0  0  1 -3 -3  0 -2 -3 -2  1  0 -4 -2 -3  3  0 -1 -4\n"); 
        buf.append("D -2 -2  1  6 -3  0  2 -1 -1 -3 -4 -1 -3 -3 -1  0 -1 -4 -3 -3  4  1 -1 -4\n"); 
        buf.append("C  0 -3 -3 -3  9 -3 -4 -3 -3 -1 -1 -3 -1 -2 -3 -1 -1 -2 -2 -1 -3 -3 -2 -4\n"); 
        buf.append("Q -1  1  0  0 -3  5  2 -2  0 -3 -2  1  0 -3 -1  0 -1 -2 -1 -2  0  3 -1 -4\n"); 
        buf.append("E -1  0  0  2 -4  2  5 -2  0 -3 -3  1 -2 -3 -1  0 -1 -3 -2 -2  1  4 -1 -4\n"); 
        buf.append("G  0 -2  0 -1 -3 -2 -2  6 -2 -4 -4 -2 -3 -3 -2  0 -2 -2 -3 -3 -1 -2 -1 -4\n"); 
        buf.append("H -2  0  1 -1 -3  0  0 -2  8 -3 -3 -1 -2 -1 -2 -1 -2 -2  2 -3  0  0 -1 -4\n"); 
        buf.append("I -1 -3 -3 -3 -1 -3 -3 -4 -3  4  2 -3  1  0 -3 -2 -1 -3 -1  3 -3 -3 -1 -4\n"); 
        buf.append("L -1 -2 -3 -4 -1 -2 -3 -4 -3  2  4 -2  2  0 -3 -2 -1 -2 -1  1 -4 -3 -1 -4\n"); 
        buf.append("K -1  2  0 -1 -3  1  1 -2 -1 -3 -2  5 -1 -3 -1  0 -1 -3 -2 -2  0  1 -1 -4\n"); 
        buf.append("M -1 -1 -2 -3 -1  0 -2 -3 -2  1  2 -1  5  0 -2 -1 -1 -1 -1  1 -3 -1 -1 -4\n"); 
        buf.append("F -2 -3 -3 -3 -2 -3 -3 -3 -1  0  0 -3  0  6 -4 -2 -2  1  3 -1 -3 -3 -1 -4\n"); 
        buf.append("P -1 -2 -2 -1 -3 -1 -1 -2 -2 -3 -3 -1 -2 -4  7 -1 -1 -4 -3 -2 -2 -1 -2 -4\n"); 
        buf.append("S  1 -1  1  0 -1  0  0  0 -1 -2 -2  0 -1 -2 -1  4  1 -3 -2 -2  0  0  0 -4\n"); 
        buf.append("T  0 -1  0 -1 -1 -1 -1 -2 -2 -1 -1 -1 -1 -2 -1  1  5 -2 -2  0 -1 -1  0 -4\n"); 
        buf.append("W -3 -3 -4 -4 -2 -2 -3 -2 -2 -3 -2 -3 -1  1 -4 -3 -2 11  2 -3 -4 -3 -2 -4\n"); 
        buf.append("Y -2 -2 -2 -3 -2 -1 -2 -3  2 -1 -1 -2 -1  3 -3 -2 -2  2  7 -1 -3 -2 -1 -4\n"); 
        buf.append("V  0 -3 -3 -3 -1 -2 -2 -3 -3  3  1 -2  1 -1 -2 -2  0 -3 -1  4 -3 -2 -1 -4\n"); 
        buf.append("B -2 -1  3  4 -3  0  1 -1  0 -3 -4  0 -3 -3 -2  0 -1 -4 -3 -3  4  1 -1 -4\n"); 
        buf.append("Z -1  0  0  1 -3  3  4 -2  0 -3 -3  1 -1 -3 -1  0 -1 -3 -2 -2  1  4 -1 -4\n"); 
        buf.append("X  0 -1 -1 -1 -2 -1 -1 -1 -1 -1 -1 -1 -1 -1 -2  0  0 -2 -1 -1 -1 -1 -1 -4\n"); 
        buf.append("* -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4  1\n"); 
        return buf.toString();
    }
}
