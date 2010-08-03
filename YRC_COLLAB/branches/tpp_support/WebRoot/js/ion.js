function Ion (t, color, charge) {
	this.type = t;
	this.color = color;
	this.charge = charge;
	this.label = this.type;
	if(this.charge > 1)
		this.label += charge;
	this.label += "+";
}

// Source: http://en.wikipedia.org/wiki/Web_colors

// charge +1
Ion.A_1 = new Ion("a", "#008000", 1); // green
Ion.B_1 = new Ion("b", "#ff0000", 1); // red
Ion.C_1 = new Ion("c", "#FF8C00", 1); // dark orange
Ion.Y_1 = new Ion("y", "#0000ff", 1); // blue
Ion.Z_1 = new Ion("z", "#008B8B", 1); // dark cyan

// charge +2
Ion.A_2 = new Ion("a", "#0ff000", 2); // lime
Ion.B_2 = new Ion("b", "#FA8072", 2); // salmon
Ion.C_2 = new Ion("c", "#FFA500", 2); // orange
Ion.Y_2 = new Ion("y", "#4169E1", 2); // royal blue
Ion.Z_2 = new Ion("z", "#20B2AA", 2); // light sea green

// charge +3
Ion.A_3 = new Ion("a", "#9ACD32", 3); // yellow green
Ion.B_3 = new Ion("b", "#FFA07A", 3); // light salmon
Ion.C_3 = new Ion("c", "#FFD700", 3); // gold
Ion.Y_3 = new Ion("y", "#00BFFF", 3); // deep sky blue
Ion.Z_3 = new Ion("z", "#66CDAA", 3); // medium aquamarine

Ion.get = _getIon;

function _getIon(type, charge) {
	
	if(type == "a") {
		if(charge == 1)
			return Ion.A_1;
		if(charge == 2)
			return Ion.A_2;
		if(charge == 3)
			return Ion.A_3;
	}
	if(type == "b") {
		if(charge == 1)
			return Ion.B_1;
		if(charge == 2)
			return Ion.B_2;
		if(charge == 3)
			return Ion.B_3;
	}
	if(type == "c") {
		if(charge == 1)
			return Ion.C_1;
		if(charge == 2)
			return Ion.C_2;
		if(charge == 3)
			return Ion.C_3;
	}
	if(type == "y") {
		if(charge == 1)
			return Ion.Y_1;
		if(charge == 2)
			return Ion.Y_2;
		if(charge == 3)
			return Ion.Y_3;
	}
	if(type == "z") {
		if(charge == 1)
			return Ion.Z_1;
		if(charge == 2)
			return Ion.Z_2;
		if(charge == 3)
			return Ion.Z_3;
	}
}

//-----------------------------------------------------------------------------
// Ion Series
//-----------------------------------------------------------------------------
MASS_H = 1.00794;
MASS_C = 12.011;
MASS_N = 14.00674;
MASS_O = 15.9994;
MASS_PROTON = 1.007276;


function _makeIonLabel(type, index, charge) {
	var label = type+""+index;
	for(var i = 1; i <= charge; i+=1) 
		label += "+";
	return label;
}

function _getMz(neutralMass, charge) {
	return ( neutralMass + (charge * MASS_PROTON) ) / charge
}

function Ion_A (sequence, endIdxPlusOne, charge) {
	// Neutral mass:  	 [N]+[M]-CHO  ; N = mass of neutral N terminal group
	var mass = Peptide.getSeqMass(sequence, endIdxPlusOne, "n") - (MASS_C + MASS_O);
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("a",endIdxPlusOne, charge);
	this.match = false;
	return this;
}

function Ion_B (sequence, endIdxPlusOne, charge) {
	// Neutral mass:    [N]+[M]-H  ; N = mass of neutral N terminal group
	var mass = Peptide.getSeqMass(sequence, endIdxPlusOne, "n");
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("b", endIdxPlusOne, charge);
	this.match = false;
	return this;
}

function Ion_C (sequence, endIdxPlusOne, charge) {
	// Neutral mass:    [N]+[M]+NH2  ; N = mass of neutral N terminal group
	var mass = Peptide.getSeqMass(sequence, endIdxPlusOne, "n") + MASS_H + (MASS_N + 2*MASS_H);
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("c", endIdxPlusOne, charge);
	this.match = false;
	return this;
}

function Ion_Y (sequence, startIdx, charge) {
	// Neutral mass = [C]+[M]+H ; C = mass of neutral C-terminal group (OH)
	var mass = Peptide.getSeqMass(sequence, startIdx, "c") + 2*MASS_H + MASS_O;
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("y", startIdx, charge);
	this.match = false;
	return this;
}

function Ion_Z (sequence, startIdx, charge) {
	// Neutral mass = [C]+[M]-NH2 ; C = mass of neutral C-terminal group (OH)
	// After comparing with TPP's Comet Spectrum View added one proton
	var mass = Peptide.getSeqMass(sequence, startIdx, "c") + MASS_O - (MASS_N + MASS_H) + MASS_PROTON;
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("z", startIdx, charge);
	this.match = false;
	return this;
}