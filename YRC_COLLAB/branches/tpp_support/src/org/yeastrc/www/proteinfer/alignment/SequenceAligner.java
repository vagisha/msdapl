package org.yeastrc.www.proteinfer.alignment;

import jaligner.Alignment;
import jaligner.Sequence;
import jaligner.SmithWatermanGotoh;
import jaligner.matrix.Matrix;
import jaligner.matrix.MatrixLoader;
import jaligner.matrix.MatrixLoaderException;
import jaligner.util.SequenceParser;
import jaligner.util.SequenceParserException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.nrseq.NrSeqLookupUtil;
import org.yeastrc.www.proteinfer.alignment.AlignedProtein.AlignedPosition;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerCluster;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProteinGroup;

import edu.uwpr.protinfer.PeptideDefinition;
import edu.uwpr.protinfer.database.dto.idpicker.IdPickerPeptideBase;

public class SequenceAligner {

    private static final SequenceAligner instance = new SequenceAligner();

    private static final Logger log = Logger.getLogger(SequenceAligner.class.getName());

    public static SequenceAligner instance() {
        return instance;
    }

    public AlignedProteins alignCluster(WIdPickerCluster cluster) throws AlignmentException {

        WIdPickerProtein anchorProtein = getAnchorProtein(cluster);
        
        List<AlignedPair> alignmentList = new ArrayList<AlignedPair>();
        
        for(WIdPickerProteinGroup prGrp: cluster.getProteinGroups()) {

            for(WIdPickerProtein prot: prGrp.getProteins()) {
                if(prot.getProtein().getId() == anchorProtein.getProtein().getId())
                    continue;
                AlignedPair alignedPair = alignProteins(anchorProtein, prot);
                alignmentList.add(alignedPair);
            }
        }
        // merge alignments
        AlignedProteins aligned =  mergeAlignedPairs(alignmentList);
        // mark mismatched indexes
        aligned.updateMismatches();
        return aligned;
    }
    
    public AlignedProteins alignProteins(List<WIdPickerProtein> proteins) throws AlignmentException {
        
        WIdPickerProtein anchorProtein = getAnchorProtein(proteins);
        List<AlignedPair> alignmentList = new ArrayList<AlignedPair>();
        for(WIdPickerProtein prot: proteins) {
            if(prot.getProtein().getId() == anchorProtein.getProtein().getId())
                continue;
            AlignedPair alignedPair = alignProteins(anchorProtein, prot);
            alignmentList.add(alignedPair);
        }
        // merge alignments
        AlignedProteins aligned =  mergeAlignedPairs(alignmentList);
        // mark mismatched indexes
        aligned.updateMismatches();
        return aligned;
    }

    public AlignedProteins mergeAlignedPairs(List<AlignedPair> alignedPairs) throws AlignmentException {
        
        // sort the aligned pairs by length
        Collections.sort(alignedPairs, new Comparator<AlignedPair> () {
            public int compare(AlignedPair o1, AlignedPair o2) {
                return Integer.valueOf(o2.getAlignedLength()).compareTo(o1.getAlignedLength());
            }});
        
        AlignedProteins aProteins = new AlignedProteins();
        if(alignedPairs.size() > 0) {
            aProteins.setAnchorProtein(alignedPairs.get(0).getProtein1());
        }
        for(AlignedPair aPair: alignedPairs) {
            aProteins.addAlignedPair(aPair);
        }
        return aProteins;
    }

    private WIdPickerProtein getAnchorProtein(WIdPickerCluster cluster) {

        List<WIdPickerProtein> list = new ArrayList<WIdPickerProtein>();
        for(WIdPickerProteinGroup prGrp: cluster.getProteinGroups()) {
            list.addAll(prGrp.getProteins());
        }
        return getAnchorProtein(list);
    }
    
    private WIdPickerProtein getAnchorProtein(List<WIdPickerProtein> proteins) {
        WIdPickerProtein protein = null;
        for(WIdPickerProtein prot: proteins) {
            if(protein == null)
                protein = prot;
            else {
                if(prot.getProtein().getPeptideCount() > protein.getProtein().getPeptideCount()) 
                    protein = prot;
            }
        }
        return protein;
    }

    public AlignedPair alignProteins(WIdPickerProtein protein1, WIdPickerProtein protein2) throws AlignmentException {

        Protein p1 = makeProtein(protein1);
        Protein p2 = makeProtein(protein2);
        return alignProteins(p1, p2);
    }
    
    public AlignedPair alignProteins(Protein protein1, Protein protein2) throws AlignmentException {

        String a_seq = protein1.getSequence();
        Sequence s1 = null;
        try {
            s1 = SequenceParser.parse(a_seq);
            s1.setId(protein1.getAccession());
        }
        catch (SequenceParserException e) {
            throw new AlignmentException("Error parsing protein sequence:\n"+a_seq, e);
        }

        String b_seq = protein2.getSequence();

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
        return makeAlignedPair(alignment, protein1, protein2);
    }

    private String getProteinSequence(int nrseqId) {
        return NrSeqLookupUtil.getProteinSequence(nrseqId);
    }
    
    private Protein makeProtein(WIdPickerProtein wProtein) {
        String sequence = getProteinSequence(wProtein.getProtein().getNrseqProteinId());
        Protein protein = new Protein(wProtein.getProtein().getId(), wProtein.getProtein().getGroupId(), sequence);
        protein.setAccession(wProtein.getAccession());
        protein.setDescription(wProtein.getDescription());
        protein.setNrseqId(wProtein.getProtein().getNrseqProteinId());
        for(IdPickerPeptideBase peptide: wProtein.getProtein().getPeptides()) {
            protein.addCoveredFragment(peptide.getSequence());
        }
        return protein;
    }
    
    private AlignedPair makeAlignedPair(Alignment alignment, Protein protein1, Protein protein2) throws AlignmentException {


        AlignedProtein aprot1 = new AlignedProtein(protein1);

        AlignedProtein aprot2 = new AlignedProtein(protein2);
        AlignedPair pair = new AlignedPair();
        pair.setProtein1(aprot1);
        pair.setProtein2(aprot2);

        String aseq1 = String.valueOf(alignment.getSequence1());
        String aseq2 = String.valueOf(alignment.getSequence2());
        int s1 = alignment.getStart1();
        if(s1 > 0) {
            aseq1 = aprot1.getSequence().substring(0, s1) + aseq1;
            aseq2 = addGaps(aseq2, s1, true);
        }
        int s2 = alignment.getStart2();
        if(s2 > 0) {
            aseq2 = aprot2.getSequence().substring(0, s2) + aseq2;
            aseq1 = addGaps(aseq1, s2, true);
        }

        int e1 = getAlignmentEnd(aprot1.getSequence(), aseq1);
        if(e1 < aprot1.getLength()) {
            aseq1 = aseq1 + aprot1.getSequence().substring(e1);
            aseq2 = addGaps(aseq2, aprot1.getLength() - e1, false);
        }
        int e2 = getAlignmentEnd(aprot2.getSequence(), aseq2);
        if(e2 < aprot2.getLength()) {
            aseq2 = aseq2 + aprot2.getSequence().substring(e2);
            aseq1 = addGaps(aseq1, aprot2.getLength() - e2, false);
        }

        aprot1.setAlignedSequence(aseq1);
        aprot2.setAlignedSequence(aseq2);

        return pair;
    }

    private int getAlignmentEnd(String originalSeq, String alignedSeq) throws AlignmentException {
        String alignedMinusGaps = alignedSeq.replaceAll("-", "");
        int sIdx = originalSeq.lastIndexOf(alignedMinusGaps);
        if(sIdx == -1)
            throw new AlignmentException("Original sequence and aligned sequence do not match.\n"+
                    "ORIG: "+originalSeq+"\n"+
                    "ALGN: "+alignedSeq+"\n");
        return sIdx + alignedMinusGaps.length();
    }

    private String addGaps(String sequence, int gapCount, boolean pre) {
        String gaps = "";
        for(int i = 0; i < gapCount; i++)
            gaps = gaps+"-";
        if(pre)
            return gaps + sequence;
        else
            return sequence + gaps;
    }

    
    public static void main(String[] args) throws AlignmentException {

        WIdPickerCluster cluster = IdPickerResultsLoader.getIdPickerCluster(14, 1, new PeptideDefinition());

        SequenceAligner aligner = SequenceAligner.instance();
        AlignedProteins aligned = aligner.alignCluster(cluster);
        System.out.println(aligned.printAlignment());
        
        
//        Protein protein1 = new Protein(1, "ABCDXYZEFGHI");
//        Protein protein2 = new Protein(2, "CDEFGK");
//        Protein protein3 = new Protein(3, "ABCDGHI");
//        AlignedPair alignedPair1 = aligner.alignProteins(protein1, protein2);
//        AlignedPair alignedPair2 = aligner.alignProteins(protein1, protein3);
//        System.out.println(alignedPair1.getProtein1().getAlignedSequence());
//        System.out.println(alignedPair1.getProtein2().getAlignedSequence());
//        System.out.println(alignedPair2.getProtein1().getAlignedSequence());
//        System.out.println(alignedPair2.getProtein2().getAlignedSequence());
//        
//        List<AlignedPair> alignedPairs = new ArrayList<AlignedPair>();
//        alignedPairs.add(alignedPair1);
//        alignedPairs.add(alignedPair2);
//        AlignedProteins aligned = aligner.mergeAlignedPairs(alignedPairs);
//        aligned.updateMismatches();
//        System.out.println("\nAFTER MERGING\n");
//        System.out.println(aligned.getAnchorProtein().getAlignedSequence());
//        for(AlignedProtein prot: aligned.getAlignedProteins())
//            System.out.println(prot.getAlignedSequence());
        for(AlignedPosition pos: aligned.getAnchorProtein().getAlignedPositions())
            System.out.print(pos);
        System.out.println();
    }
}
