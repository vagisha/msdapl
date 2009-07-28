package org.yeastrc.ms.service;

import org.yeastrc.ms.domain.general.MsEnzyme.Sense;

public class EnzymeRules {

    public static enum ENZYME {
        
        TRYPSIN (Sense.CTERM, "KR", "P");
        
        private Sense sense;
        private char[] cut;
        private char[] noCut;
        
        private ENZYME(Sense sense, String cut, String noCut) {
            this.sense = sense;
            this.cut = new char[cut.length()];
            for (int i = 0; i < cut.length(); i++)
                this.cut[i] = cut.charAt(i);
            this.noCut = new char[noCut.length()];
            for(int i = 0; i < noCut.length(); i++)
                this.noCut[i] = noCut.charAt(i);
        }

        public Sense getSense() {
            return sense;
        }

        public boolean isValidTermResidue(char residue) {
            for(char c: cut)
                if(c == residue)    return true;
            return false;
        }
        
        public String getCut() {
            String s = "";
            for(char c: cut)
                s += c;
            return s;
        }
        public String getNoCut() {
            String s = "";
            for(char c: noCut)
                s += c;
            return s;
        }
    }
    
    public static int numEnzymaticTermini(char ntermResidue, String sequence, char ctermResidue, ENZYME enzyme) {
        int net = 0;
        if(enzyme.sense == Sense.CTERM) {
            if(ntermResidue == '-' || enzyme.isValidTermResidue(ntermResidue))
                net++;
            if(ctermResidue == '-' || enzyme.isValidTermResidue(sequence.charAt(sequence.length() - 1)))
                net++;
        }
        else if (enzyme.sense == Sense.NTERM) {
            if(ntermResidue == '-' || enzyme.isValidTermResidue(sequence.charAt(0)))
                net++;
            if(ctermResidue == '-' || enzyme.isValidTermResidue(ctermResidue))
                    net++;
        }
        return net;
    }

    public static void main(String[] args) {
        String seq = "PEPTIDE";
        char pre = 'K';
        char post = '-';
        int num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 2)");
        
        pre = 'R';
        num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 2)");
        
        pre = '-';
        num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 2)");
        
        pre = 'Q';
        num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 1)");
        
        post = 'K';
        num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 0)");
        
        post = 'R';
        num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 0)");
        
        post = 'L';
        num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 0)");
        
        seq = "PEPTIDER";
        num = numEnzymaticTermini(pre, seq, post, ENZYME.TRYPSIN);
        System.out.println(pre+"."+seq+"."+post+"\tntt: "+num+" (SHOULD BE 1)");
    }
}
