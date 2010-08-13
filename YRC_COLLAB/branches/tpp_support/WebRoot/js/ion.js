function Ion (t, color, charge, terminus) {
	this.type = t;
	this.color = color;
	this.charge = charge;
	this.label = this.type;
	if(this.charge > 1)
		this.label += charge;
	this.label += "+";
	this.term = terminus;
}

// Source: http://en.wikipedia.org/wiki/Web_colors

// charge +1
Ion.A_1 = new Ion("a", "#008000", 1, "n"); // green
Ion.B_1 = new Ion("b", "#ff0000", 1, "n"); // red
Ion.C_1 = new Ion("c", "#FF8C00", 1, "n"); // dark orange
Ion.X_1 = new Ion("x", "#4B0082", 1, "y"); // indigo
Ion.Y_1 = new Ion("y", "#0000ff", 1, "y"); // blue
Ion.Z_1 = new Ion("z", "#008B8B", 1, "y"); // dark cyan

// charge +2
Ion.A_2 = new Ion("a", "#2E8B57", 2, "n"); // sea green
Ion.B_2 = new Ion("b", "#FA8072", 2, "n"); // salmon
Ion.C_2 = new Ion("c", "#FFA500", 2, "n"); // orange
Ion.X_2 = new Ion("x", "#800080", 2, "y"); // purple
Ion.Y_2 = new Ion("y", "#4169E1", 2, "y"); // royal blue
Ion.Z_2 = new Ion("z", "#20B2AA", 2, "y"); // light sea green

// charge +3
Ion.A_3 = new Ion("a", "#9ACD32", 3, "n"); // yellow green
Ion.B_3 = new Ion("b", "#FFA07A", 3, "n"); // light salmon
Ion.C_3 = new Ion("c", "#FFD700", 3, "n"); // gold
Ion.X_3 = new Ion("x", "#9932CC", 3, "y"); // dark orchid
Ion.Y_3 = new Ion("y", "#00BFFF", 3, "y"); // deep sky blue
Ion.Z_3 = new Ion("z", "#66CDAA", 3, "y"); // medium aquamarine

var _ions = [];
_ions["a"] = [];
_ions["a"][1] = Ion.A_1;
_ions["a"][2] = Ion.A_2;
_ions["a"][3] = Ion.A_3;
_ions["b"] = [];
_ions["b"][1] = Ion.B_1;
_ions["b"][2] = Ion.B_2;
_ions["b"][3] = Ion.B_3;
_ions["c"] = [];
_ions["c"][1] = Ion.C_1;
_ions["c"][2] = Ion.C_2;
_ions["c"][3] = Ion.C_3;
_ions["x"] = [];
_ions["x"][1] = Ion.X_1;
_ions["x"][2] = Ion.X_2;
_ions["x"][3] = Ion.X_3;
_ions["y"] = [];
_ions["y"][1] = Ion.Y_1;
_ions["y"][2] = Ion.Y_2;
_ions["y"][3] = Ion.Y_3;
_ions["z"] = [];
_ions["z"][1] = Ion.Z_1;
_ions["z"][2] = Ion.Z_2;
_ions["z"][3] = Ion.Z_3;

Ion.get = function _getIon(type, charge) {
	
	return _ions[type][charge];
}

Ion.getSeriesColor = function _getSeriesColor(ion) {
	
	return _ions[ion.type][ion.charge].color;
}


//-----------------------------------------------------------------------------
// Ion Series
//-----------------------------------------------------------------------------
MASS_H = 1.00794;
MASS_C = 12.011;
MASS_N = 14.00674;
MASS_O = 15.9994;
MASS_PROTON = 1.007276;

// massType can be "mono" or "avg"
Ion.getSeriesIon = function _getSeriesIon(ion, sequence, idxInSeq, massType) {
	if(ion.type == "a")	
		return new Ion_A (sequence, idxInSeq, ion.charge, massType);
	if(ion.type == "b")
		return new Ion_B (sequence, idxInSeq, ion.charge, massType);
	if(ion.type == "c")
		return new Ion_C (sequence, idxInSeq, ion.charge, massType);
	if(ion.type == "x")
		return new Ion_X (sequence, idxInSeq, ion.charge, massType);
	if(ion.type == "y")
		return new Ion_Y (sequence, idxInSeq, ion.charge, massType);
	if(ion.type == "z")
		return new Ion_Z (sequence, idxInSeq, ion.charge, massType);
}

function _makeIonLabel(type, index, charge) {
	var label = type+""+index;
	for(var i = 1; i <= charge; i+=1) 
		label += "+";
	return label;
}

function _getMz(neutralMass, charge) {
	return ( neutralMass + (charge * MASS_PROTON) ) / charge
}

function Ion_A (sequence, endIdxPlusOne, charge, massType) {
	// Neutral mass:  	 [N]+[M]-CHO  ; N = mass of neutral N terminal group
	var mass = 0;
	if(massType == "mono")
		mass = Peptide.getSeqMassMono(sequence, endIdxPlusOne, "n") - (MASS_C + MASS_O);
	else if(massType == "avg")
		mass = Peptide.getSeqMassAvg(sequence, endIdxPlusOne, "n") - (MASS_C + MASS_O);
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("a",endIdxPlusOne, charge);
	this.match = false;
	this.term = "n";
	return this;
}

function Ion_B (sequence, endIdxPlusOne, charge, massType) {
	// Neutral mass:    [N]+[M]-H  ; N = mass of neutral N terminal group
	var mass = 0;
	if(massType == "mono")
		mass = Peptide.getSeqMassMono(sequence, endIdxPlusOne, "n");
	else if(massType == "avg")
		mass = Peptide.getSeqMassAvg(sequence, endIdxPlusOne, "n");
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("b", endIdxPlusOne, charge);
	this.match = false;
	this.term = "n";
	return this;
}

function Ion_C (sequence, endIdxPlusOne, charge, massType) {
	// Neutral mass:    [N]+[M]+NH2  ; N = mass of neutral N terminal group
	var mass = 0;
	if(massType == "mono")
		mass = Peptide.getSeqMassMono(sequence, endIdxPlusOne, "n") + MASS_H + (MASS_N + 2*MASS_H);
	else if(massType == "avg")
		mass = Peptide.getSeqMassAvg(sequence, endIdxPlusOne, "n") + MASS_H + (MASS_N + 2*MASS_H);
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("c", endIdxPlusOne, charge);
	this.match = false;
	this.term = "n";
	return this;
}

function Ion_X (sequence, startIdx, charge, massType) {
	// Neutral mass = [C]+[M]+CO-H ; C = mass of neutral C-terminal group (OH)
	var mass = 0;
	if(massType == "mono")
		mass = Peptide.getSeqMassMono(sequence, startIdx, "c") + 2*MASS_O + MASS_C;
	else if(massType == "avg")
		mass = Peptide.getSeqMassAvg(sequence, startIdx, "c") + 2*MASS_O + MASS_C;
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("x", sequence.length - startIdx, charge);
	this.match = false;
	this.term = "c";
	return this;
}

function Ion_Y (sequence, startIdx, charge, massType) {
	// Neutral mass = [C]+[M]+H ; C = mass of neutral C-terminal group (OH)
	var mass = 0;
	if(massType == "mono")
		mass = Peptide.getSeqMassMono(sequence, startIdx, "c") + 2*MASS_H + MASS_O;
	else if(massType == "avg")
		mass = Peptide.getSeqMassAvg(sequence, startIdx, "c") + 2*MASS_H + MASS_O;
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("y", sequence.length - startIdx, charge);
	this.match = false;
	this.term = "c";
	return this;
}

function Ion_Z (sequence, startIdx, charge, massType) {
	// Neutral mass = [C]+[M]-NH2 ; C = mass of neutral C-terminal group (OH)
	// After comparing with TPP's Comet Spectrum View added one proton
	var mass = 0;
	if(massType == "mono")
		mass = Peptide.getSeqMassMono(sequence, startIdx, "c") + MASS_O - (MASS_N + MASS_H) + MASS_PROTON;
	else if(massType == "avg")
		mass = Peptide.getSeqMassAvg(sequence, startIdx, "c") + MASS_O - (MASS_N + MASS_H) + MASS_PROTON;
	this.mz = _getMz(mass, charge);
	this.label = _makeIonLabel("z", sequence.length - startIdx, charge);
	this.match = false;
	this.term = "c";
	return this;
}