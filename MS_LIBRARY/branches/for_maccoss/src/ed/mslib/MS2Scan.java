package ed.mslib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class MS2Scan implements java.lang.Cloneable {

	public MS2Scan(){
		charges = new ArrayList<Integer>();
		masses = new ArrayList<Double>();
		dfield = new ArrayList<String>();
		ifield = new ArrayList<String>();
		mzint = new ArrayList<MzInt>();
	}
	
	private int scan;
	private int endscan;
	private float precursor = -1;
	
	/**
	 * Z FIELD - charge
	 */
	private List<Integer> charges;
	/**
	 * Z FIELD - mass (M+H)
	 */
	private List<Double> masses;
	
	/**
	 * D FIELD - Charge dependent data
	 */
	private List<String> dfield;
	
	/**
	 * I FIELD - Charge independent data
	 */
	private List<String> ifield;
	
	/**
	 * list of mz/intensity data objects
	 */
	private List<MzInt> mzint;
	
	public int getscan(){return scan;}
	public void setscan(int sc){scan = sc;}
	
	public int getendscan(){return endscan;}
	public void setendscan(int endsc){endscan = endsc;}
	
	public float getprecursor(){return precursor;}
	public void setprecursor(float pre){precursor = pre;}
	
	/**
	 * Z FIELD
	 * Add Charge and Mass to List
	 * @param chrg
	 * @param mass
	 */
	public void addchargemass(int chrg, double mass){
		charges.add(chrg);
		masses.add(mass);
	}
	/**
	 * Z FIELD
	 * Get charge from List at position
	 * @param position
	 * @return
	 */
	public int getcharge(int position){
		return charges.get(position);
	}
	/**
	 * Z FIELD
	 * Get mass from List at Position
	 * @param position
	 * @return
	 */
	public double getmass(int position){
		return masses.get(position);
	}
	
	/**
	 * D FIELD
	 * add String to dfield List
	 * @param d
	 */
	public void addDField(String d){
		dfield.add(d);
	}
	/**
	 * D FIELD
	 * get String from dfield List
	 * @param position
	 * @return
	 */
	public String getDField(int position){
		return dfield.get(position);
	}
	
	public int numberOfDLines() {
	    return dfield.size();
	}
	
	/**
	 * I FIELD
	 * add String to ifield List
	 * @param i string to add
	 */
	public void addIfield(String i){
		ifield.add(i);
	}
	/**
	 * I FIELD
	 * get String from ifield List
	 * @param position
	 * @return string from ifield List
	 */
	public String getIfield(int position){
		return ifield.get(position);
	}
	public int numberOfILines(){
		return ifield.size();
	}
	
	/**
	 * Mass/Intensity data
	 * Add m/z and intensity to Mass/Intensity data List
	 * @param masscharge
	 * @param inten
	 */
	public void addscan(double masscharge, float inten){
		mzint.add(new MzInt(masscharge, inten));
	}
	/**
	 * Mass/Intensity data
	 * Get m/z from data List at position
	 * @param position
	 * @return
	 */
	public double getmz(int position){
		return mzint.get(position).getmz();
	}
	/**
	 * Mass/Intensity data
	 * Get intensity from data List at position
	 * @param position
	 * @return
	 */
	public float getintensity(int position){
		return mzint.get(position).getint();
	}
	
	public MzInt getmzint(int position){
		return mzint.get(position);
	}
	public List<MzInt> getmzintlist(){
		return mzint;
	}
	public int getdatasize(){
		return mzint.size();
	}
	
	/**
	 * Z FIELD - Charge
	 * @return Z FIELD Charge List
	 */
	public List<Integer> getchargeslist(){return charges;}
	
	/**
	 * Z FIELD - Mass (M+H)
	 * @return Z Field Mass List
	 */
	public List<Double> getmasseslist(){return masses;}
	
	public void setmzintlist(List<MzInt> list){mzint = list;}
	
	public void outputall(){	
		if (precursor != -1){
			System.out.println("S\t"+ scan + "\t" + endscan + "\t" + precursor);
			for (int i=0; i<ifield.size();i++){
				System.out.println("I\t"+ifield.get(i));
			}
			for (int i=0; i<charges.size(); i++){
				System.out.println("Z\t" + charges.get(i) + "\t" + masses.get(i));
				if (dfield.size() != 0 && dfield.get(i) != ""){
					System.out.println("D\t" + dfield.get(i));
				}
			}
			for (int i=0; i<mzint.size(); i++){
				System.out.println("" + mzint.get(i).getmz() + "\t" + mzint.get(i).getint());
			}
			System.out.println();
		}		
	}
	
	public void outputall(BufferedWriter writer) throws IOException{   
        if (precursor != -1){
            writer.write("S\t"+ scan + "\t" + endscan + "\t" + precursor+"\n");
            for (int i=0; i<ifield.size();i++){
                writer.write("I\t"+ifield.get(i)+"\n");
            }
            for (int i=0; i<charges.size(); i++){
                writer.write("Z\t" + charges.get(i) + "\t" + masses.get(i)+"\n");
                if (dfield.size() != 0 && dfield.get(i) != ""){
                    writer.write("D\t" + dfield.get(i)+"\n");
                }
            }
            for (int i=0; i<mzint.size(); i++){
                writer.write("" + mzint.get(i).getmz() + "\t" + mzint.get(i).getint()+"\n");
            }
//            System.out.println();
        }       
    }
	
	/**
	 * calls cloneNoData, then also adds mz/int data
	 */
	public MS2Scan clone(){
		MS2Scan result = cloneNoData();
		for (int m=0; m<mzint.size(); m++){
			result.addscan(mzint.get(m).getmz(), mzint.get(m).getint());
		}
		return result;
	}
	
	/**
	 * Makes new MS2Scan, copies all data except mz/int data.
	 * @return
	 */
	public MS2Scan cloneNoData(){
		MS2Scan result = new MS2Scan();
		result.setscan(this.scan);
		result.setendscan(this.endscan);
		result.setprecursor(this.precursor);
		for (int c=0; c<charges.size(); c++){
			result.addchargemass(charges.get(c), masses.get(c));
		}
		for (int d=0; d<dfield.size(); d++){
			result.addDField(dfield.get(d));
		}
		for (int i=0; i<ifield.size(); i++){
			result.addIfield(ifield.get(i));
		}
		return result;
	}
	
}
