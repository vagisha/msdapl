// -----------------------------------------------------------------------------
// Peptide sequence and modifications
// -----------------------------------------------------------------------------
Peptide.sequence;
Peptide.staticMods;
Peptide.varMods;
Peptide.ntermMod;
Peptide.ctermMod;

function Peptide(seq, staticModifications, varModifications, ntermModification, ctermModification) {
	
	Peptide.sequence = seq;
	Peptide.ntermMod = ntermModification;
	Peptide.ctermMod = ctermModification;
	Peptide.staticMods = staticModifications;
	
	Peptide.varMods = [];
	if(varModifications) {
		for(var i = 0; i < varModifications.length; i += 1) {
			var mod = varModifications[i];
			Peptide.varMods[mod.position] = mod;
		}
	}
}


// index: index in the original sequence
Peptide.getSeqMass = function _seqMass(seq, index, term) {
	
	var mass = 0;
	var aa_obj = new AminoAcid();
	if(seq) {
		for( var i = 0; i < seq.length; i += 1) {
			var aa = aa_obj.get(seq[i]);
			mass += aa.mono;
		}
	}
	
	// add any terminal modifications
	if(term == "n" && Peptide.ntermMod)
		mass += Peptide.ntermMod;
	if(term == "c" && Peptide.ctermMod)
		mass += Peptide.ctermMod;
	
	// add any static modifications
	for(var i = 0; i < seq.length; i += 1) {
		var mod = Peptide.staticMods[seq[i]];
		if(mod)
			mass += mod.modMass;
	}
	
	// add any varible modifications
	if(term == "n") {
		for(var i = 0; i < index; i += 1) {
			var mod = Peptide.varMods[i+1]; // varMods index in the sequence is 1-based
			if(mod) {
				mass += mod.modMass;
			}
		}
	}
	if(term == "c") {
		for(var i = (Peptide.sequence.length - index); i < Peptide.sequence.length; i += 1) {
			var mod = Peptide.varMods[i+1]; // varMods index in the sequence is 1-based
			if(mod) {
				mass += mod.modMass;
			}
		}
	}
	
	return mass;
}


//-----------------------------------------------------------------------------
// Modification
//-----------------------------------------------------------------------------
function Modification(aminoAcid, mass) {
	this.aa = aminoAcid;
	this.modMass = mass;
}

function VariableModification(pos, mass, aminoAcid) {
	this.position = pos
	this.aa = aminoAcid;
	this.modMass = mass;
}



