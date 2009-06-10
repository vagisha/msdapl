/**
 * AminoAcidUtils.java
 * @author Vagisha Sharma
 * Jan 18, 2009
 * @version 1.0
 */
package org.yeastrc.ms.util;

/**
 * 
 */
public class AminoAcidUtils {

    public static final double HYDROGEN = 1.00794;
    public static final double OXYGEN = 15.9994;
    public static final double PROTON = 1.007276;
    
    private AminoAcidUtils() {}
    
    public static double avgMass(char aminoAcid) {
        switch(aminoAcid) {
            case 'G': return avgMass_G();
            case 'A': return avgMass_A();
            case 'S': return avgMass_S();
            case 'P': return avgMass_P();
            case 'V': return avgMass_V();
            case 'T': return avgMass_T();
            case 'C': return avgMass_C();
            case 'L': return avgMass_L();
            case 'I': return avgMass_I();
            case 'X': return avgMass_X();
            case 'N': return avgMass_N();
            case 'O': return avgMass_O();
            case 'B': return avgMass_B();
            case 'D': return avgMass_D();
            case 'Q': return avgMass_Q();
            case 'K': return avgMass_K();
            case 'Z': return avgMass_Z();
            case 'E': return avgMass_E();
            case 'M': return avgMass_M();
            case 'H': return avgMass_H();
            case 'F': return avgMass_F();
            case 'R': return avgMass_R();
            case 'Y': return avgMass_Y();
            case 'W': return avgMass_W();
            default : return 0;
        }
    }
    
    public static double monoMass(char aminoAcid) {
        switch(aminoAcid) {
            case 'G': return monoMass_G();
            case 'A': return monoMass_A();
            case 'S': return monoMass_S();
            case 'P': return monoMass_P();
            case 'V': return monoMass_V();
            case 'T': return monoMass_T();
            case 'C': return monoMass_C();
            case 'L': return monoMass_L();
            case 'I': return monoMass_I();
            case 'X': return monoMass_X();
            case 'N': return monoMass_N();
            case 'O': return monoMass_O();
            case 'B': return monoMass_B();
            case 'D': return monoMass_D();
            case 'Q': return monoMass_Q();
            case 'K': return monoMass_K();
            case 'Z': return monoMass_Z();
            case 'E': return monoMass_E();
            case 'M': return monoMass_M();
            case 'H': return monoMass_H();
            case 'F': return monoMass_F();
            case 'R': return monoMass_R();
            case 'Y': return monoMass_Y();
            case 'W': return monoMass_W();
            default : return 0;
        }
    }
    
    public static double monoMassPeptide(String peptide) {
        if(peptide == null || peptide.length() == 0)
            return 0;
        double mass = 0;
        peptide.toUpperCase();
        for(int i = 0; i < peptide.length(); i++) {
            mass += monoMass(peptide.charAt(i));
        }
        return mass;
    }
    
    public static double avgMassPeptide(String peptide) {
        if(peptide == null || peptide.length() == 0)
            return 0;
        double mass = 0;
        peptide.toUpperCase();
        for(int i = 0; i < peptide.length(); i++) {
            mass += avgMass(peptide.charAt(i));
        }
        return mass;
    }
    
//  add_G_Glycine = 0.0000                 ; added to G - avg.  57.0519, mono.  57.02146
    public static double avgMass_G() {return 57.0519;}
    public static double monoMass_G(){return 57.02146;}
//  add_A_Alanine = 0.0000                 ; added to A - avg.  71.0788, mono.  71.03711
    public static double avgMass_A() {return 71.0788;}
    public static double monoMass_A(){return 71.03711;}
//  add_S_Serine = 0.0000                  ; added to S - avg.  87.0782, mono.  87.02303
    public static double avgMass_S() {return 87.0782;}
    public static double monoMass_S(){return 87.02303;}
//  add_P_Proline = 0.0000                 ; added to P - avg.  97.1167, mono.  97.05276
    public static double avgMass_P() {return 97.1167;}
    public static double monoMass_P(){return 97.05276;}
//  add_V_Valine = 0.0000                  ; added to V - avg.  99.1326, mono.  99.06841
    public static double avgMass_V() {return 99.1326;}
    public static double monoMass_V(){return 99.06841;}
//  add_T_Threonine = 0.0000               ; added to T - avg. 101.1051, mono. 101.04768
    public static double avgMass_T() {return 101.1051;}
    public static double monoMass_T(){return 101.04768;}
//  add_C_Cysteine = 57.021                ; added to C - avg. 103.1388, mono. 103.00919
    public static double avgMass_C() {return 103.1388;}
    public static double monoMass_C(){return 103.00919;}
//  add_L_Leucine = 0.0000                 ; added to L - avg. 113.1594, mono. 113.08406
    public static double avgMass_L() {return 113.1594;}
    public static double monoMass_L(){return 113.08406;}
//  add_I_Isoleucine = 0.0000              ; added to I - avg. 113.1594, mono. 113.08406
    public static double avgMass_I() {return 113.1594;}
    public static double monoMass_I(){return 113.08406;}
//  add_X_LorI = 0.0000                    ; added to X - avg. 113.1594, mono. 113.08406
    public static double avgMass_X() {return 113.1594;}
    public static double monoMass_X(){return 113.08406;}
//  add_N_Asparagine = 0.0000              ; added to N - avg. 114.1038, mono. 114.04293
    public static double avgMass_N() {return 114.1038;}
    public static double monoMass_N(){return 114.04293;}
//  add_O_Ornithine = 0.0000               ; added to O - avg. 114.1472, mono  114.07931
    public static double avgMass_O() {return 114.1472;}
    public static double monoMass_O(){return 114.07931;}
//  add_B_avg_NandD = 0.0000               ; added to B - avg. 114.5962, mono. 114.53494
    public static double avgMass_B() {return 114.5962;}
    public static double monoMass_B(){return 114.53494;}
//  add_D_Aspartic_Acid = 0.0000           ; added to D - avg. 115.0886, mono. 115.02694
    public static double avgMass_D() {return 115.0886;}
    public static double monoMass_D(){return 115.02694;}
//  add_Q_Glutamine = 0.0000               ; added to Q - avg. 128.1307, mono. 128.05858
    public static double avgMass_Q() {return 128.1307;}
    public static double monoMass_Q(){return 128.05858;}
//  add_K_Lysine = 0.0000                  ; added to K - avg. 128.1741, mono. 128.09496
    public static double avgMass_K() {return 128.1741;}
    public static double monoMass_K(){return 128.09496;}
//  add_Z_avg_QandE = 0.0000               ; added to Z - avg. 128.6231, mono. 128.55059
    public static double avgMass_Z() {return 128.6231;}
    public static double monoMass_Z(){return 128.55059;}
//  add_E_Glutamic_Acid = 0.0000           ; added to E - avg. 129.1155, mono. 129.04259
    public static double avgMass_E() {return 129.1155;}
    public static double monoMass_E(){return 129.04259;}
//  add_M_Methionine = 0.0000              ; added to M - avg. 131.1926, mono. 131.04049
    public static double avgMass_M() {return 131.1926;}
    public static double monoMass_M(){return 131.04049;}
//  add_H_Histidine = 0.0000               ; added to H - avg. 137.1411, mono. 137.05891
    public static double avgMass_H() {return 137.1411;}
    public static double monoMass_H(){return 137.05891;}
//  add_F_Phenyalanine = 0.0000            ; added to F - avg. 147.1766, mono. 147.06841
    public static double avgMass_F() {return 147.1766;}
    public static double monoMass_F(){return 147.06841;}
//  add_R_Arginine = 0.0000                ; added to R - avg. 156.1875, mono. 156.10111
    public static double avgMass_R() {return 156.1875;}
    public static double monoMass_R(){return 156.10111;}
//  add_Y_Tyrosine = 0.0000                ; added to Y - avg. 163.1760, mono. 163.06333
    public static double avgMass_Y() {return 163.1760;}
    public static double monoMass_Y(){return 163.06333;}
//  add_W_Tryptophan = 0.0000              ; added to W - avg. 186.2132, mono. 186.07931
    public static double avgMass_W() {return 186.2132;}
    public static double monoMass_W(){return 186.07931;}
}